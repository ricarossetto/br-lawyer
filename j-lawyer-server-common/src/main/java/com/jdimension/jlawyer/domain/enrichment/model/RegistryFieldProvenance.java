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
 * Proveniência granular por campo cadastral (lineage).
 *
 * @author BR-LAWYER Team
 */
public class RegistryFieldProvenance implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String value;
    private String sourceProvider;
    private Date updatedAt;

    public RegistryFieldProvenance() {
        this.updatedAt = new Date();
    }

    public RegistryFieldProvenance(String fieldName, String value, String sourceProvider) {
        this.fieldName = fieldName;
        this.value = value;
        this.sourceProvider = sourceProvider;
        this.updatedAt = new Date();
    }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getSourceProvider() { return sourceProvider; }
    public void setSourceProvider(String sourceProvider) { this.sourceProvider = sourceProvider; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
