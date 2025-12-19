-- Flyway Migration: Initial schema for KYC Onboarding (MySQL)
-- NOTE: Use InnoDB + utf8mb4 for safe text + indexes.

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(120) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role VARCHAR(80) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS onboarding_case (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_by_user_id BIGINT NOT NULL,
  status VARCHAR(40) NOT NULL,
  due_at TIMESTAMP NULL,
  sla_state VARCHAR(20) NOT NULL DEFAULT 'ON_TRACK',
  sla_breached_at TIMESTAMP NULL,
  -- AccountDetails (embedded)
  account_first_name VARCHAR(120) NULL,
  account_last_name VARCHAR(120) NULL,
  account_phone VARCHAR(40) NULL,
  account_type VARCHAR(60) NULL,
  account_address_line1 VARCHAR(180) NULL,
  account_address_line2 VARCHAR(180) NULL,
  account_city VARCHAR(120) NULL,
  account_state VARCHAR(80) NULL,
  account_zip VARCHAR(20) NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_case_created_by (created_by_user_id),
  KEY idx_case_status (status),
  KEY idx_case_due_at (due_at),
  CONSTRAINT fk_case_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS document_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  onboarding_case_id BIGINT NOT NULL,
  doc_type VARCHAR(60) NOT NULL,
  label VARCHAR(180) NULL,
  original_filename VARCHAR(255) NOT NULL,
  storage_path VARCHAR(1024) NOT NULL,
  content_type VARCHAR(200) NULL,
  size_bytes BIGINT NOT NULL,
  uploaded_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  uploaded_by_user_id BIGINT NULL,
  PRIMARY KEY (id),
  KEY idx_doc_case (onboarding_case_id),
  KEY idx_doc_type (doc_type),
  CONSTRAINT fk_doc_case FOREIGN KEY (onboarding_case_id) REFERENCES onboarding_case(id) ON DELETE CASCADE,
  CONSTRAINT fk_doc_uploaded_by FOREIGN KEY (uploaded_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS audit_event (
  id BIGINT NOT NULL AUTO_INCREMENT,
  onboarding_case_id BIGINT NOT NULL,
  action VARCHAR(60) NOT NULL,
  actor VARCHAR(180) NULL,
  actor_user_id BIGINT NULL,
  from_status VARCHAR(40) NULL,
  to_status VARCHAR(40) NULL,
  message VARCHAR(600) NULL,
  metadata_json TEXT NULL,
  event_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_audit_case (onboarding_case_id),
  KEY idx_audit_event_at (event_at),
  CONSTRAINT fk_audit_case FOREIGN KEY (onboarding_case_id) REFERENCES onboarding_case(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
