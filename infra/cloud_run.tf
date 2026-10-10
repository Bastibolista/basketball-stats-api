locals {
  secret_env = {
    DB_PASSWORD = "db-password"
  }
}

resource "google_cloud_run_v2_service" "api" {
  count    = var.deploy_service ? 1 : 0
  name     = "basketball-stats-api"
  location = var.region
  ingress  = "INGRESS_TRAFFIC_ALL"

  # Lets `terraform destroy` remove the service between sessions.
  deletion_protection = false

  template {
    service_account = google_service_account.api.email

    # One instance keeps the in-memory rate limiter consistent.
    scaling {
      min_instance_count = 0
      max_instance_count = 1
    }

    containers {
      image = local.image

      ports {
        container_port = 8080
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "1Gi"
        }
        cpu_idle = true
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = var.database_url
      }
      env {
        name  = "DB_USER"
        value = var.db_user
      }
      env {
        name  = "APP_AUTH_MODE"
        value = "google"
      }
      env {
        name  = "GOOGLE_CLIENT_ID"
        value = var.google_client_id
      }
      env {
        name  = "APP_ALLOWED_EMAILS"
        value = var.allowed_emails
      }
      env {
        name  = "CORS_ALLOWED_ORIGINS"
        value = var.cors_allowed_origins
      }

      dynamic "env" {
        for_each = local.secret_env
        content {
          name = env.key
          value_source {
            secret_key_ref {
              secret  = google_secret_manager_secret.app[env.value].secret_id
              version = "latest"
            }
          }
        }
      }

      startup_probe {
        http_get {
          path = "/actuator/health"
        }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 24
      }
    }
  }

  depends_on = [google_secret_manager_secret_iam_member.api_access]

  # The API fills a service-level scaling block that the provider then reports as drift.
  # CI owns the deployed image and gcloud stamps client metadata, so Terraform must not revert them.
  lifecycle {
    ignore_changes = [
      scaling,
      client,
      client_version,
      template[0].containers[0].image,
      template[0].labels,
      template[0].revision,
    ]
  }
}

# The API enforces its own JWT auth; Cloud Run IAM stays open so a browser or frontend can call it.
resource "google_cloud_run_v2_service_iam_member" "public" {
  count    = var.deploy_service ? 1 : 0
  name     = google_cloud_run_v2_service.api[0].name
  location = var.region
  role     = "roles/run.invoker"
  member   = "allUsers"
}

output "service_url" {
  value = var.deploy_service ? google_cloud_run_v2_service.api[0].uri : null
}
