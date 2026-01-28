-- Create database
CREATE DATABASE IF NOT EXISTS sludge_nir
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE sludge_nir;

-- Sampling site
CREATE TABLE IF NOT EXISTS sampling_site (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  site_code VARCHAR(64) NOT NULL UNIQUE,
  site_name VARCHAR(128) NULL,
  lat DECIMAL(10,7) NULL,
  lng DECIMAL(10,7) NULL,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Spectrum sample
CREATE TABLE IF NOT EXISTS spectrum_sample (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  site_id BIGINT NULL,
  bands JSON NOT NULL,
  band_count INT NOT NULL,
  source VARCHAR(32) NOT NULL,
  captured_at DATETIME NULL,
  qc_status VARCHAR(16) NOT NULL,
  qc_message VARCHAR(512) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_spectrum_site FOREIGN KEY (site_id) REFERENCES sampling_site(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Chem label
CREATE TABLE IF NOT EXISTS chem_label (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sample_id BIGINT NOT NULL UNIQUE,
  organic DOUBLE NULL,
  tn DOUBLE NULL,
  tp DOUBLE NULL,
  unit VARCHAR(32) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_label_sample FOREIGN KEY (sample_id) REFERENCES spectrum_sample(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Prediction history
CREATE TABLE IF NOT EXISTS prediction_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_id BIGINT NULL,
  task_type VARCHAR(16) NOT NULL,
  model_type VARCHAR(32) NOT NULL,
  input_sample_id BIGINT NULL,
  input_bands JSON NULL,
  pred_site_id BIGINT NULL,
  pred_site_code VARCHAR(64) NULL,
  pred_organic DOUBLE NULL,
  pred_tn DOUBLE NULL,
  pred_tp DOUBLE NULL,
  metrics JSON NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pred_input_sample FOREIGN KEY (input_sample_id) REFERENCES spectrum_sample(id),
  CONSTRAINT fk_pred_site FOREIGN KEY (pred_site_id) REFERENCES sampling_site(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indexes
CREATE INDEX idx_spectrum_site ON spectrum_sample(site_id);
CREATE INDEX idx_spectrum_qc ON spectrum_sample(qc_status);
CREATE INDEX idx_pred_created ON prediction_history(created_at);
CREATE INDEX idx_pred_task ON prediction_history(task_type);

-- Optional seed data
INSERT INTO sampling_site (site_code, site_name, lat, lng, remark)
VALUES ('S001', 'Site-01', 31.2304000, 121.4737000, 'seed')
ON DUPLICATE KEY UPDATE site_name = VALUES(site_name);
