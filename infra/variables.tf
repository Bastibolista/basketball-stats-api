variable "project_id" {
  type    = string
  default = "personal-511214"
}

variable "region" {
  type    = string
  default = "us-central1"
}

variable "image_tag" {
  type    = string
  default = "0.3.0"
}

# Phase 2 switch: Cloud Run needs every secret to already have a version.
variable "deploy_service" {
  type    = bool
  default = false
}

# The values below are not secrets but identify the external DB; set them in a gitignored terraform.tfvars.
variable "database_url" {
  description = "JDBC URL of the external Postgres, e.g. jdbc:postgresql://HOST/DB?sslmode=require"
  type        = string
  default     = ""
}

variable "db_user" {
  type    = string
  default = ""
}

variable "google_client_id" {
  description = "OAuth client ID that Google ID tokens must be issued for"
  type        = string
  default     = ""
}

variable "allowed_emails" {
  description = "Comma-separated Google accounts allowed to use the API"
  type        = string
  default     = ""
}

variable "cors_allowed_origins" {
  type    = string
  default = "http://localhost:5173"
}
