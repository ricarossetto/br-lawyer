/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.spi;

import com.jdimension.jlawyer.domain.enrichment.model.BankingInstitutionResult;
import com.jdimension.jlawyer.domain.enrichment.model.ProviderConfig;
import java.util.List;

/**
 * SPI para catálogo de bancos, códigos COMPE, ISPB e participantes PIX do BACEN.
 *
 * @author BR-LAWYER Team
 */
public interface BankingDirectoryProvider extends RegistryProvider {

    /**
     * Lista todas as instituições bancárias participantes do STR/COMPE/PIX.
     *
     * @param config Configuração do provedor
     * @return Lista de bancos
     * @throws Exception em caso de erro
     */
    List<BankingInstitutionResult> listBanks(ProviderConfig config) throws Exception;

    /**
     * Localiza instituição bancária por código COMPE (ex: "001") ou ISPB (ex: "00000000").
     *
     * @param codeOrIspb Código de 3 ou 8 dígitos
     * @param config Configuração do provedor
     * @return Instituição localizada ou null
     * @throws Exception em caso de erro
     */
    BankingInstitutionResult findBank(String codeOrIspb, ProviderConfig config) throws Exception;
}
