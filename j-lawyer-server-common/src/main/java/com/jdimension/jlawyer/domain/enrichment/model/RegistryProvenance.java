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
import java.util.HashMap;
import java.util.Map;

/**
 * Metadados de proveniência de dados cadastrais (Origem, Data, Cache, Confiança).
 *
 * @author BR-LAWYER Team
 */
public class RegistryProvenance implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum CacheStatus { HIT, MISS, EXPIRED, STALE, BYPASS }

    private String providerId;
    private String providerName;
    private String sourceUrlOrDescription;
    private Date consultedAt;
    private boolean cached;
    private CacheStatus cacheStatus = CacheStatus.MISS;
    private long cacheAgeSeconds;
    private double confidenceScore = 1.0; // 1.0 = Fonte Governamental Oficial
    private String rawDataPreview;
    private Map<String, RegistryFieldProvenance> fieldProvenanceMap = new HashMap<>();

    public RegistryProvenance() {
        this.consultedAt = new Date();
    }

    public RegistryProvenance(String providerId, String providerName, String sourceUrlOrDescription) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.sourceUrlOrDescription = sourceUrlOrDescription;
        this.consultedAt = new Date();
        this.cached = false;
        this.cacheStatus = CacheStatus.MISS;
    }

    public void addFieldProvenance(String fieldName, String value) {
        if (fieldName != null) {
            fieldProvenanceMap.put(fieldName, new RegistryFieldProvenance(fieldName, value, this.providerName));
        }
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getSourceUrlOrDescription() { return sourceUrlOrDescription; }
    public void setSourceUrlOrDescription(String sourceUrlOrDescription) { this.sourceUrlOrDescription = sourceUrlOrDescription; }

    public Date getConsultedAt() { return consultedAt; }
    public void setConsultedAt(Date consultedAt) { this.consultedAt = consultedAt; }

    public boolean isCached() { return cached; }
    public void setCached(boolean cached) { this.cached = cached; }

    public CacheStatus getCacheStatus() { return cacheStatus; }
    public void setCacheStatus(CacheStatus cacheStatus) { this.cacheStatus = cacheStatus; }

    public long getCacheAgeSeconds() { return cacheAgeSeconds; }
    public void setCacheAgeSeconds(long cacheAgeSeconds) { this.cacheAgeSeconds = cacheAgeSeconds; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getRawDataPreview() { return rawDataPreview; }
    public void setRawDataPreview(String rawDataPreview) { this.rawDataPreview = rawDataPreview; }

    public Map<String, RegistryFieldProvenance> getFieldProvenanceMap() { return fieldProvenanceMap; }
    public void setFieldProvenanceMap(Map<String, RegistryFieldProvenance> fieldProvenanceMap) { this.fieldProvenanceMap = fieldProvenanceMap; }
}
