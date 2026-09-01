/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services.enrichment;

import com.jdimension.jlawyer.domain.enrichment.matching.LegalEntityNormalizer;
import com.jdimension.jlawyer.domain.enrichment.matching.PortugueseMetaphone;
import com.jdimension.jlawyer.domain.enrichment.model.ContactDeduplicationMatch;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;
import com.jdimension.jlawyer.persistence.AddressBean;
import org.jlawyer.text.similarity.JaroWinkler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Motor de deduplicação e correspondência inteligente de contatos brasileiros.
 * Combina validação de documentos fiscais (CPF/CNPJ), normalização de stopwords empresariais,
 * fonética brasileira (Metaphone-PT) e distância de Jaro-Winkler.
 *
 * @author BR-LAWYER Team
 */
public final class BrazilianContactDeduplicator {

    private BrazilianContactDeduplicator() {}

    /**
     * Compara um candidato com a lista de contatos existentes no sistema.
     *
     * @param identifier CPF ou CNPJ (opcional)
     * @param candidateName Nome ou Razão Social
     * @param city Cidade (opcional)
     * @param state UF (opcional)
     * @param existingContacts Lista de contatos existentes no banco
     * @return Lista ordenada de correspondências (maior confiança primeiro)
     */
    public static List<ContactDeduplicationMatch> findDuplicates(
            String identifier,
            String candidateName,
            String city,
            String state,
            List<AddressBean> existingContacts) {

        if (existingContacts == null || existingContacts.isEmpty()) {
            return Collections.emptyList();
        }

        List<ContactDeduplicationMatch> matches = new ArrayList<>();
        String cleanId = identifier != null ? identifier.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim() : "";
        String normCandidateName = LegalEntityNormalizer.normalizeCompanyName(candidateName);
        String phonCandidateName = PortugueseMetaphone.encodePhrase(candidateName);

        for (AddressBean contact : existingContacts) {
            String contactFullName = buildFullName(contact);
            if (contactFullName.isEmpty()) continue;

            String contactId = extractIdentifier(contact);
            String normContactName = LegalEntityNormalizer.normalizeCompanyName(contactFullName);
            String phonContactName = PortugueseMetaphone.encodePhrase(contactFullName);

            // 1. Match Exato por CPF ou CNPJ
            if (!cleanId.isEmpty() && !contactId.isEmpty() && cleanId.equals(contactId)) {
                ContactDeduplicationMatch match = new ContactDeduplicationMatch();
                match.setExistingContactId(contact.getId());
                match.setExistingContactName(contactFullName);
                match.setExistingContactIdentifier(contactId);
                match.setExistingContactCity(contact.getCity());
                match.setExistingContactState(contact.getState());
                match.setMatchLevel(ContactDeduplicationMatch.MatchLevel.EXACT_IDENTIFIER);
                match.setSimilarityScore(1.0);
                match.setExplanation("CPF/CNPJ idêntico (" + BrazilianDocumentValidator.formatCpf(cleanId) + ")");
                matches.add(match);
                continue;
            }

            // 2. Match Exato por Nome Normalizado
            if (!normCandidateName.isEmpty() && normCandidateName.equals(normContactName)) {
                ContactDeduplicationMatch match = new ContactDeduplicationMatch();
                match.setExistingContactId(contact.getId());
                match.setExistingContactName(contactFullName);
                match.setExistingContactIdentifier(contactId);
                match.setExistingContactCity(contact.getCity());
                match.setExistingContactState(contact.getState());
                match.setMatchLevel(ContactDeduplicationMatch.MatchLevel.EXACT_NORMALIZED_NAME);
                match.setSimilarityScore(0.98);
                match.setExplanation("Razão Social / Nome Normalizado idêntico");
                matches.add(match);
                continue;
            }

            // 3. Similaridade Composta (Jaro-Winkler + Metaphone-PT)
            if (!candidateName.isEmpty() && !contactFullName.isEmpty()) {
                double jwScore = JaroWinkler.jaroWinklerDistance(candidateName.toUpperCase(), contactFullName.toUpperCase());
                double normJwScore = JaroWinkler.jaroWinklerDistance(normCandidateName, normContactName);
                double bestJw = Math.max(jwScore, normJwScore);
                boolean phonMatch = !phonCandidateName.isEmpty() && phonCandidateName.equals(phonContactName);

                double combinedScore = 0.70 * bestJw + 0.30 * (phonMatch ? 1.0 : (bestJw * 0.8));

                if (combinedScore >= 0.88) {
                    ContactDeduplicationMatch match = new ContactDeduplicationMatch();
                    match.setExistingContactId(contact.getId());
                    match.setExistingContactName(contactFullName);
                    match.setExistingContactIdentifier(contactId);
                    match.setExistingContactCity(contact.getCity());
                    match.setExistingContactState(contact.getState());
                    match.setMatchLevel(ContactDeduplicationMatch.MatchLevel.STRONG_FUZZY_MATCH);
                    match.setSimilarityScore(combinedScore);
                    match.setExplanation(String.format("Forte similaridade fonética/textual (%.1f%%)", combinedScore * 100));
                    matches.add(match);
                } else if (combinedScore >= 0.75) {
                    ContactDeduplicationMatch match = new ContactDeduplicationMatch();
                    match.setExistingContactId(contact.getId());
                    match.setExistingContactName(contactFullName);
                    match.setExistingContactIdentifier(contactId);
                    match.setExistingContactCity(contact.getCity());
                    match.setExistingContactState(contact.getState());
                    match.setMatchLevel(ContactDeduplicationMatch.MatchLevel.POSSIBLE_FUZZY_MATCH);
                    match.setSimilarityScore(combinedScore);
                    match.setExplanation(String.format("Possível homônimo ou variação de grafia (%.1f%%)", combinedScore * 100));
                    matches.add(match);
                }
            }
        }

        // Ordena por maior pontuação de similaridade
        matches.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        return matches;
    }

    private static String buildFullName(AddressBean contact) {
        if (contact == null) return "";
        if (contact.getCompany() != null && !contact.getCompany().trim().isEmpty()) {
            return contact.getCompany().trim();
        }
        StringBuilder sb = new StringBuilder();
        if (contact.getFirstName() != null && !contact.getFirstName().trim().isEmpty()) {
            sb.append(contact.getFirstName().trim());
        }
        if (contact.getName() != null && !contact.getName().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(contact.getName().trim());
        }
        return sb.toString().trim();
    }

    private static String extractIdentifier(AddressBean contact) {
        if (contact == null) return "";
        // No j-lawyer, campos vatId, tin ou externalId armazenam IDs fiscais
        if (contact.getVatId() != null && !contact.getVatId().trim().isEmpty()) {
            return contact.getVatId().replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        }
        if (contact.getTin() != null && !contact.getTin().trim().isEmpty()) {
            return contact.getTin().replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        }
        if (contact.getExternalId1() != null && !contact.getExternalId1().trim().isEmpty()) {
            return contact.getExternalId1().replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        }
        return "";
    }
}
