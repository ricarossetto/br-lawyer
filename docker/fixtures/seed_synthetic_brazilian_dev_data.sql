-- BR-LAWYER: Synthetic Brazilian Development Seed Fixture
INSERT INTO contacts (
    id, name, company, person_type, cnpj, email, phone, city, state, creator, creationDate
) VALUES (
    'contact-seed-001',
    'EMPRESA TESTE BR-LAWYER LTDA.',
    'EMPRESA TESTE BR-LAWYER LTDA.',
    'JURIDICAL',
    '12.345.678/0001-90',
    'contato@empresateste.com.br',
    '(51) 3333-4444',
    'Porto Alegre',
    'RS',
    'admin',
    NOW()
) ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO contacts (
    id, name, company, person_type, cnpj, email, phone, city, state, creator, creationDate
) VALUES (
    'contact-seed-002',
    'UNIÃO FEDERAL - FAZENDA NACIONAL',
    'PROCURADORIA-GERAL DA FAZENDA NACIONAL',
    'JURIDICAL',
    '00.394.460/0058-87',
    'pgfn@pgfn.gov.br',
    '(61) 3412-1000',
    'Brasília',
    'DF',
    'admin',
    NOW()
) ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO cases (
    id, name, fileNumber, claimNumber, claimValue, archived, lawyer, assistant,
    date_created, date_changed, cnj_number, cnj_number_clean, court_code,
    justice_segment, jurisdiction_degree, court_unit, comarca, tpu_class_code,
    tpu_class_name, tpu_subject_codes, tpu_subject_names, distribution_date,
    case_status_br, provenance_system
) VALUES (
    'case-seed-001',
    'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
    'BR-2026/0001',
    '5001234-56.2026.4.04.7105',
    150000.00,
    0,
    'admin',
    'admin',
    '2026-02-15 10:00:00',
    NOW(),
    '5001234-56.2026.4.04.7105',
    '50012345620264047105',
    'TRF4',
    4,
    'G1',
    '1ª Vara Federal de Porto Alegre',
    'Porto Alegre',
    7,
    'Procedimento Comum Cível',
    '10685',
    'DIREITO TRIBUTÁRIO',
    '2026-02-15 10:30:00',
    'ACTIVE',
    'EPROC'
) ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO case_contacts (
    id, archiveFileKey, addressKey, referenceType, reference
) VALUES (
    'party-seed-001',
    'case-seed-001',
    'contact-seed-001',
    '10',
    'Autora / Cliente'
) ON DUPLICATE KEY UPDATE reference=VALUES(reference);

INSERT INTO case_contacts (
    id, archiveFileKey, addressKey, referenceType, reference
) VALUES (
    'party-seed-002',
    'case-seed-001',
    'contact-seed-002',
    '20',
    'Ré / Fazenda Nacional'
) ON DUPLICATE KEY UPDATE reference=VALUES(reference);

INSERT INTO case_tags (
    id, archiveFileKey, tagName, tag_value, date_set
) VALUES (
    'tag-seed-001',
    'case-seed-001',
    'TRIBUTÁRIO',
    'URGENTE',
    NOW()
) ON DUPLICATE KEY UPDATE tag_value=VALUES(tag_value);

INSERT INTO case_events (
    id, archiveFileKey, summary, beginDate, done, assignee, eventType, created_by
) VALUES (
    'event-seed-001',
    'case-seed-001',
    'Prazo: Réplica à Contestação',
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    0,
    'admin',
    10,
    'admin'
) ON DUPLICATE KEY UPDATE summary=VALUES(summary);

INSERT INTO case_history (
    id, principal, archiveFileKey, changeDescription, changeDate
) VALUES (
    'hist-seed-001',
    'admin',
    'case-seed-001',
    'Processo distribuído e autuado eletronicamente no EPROC TRF4',
    '2026-02-15 10:30:00'
) ON DUPLICATE KEY UPDATE changeDescription=VALUES(changeDescription);

INSERT INTO case_history (
    id, principal, archiveFileKey, changeDescription, changeDate
) VALUES (
    'hist-seed-002',
    'admin',
    'case-seed-001',
    'Contestação e documentos juntados pela União Federal',
    '2026-08-20 14:15:00'
) ON DUPLICATE KEY UPDATE changeDescription=VALUES(changeDescription);

INSERT INTO case_documents (
    id, name, archiveFileKey, creationDate, size, favorite, deleted, version, highlight1, highlight2, document_type,
    content
) VALUES (
    'doc-seed-001',
    'Peticao_Inicial_Mandado_de_Seguranca.pdf',
    'case-seed-001',
    '2026-02-15 10:00:00',
    302,
    0,
    0,
    1,
    -2147483648,
    -2147483648,
    10,
    0x255044462d312e340a25c4e5f2e40a312030206f626a0a3c3c2f547970652f436174616c6f672f50616765732032203020523e3e0a656e646f626a0a322030206f626a0a3c3c2f547970652f50616765732f4b6964735b33203020525d2f436f756e7420313e3e0a656e646f626a0a332030206f626a0a3c3c2f547970652f506167652f506172656e742032203020522f4d65646961426f785b30203020363132203739325d2f436f6e74656e74732034203020523e3e0a656e646f626a0a342030206f626a0a3c3c2f4c656e6774682034343e3e0a73747265616d0a42540a2f46312032342054660a313030203730302054640a2842522d4c41575945522054657374652920546a0a45540a656e6473747265616d0a656e646f626a0a787265660a3020350a3030303030303030303020363535333520660a30303030303030303135203030303030206e0a30303030303030303638203030303030206e0a30303030303030313235203030303030206e0a30303030303030323133203030303030206e0a747261696c65720a3c3c2f53697a6520352f526f6f742031203020523e3e0a7374617274787265660a3330380a2525454f46
) ON DUPLICATE KEY UPDATE name=VALUES(name);
