-- ============================================================================
-- BR-LAWYER: Migration V3_6_0_9 - Brazilian Legal Domain & Persistence Model
-- ============================================================================

-- 1. Brazilian Persons & Contacts Extensions (contacts table)
ALTER TABLE contacts
    ADD COLUMN `cpf` VARCHAR(14),
    ADD COLUMN `cnpj` VARCHAR(18),
    ADD COLUMN `rg` VARCHAR(20),
    ADD COLUMN `person_type` VARCHAR(10),
    ADD COLUMN `trade_name` VARCHAR(255),
    ADD COLUMN `fantasy_name` VARCHAR(255),
    ADD COLUMN `state_registration` VARCHAR(30),
    ADD COLUMN `municipal_registration` VARCHAR(30);

ALTER TABLE contacts ADD INDEX `idx_contacts_cpf` (`cpf`);
ALTER TABLE contacts ADD INDEX `idx_contacts_cnpj` (`cnpj`);
ALTER TABLE contacts ADD INDEX `idx_contacts_person_type` (`person_type`);

-- 2. Brazilian Legal Cases / Processos Extensions (cases table)
ALTER TABLE cases
    ADD COLUMN `cnj_number` VARCHAR(25),
    ADD COLUMN `cnj_number_clean` VARCHAR(20),
    ADD COLUMN `court_code` VARCHAR(20),
    ADD COLUMN `justice_segment` INT,
    ADD COLUMN `jurisdiction_degree` VARCHAR(10),
    ADD COLUMN `court_unit` VARCHAR(150),
    ADD COLUMN `comarca` VARCHAR(150),
    ADD COLUMN `judicial_subsection` VARCHAR(150),
    ADD COLUMN `tpu_class_code` INT,
    ADD COLUMN `tpu_class_name` VARCHAR(255),
    ADD COLUMN `tpu_subject_codes` TEXT,
    ADD COLUMN `tpu_subject_names` TEXT,
    ADD COLUMN `secrecy_level` BIT(1) DEFAULT 0,
    ADD COLUMN `distribution_date` DATETIME,
    ADD COLUMN `case_status_br` VARCHAR(30),
    ADD COLUMN `provenance_system` VARCHAR(50);

ALTER TABLE cases ADD INDEX `idx_cases_cnj_clean` (`cnj_number_clean`);
ALTER TABLE cases ADD INDEX `idx_cases_court_code` (`court_code`);
ALTER TABLE cases ADD INDEX `idx_cases_justice_segment` (`justice_segment`);

