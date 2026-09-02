/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.e2e.fixtures;

import com.jdimension.jlawyer.domain.legal.model.*;
import java.util.*;

/**
 * Fixtures canônicas e dados de teste autoritativos para a suíte E2E do BR-LAWYER.
 *
 * @author BR-LAWYER Team
 */
public final class BrazilianLegalFixtures {

    private BrazilianLegalFixtures() {}

    // =========================================================================
    // 1. NPU / CNJ (Resolução CNJ nº 65/2008 - ISO 7064 Módulo 97-10)
    // =========================================================================
    public static final String VALID_CNJ_TJSP = "0001234-08.2023.8.26.0100";
    public static final String VALID_CNJ_TJSP_CLEAN = "00012340820238260100";

    public static final String VALID_CNJ_TRF3 = "5001234-03.2024.4.03.6100";
    public static final String VALID_CNJ_TRF3_CLEAN = "50012340320244036100";

    public static final String VALID_CNJ_TRT2 = "1000123-93.2023.5.02.0001";
    public static final String VALID_CNJ_TRT2_CLEAN = "10001239320235020001";

    public static final String VALID_CNJ_STF = "0000001-95.2020.1.00.0000";
    public static final String VALID_CNJ_STJ = "0000045-15.2021.3.00.0000";
    public static final String VALID_CNJ_TJRJ = "0005678-22.2022.8.19.0001";
    public static final String VALID_CNJ_TJMG = "5009876-76.2023.8.13.0024";

    public static final String[] ALL_VALID_CNJ = {
        VALID_CNJ_TJSP, VALID_CNJ_TRF3, VALID_CNJ_TRT2,
        VALID_CNJ_STF, VALID_CNJ_STJ, VALID_CNJ_TJRJ, VALID_CNJ_TJMG
    };

    // CNJs Inválidos
    public static final String INVALID_CNJ_BAD_DV_1 = "0001234-99.2023.8.26.0100";
    public static final String INVALID_CNJ_BAD_DV_2 = "5001234-00.2024.4.03.6100";
    public static final String INVALID_CNJ_SHORT = "0001234-08.2023.8.26";
    public static final String INVALID_CNJ_LONG = "0001234-08.2023.8.26.010099";
    public static final String INVALID_CNJ_SEGMENT_ZERO = "0001234-08.2023.0.26.0100";
    public static final String INVALID_CNJ_LETTERS = "0001234-AB.2023.8.26.0100";

    // =========================================================================
    // 2. CPF (Módulo 11)
    // =========================================================================
    public static final String VALID_CPF_1 = "111.444.777-35";
    public static final String VALID_CPF_1_CLEAN = "11144477735";

    public static final String VALID_CPF_2 = "529.982.247-25";
    public static final String VALID_CPF_2_CLEAN = "52998224725";

    public static final String VALID_CPF_3 = "123.456.789-09";
    public static final String VALID_CPF_3_CLEAN = "12345678909";

    public static final String[] ALL_VALID_CPF = {
        VALID_CPF_1, VALID_CPF_2, VALID_CPF_3
    };

    public static final String INVALID_CPF_REPEATED_1 = "111.111.111-11";
    public static final String INVALID_CPF_REPEATED_0 = "000.000.000-00";
    public static final String INVALID_CPF_BAD_DV = "123.456.789-00";
    public static final String INVALID_CPF_SHORT = "123.456.78";

    // =========================================================================
    // 3. CNPJ (Módulo 11 Tradicional + IN RFB 2229/2024 Alfanumérico)
    // =========================================================================
    public static final String VALID_CNPJ_BANCO_BRASIL = "00.000.000/0001-91";
    public static final String VALID_CNPJ_PETROBRAS = "33.000.167/0001-01";
    public static final String VALID_CNPJ_EMPRESA_3 = "04.524.238/0001-77";

    public static final String[] ALL_VALID_CNPJ = {
        VALID_CNPJ_BANCO_BRASIL, VALID_CNPJ_PETROBRAS, VALID_CNPJ_EMPRESA_3
    };

    public static final String INVALID_CNPJ_REPEATED = "11.111.111/1111-11";
    public static final String INVALID_CNPJ_BAD_DV = "00.000.000/0001-00";
    public static final String INVALID_CNPJ_SHORT = "33.000.167/0001";

    // =========================================================================
    // 4. CEP & Endereçamento
    // =========================================================================
    public static final String VALID_CEP_SP = "01001-000";
    public static final String VALID_CEP_SP_CLEAN = "01001000";
    public static final String VALID_CEP_RJ = "20040-002";
    public static final String VALID_CEP_DF = "70070-900";

