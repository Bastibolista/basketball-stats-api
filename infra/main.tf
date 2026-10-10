terraform {
  required_version = ">= 1.6"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.0"
    }
  }

  backend "gcs" {
    bucket = "personal-511214-tfstate"
    prefix = "stack"
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

locals {
  image = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.ghcr_remote.repository_id}/bastibolista/basketball-stats-api:${var.image_tag}"

  secret_ids = ["db-password", "auth-password-hash", "jwt-secret"]
}
