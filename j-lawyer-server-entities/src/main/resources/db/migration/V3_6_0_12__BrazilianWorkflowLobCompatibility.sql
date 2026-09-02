-- ============================================================================
-- BR-LAWYER: Migration V3_6_0_12
-- Align Brazilian Workflow LOB columns with Hibernate/MariaDB mapping
-- ============================================================================

ALTER TABLE br_publications
    MODIFY COLUMN content LONGTEXT,
    MODIFY COLUMN raw_content LONGTEXT,
    MODIFY COLUMN notes LONGTEXT;

ALTER TABLE br_tasks
    MODIFY COLUMN description LONGTEXT,
    MODIFY COLUMN notes LONGTEXT;

ALTER TABLE br_task_comments
    MODIFY COLUMN comment_text LONGTEXT NOT NULL;

ALTER TABLE br_publication_events
    MODIFY COLUMN details LONGTEXT;

INSERT INTO server_settings(settingKey, settingValue)
    VALUES('jlawyer.server.database.version', '3.6.0.12')
    ON DUPLICATE KEY UPDATE settingValue = '3.6.0.12';

COMMIT;
