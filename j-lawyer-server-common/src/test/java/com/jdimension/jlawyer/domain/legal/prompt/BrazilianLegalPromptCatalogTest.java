/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.prompt;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Testes Unitários do Catálogo Canônico de Prompts e Modelos Jurídicos do BR-LAWYER.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianLegalPromptCatalogTest {

    private BrazilianLegalPromptCatalog catalog;

    @Before
    public void setUp() {
        catalog = new BrazilianLegalPromptCatalog();
    }

    @Test
    public void testCatalogInitialization() {
        assertFalse(catalog.getAllTemplates().isEmpty());
        assertNotNull(catalog.getTemplate("inicial-civel"));
        assertNotNull(catalog.getTemplate("contestacao-civel"));
        assertNotNull(catalog.getTemplate("apelacao-civel"));
        assertNotNull(catalog.getTemplate("embargos-declaracao"));
    }

    @Test
    public void testGetTemplatesByCategory() {
        List<LegalPromptTemplate> recursos = catalog.getTemplatesByCategory("RECURSOS");
        assertFalse(recursos.isEmpty());
        assertEquals("apelacao-civel", recursos.get(0).getId());

        List<LegalPromptTemplate> embargos = catalog.getTemplatesByCategory("EMBARGOS");
        assertFalse(embargos.isEmpty());
        assertEquals("embargos-declaracao", embargos.get(0).getId());
    }

    @Test
    public void testRenderTemplateWithTokenReplacement() {
        Map<String, String> values = new HashMap<>();
        values.put("VARA", "2ª Vara Cível");
        values.put("COMARCA", "Porto Alegre");
        values.put("UF", "RS");
        values.put("NUMERO_PROCESSO", "5001234-56.2023.8.21.0001");
        values.put("NOME_EMBARGANTE", "Empresa Exemplo Ltda");
        values.put("TEMPESTIVIDADE", "Decisão publicada em 10/08/2023, tempestivo nesta data.");
        values.put("TIPO_VICIO", "Omissão");
        values.put("DEMONSTRACAO_VICIO", "O r. despacho não apreciou o pedido de tutela de urgência.");
        values.put("DATA", "15/08/2023");
        values.put("NOME_ADVOGADO", "Dra. Advogada Teste");
        values.put("OAB_ADVOGADO", "OAB/RS 123456");

        String rendered = catalog.renderTemplate("embargos-declaracao", values);

        assertNotNull(rendered);
        assertTrue(rendered.contains("2ª Vara Cível DA COMARCA DE Porto Alegre - RS"));
        assertTrue(rendered.contains("Processo nº: 5001234-56.2023.8.21.0001"));
        assertTrue(rendered.contains("Empresa Exemplo Ltda"));
        assertTrue(rendered.contains("Omissão"));
        assertTrue(rendered.contains("Dra. Advogada Teste"));
        assertTrue(rendered.contains("OAB/RS 123456"));
        assertFalse(rendered.contains("{{VARA}}"));
        assertFalse(rendered.contains("{{OAB_ADVOGADO}}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTemplateIdThrowsException() {
        catalog.renderTemplate("template-inexistente", new HashMap<>());
    }
}
