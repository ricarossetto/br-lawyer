USE jlawyerdb;

-- 1. Publicações
INSERT INTO br_publications (
    id, external_id, source, source_type, court_code, process_id, cnj_number, cnj_number_clean,
    publication_date, availability_date, content, raw_content, publication_type,
    recipient, lawyer_name, lawyer_oab, status, read_status, treatment_status,
    assigned_user, link_provenance, link_confidence, suggested_due_date, suggested_deadline_days,
    suggestion_source, suggestion_confidence, fingerprint, provenance, created_at, updated_at
) VALUES 
(
    'pub-seed-001', 'DJEN-2026-TRF4-98124', 'DJEN', 'DIARIO_OFICIAL', 'TRF4', 'case-seed-001',
    '5001234-56.2026.4.04.7105', '50012345620264047105',
    NOW(), NOW(),
    'PODER JUDICIÁRIO TRIBUNAL REGIONAL FEDERAL DA 4ª REGIÃO.\n\nIntima-se o autor EMPRESA TESTE BR-LAWYER LTDA. para, no prazo legal de 15 (quinze) dias, querendo, apresentar RÉPLICA à Contestação e manifestar-se acerca dos documentos juntados pela União Federal.',
    'DJEN TRF4 - Edicao 3125 - 5001234-56.2026.4.04.7105 - Intimação Autor Réplica',
    'INTIMACAO', 'EMPRESA TESTE BR-LAWYER LTDA.', 'Dr. Advogado BR-LAWYER', 'RS123456',
    'NOVA', 'UNREAD', 'NAO_TRATADA',
    'admin', 'AUTOMATIC_CNJ', 1.0,
    DATE_ADD(NOW(), INTERVAL 15 DAY), 15, 'REGEX_REPLICA_15D', 0.95,
    'fp-pub-seed-001', 'DJEN_API', NOW(), NOW()
),
(
    'pub-seed-002', 'DJE-TJSP-2026-44102', 'DJE_TJSP', 'DIARIO_OFICIAL', 'TJSP', NULL,
    '1023456-78.2026.8.26.0100', '10234567820268260100',
    NOW(), NOW(),
    'TRIBUNAL DE JUSTIÇA DO ESTADO DE SÃO PAULO - 12ª VARA CÍVEL DA CAPITAL.\n\nVistos. Intime-se a parte autora para emendar a petição inicial no prazo de 15 dias, sob pena de indeferimento, indicando com precisão o valor controvertido da obrigação.',
    'DJE TJSP - 1023456-78.2026.8.26.0100 - Emenda à Inicial',
    'INTIMACAO', 'SILVA PARTICIPACOES S.A.', 'Dr. Advogado BR-LAWYER', 'SP998877',
    'EM_ANALISE', 'READ', 'EM_ANALISE',
    'admin', 'MANUAL', 0.0,
    DATE_ADD(NOW(), INTERVAL 15 DAY), 15, 'EMENDA_INICIAL_15D', 0.90,
    'fp-pub-seed-002', 'DJE_TJSP_CRAWLER', NOW(), NOW()
)
ON DUPLICATE KEY UPDATE content = VALUES(content);

-- 2. Tarefas Jurídicas
INSERT INTO br_tasks (
    id, title, description, process_id, publication_id, assigned_user, created_by,
    status, priority, due_date, due_time, category, estimated_minutes, actual_minutes, created_at, updated_at
) VALUES
(
    'task-seed-001',
    'Elaborar Réplica à Contestação e Análise de Documentos da União',
    'Redigir réplica enfrentando preliminares de mérito e teses da União Federal no MS 5001234-56.',
    'case-seed-001', 'pub-seed-001', 'admin', 'admin',
    'TODO', 'URGENT', DATE_ADD(NOW(), INTERVAL 5 DAY), '18:00', 'PETICAO', 180, 0, NOW(), NOW()
),
(
    'task-seed-002',
    'Triagem e Emenda à Petição Inicial - TJSP',
    'Adequar valor da causa e especificar os tópicos controvertidos conforme despacho judicial.',
    NULL, 'pub-seed-002', 'admin', 'admin',
    'IN_PROGRESS', 'HIGH', NOW(), '17:00', 'ANALISE', 60, 20, NOW(), NOW()
),
(
    'task-seed-003',
    'Juntada de Procuração Atualizada e Contrato Social',
    'Solicitar via email certidão simplificada da JUCESP e juntar aos autos.',
    'case-seed-001', NULL, 'admin', 'admin',
    'WAITING', 'NORMAL', DATE_ADD(NOW(), INTERVAL -2 DAY), '12:00', 'DILIGENCIA', 30, 0, NOW(), NOW()
),
(
    'task-seed-004',
    'Conferência de Cálculos de Liquidação e Impugnação',
    'Checar planilha pericial quanto à taxa Selic e correção monetária.',
    'case-seed-001', NULL, 'admin', 'admin',
    'TODO', 'NORMAL', DATE_ADD(NOW(), INTERVAL 8 DAY), '19:00', 'CALCULO', 120, 0, NOW(), NOW()
),
(
    'task-seed-005',
    'Distribuição e Pagamento de Custas Iniciais',
    'Custas recolhidas via DARE e comprovante acostado à exordial.',
    'case-seed-001', NULL, 'admin', 'admin',
    'DONE', 'NORMAL', DATE_ADD(NOW(), INTERVAL -1 DAY), '18:00', 'PROTOCOLOS', 45, 45, NOW(), NOW()
)
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 3. Comentários da Tarefa
INSERT INTO br_task_comments (id, task_id, user_name, comment_text, created_at) VALUES
('comm-seed-001', 'task-seed-001', 'admin', 'Documentos contábeis recebidos do cliente e salvos na pasta digital.', NOW()),
('comm-seed-002', 'task-seed-002', 'admin', 'Minuta preliminar iniciada no editor.', NOW())
ON DUPLICATE KEY UPDATE comment_text = VALUES(comment_text);

-- 4. Checklist Items
INSERT INTO br_task_checklist_items (id, task_id, title, done, item_order) VALUES
('chk-seed-001', 'task-seed-001', 'Analisar preliminar de ilegitimidade passiva da União', 1, 1),
('chk-seed-002', 'task-seed-001', 'Confrontar jurisprudência do STJ sobre creditamento PIS/COFINS', 0, 2),
('chk-seed-003', 'task-seed-001', 'Revisar petição e coletar assinatura digital', 0, 3),
('chk-seed-004', 'task-seed-002', 'Calcular novo valor da causa com atualização monetária', 1, 1),
('chk-seed-005', 'task-seed-002', 'Protocolar petição de emenda via portal TJSP', 0, 2)
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 5. Registro de Auditoria / Eventos do Workflow
INSERT INTO br_publication_events (id, publication_id, task_id, process_id, event_type, actor, details, created_at) VALUES
('ev-seed-001', 'pub-seed-001', NULL, 'case-seed-001', 'CAPTURED', 'DJEN_INTEGRATION', 'Publicação capturada automaticamente via DJEN TRF4', NOW()),
('ev-seed-002', 'pub-seed-002', NULL, NULL, 'CAPTURED', 'DJE_INTEGRATION', 'Publicação capturada via diário de justiça estadual TJSP', NOW())
ON DUPLICATE KEY UPDATE details = VALUES(details);

COMMIT;
