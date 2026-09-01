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

/**
 * Configuração de conectividade e parâmetros de provedor de dados cadastrais.
 *
 * @author BR-LAWYER Team
 */
public class ProviderConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String providerId;
    private String displayName;
    private boolean enabled = true;
    private int priority = 1;               // 1 = Primário, 2 = Secundário/Fallback
    private String baseUrl;
    private String apiKey;                  // Token / Consumer Key
    private String apiSecret;               // Consumer Secret (nunca enviado ao cliente desnecessariamente)
    private int timeoutMs = 5000;           // Timeout de conexão / leitura (5s)
    private int cacheTtlMinutes = 1440;     // TTL padrão: 24h
    private int maxRetries = 2;             // Tentativas com exponential backoff

    public ProviderConfig() {}

    public ProviderConfig(String providerId, String displayName, boolean enabled, int priority, String baseUrl) {
        this.providerId = providerId;
        this.displayName = displayName;
        this.enabled = enabled;
        this.priority = priority;
        this.baseUrl = baseUrl;
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }

    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }

    public int getCacheTtlMinutes() { return cacheTtlMinutes; }
    public void setCacheTtlMinutes(int cacheTtlMinutes) { this.cacheTtlMinutes = cacheTtlMinutes; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
}
