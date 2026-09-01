-- ============================================================================
-- BR-LAWYER: Migration V3_6_0_10 - Brazilian Legal Domain & TPU Integrity Patch
-- ============================================================================

-- 1. Extensão de Metadados em br_judiciary_courts (Tipo de Órgão)
ALTER TABLE br_judiciary_courts
    ADD COLUMN `court_type` VARCHAR(50);

-- 2. Atualização dos Tipos dos Órgãos Existentes na V3_6_0_9
UPDATE br_judiciary_courts SET `court_type` = 'TRIBUNAL_SUPERIOR' WHERE `code` IN ('STF', 'STJ', 'TST', 'TSE', 'STM');
UPDATE br_judiciary_courts SET `court_type` = 'CONSELHO' WHERE `code` = 'CNJ';
UPDATE br_judiciary_courts SET `court_type` = 'TRIBUNAL_REGIONAL_FEDERAL' WHERE `code` LIKE 'TRF%';
UPDATE br_judiciary_courts SET `court_type` = 'TRIBUNAL_DE_JUSTICA' WHERE `code` LIKE 'TJ%' AND `code` != 'TJM%';
UPDATE br_judiciary_courts SET `court_type` = 'TRIBUNAL_REGIONAL_DO_TRABALHO' WHERE `code` LIKE 'TRT%';

-- 3. Versionamento e Proveniência em Tabelas TPU
ALTER TABLE br_tpu_classes
    ADD COLUMN `source` VARCHAR(50) DEFAULT 'CNJ_TPU',
    ADD COLUMN `source_version` VARCHAR(50) DEFAULT '2026.1',
    ADD COLUMN `imported_at` DATETIME,
    ADD COLUMN `valid_from` DATETIME,
    ADD COLUMN `valid_to` DATETIME,
    ADD COLUMN `last_updated_at` DATETIME,
    ADD COLUMN `checksum` VARCHAR(64);

ALTER TABLE br_tpu_subjects
    ADD COLUMN `source` VARCHAR(50) DEFAULT 'CNJ_TPU',
    ADD COLUMN `source_version` VARCHAR(50) DEFAULT '2026.1',
    ADD COLUMN `imported_at` DATETIME,
    ADD COLUMN `valid_from` DATETIME,
    ADD COLUMN `valid_to` DATETIME,
    ADD COLUMN `last_updated_at` DATETIME,
    ADD COLUMN `checksum` VARCHAR(64);

