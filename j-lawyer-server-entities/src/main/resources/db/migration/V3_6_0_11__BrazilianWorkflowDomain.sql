-- ============================================================================
-- BR-LAWYER: Migration V3_6_0_11 - Brazilian Legal Workflow Domain
-- Publicações, Tarefas Jurídicas, Checklist, Comentários e Auditoria
-- ============================================================================

-- 1. Publicações e Intimações Judiciais Brasileiras
CREATE TABLE br_publications (
    `id` VARCHAR(36) BINARY NOT NULL,
    `external_id` VARCHAR(100),
    `source` VARCHAR(100) DEFAULT 'MANUAL',
    `source_type` VARCHAR(50) DEFAULT 'DIARIO_OFICIAL',
    `court_code` VARCHAR(20),
    `process_id` VARCHAR(250) BINARY,
    `cnj_number` VARCHAR(25),
    `cnj_number_clean` VARCHAR(20),
    `publication_date` DATETIME,
    `availability_date` DATETIME,
    `content` MEDIUMTEXT,
    `raw_content` MEDIUMTEXT,
    `publication_type` VARCHAR(50) DEFAULT 'INTIMACAO',
    `recipient` VARCHAR(255),
    `lawyer_name` VARCHAR(255),
    `lawyer_oab` VARCHAR(30),
    `status` VARCHAR(30) NOT NULL DEFAULT 'NOVA',
    `read_status` VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    `treatment_status` VARCHAR(30) NOT NULL DEFAULT 'NAO_TRATADA',
    `assigned_user` VARCHAR(100),
    `link_provenance` VARCHAR(50) DEFAULT 'MANUAL',
    `link_confidence` DOUBLE DEFAULT 0.0,
    `suggested_due_date` DATETIME,
    `suggested_deadline_days` INT DEFAULT 0,
    `suggestion_source` VARCHAR(100),
    `suggestion_confidence` DOUBLE DEFAULT 0.0,
    `fingerprint` VARCHAR(64),
    `provenance` VARCHAR(100) DEFAULT 'MANUAL',
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `read_at` DATETIME,
    `treated_at` DATETIME,
    `treated_by` VARCHAR(100),
    `archived_at` DATETIME,
    `archived_by` VARCHAR(100),
    `notes` TEXT,
    CONSTRAINT `pk_br_publications` PRIMARY KEY (`id`),
    FOREIGN KEY (`process_id`) REFERENCES cases(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE br_publications ADD INDEX `idx_br_pub_status` (`status`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_read_status` (`read_status`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_treatment_status` (`treatment_status`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_process` (`process_id`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_cnj_clean` (`cnj_number_clean`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_court` (`court_code`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_assigned` (`assigned_user`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_pub_date` (`publication_date`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_fingerprint` (`fingerprint`);
ALTER TABLE br_publications ADD INDEX `idx_br_pub_external_src` (`external_id`, `source`);

-- 2. Tarefas Jurídicas
CREATE TABLE br_tasks (
    `id` VARCHAR(36) BINARY NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `description` MEDIUMTEXT,
    `process_id` VARCHAR(250) BINARY,
    `publication_id` VARCHAR(36) BINARY,
    `calendar_event_id` VARCHAR(36) BINARY,
    `assigned_user` VARCHAR(100),
    `created_by` VARCHAR(100),
    `status` VARCHAR(30) NOT NULL DEFAULT 'TODO',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    `due_date` DATETIME,
    `due_time` VARCHAR(10),
    `completed_at` DATETIME,
    `completed_by` VARCHAR(100),
    `estimated_minutes` INT DEFAULT 0,
    `actual_minutes` INT DEFAULT 0,
    `category` VARCHAR(50) DEFAULT 'ANALISE',
    `notes` TEXT,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    CONSTRAINT `pk_br_tasks` PRIMARY KEY (`id`),
    FOREIGN KEY (`process_id`) REFERENCES cases(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`publication_id`) REFERENCES br_publications(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`calendar_event_id`) REFERENCES case_events(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_status` (`status`);
ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_assigned` (`assigned_user`);
ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_process` (`process_id`);
ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_publication` (`publication_id`);
ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_priority` (`priority`);
ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_due_date` (`due_date`);
ALTER TABLE br_tasks ADD INDEX `idx_br_tasks_category` (`category`);

-- 3. Comentários da Tarefa
CREATE TABLE br_task_comments (
    `id` VARCHAR(36) BINARY NOT NULL,
    `task_id` VARCHAR(36) BINARY NOT NULL,
    `user_name` VARCHAR(100) NOT NULL,
    `comment_text` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL,
    CONSTRAINT `pk_br_task_comments` PRIMARY KEY (`id`),
    FOREIGN KEY (`task_id`) REFERENCES br_tasks(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE br_task_comments ADD INDEX `idx_br_task_comm_task` (`task_id`);

-- 4. Itens de Checklist da Tarefa
CREATE TABLE br_task_checklist_items (
    `id` VARCHAR(36) BINARY NOT NULL,
    `task_id` VARCHAR(36) BINARY NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `done` BIT(1) NOT NULL DEFAULT 0,
    `item_order` INT NOT NULL DEFAULT 0,
    `completed_at` DATETIME,
    `completed_by` VARCHAR(100),
    CONSTRAINT `pk_br_task_checklist` PRIMARY KEY (`id`),
    FOREIGN KEY (`task_id`) REFERENCES br_tasks(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE br_task_checklist_items ADD INDEX `idx_br_task_check_task` (`task_id`);

-- 5. Registro de Auditoria e Histórico de Eventos do Workflow
CREATE TABLE br_publication_events (
    `id` VARCHAR(36) BINARY NOT NULL,
    `publication_id` VARCHAR(36) BINARY,
    `task_id` VARCHAR(36) BINARY,
    `process_id` VARCHAR(250) BINARY,
    `event_type` VARCHAR(50) NOT NULL,
    `actor` VARCHAR(100) NOT NULL,
    `details` TEXT,
    `created_at` DATETIME NOT NULL,
    CONSTRAINT `pk_br_pub_events` PRIMARY KEY (`id`),
    FOREIGN KEY (`publication_id`) REFERENCES br_publications(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE br_publication_events ADD INDEX `idx_br_pubevent_pub` (`publication_id`);
ALTER TABLE br_publication_events ADD INDEX `idx_br_pubevent_task` (`task_id`);
ALTER TABLE br_publication_events ADD INDEX `idx_br_pubevent_process` (`process_id`);
ALTER TABLE br_publication_events ADD INDEX `idx_br_pubevent_date` (`created_at`);

-- 6. Atualização do Version Tracker do Banco de Dados para 3.6.0.11
INSERT INTO server_settings(settingKey, settingValue) 
    VALUES('jlawyer.server.database.version', '3.6.0.11') 
    ON DUPLICATE KEY UPDATE settingValue = '3.6.0.11';

COMMIT;
