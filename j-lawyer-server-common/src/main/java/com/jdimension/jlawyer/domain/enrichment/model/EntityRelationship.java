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
import java.util.Date;

/**
 * Modelo de relacionamento formal entre entidades (PF e PJ) no BR-LAWYER:
 * SÓCIO_DE, ADMINISTRADOR_DE, REPRESENTANTE_LEGAL_DE, FILIAL_DE, MATRIZ_DE, PARTE_CONTRARIA, ADVOGADO_DE.
 *
 * @author BR-LAWYER Team
 */
public class EntityRelationship implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum RelationshipType {
        SOCIO_DE,
        ADMINISTRADOR_DE,
        REPRESENTANTE_LEGAL_DE,
        FILIAL_DE,
        MATRIZ_DE,
        ADVOGADO_DE,
        PARTE_CONTRARIA,
        OUTRO
    }

    private String id;
    private String sourceAddressId;      // ID do contato de origem (ex: Sócio PF)
    private String targetAddressId;      // ID do contato de destino (ex: Empresa PJ)
    private String sourceName;           // Nome denormalizado para consulta rápida
    private String targetName;           // Nome da empresa de destino
    private String sourceIdentifier;     // CPF ou CNPJ da origem
    private String targetIdentifier;     // CNPJ do destino
    private RelationshipType relationshipType = RelationshipType.SOCIO_DE;
    private String roleDescription;      // Ex: "Sócio-Administrador com 60% do capital"
    private Double capitalPercentage;
    private Date sinceDate;
    private RegistryProvenance provenance;

    public EntityRelationship() {
        this.provenance = new RegistryProvenance();
    }

    public EntityRelationship(String sourceAddressId, String targetAddressId, RelationshipType relationshipType, String roleDescription) {
        this.sourceAddressId = sourceAddressId;
        this.targetAddressId = targetAddressId;
        this.relationshipType = relationshipType;
        this.roleDescription = roleDescription;
        this.provenance = new RegistryProvenance();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSourceAddressId() { return sourceAddressId; }
    public void setSourceAddressId(String sourceAddressId) { this.sourceAddressId = sourceAddressId; }

    public String getTargetAddressId() { return targetAddressId; }
    public void setTargetAddressId(String targetAddressId) { this.targetAddressId = targetAddressId; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }

    public String getTargetIdentifier() { return targetIdentifier; }
    public void setTargetIdentifier(String targetIdentifier) { this.targetIdentifier = targetIdentifier; }

    public RelationshipType getRelationshipType() { return relationshipType; }
    public void setRelationshipType(RelationshipType relationshipType) { this.relationshipType = relationshipType; }

    public String getRoleDescription() { return roleDescription; }
    public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }

    public Double getCapitalPercentage() { return capitalPercentage; }
    public void setCapitalPercentage(Double capitalPercentage) { this.capitalPercentage = capitalPercentage; }

    public Date getSinceDate() { return sinceDate; }
    public void setSinceDate(Date sinceDate) { this.sinceDate = sinceDate; }

    public RegistryProvenance getProvenance() { return provenance; }
    public void setProvenance(RegistryProvenance provenance) { this.provenance = provenance; }
}