-- 4. Tabela de Relacionamento Normalizado: Processo ↔ Assuntos TPU (Múltiplos Assuntos)
CREATE TABLE br_case_tpu_subjects (
    `id` VARCHAR(36) BINARY NOT NULL,
    `case_id` VARCHAR(250) BINARY NOT NULL,
    `subject_code` INT NOT NULL,
    `subject_id` VARCHAR(36) BINARY,
    `subject_name` VARCHAR(255),
    `primary_subject` BIT(1) DEFAULT 0,
    `provenance` VARCHAR(50) DEFAULT 'MANUAL',
    `created_at` DATETIME,
    CONSTRAINT `pk_br_case_tpu_subjects` PRIMARY KEY (`id`),
    FOREIGN KEY (`case_id`) REFERENCES cases(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE br_case_tpu_subjects ADD INDEX `idx_br_case_tpu_case` (`case_id`);
ALTER TABLE br_case_tpu_subjects ADD INDEX `idx_br_case_tpu_code` (`subject_code`);

-- 5. Inserção Idempotente dos 18 Tribunais Regionais do Trabalho (TRTs) Ausentes
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `court_type`, `active`) VALUES
('court-trt6', 'TRT6', 'Tribunal Regional do Trabalho da 6ª Região', 5, 'JUSTICA_DO_TRABALHO', 'PE', 6, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt7', 'TRT7', 'Tribunal Regional do Trabalho da 7ª Região', 5, 'JUSTICA_DO_TRABALHO', 'CE', 7, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt8', 'TRT8', 'Tribunal Regional do Trabalho da 8ª Região', 5, 'JUSTICA_DO_TRABALHO', 'PA', 8, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt9', 'TRT9', 'Tribunal Regional do Trabalho da 9ª Região', 5, 'JUSTICA_DO_TRABALHO', 'PR', 9, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt10', 'TRT10', 'Tribunal Regional do Trabalho da 10ª Região', 5, 'JUSTICA_DO_TRABALHO', 'DF', 10, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt11', 'TRT11', 'Tribunal Regional do Trabalho da 11ª Região', 5, 'JUSTICA_DO_TRABALHO', 'AM', 11, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt12', 'TRT12', 'Tribunal Regional do Trabalho da 12ª Região', 5, 'JUSTICA_DO_TRABALHO', 'SC', 12, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt13', 'TRT13', 'Tribunal Regional do Trabalho da 13ª Região', 5, 'JUSTICA_DO_TRABALHO', 'PB', 13, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt14', 'TRT14', 'Tribunal Regional do Trabalho da 14ª Região', 5, 'JUSTICA_DO_TRABALHO', 'RO', 14, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt16', 'TRT16', 'Tribunal Regional do Trabalho da 16ª Região', 5, 'JUSTICA_DO_TRABALHO', 'MA', 16, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt17', 'TRT17', 'Tribunal Regional do Trabalho da 17ª Região', 5, 'JUSTICA_DO_TRABALHO', 'ES', 17, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt18', 'TRT18', 'Tribunal Regional do Trabalho da 18ª Região', 5, 'JUSTICA_DO_TRABALHO', 'GO', 18, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt19', 'TRT19', 'Tribunal Regional do Trabalho da 19ª Região', 5, 'JUSTICA_DO_TRABALHO', 'AL', 19, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt20', 'TRT20', 'Tribunal Regional do Trabalho da 20ª Região', 5, 'JUSTICA_DO_TRABALHO', 'SE', 20, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt21', 'TRT21', 'Tribunal Regional do Trabalho da 21ª Região', 5, 'JUSTICA_DO_TRABALHO', 'RN', 21, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt22', 'TRT22', 'Tribunal Regional do Trabalho da 22ª Região', 5, 'JUSTICA_DO_TRABALHO', 'PI', 22, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt23', 'TRT23', 'Tribunal Regional do Trabalho da 23ª Região', 5, 'JUSTICA_DO_TRABALHO', 'MT', 23, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1),
('court-trt24', 'TRT24', 'Tribunal Regional do Trabalho da 24ª Região', 5, 'JUSTICA_DO_TRABALHO', 'MS', 24, 'TRIBUNAL_REGIONAL_DO_TRABALHO', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `court_type` = VALUES(`court_type`);

-- 6. Inserção dos 27 Tribunais Regionais Eleitorais (TREs)
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `court_type`, `active`) VALUES
('court-tre-ac', 'TRE-AC', 'Tribunal Regional Eleitoral do Acre', 6, 'JUSTICA_ELEITORAL', 'AC', 1, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-al', 'TRE-AL', 'Tribunal Regional Eleitoral de Alagoas', 6, 'JUSTICA_ELEITORAL', 'AL', 2, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-ap', 'TRE-AP', 'Tribunal Regional Eleitoral do Amapá', 6, 'JUSTICA_ELEITORAL', 'AP', 3, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-am', 'TRE-AM', 'Tribunal Regional Eleitoral do Amazonas', 6, 'JUSTICA_ELEITORAL', 'AM', 4, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-ba', 'TRE-BA', 'Tribunal Regional Eleitoral da Bahia', 6, 'JUSTICA_ELEITORAL', 'BA', 5, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-ce', 'TRE-CE', 'Tribunal Regional Eleitoral do Ceará', 6, 'JUSTICA_ELEITORAL', 'CE', 6, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-df', 'TRE-DF', 'Tribunal Regional Eleitoral do Distrito Federal', 6, 'JUSTICA_ELEITORAL', 'DF', 7, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-es', 'TRE-ES', 'Tribunal Regional Eleitoral do Espírito Santo', 6, 'JUSTICA_ELEITORAL', 'ES', 8, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-go', 'TRE-GO', 'Tribunal Regional Eleitoral de Goiás', 6, 'JUSTICA_ELEITORAL', 'GO', 9, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-ma', 'TRE-MA', 'Tribunal Regional Eleitoral do Maranhão', 6, 'JUSTICA_ELEITORAL', 'MA', 10, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-mt', 'TRE-MT', 'Tribunal Regional Eleitoral de Mato Grosso', 6, 'JUSTICA_ELEITORAL', 'MT', 11, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-ms', 'TRE-MS', 'Tribunal Regional Eleitoral de Mato Grosso do Sul', 6, 'JUSTICA_ELEITORAL', 'MS', 12, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-mg', 'TRE-MG', 'Tribunal Regional Eleitoral de Minas Gerais', 6, 'JUSTICA_ELEITORAL', 'MG', 13, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-pa', 'TRE-PA', 'Tribunal Regional Eleitoral do Pará', 6, 'JUSTICA_ELEITORAL', 'PA', 14, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-pb', 'TRE-PB', 'Tribunal Regional Eleitoral da Paraíba', 6, 'JUSTICA_ELEITORAL', 'PB', 15, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-pr', 'TRE-PR', 'Tribunal Regional Eleitoral do Paraná', 6, 'JUSTICA_ELEITORAL', 'PR', 16, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-pe', 'TRE-PE', 'Tribunal Regional Eleitoral de Pernambuco', 6, 'JUSTICA_ELEITORAL', 'PE', 17, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-pi', 'TRE-PI', 'Tribunal Regional Eleitoral do Piauí', 6, 'JUSTICA_ELEITORAL', 'PI', 18, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-rj', 'TRE-RJ', 'Tribunal Regional Eleitoral do Rio de Janeiro', 6, 'JUSTICA_ELEITORAL', 'RJ', 19, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-rn', 'TRE-RN', 'Tribunal Regional Eleitoral do Rio Grande do Norte', 6, 'JUSTICA_ELEITORAL', 'RN', 20, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-rs', 'TRE-RS', 'Tribunal Regional Eleitoral do Rio Grande do Sul', 6, 'JUSTICA_ELEITORAL', 'RS', 21, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-ro', 'TRE-RO', 'Tribunal Regional Eleitoral de Rondônia', 6, 'JUSTICA_ELEITORAL', 'RO', 22, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-rr', 'TRE-RR', 'Tribunal Regional Eleitoral de Roraima', 6, 'JUSTICA_ELEITORAL', 'RR', 23, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-sc', 'TRE-SC', 'Tribunal Regional Eleitoral de Santa Catarina', 6, 'JUSTICA_ELEITORAL', 'SC', 24, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-se', 'TRE-SE', 'Tribunal Regional Eleitoral de Sergipe', 6, 'JUSTICA_ELEITORAL', 'SE', 25, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-sp', 'TRE-SP', 'Tribunal Regional Eleitoral de São Paulo', 6, 'JUSTICA_ELEITORAL', 'SP', 26, 'TRIBUNAL_REGIONAL_ELEITORAL', 1),
('court-tre-to', 'TRE-TO', 'Tribunal Regional Eleitoral do Tocantins', 6, 'JUSTICA_ELEITORAL', 'TO', 27, 'TRIBUNAL_REGIONAL_ELEITORAL', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `court_type` = VALUES(`court_type`);

-- 7. Inserção dos 3 Tribunais de Justiça Militar Estadual (TJMs)
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `court_type`, `active`) VALUES
('court-tjmsp', 'TJMSP', 'Tribunal de Justiça Militar de São Paulo', 9, 'JUSTICA_MILITAR_ESTADUAL', 'SP', 26, 'TRIBUNAL_DE_JUSTICA_MILITAR', 1),
('court-tjmmg', 'TJMMG', 'Tribunal de Justiça Militar de Minas Gerais', 9, 'JUSTICA_MILITAR_ESTADUAL', 'MG', 13, 'TRIBUNAL_DE_JUSTICA_MILITAR', 1),
('court-tjmrs', 'TJMRS', 'Tribunal de Justiça Militar do Rio Grande do Sul', 9, 'JUSTICA_MILITAR_ESTADUAL', 'RS', 21, 'TRIBUNAL_DE_JUSTICA_MILITAR', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `court_type` = VALUES(`court_type`);

-- 8. Inserção dos Conselhos da Justiça (CJF e CSJT)
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `court_type`, `active`) VALUES
('court-cjf', 'CJF', 'Conselho da Justiça Federal', 4, 'JUSTICA_FEDERAL', 'DF', 0, 'CONSELHO', 1),
('court-csjt', 'CSJT', 'Conselho Superior da Justiça do Trabalho', 5, 'JUSTICA_DO_TRABALHO', 'DF', 0, 'CONSELHO', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `court_type` = VALUES(`court_type`);

-- 9. Atualização do Version Tracker do Servidor para 3.6.0.10
INSERT INTO server_settings(settingKey, settingValue) 
    VALUES('jlawyer.server.database.version', '3.6.0.10') 
    ON DUPLICATE KEY UPDATE settingValue = '3.6.0.10';

COMMIT;
