-- ============================================================================
-- BR-LAWYER: Migration V3_6_0_12 - Brazilian Seed Data Localization
-- Tradução e Localização de Dados de Domínio, Opções e Tipos de Envolvidos
-- ============================================================================

-- 1. Tipos de Partes / Envolvidos
UPDATE party_types SET name = 'Cliente', placeholder = 'CLIENTE' WHERE name = 'Mandant' OR id = '10';
UPDATE party_types SET name = 'Parte Contrária', placeholder = 'PARTE_CONTRARIA' WHERE name = 'Gegner' OR id = '20';
UPDATE party_types SET name = 'Terceiro Interessado', placeholder = 'TERCEIRO' WHERE name = 'Dritte' OR id = '30';

-- 2. Ramos do Direito / Áreas de Atuação
UPDATE AppOptionGroupBean SET value = 'Direito Civil' WHERE value = 'Zivilrecht';
UPDATE AppOptionGroupBean SET value = 'Direito de Família' WHERE value = 'Familienrecht';
UPDATE AppOptionGroupBean SET value = 'Direito do Trabalho' WHERE value = 'Arbeitsrecht';
UPDATE AppOptionGroupBean SET value = 'Direito Tributário' WHERE value = 'Steuerrecht';
UPDATE AppOptionGroupBean SET value = 'Direito Penal' WHERE value = 'Strafrecht';
UPDATE AppOptionGroupBean SET value = 'Direito do Consumidor' WHERE value = 'Verbraucherrecht';
UPDATE AppOptionGroupBean SET value = 'Direito de Trânsito' WHERE value = 'Verkehrsrecht';

-- 3. Fechos de Correspondência (Complimentary Close)
UPDATE server_options SET value = 'Atenciosamente,' WHERE value = 'Mit freundlichen Grüßen';
UPDATE server_options SET value = 'Cordialmente,' WHERE value = 'Mit freundlichen kollegialen Grüßen';
UPDATE server_options SET value = 'Respeitosamente,' WHERE value = 'Mit vorzüglicher Hochachtung';
UPDATE server_options SET value = 'Saudações cordiais,' WHERE value = 'Liebe Grüße' OR value = 'Lieber Gruß';

-- 4. Formas de Tratamento / Saudações (Salutations)
UPDATE server_options SET value = 'Prezado(a) Senhor(a),' WHERE value = 'Sehr geehrte Damen und Herren';
UPDATE server_options SET value = 'Prezada Senhora,' WHERE value = 'Sehr geehrte Frau';
UPDATE server_options SET value = 'Prezado Senhor,' WHERE value = 'Sehr geehrter Herr';
UPDATE server_options SET value = 'Prezados Colegas,' WHERE value = 'Sehr geehrte Kollegen';
UPDATE server_options SET value = 'Prezada Colega,' WHERE value = 'Sehr geehrte Frau Kollegin';
UPDATE server_options SET value = 'Prezado Colega,' WHERE value = 'Sehr geehrter Herr Kollege';
UPDATE server_options SET value = 'Caro(a),' WHERE value = 'Liebe' OR value = 'Lieber';

-- 5. Atualização do Version Tracker
INSERT INTO server_settings(settingKey, settingValue) 
    VALUES('jlawyer.server.database.version', '3.6.0.12') 
    ON DUPLICATE KEY UPDATE settingValue = '3.6.0.12';

COMMIT;
