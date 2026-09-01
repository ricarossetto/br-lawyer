/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.matching;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Normalizador de nomes empresariais e pessoas para deduplicação e detecção de conflitos de interesse.
 * Remove stopwords empresariais (LTDA, S/A, EIRELI, ME, EPP, etc.) e normaliza acentuação.
 *
 * @author BR-LAWYER Team
 */
public final class LegalEntityNormalizer {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Z0-9\\s]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    private static final Set<String> CORPORATE_STOPWORDS = new HashSet<>(Arrays.asList(
        "LTDA", "LIMITADA", "SA", "S/A", "S.A.", "S.A", "CIA", "COMPANHIA",
        "EIRELI", "ME", "EPP", "MICROEMPRESA", "MEI", "SOCIEDADE", "UNIPESSOAL",
        "ANONIMA", "HOLDING", "PARTICIPACOES", "SERVICOS", "COMERCIO",
        "INDUSTRIA", "DO", "DA", "DE", "DOS", "DAS", "E", "EM", "PARA", "COM"
    ));

    private LegalEntityNormalizer() {
        // Utilitário estático
    }

    /**
     * Normaliza uma razão social ou nome empresarial para fins de comparação canônica.
     * Ex: "ACME Comércio e Serviços de Tecnologia LTDA - ME" -> "ACME TECNOLOGIA"
     *
     * @param corporateName Razão social ou nome fantasia
     * @return Nome normalizado sem ruído societário
     */
    public static String normalizeCompanyName(String corporateName) {
        if (corporateName == null || corporateName.trim().isEmpty()) {
            return "";
        }

        // 1. Desacentuação
        String decomposed = Normalizer.normalize(corporateName, Normalizer.Form.NFD);
        String noAccents = DIACRITICS.matcher(decomposed).replaceAll("");

        // 2. Limpeza de pontuação e caracteres não alfanuméricos
        String clean = NON_ALPHANUMERIC.matcher(noAccents.toUpperCase(Locale.ROOT)).replaceAll(" ");

        // 3. Remoção de stopwords societárias e tokens unitários
        String[] tokens = MULTIPLE_SPACES.matcher(clean).replaceAll(" ").trim().split(" ");
        StringBuilder result = new StringBuilder();
        for (String token : tokens) {
            if (!CORPORATE_STOPWORDS.contains(token) && token.length() > 1) {
                if (result.length() > 0) result.append(" ");
                result.append(token);
            }
        }

        // Se após remoção sobrou nada (ex: "S/A"), retorna o texto limpo
        if (result.length() == 0) {
            return MULTIPLE_SPACES.matcher(clean).replaceAll(" ").trim();
        }

        return result.toString().trim();
    }

    /**
     * Normaliza o nome de uma pessoa física (remoção de acentos, maiúsculas e espaços múltiplos).
     *
     * @param personName Nome da pessoa física
     * @return Nome normalizado
     */
    public static String normalizePersonName(String personName) {
        if (personName == null || personName.trim().isEmpty()) {
            return "";
        }
        String decomposed = Normalizer.normalize(personName, Normalizer.Form.NFD);
        String noAccents = DIACRITICS.matcher(decomposed).replaceAll("");
        String clean = NON_ALPHANUMERIC.matcher(noAccents.toUpperCase(Locale.ROOT)).replaceAll(" ");
        return MULTIPLE_SPACES.matcher(clean).replaceAll(" ").trim();
    }
}
