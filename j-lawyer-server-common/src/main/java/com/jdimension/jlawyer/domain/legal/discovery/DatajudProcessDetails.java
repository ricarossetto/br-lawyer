/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.discovery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Detalhes estruturados de processo judicial obtidos via API Pública do DataJud (CNJ).
 *
 * @author BR-LAWYER Team
 */
public final class DatajudProcessDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String cnjNumber;
    private final String court;           // Sigla do Tribunal (ex: TJSP, TRF4)
    private final String degree;          // G1, G2, etc.
    private final Integer classCode;      // Código TPU da Classe
    private final String className;       // Nome da Classe Processual
    private final String judgingBody;     // Órgão Julgador / Vara
    private final String filingDate;      // Data de ajuizamento
    private final List<SubjectItem> subjects;
    private final List<PartyItem> parties;
    private final List<MovementItem> movements;

    public DatajudProcessDetails(String cnjNumber, String court, String degree, Integer classCode,
                                 String className, String judgingBody, String filingDate,
                                 List<SubjectItem> subjects, List<PartyItem> parties, List<MovementItem> movements) {
        this.cnjNumber = cnjNumber;
        this.court = court;
        this.degree = degree;
        this.classCode = classCode;
        this.className = className;
        this.judgingBody = judgingBody;
        this.filingDate = filingDate;
        this.subjects = subjects != null ? Collections.unmodifiableList(new ArrayList<>(subjects)) : Collections.emptyList();
        this.parties = parties != null ? Collections.unmodifiableList(new ArrayList<>(parties)) : Collections.emptyList();
        this.movements = movements != null ? Collections.unmodifiableList(new ArrayList<>(movements)) : Collections.emptyList();
    }

    public String getCnjNumber() { return cnjNumber; }
    public String getCourt() { return court; }
    public String getDegree() { return degree; }
    public Integer getClassCode() { return classCode; }
    public String getClassName() { return className; }
    public String getJudgingBody() { return judgingBody; }
    public String getFilingDate() { return filingDate; }
    public List<SubjectItem> getSubjects() { return subjects; }
    public List<PartyItem> getParties() { return parties; }
    public List<MovementItem> getMovements() { return movements; }

    public static final class SubjectItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Integer code;
        private final String name;
        private final boolean isPrincipal;

        public SubjectItem(Integer code, String name, boolean isPrincipal) {
            this.code = code;
            this.name = name;
            this.isPrincipal = isPrincipal;
        }
        public Integer getCode() { return code; }
        public String getName() { return name; }
        public boolean isPrincipal() { return isPrincipal; }
    }

    public static final class PartyItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String role; // ATIVO, PASSIVO, TERCEIRO, etc.
        private final String name;
        private final String documentNumber; // CPF ou CNPJ
        private final List<String> lawyers;  // Nomes ou OABs dos advogados

        public PartyItem(String role, String name, String documentNumber, List<String> lawyers) {
            this.role = role;
            this.name = name;
            this.documentNumber = documentNumber;
            this.lawyers = lawyers != null ? Collections.unmodifiableList(new ArrayList<>(lawyers)) : Collections.emptyList();
        }
        public String getRole() { return role; }
        public String getName() { return name; }
        public String getDocumentNumber() { return documentNumber; }
        public List<String> getLawyers() { return lawyers; }
    }

    public static final class MovementItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Integer code;
        private final String name;
        private final String dateTime;
        private final String complement;

        public MovementItem(Integer code, String name, String dateTime, String complement) {
            this.code = code;
            this.name = name;
            this.dateTime = dateTime;
            this.complement = complement;
        }
        public Integer getCode() { return code; }
        public String getName() { return name; }
        public String getDateTime() { return dateTime; }
        public String getComplement() { return complement; }
    }
}
