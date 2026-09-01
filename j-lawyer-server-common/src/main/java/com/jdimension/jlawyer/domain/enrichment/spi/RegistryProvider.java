/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.spi;

import com.jdimension.jlawyer.domain.enrichment.model.ProviderCapabilities;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;

/**
 * Interface base para provedores e adaptadores de fontes cadastrais brasileiras.
 *
 * @author BR-LAWYER Team
 */
public interface RegistryProvider {

    /**
     * Identificador unívoco do provedor (ex: "brasilapi", "viacep", "serpro-cpf", "mock").
     */
    String getProviderId();

    /**
     * Nome de exibição amigável para a interface de usuário.
     */
    String getDisplayName();

    /**
     * Retorna as capacidades declaradas pelo provedor.
     */
    ProviderCapabilities getCapabilities();

    /**
     * Executa um teste de conectividade e validação de credenciais.
     *
     * @param config Configuração ativa do provedor
     * @return true se o teste de conexão foi bem-sucedido
     * @throws Exception em caso de falha de conexão, timeout ou credencial inválida
     */
    boolean testConnection(ProviderConfig config) throws Exception;
}