    // =========================================================================
    // 5. OAB & Representação Jurídica
    // =========================================================================
    public static final String VALID_OAB_SP = "123456/SP";
    public static final String VALID_OAB_RJ_NUM = "456789";
    public static final String VALID_OAB_RJ_UF = "RJ";

    // =========================================================================
    // 6. Catálogo de Tribunais e Segmentos de Justiça
    // =========================================================================
    public static final List<JudiciaryCourtDTO> SAMPLE_COURTS = Arrays.asList(
        new JudiciaryCourtDTO("STF", "Supremo Tribunal Federal", 1, "DF"),
        new JudiciaryCourtDTO("STJ", "Superior Tribunal de Justiça", 3, "DF"),
        new JudiciaryCourtDTO("TRF3", "Tribunal Regional Federal da 3ª Região", 4, "SP"),
        new JudiciaryCourtDTO("TRT2", "Tribunal Regional do Trabalho da 2ª Região", 5, "SP"),
        new JudiciaryCourtDTO("TJSP", "Tribunal de Justiça do Estado de São Paulo", 8, "SP"),
        new JudiciaryCourtDTO("TJRJ", "Tribunal de Justiça do Estado do Rio de Janeiro", 8, "RJ")
    );

    // =========================================================================
    // 7. Tabelas Processuais Unificadas (TPU) - Classes e Assuntos
    // =========================================================================
    public static final TpuClassDTO TPU_PROCEDIMENTO_COMUM = new TpuClassDTO(7, "Procedimento Comum Cível");
    public static final TpuClassDTO TPU_EXECUCAO_TITULO = new TpuClassDTO(1116, "Execução de Título Extrajudicial");
    public static final TpuClassDTO TPU_RECLAMACAO_TRABALHISTA = new TpuClassDTO(985, "Ação Trabalhista - Rito Ordinário");
    public static final TpuClassDTO TPU_EXECUCAO_FISCAL = new TpuClassDTO(1117, "Execução Fiscal");

    public static final TpuSubjectDTO TPU_DANO_MORAL = new TpuSubjectDTO(10433, "Indenização por Dano Moral");
    public static final TpuSubjectDTO TPU_INADIMPLEMENTO = new TpuSubjectDTO(7780, "Inadimplemento");
    public static final TpuSubjectDTO TPU_HORAS_EXTRAS = new TpuSubjectDTO(2088, "Horas Extras");
    public static final TpuSubjectDTO TPU_ICMS = new TpuSubjectDTO(5966, "ICMS / Imposto sobre Circulação de Mercadorias");

    // =========================================================================
    // 8. Publicações Oficiais (DJEN / ComunicaAPI)
    // =========================================================================
    public static PublicationDetailDTO createSamplePublicationTJSP() {
        PublicationDetailDTO pub = new PublicationDetailDTO();
        pub.setId("pub-tjsp-001");
        pub.setCnjNumber(VALID_CNJ_TJSP);
        pub.setCnjNumberClean(VALID_CNJ_TJSP_CLEAN);
        pub.setCourtCode("TJSP");
        pub.setAvailabilityDate(new Date());
        pub.setPublicationDate(new Date(System.currentTimeMillis() + 86400000L));
        pub.setSource("DJEN");
        pub.setPublicationType("INTIMACAO");
        pub.setContent("Processo 0001234-08.2023.8.26.0100 - Fica a parte requerida intimada para apresentar Contestação no prazo de 15 (quinze) dias úteis, nos termos do art. 335 do CPC.");
        pub.setStatus("NEW");
        pub.setReadStatus("UNREAD");
        pub.setTreatmentStatus("UNTREATED");
        pub.setProcessId("case-tjsp-101");
        return pub;
    }

    public static PublicationDetailDTO createSamplePublicationTRT2() {
        PublicationDetailDTO pub = new PublicationDetailDTO();
        pub.setId("pub-trt2-002");
        pub.setCnjNumber(VALID_CNJ_TRT2);
        pub.setCnjNumberClean(VALID_CNJ_TRT2_CLEAN);
        pub.setCourtCode("TRT2");
        pub.setAvailabilityDate(new Date());
        pub.setPublicationDate(new Date());
        pub.setSource("DEJT");
        pub.setPublicationType("NOTIFICACAO");
        pub.setContent("Processo 1000123-93.2023.5.02.0001 - Ficam as partes notificadas do comparecimento à Audiência Una designada para 20/10/2026 às 14:00.");
        pub.setStatus("NEW");
        pub.setReadStatus("UNREAD");
        pub.setTreatmentStatus("UNTREATED");
        pub.setProcessId("case-trt2-202");
        return pub;
    }
}