-- 3. Professional Lawyer Registrations (OAB Multiple Registrations)
CREATE TABLE br_lawyer_registrations (
    `id` VARCHAR(36) BINARY NOT NULL,
    `contact_id` VARCHAR(250) BINARY,
    `oab_number` VARCHAR(20) NOT NULL,
    `oab_uf` VARCHAR(2) NOT NULL,
    `oab_type` VARCHAR(20),
    `status` VARCHAR(20) DEFAULT 'ATIVO',
    `issuance_date` VARCHAR(10),
    `security_code` VARCHAR(50),
    `notice` TEXT,
    `creation_date` DATETIME,
    `modification_date` DATETIME,
    CONSTRAINT `pk_br_lawyer_registrations` PRIMARY KEY (`id`),
    FOREIGN KEY (`contact_id`) REFERENCES contacts(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE br_lawyer_registrations ADD INDEX `idx_br_lawyer_reg_contact` (`contact_id`);
ALTER TABLE br_lawyer_registrations ADD INDEX `idx_br_lawyer_reg_oab` (`oab_number`, `oab_uf`);

-- 4. Brazilian Judiciary Courts Canonical Catalog
CREATE TABLE br_judiciary_courts (
    `id` VARCHAR(36) BINARY NOT NULL,
    `code` VARCHAR(20) NOT NULL,
    `name` VARCHAR(150) NOT NULL,
    `justice_segment` INT NOT NULL,
    `segment_name` VARCHAR(50),
    `uf` VARCHAR(2),
    `court_number` INT DEFAULT 0,
    `datajud_code` VARCHAR(50),
    `djen_code` VARCHAR(50),
    `electronic_portal_url` VARCHAR(255),
    `active` BIT(1) DEFAULT 1,
    CONSTRAINT `pk_br_judiciary_courts` PRIMARY KEY (`id`),
    UNIQUE KEY `uk_br_judiciary_courts_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE br_judiciary_courts ADD INDEX `idx_br_courts_segment` (`justice_segment`);
ALTER TABLE br_judiciary_courts ADD INDEX `idx_br_courts_uf` (`uf`);

-- 5. Brazilian TPU Classes Catalog
CREATE TABLE br_tpu_classes (
    `id` VARCHAR(36) BINARY NOT NULL,
    `code` INT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `glossary` TEXT,
    `nature` VARCHAR(50),
    `active` BIT(1) DEFAULT 1,
    CONSTRAINT `pk_br_tpu_classes` PRIMARY KEY (`id`),
    UNIQUE KEY `uk_br_tpu_classes_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 6. Brazilian TPU Subjects Catalog
CREATE TABLE br_tpu_subjects (
    `id` VARCHAR(36) BINARY NOT NULL,
    `code` INT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `parent_code` INT,
    `glossary` TEXT,
    `active` BIT(1) DEFAULT 1,
    CONSTRAINT `pk_br_tpu_subjects` PRIMARY KEY (`id`),
    UNIQUE KEY `uk_br_tpu_subjects_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE br_tpu_subjects ADD INDEX `idx_br_tpu_subj_parent` (`parent_code`);

-- 7. Seed Canonical Brazilian Judiciary Courts (91 Tribunais)
-- Superiores & Conselhos
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `datajud_code`, `djen_code`, `active`) VALUES
('court-stf', 'STF', 'Supremo Tribunal Federal', 1, 'SUPERIOR', 'DF', 0, 'api_publica_stf', 'STF', 1),
('court-cnj', 'CNJ', 'Conselho Nacional de Justiça', 2, 'CONSELHO', 'DF', 0, 'api_publica_cnj', 'CNJ', 1),
('court-stj', 'STJ', 'Superior Tribunal de Justiça', 3, 'SUPERIOR', 'DF', 0, 'api_publica_stj', 'STJ', 1),
('court-tst', 'TST', 'Tribunal Superior do Trabalho', 5, 'TRABALHO_SUPERIOR', 'DF', 0, 'api_publica_tst', 'TST', 1),
('court-tse', 'TSE', 'Tribunal Superior Eleitoral', 6, 'ELEITORAL_SUPERIOR', 'DF', 0, 'api_publica_tse', 'TSE', 1),
('court-stm', 'STM', 'Superior Tribunal Militar', 7, 'MILITAR_SUPERIOR', 'DF', 0, 'api_publica_stm', 'STM', 1);

-- Tribunais Regionais Federais (TRF1 - TRF6)
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `datajud_code`, `djen_code`, `active`) VALUES
('court-trf1', 'TRF1', 'Tribunal Regional Federal da 1ª Região', 4, 'JUSTICA_FEDERAL', 'DF', 1, 'api_publica_trf1', 'TRF1', 1),
('court-trf2', 'TRF2', 'Tribunal Regional Federal da 2ª Região', 4, 'JUSTICA_FEDERAL', 'RJ', 2, 'api_publica_trf2', 'TRF2', 1),
('court-trf3', 'TRF3', 'Tribunal Regional Federal da 3ª Região', 4, 'JUSTICA_FEDERAL', 'SP', 3, 'api_publica_trf3', 'TRF3', 1),
('court-trf4', 'TRF4', 'Tribunal Regional Federal da 4ª Região', 4, 'JUSTICA_FEDERAL', 'RS', 4, 'api_publica_trf4', 'TRF4', 1),
('court-trf5', 'TRF5', 'Tribunal Regional Federal da 5ª Região', 4, 'JUSTICA_FEDERAL', 'PE', 5, 'api_publica_trf5', 'TRF5', 1),
('court-trf6', 'TRF6', 'Tribunal Regional Federal da 6ª Região', 4, 'JUSTICA_FEDERAL', 'MG', 6, 'api_publica_trf6', 'TRF6', 1);

-- Tribunais de Justiça Estaduais (27 TJs)
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `datajud_code`, `djen_code`, `active`) VALUES
('court-tjac', 'TJAC', 'Tribunal de Justiça do Acre', 8, 'JUSTICA_ESTADUAL', 'AC', 1, 'api_publica_tjac', 'TJAC', 1),
('court-tjal', 'TJAL', 'Tribunal de Justiça de Alagoas', 8, 'JUSTICA_ESTADUAL', 'AL', 2, 'api_publica_tjal', 'TJAL', 1),
('court-tjap', 'TJAP', 'Tribunal de Justiça do Amapá', 8, 'JUSTICA_ESTADUAL', 'AP', 3, 'api_publica_tjap', 'TJAP', 1),
('court-tjam', 'TJAM', 'Tribunal de Justiça do Amazonas', 8, 'JUSTICA_ESTADUAL', 'AM', 4, 'api_publica_tjam', 'TJAM', 1),
('court-tjba', 'TJBA', 'Tribunal de Justiça da Bahia', 8, 'JUSTICA_ESTADUAL', 'BA', 5, 'api_publica_tjba', 'TJBA', 1),
('court-tjce', 'TJCE', 'Tribunal de Justiça do Ceará', 8, 'JUSTICA_ESTADUAL', 'CE', 6, 'api_publica_tjce', 'TJCE', 1),
('court-tjdft', 'TJDFT', 'Tribunal de Justiça do Distrito Federal e Territórios', 8, 'JUSTICA_ESTADUAL', 'DF', 7, 'api_publica_tjdft', 'TJDFT', 1),
('court-tjes', 'TJES', 'Tribunal de Justiça do Espírito Santo', 8, 'JUSTICA_ESTADUAL', 'ES', 8, 'api_publica_tjes', 'TJES', 1),
('court-tjgo', 'TJGO', 'Tribunal de Justiça de Goiás', 8, 'JUSTICA_ESTADUAL', 'GO', 9, 'api_publica_tjgo', 'TJGO', 1),
('court-tjma', 'TJMA', 'Tribunal de Justiça do Maranhão', 8, 'JUSTICA_ESTADUAL', 'MA', 10, 'api_publica_tjma', 'TJMA', 1),
('court-tjmt', 'TJMT', 'Tribunal de Justiça de Mato Grosso', 8, 'JUSTICA_ESTADUAL', 'MT', 11, 'api_publica_tjmt', 'TJMT', 1),
('court-tjms', 'TJMS', 'Tribunal de Justiça de Mato Grosso do Sul', 8, 'JUSTICA_ESTADUAL', 'MS', 12, 'api_publica_tjms', 'TJMS', 1),
('court-tjmg', 'TJMG', 'Tribunal de Justiça de Minas Gerais', 8, 'JUSTICA_ESTADUAL', 'MG', 13, 'api_publica_tjmg', 'TJMG', 1),
('court-tjpa', 'TJPA', 'Tribunal de Justiça do Pará', 8, 'JUSTICA_ESTADUAL', 'PA', 14, 'api_publica_tjpa', 'TJPA', 1),
('court-tjpb', 'TJPB', 'Tribunal de Justiça da Paraíba', 8, 'JUSTICA_ESTADUAL', 'PB', 15, 'api_publica_tjpb', 'TJPB', 1),
('court-tjpr', 'TJPR', 'Tribunal de Justiça do Paraná', 8, 'JUSTICA_ESTADUAL', 'PR', 16, 'api_publica_tjpr', 'TJPR', 1),
('court-tjpe', 'TJPE', 'Tribunal de Justiça de Pernambuco', 8, 'JUSTICA_ESTADUAL', 'PE', 17, 'api_publica_tjpe', 'TJPE', 1),
('court-tjpi', 'TJPI', 'Tribunal de Justiça do Piauí', 8, 'JUSTICA_ESTADUAL', 'PI', 18, 'api_publica_tjpi', 'TJPI', 1),
('court-tjrj', 'TJRJ', 'Tribunal de Justiça do Rio de Janeiro', 8, 'JUSTICA_ESTADUAL', 'RJ', 19, 'api_publica_tjrj', 'TJRJ', 1),
('court-tjrn', 'TJRN', 'Tribunal de Justiça do Rio Grande do Norte', 8, 'JUSTICA_ESTADUAL', 'RN', 20, 'api_publica_tjrn', 'TJRN', 1),
('court-tjrs', 'TJRS', 'Tribunal de Justiça do Rio Grande do Sul', 8, 'JUSTICA_ESTADUAL', 'RS', 21, 'api_publica_tjrs', 'TJRS', 1),
('court-tjro', 'TJRO', 'Tribunal de Justiça de Rondônia', 8, 'JUSTICA_ESTADUAL', 'RO', 22, 'api_publica_tjro', 'TJRO', 1),
('court-tjrr', 'TJRR', 'Tribunal de Justiça de Roraima', 8, 'JUSTICA_ESTADUAL', 'RR', 23, 'api_publica_tjrr', 'TJRR', 1),
('court-tjsc', 'TJSC', 'Tribunal de Justiça de Santa Catarina', 8, 'JUSTICA_ESTADUAL', 'SC', 24, 'api_publica_tjsc', 'TJSC', 1),
('court-tjsp', 'TJSP', 'Tribunal de Justiça de São Paulo', 8, 'JUSTICA_ESTADUAL', 'SP', 26, 'api_publica_tjsp', 'TJSP', 1),
('court-tjse', 'TJSE', 'Tribunal de Justiça de Sergipe', 8, 'JUSTICA_ESTADUAL', 'SE', 25, 'api_publica_tjse', 'TJSE', 1),
('court-tjto', 'TJTO', 'Tribunal de Justiça do Tocantins', 8, 'JUSTICA_ESTADUAL', 'TO', 27, 'api_publica_tjto', 'TJTO', 1);

-- Tribunais Regionais do Trabalho (24 TRTs)
INSERT INTO br_judiciary_courts (`id`, `code`, `name`, `justice_segment`, `segment_name`, `uf`, `court_number`, `datajud_code`, `djen_code`, `active`) VALUES
('court-trt1', 'TRT1', 'Tribunal Regional do Trabalho da 1ª Região (RJ)', 5, 'JUSTICA_DO_TRABALHO', 'RJ', 1, 'api_publica_trt1', 'TRT1', 1),
('court-trt2', 'TRT2', 'Tribunal Regional do Trabalho da 2ª Região (SP)', 5, 'JUSTICA_DO_TRABALHO', 'SP', 2, 'api_publica_trt2', 'TRT2', 1),
('court-trt3', 'TRT3', 'Tribunal Regional do Trabalho da 3ª Região (MG)', 5, 'JUSTICA_DO_TRABALHO', 'MG', 3, 'api_publica_trt3', 'TRT3', 1),
('court-trt4', 'TRT4', 'Tribunal Regional do Trabalho da 4ª Região (RS)', 5, 'JUSTICA_DO_TRABALHO', 'RS', 4, 'api_publica_trt4', 'TRT4', 1),
('court-trt5', 'TRT5', 'Tribunal Regional do Trabalho da 5ª Região (BA)', 5, 'JUSTICA_DO_TRABALHO', 'BA', 5, 'api_publica_trt5', 'TRT5', 1),
('court-trt15', 'TRT15', 'Tribunal Regional do Trabalho da 15ª Região (Campinas)', 5, 'JUSTICA_DO_TRABALHO', 'SP', 15, 'api_publica_trt15', 'TRT15', 1);

-- 8. Seed Initial TPU Classes (Principais)
INSERT INTO br_tpu_classes (`id`, `code`, `name`, `nature`, `active`) VALUES
('tpu-c-7', 7, 'Procedimento Comum Cível', 'CIVEL', 1),
('tpu-c-1116', 1116, 'Execução de Título Extrajudicial', 'CIVEL', 1),
('tpu-c-156', 156, 'Cumprimento de Sentença', 'CIVEL', 1),
('tpu-c-120', 120, 'Mandado de Segurança Cível', 'CIVEL', 1),
('tpu-c-65', 65, 'Ação Trabalhista - Rito Ordinário', 'TRABALHISTA', 1),
('tpu-c-1125', 1125, 'Ação Trabalhista - Rito Sumaríssimo', 'TRABALHISTA', 1),
('tpu-c-283', 283, 'Ação Penal - Procedimento Ordinário', 'CRIMINAL', 1),
('tpu-c-436', 436, 'Inventário', 'FAMILIA_SUCESSOES', 1),
('tpu-c-1298', 1298, 'Divórcio Consensual', 'FAMILIA_SUCESSOES', 1),
('tpu-c-1118', 1118, 'Embargos à Execução', 'CIVEL', 1);

-- 9. Seed Initial TPU Subjects (Principais)
INSERT INTO br_tpu_subjects (`id`, `code`, `name`, `parent_code`, `active`) VALUES
('tpu-s-10433', 10433, 'Indenização por Dano Moral', 9518, 1),
('tpu-s-10434', 10434, 'Indenização por Dano Material', 9518, 1),
('tpu-s-9518', 9518, 'Responsabilidade Civil', 899, 1),
('tpu-s-7780', 7780, 'Inadimplemento', 7681, 1),
('tpu-s-7681', 7681, 'Obrigações', 899, 1),
('tpu-s-2546', 2546, 'Rescisão do Contrato de Trabalho', 864, 1),
('tpu-s-1855', 1855, 'Horas Extras', 864, 1),
('tpu-s-6031', 6031, 'Alimentos', 5946, 1),
('tpu-s-7771', 7771, 'Contratos Bancários', 7681, 1),
('tpu-s-10582', 10582, 'Benefício Previdenciário por Incapacidade', 10580, 1);

-- 10. Update Server Database Version Tracker
INSERT INTO server_settings(settingKey, settingValue) 
    VALUES('jlawyer.server.database.version', '3.6.0.9') 
    ON DUPLICATE KEY UPDATE settingValue = '3.6.0.9';

COMMIT;
