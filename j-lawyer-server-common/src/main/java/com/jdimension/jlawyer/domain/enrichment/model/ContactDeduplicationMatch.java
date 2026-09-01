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
 * Resultado de análise de deduplicação cadastral de contatos.
 *
 * @author BR-LAWYER Team
 */
public class ContactDeduplicationMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MatchLevel {
        EXACT_IDENTIFIER,       // CPF ou CNPJ idêntico (100% certeza)
        EXACT_NORMALIZED_NAME,  // Nome normalizado idêntico
        STRONG_FUZZY_MATCH,     // Similaridade Fonética + Jaro-Winkler > 0.88
        POSSIBLE_FUZZY_MATCH,   // Similaridade > 0.75
        RELATIONSHIP_MATCH      // Sócio/Administrador já cadastrado
    }

    private String existingContactId;
    private String existingContactName;
    private String existingContactIdentifier;
    private String existingContactCity;
    private String existingContactState;
    private MatchLevel matchLevel = MatchLevel.POSSIBLE_FUZZY_MATCH;
    private double similarityScore; // 0.0 a 1.0
    private String explanation;
    private List<String> conflictingFields = new ArrayList<>();

    public ContactDeduplicationMatch() {}

    public String getExistingContactId() { return existingContactId; }
    public void setExistingContactId(String existingContactId) { this.existingContactId = existingContactId; }

    public String getExistingContactName() { return existingContactName; }
    public void setExistingContactName(String existingContactName) { this.existingContactName = existingContactName; }

    public String getExistingContactIdentifier() { return existingContactIdentifier; }
    public void setExistingContactIdentifier(String existingContactIdentifier) { this.existingContactIdentifier = existingContactIdentifier; }

    public String getExistingContactCity() { return existingContactCity; }
    public void setExistingContactCity(String existingContactCity) { this.existingContactCity = existingContactCity; }

    public String getExistingContactState() { return existingContactState; }
    public void setExistingContactState(String existingContactState) { this.existingContactState = existingContactState; }

    public MatchLevel getMatchLevel() { return matchLevel; }
    public void setMatchLevel(MatchLevel matchLevel) { this.matchLevel = matchLevel; }

    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getConflictingFields() { return conflictingFields; }
    public void setConflictingFields(List<String> conflictingFields) { this.conflictingFields = conflictingFields; }
}
