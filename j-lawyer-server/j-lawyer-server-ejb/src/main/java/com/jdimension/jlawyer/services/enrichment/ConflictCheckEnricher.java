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
import com.jdimension.jlawyer.domain.enrichment.model.ConflictCheckEnrichmentResult;
import com.jdimension.jlawyer.persistence.AddressBean;
import com.jdimension.jlawyer.persistence.ArchiveFileAddressesBean;
import org.jlawyer.text.similarity.JaroWinkler;

import java.util.ArrayList;
import java.util.List;

/**
 * Enriquecedor de verificação de conflitos de interesse (Conflict Check).
 * Cruza candidatos, sócios e administradores do QSA contra as partes de processos judiciais ativos no BR-LAWYER.
 *
 * @author BR-LAWYER Team
 */
public final class ConflictCheckEnricher {

    private ConflictCheckEnricher() {}

    /**
     * Executa a análise de conflito de interesses.
     *
     * @param targetIdentifier CPF ou CNPJ da parte candidata
     * @param targetName Nome ou Razão Social
     * @param relatedMembers Lista de nomes de sócios/administradores do QSA
     * @param caseAddresses Vínculos de endereços com processos judiciais
     * @return Resultado detalhado do Conflict Check
     */
    public static ConflictCheckEnrichmentResult evaluateConflicts(
            String targetIdentifier,
            String targetName,
            List<String> relatedMembers,
            List<ArchiveFileAddressesBean> caseAddresses) {

        ConflictCheckEnrichmentResult result = new ConflictCheckEnrichmentResult();
        result.setTargetCandidateName(targetName);
        result.setTargetCandidateIdentifier(targetIdentifier);

        List<String> allInvestigated = new ArrayList<>();
        if (targetName != null && !targetName.trim().isEmpty()) {
            allInvestigated.add(targetName.trim());
        }
        if (relatedMembers != null) {
            allInvestigated.addAll(relatedMembers);
        }
        result.setInvestigatedParties(allInvestigated);

        if (caseAddresses == null || caseAddresses.isEmpty() || allInvestigated.isEmpty()) {
            return result;
        }

        String cleanTargetId = targetIdentifier != null ? targetIdentifier.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim() : "";

        for (ArchiveFileAddressesBean link : caseAddresses) {
            AddressBean addr = link.getAddressKey();
            if (addr == null) continue;

            String partyName = getDisplayName(addr);
            String partyId = getIdentifier(addr);
            String roleName = link.getReferenceType() != null ? link.getReferenceType().getName() : "Parte";
            String caseNum = link.getArchiveFileKey() != null ? link.getArchiveFileKey().getFileNumber() : "Sem Número";
            String caseTitle = link.getArchiveFileKey() != null ? link.getArchiveFileKey().getName() : "Processo";
            String caseId = link.getArchiveFileKey() != null ? link.getArchiveFileKey().getId() : "";

            // 1. Verificação direta do candidato principal
            if (!cleanTargetId.isEmpty() && !partyId.isEmpty() && cleanTargetId.equals(partyId)) {
                ConflictCheckEnrichmentResult.ConflictDetail detail = new ConflictCheckEnrichmentResult.ConflictDetail();
                detail.setCaseId(caseId);
                detail.setCaseNumber(caseNum);
                detail.setCaseName(caseTitle);
                detail.setPartyRole(roleName);
                detail.setMatchedEntityName(partyName);
                detail.setMatchedIdentifier(partyId);
                detail.setSeverity(ConflictCheckEnrichmentResult.ConflictSeverity.EXACT_MATCH);
                detail.setRationale("CPF/CNPJ idêntico ao de " + roleName + " no processo " + caseNum);
                result.addConflict(detail);
                continue;
            }

            // Comparação de nomes normalizados
            String normParty = LegalEntityNormalizer.normalizeCompanyName(partyName);
            String phonParty = PortugueseMetaphone.encodePhrase(partyName);

            String normTarget = LegalEntityNormalizer.normalizeCompanyName(targetName);
            String phonTarget = PortugueseMetaphone.encodePhrase(targetName);

            if (!normTarget.isEmpty() && normTarget.equals(normParty)) {
                ConflictCheckEnrichmentResult.ConflictDetail detail = new ConflictCheckEnrichmentResult.ConflictDetail();
                detail.setCaseId(caseId);
                detail.setCaseNumber(caseNum);
                detail.setCaseName(caseTitle);
                detail.setPartyRole(roleName);
                detail.setMatchedEntityName(partyName);
                detail.setMatchedIdentifier(partyId);
                detail.setSeverity(ConflictCheckEnrichmentResult.ConflictSeverity.EXACT_MATCH);
                detail.setRationale("Nome/Razão Social idêntico a " + roleName + " no processo " + caseNum);
                result.addConflict(detail);
                continue;
            }

            // 2. Verificação de sócios e administradores do QSA
            if (relatedMembers != null && !relatedMembers.isEmpty()) {
                for (String memberName : relatedMembers) {
                    if (memberName == null || memberName.trim().isEmpty()) continue;
                    String normMember = LegalEntityNormalizer.normalizeCompanyName(memberName);
                    String phonMember = PortugueseMetaphone.encodePhrase(memberName);

                    if (!normMember.isEmpty() && normMember.equals(normParty)) {
                        ConflictCheckEnrichmentResult.ConflictDetail detail = new ConflictCheckEnrichmentResult.ConflictDetail();
                        detail.setCaseId(caseId);
                        detail.setCaseNumber(caseNum);
                        detail.setCaseName(caseTitle);
                        detail.setPartyRole(roleName);
                        detail.setMatchedEntityName(memberName + " (Sócio/QSA de " + targetName + ")");
                        detail.setMatchedIdentifier(partyId);
                        detail.setSeverity(ConflictCheckEnrichmentResult.ConflictSeverity.STRONG_MATCH);
                        detail.setRationale("Sócio/Administrador (" + memberName + ") consta como " + roleName + " no processo " + caseNum);
                        result.addConflict(detail);
                        break;
                    }

                    // Fuzzy matching no sócio
                    double jw = JaroWinkler.jaroWinklerDistance(normMember, normParty);
                    if (jw >= 0.88 || (!phonMember.isEmpty() && phonMember.equals(phonParty))) {
                        ConflictCheckEnrichmentResult.ConflictDetail detail = new ConflictCheckEnrichmentResult.ConflictDetail();
                        detail.setCaseId(caseId);
                        detail.setCaseNumber(caseNum);
                        detail.setCaseName(caseTitle);
                        detail.setPartyRole(roleName);
                        detail.setMatchedEntityName(memberName + " (Sócio/QSA)");
                        detail.setSeverity(ConflictCheckEnrichmentResult.ConflictSeverity.POSSIBLE_MATCH);
                        detail.setRationale(String.format("Similaridade com Sócio (%s) e %s no processo %s (%.1f%%)", memberName, roleName, caseNum, jw * 100));
                        result.addConflict(detail);
                    }
                }
            }

            // 3. Similaridade fuzzy no candidato principal
            if (!normTarget.isEmpty() && !normParty.isEmpty()) {
                double jw = JaroWinkler.jaroWinklerDistance(normTarget, normParty);
                if (jw >= 0.88) {
                    ConflictCheckEnrichmentResult.ConflictDetail detail = new ConflictCheckEnrichmentResult.ConflictDetail();
                    detail.setCaseId(caseId);
                    detail.setCaseNumber(caseNum);
                    detail.setCaseName(caseTitle);
                    detail.setPartyRole(roleName);
                    detail.setMatchedEntityName(partyName);
                    detail.setSeverity(ConflictCheckEnrichmentResult.ConflictSeverity.STRONG_MATCH);
                    detail.setRationale(String.format("Forte similaridade nominal com %s no processo %s (%.1f%%)", roleName, caseNum, jw * 100));
                    result.addConflict(detail);
                } else if (jw >= 0.75) {
                    ConflictCheckEnrichmentResult.ConflictDetail detail = new ConflictCheckEnrichmentResult.ConflictDetail();
                    detail.setCaseId(caseId);
                    detail.setCaseNumber(caseNum);
                    detail.setCaseName(caseTitle);
                    detail.setPartyRole(roleName);
                    detail.setMatchedEntityName(partyName);
                    detail.setSeverity(ConflictCheckEnrichmentResult.ConflictSeverity.POSSIBLE_MATCH);
                    detail.setRationale(String.format("Possível homônimo com %s no processo %s (%.1f%%)", roleName, caseNum, jw * 100));
                    result.addConflict(detail);
                }
            }
        }

        return result;
    }

    private static String getDisplayName(AddressBean a) {
        if (a == null) return "";
        if (a.getCompany() != null && !a.getCompany().trim().isEmpty()) return a.getCompany().trim();
        StringBuilder sb = new StringBuilder();
        if (a.getFirstName() != null) sb.append(a.getFirstName().trim());
        if (a.getName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(a.getName().trim());
        }
        return sb.toString().trim();
    }

    private static String getIdentifier(AddressBean a) {
        if (a == null) return "";
        if (a.getVatId() != null && !a.getVatId().trim().isEmpty()) {
            return a.getVatId().replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        }
        if (a.getTin() != null && !a.getTin().trim().isEmpty()) {
            return a.getTin().replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
        }
        return "";
    }
}
