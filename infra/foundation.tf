# Cloud Run cannot pull from GHCR directly; this repo proxies and caches the public image.
resource "google_artifact_registry_repository" "ghcr_remote" {
  repository_id = "ghcr-remote"
  location      = var.region
  format        = "DOCKER"
  mode          = "REMOTE_REPOSITORY"
  description   = "Pull-through cache of ghcr.io"

  remote_repository_config {
    docker_repository {
      custom_repository {
        uri = "https://ghcr.io"
      }
    }
  }
}

resource "google_service_account" "api" {
  account_id   = "basketball-api-run"
  display_name = "Runtime identity of basketball-stats-api"
}

# Secret values are added out of band (gcloud) so they never reach code or Terraform state.
resource "google_secret_manager_secret" "app" {
  for_each  = toset(local.secret_ids)
  secret_id = each.value

  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_iam_member" "api_access" {
  for_each  = google_secret_manager_secret.app
  secret_id = each.value.id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.api.email}"
}
