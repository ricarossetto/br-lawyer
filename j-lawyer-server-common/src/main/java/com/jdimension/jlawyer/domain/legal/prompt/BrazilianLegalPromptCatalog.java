/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.prompt;

import java.util.*;

/**
 * Catálogo Canônico de Prompts e Modelos de Peças Processuais Brasileiras.
 *
 * Oferece templates estruturados para:
 * 1. Petição Inicial (Cível, Trabalhista, Cobrança, Consumidor)
 * 2. Contestação com preliminares do CPC/2015
 * 3. Recursos (Apelação, Agravo de Instrumento com Pedido Suspensivo, Recurso Ordinário)
 * 4. Embargos de Declaração (Omissão, Contradição, Obscuridade, Erro Material)
 * 5. Notificação Extrajudicial e Pareceres
 *
 * Princípio de Minimização de Contexto (ATRIUM / BR-LAWYER):
 * - Envia apenas dados essenciais do processo para o assistente IA.
 * - Elimina PII desnecessária.
 *
 * @author BR-LAWYER Team
 */
public class BrazilianLegalPromptCatalog {

    private final Map<String, LegalPromptTemplate> catalog = new HashMap<>();

    public BrazilianLegalPromptCatalog() {
        initializeCatalog();
    }

    private void initializeCatalog() {
        // 1. PETIÇÃO INICIAL CÍVEL
        addTemplate(new LegalPromptTemplate(
                "inicial-civel",
                "PETICAO_INICIAL",
                "Petição Inicial - Procedimento Comum Cível",
                "Estrutura completa sob o Art. 319 do CPC/2015 com pedidos, tutela e valor da causa.",
                "EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO DA {{VARA}} DA COMARCA DE {{COMARCA}} - {{UF}}\n\n" +
                        "{{NOME_AUTOR}}, {{QUALIFICACAO_AUTOR}}, por seu advogado subscrito ({{OAB_ADVOGADO}}), vem propor a presente\n\n" +
                        "AÇÃO {{NOME_ACAO}}\n\n" +
                        "em face de {{NOME_REU}}, {{QUALIFICACAO_REU}}, pelos fatos e fundamentos a seguir expostos:\n\n" +
                        "I. DOS FATOS\n{{FATOS}}\n\n" +
                        "II. DO DIREITO\n{{FUNDAMENTOS}}\n\n" +
                        "III. DOS PEDIDOS\n{{PEDIDOS}}\n\n" +
                        "Dá-se à causa o valor de R$ {{VALOR_CAUSA}}.\n\n" +
                        "Termos em que pede deferimento.\n{{COMARCA}}, {{DATA}}.\n{{NOME_ADVOGADO}}\n{{OAB_ADVOGADO}}",
                Arrays.asList("VARA", "COMARCA", "UF", "NOME_AUTOR", "QUALIFICACAO_AUTOR", "NOME_ADVOGADO", "OAB_ADVOGADO", "NOME_ACAO", "NOME_REU", "QUALIFICACAO_REU", "FATOS", "FUNDAMENTOS", "PEDIDOS", "VALOR_CAUSA", "DATA")
        ));

        // 2. CONTESTAÇÃO COM PRELIMINARES DO CPC/2015
        addTemplate(new LegalPromptTemplate(
                "contestacao-civel",
                "CONTESTACAO",
                "Contestação Cível com Preliminares (Art. 337 CPC)",
                "Peça de defesa com preliminares processuais, impugnação ao valor da causa e mérito.",
                "EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO DA {{VARA}} DA COMARCA DE {{COMARCA}} - {{UF}}\n\n" +
                        "Processo nº: {{NUMERO_PROCESSO}}\n\n" +
                        "{{NOME_REU}}, já qualificado nos autos em epígrafe, vem apresentar sua\n\n" +
                        "CONTESTAÇÃO\n\n" +
                        "à ação movida por {{NOME_AUTOR}}, consoante as razões fáticas e jurídicas adiante articuladas:\n\n" +
                        "I. DAS PRELIMINARES (ART. 337 DO CPC)\n{{PRELIMINARES}}\n\n" +
                        "II. DA REALIDADE DOS FATOS\n{{FATOS_DEFESA}}\n\n" +
                        "III. DO MÉRITO E DO DIREITO\n{{MERITO}}\n\n" +
                        "IV. DOS PEDIDOS E REQUERIMENTOS\n{{PEDIDOS_DEFESA}}\n\n" +
                        "Nestes termos, pede deferimento.\n{{COMARCA}}, {{DATA}}.\n{{NOME_ADVOGADO}}\n{{OAB_ADVOGADO}}",
                Arrays.asList("VARA", "COMARCA", "UF", "NUMERO_PROCESSO", "NOME_REU", "NOME_AUTOR", "PRELIMINARES", "FATOS_DEFESA", "MERITO", "PEDIDOS_DEFESA", "DATA", "NOME_ADVOGADO", "OAB_ADVOGADO")
        ));

        // 3. APELAÇÃO CÍVEL (ART. 1.009 CPC)
        addTemplate(new LegalPromptTemplate(
                "apelacao-civel",
                "RECURSOS",
                "Apelação Cível (Art. 1.009 CPC)",
                "Razões recursais de apelação com prequestionamento, preliminar e reforma da sentença.",
                "EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO DA {{VARA}} DA COMARCA DE {{COMARCA}} - {{UF}}\n\n" +
                        "Processo nº: {{NUMERO_PROCESSO}}\n\n" +
                        "{{NOME_APELANTE}}, inconformado com a r. sentença de fls., vem interpor o presente\n\n" +
                        "RECURSO DE APELAÇÃO\n\n" +
                        "requerendo a remessa das inclusas razões ao Egrégio Tribunal de Justiça.\n\n" +
                        "RAZÕES DO APELANTE\n" +
                        "EGRÉGIO TRIBUNAL,\n" +
                        "COLENDA CÂMARA,\n\n" +
                        "I. DA TEMPESTIVIDADE E DO PREPARO\n{{TEMPESTIVIDADE_PREPARO}}\n\n" +
                        "II. SÍNTESE DO PROCESSO E DA SENTENÇA RECORRIDA\n{{SINTESE}}\n\n" +
                        "III. DAS RAZÕES PARA REFORMA (ERROR IN PROCEDENDO / ERROR IN JUDICANDO)\n{{RAZOES_REFORMA}}\n\n" +
                        "IV. DO PEDIDO\nAnte o exposto, requer o CONHECIMENTO e PROVIMENTO do recurso para reforma da decisão.\n\n" +
                        "{{COMARCA}}, {{DATA}}.\n{{NOME_ADVOGADO}}\n{{OAB_ADVOGADO}}",
                Arrays.asList("VARA", "COMARCA", "UF", "NUMERO_PROCESSO", "NOME_APELANTE", "TEMPESTIVIDADE_PREPARO", "SINTESE", "RAZOES_REFORMA", "DATA", "NOME_ADVOGADO", "OAB_ADVOGADO")
        ));

        // 4. EMBARGOS DE DECLARAÇÃO (ART. 1.022 CPC)
        addTemplate(new LegalPromptTemplate(
                "embargos-declaracao",
                "EMBARGOS",
                "Embargos de Declaração (Art. 1.022 CPC)",
                "Peça recursal para sanar omissão, contradição, obscuridade ou erro material com efeito infringente.",
                "EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO DA {{VARA}} DA COMARCA DE {{COMARCA}} - {{UF}}\n\n" +
                        "Processo nº: {{NUMERO_PROCESSO}}\n\n" +
                        "{{NOME_EMBARGANTE}}, nos autos em epígrafe, vem opor tempestivamente\n\n" +
                        "EMBARGOS DE DECLARAÇÃO\n\n" +
                        "em face da r. decisão/sentença de fls., com fulcro no Art. 1.022 do CPC/2015:\n\n" +
                        "I. DA TEMPESTIVIDADE (PRAZO DE 5 DIAS ÚTEIS - ART. 1.023 CPC)\n{{TEMPESTIVIDADE}}\n\n" +
                        "II. DO VÍCIO APONTADO ({{TIPO_VICIO}})\n{{DEMONSTRACAO_VICIO}}\n\n" +
                        "III. DO PEDIDO\nRequer sejam acolhidos os presentes embargos para suprir o vício apontado.\n\n" +
                        "{{COMARCA}}, {{DATA}}.\n{{NOME_ADVOGADO}}\n{{OAB_ADVOGADO}}",
                Arrays.asList("VARA", "COMARCA", "UF", "NUMERO_PROCESSO", "NOME_EMBARGANTE", "TEMPESTIVIDADE", "TIPO_VICIO", "DEMONSTRACAO_VICIO", "DATA", "NOME_ADVOGADO", "OAB_ADVOGADO")
        ));
    }

    private void addTemplate(LegalPromptTemplate template) {
        catalog.put(template.getId(), template);
    }

    public LegalPromptTemplate getTemplate(String id) {
        return catalog.get(id);
    }

    public Collection<LegalPromptTemplate> getAllTemplates() {
        return Collections.unmodifiableCollection(catalog.values());
    }

    public List<LegalPromptTemplate> getTemplatesByCategory(String category) {
        if (category == null) return Collections.emptyList();
        List<LegalPromptTemplate> list = new ArrayList<>();
        for (LegalPromptTemplate t : catalog.values()) {
            if (category.equalsIgnoreCase(t.getCategory())) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Renderiza o template substituindo tokens {{PLACEHOLDER}} pelos valores fornecidos no mapa.
     */
    public String renderTemplate(String templateId, Map<String, String> values) {
        LegalPromptTemplate template = getTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template não encontrado para o ID: " + templateId);
        }

        String rendered = template.getContent();
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String token = "{{" + entry.getKey() + "}}";
                String val = entry.getValue() != null ? entry.getValue() : "";
                rendered = rendered.replace(token, val);
            }
        }
        return rendered;
    }
}
