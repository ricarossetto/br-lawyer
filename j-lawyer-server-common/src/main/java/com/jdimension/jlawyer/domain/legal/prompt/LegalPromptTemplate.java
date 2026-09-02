/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.prompt;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Modelo de template de peça jurídica e prompt assistencial brasileiro.
 *
 * @author BR-LAWYER Team
 */
public final class LegalPromptTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String category;      // INICIAL, CONTESTACAO, RECURSO, EMBARGOS, NOTIFICACAO
    private final String title;         // Nome descritivo da peça
    private final String description;   // Resumo de aplicação prática
    private final String content;       // Estrutura/Minuta padrão com placeholders
    private final List<String> placeholders;

    public LegalPromptTemplate(String id, String category, String title, String description, String content, List<String> placeholders) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.content = content;
        this.placeholders = placeholders != null ? Collections.unmodifiableList(placeholders) : Collections.emptyList();
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public List<String> getPlaceholders() { return placeholders; }
}
