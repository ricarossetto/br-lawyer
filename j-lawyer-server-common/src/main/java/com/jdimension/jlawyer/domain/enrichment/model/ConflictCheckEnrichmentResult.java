/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado detalhado de verificação de conflito de interesses (Conflict Check)
 * cruzando partes de processos, sócios de QSA, administradores e entidades relacionadas.
 *
 * @author BR-LAWYER Team
 */
public class ConflictCheckEnrichmentResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ConflictSeverity {
        EXACT_MATCH,        // Conflito crítico direto (CPF/CNPJ exato ou parte contrária ativa)
        STRONG_MATCH,       // Conflito provável (Sócio/Administrador de parte contrária ou score > 0.88)
        POSSIBLE_MATCH,     // Alerta de homônimo / similaridade (> 0.75) requerendo revisão
        NO_CONFLICT         // Nenhum conflito identificado
    }

    public static class ConflictDetail implements Serializable {
        private static final long serialVersionUID = 1L;
        private String caseId;
        private String caseNumber;
        private String caseName;
        private String partyRole;               // Ex: "Parte Contrária", "Litisconsorte"
        private String matchedEntityName;       // Ex: "João da Silva (Sócio-Administrador da Ré)"
        private String matchedIdentifier;
        private ConflictSeverity severity;
        private String rationale;

        public ConflictDetail() {}

        public String getCaseId() { return caseId; }
        public void setCaseId(String caseId) { this.caseId = caseId; }

        public String getCaseNumber() { return caseNumber; }
        public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

        public String getCaseName() { return caseName; }
        public void setCaseName(String caseName) { this.caseName = caseName; }

        public String getPartyRole() { return partyRole; }
        public void setPartyRole(String partyRole) { this.partyRole = partyRole; }

        public String getMatchedEntityName() { return matchedEntityName; }
        public void setMatchedEntityName(String matchedEntityName) { this.matchedEntityName = matchedEntityName; }

        public String getMatchedIdentifier() { return matchedIdentifier; }
        public void setMatchedIdentifier(String matchedIdentifier) { this.matchedIdentifier = matchedIdentifier; }

        public ConflictSeverity getSeverity() { return severity; }
        public void setSeverity(ConflictSeverity severity) { this.severity = severity; }

        public String getRationale() { return rationale; }
        public void setRationale(String rationale) { this.rationale = rationale; }
    }

    private String targetCandidateName;
    private String targetCandidateIdentifier;
    private ConflictSeverity overallSeverity = ConflictSeverity.NO_CONFLICT;
    private List<ConflictDetail> conflicts = new ArrayList<>();
    private List<String> investigatedParties = new ArrayList<>(); // Partes investigadas (incluindo sócios do QSA)

    public ConflictCheckEnrichmentResult() {}

    public String getTargetCandidateName() { return targetCandidateName; }
    public void setTargetCandidateName(String targetCandidateName) { this.targetCandidateName = targetCandidateName; }

    public String getTargetCandidateIdentifier() { return targetCandidateIdentifier; }
    public void setTargetCandidateIdentifier(String targetCandidateIdentifier) { this.targetCandidateIdentifier = targetCandidateIdentifier; }

    public ConflictSeverity getOverallSeverity() { return overallSeverity; }
    public void setOverallSeverity(ConflictSeverity overallSeverity) { this.overallSeverity = overallSeverity; }

    public List<ConflictDetail> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictDetail> conflicts) { this.conflicts = conflicts; }

    public List<String> getInvestigatedParties() { return investigatedParties; }
    public void setInvestigatedParties(List<String> investigatedParties) { this.investigatedParties = investigatedParties; }

    public void addConflict(ConflictDetail detail) {
        if (detail != null) {
            this.conflicts.add(detail);
            if (this.overallSeverity == ConflictSeverity.NO_CONFLICT ||
                (detail.getSeverity() == ConflictSeverity.EXACT_MATCH) ||
                (detail.getSeverity() == ConflictSeverity.STRONG_MATCH && this.overallSeverity != ConflictSeverity.EXACT_MATCH)) {
                this.overallSeverity = detail.getSeverity();
            }
        }
    }
}
