/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.discovery;

import com.jdimension.jlawyer.domain.legal.cnj.CnjNumber;
import com.jdimension.jlawyer.domain.legal.cnj.CnjNumberValidator;

import java.util.*;

/**
 * Cliente e Parser Canônico de Discovery Processual para a API Pública do DataJud (CNJ).
 *
 * Princípios de Design & Guardrails (ATRIUM / BR-LAWYER):
 * 1. Operação estritamente READ-ONLY (apenas consulta e estruturação de dados).
 * 2. Nunca inventa movimentos, classes ou partes ausentes na resposta oficial.
 * 3. Encadeamento Canônico: DJEN -> Número CNJ -> DataJud -> Processo + Contatos.
 *
 * @author BR-LAWYER Team
 */
public class DatajudProcessDiscoveryClient {

    private static final String DATAJUD_BASE_URL = "https://api-publica.datajud.cnj.jus.br";

    /**
     * Constrói o corpo JSON da requisição de busca (Query DSL) para a API do DataJud.
     *
     * @param cnjNumber Número do processo formatado ou em 20 dígitos numéricos
     * @return String JSON no formato esperado pelo ElasticSearch do DataJud
     */
    public String buildSearchQueryPayload(String cnjNumber) {
        if (cnjNumber == null || cnjNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Número CNJ não pode ser nulo ou vazio");
        }
        String cleanDigits = cnjNumber.replaceAll("\\D", "");
        if (cleanDigits.length() != 20) {
            throw new IllegalArgumentException("Número CNJ deve conter exatamente 20 dígitos: " + cnjNumber);
        }

        return "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"numeroProcesso\": \"" + cleanDigits + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    /**
     * Resolve o endpoint específico do DataJud para o tribunal correspondente ao número CNJ.
     * Estrutura CNJ: NNNNNNN-DD.AAAA.J.TR.OOOO onde J=Segmento e TR=Tribunal
     */
    public String resolveDatajudEndpoint(CnjNumber cnj) {
        if (cnj == null) {
            throw new IllegalArgumentException("CnjNumber não pode ser nulo");
        }

        int j = cnj.getJusticeSegment();
        int tr = cnj.getCourtNumber();

        String apiAlias;
        switch (j) {
            case 1: // STF
                apiAlias = "api_publica_stf";
                break;
            case 2: // CNJ
                apiAlias = "api_publica_cnj";
                break;
            case 3: // STJ
                apiAlias = "api_publica_stj";
                break;
            case 4: // Justiça Federal (TRF1 a TRF6)
                apiAlias = "api_publica_trf" + tr;
                break;
            case 5: // Justiça do Trabalho (TRT1 a TRT24, TST)
                apiAlias = (tr == 0) ? "api_publica_tst" : "api_publica_trt" + tr;
                break;
            case 8: // Justiça Estadual (TJSP, TJRJ, TJMG, TJRS, etc.)
                apiAlias = "api_publica_tj" + resolveStateAbbreviation(tr);
                break;
            default:
                apiAlias = "api_publica_tjsp";
                break;
        }

        return DATAJUD_BASE_URL + "/" + apiAlias + "/_search";
    }

    /**
     * Mapeia o código TR da Justiça Estadual (J=8) para a sigla da respectiva UF.
     */
    public String resolveStateAbbreviation(int tr) {
        switch (tr) {
            case 1: return "ac";
            case 2: return "al";
            case 3: return "ap";
            case 4: return "am";
            case 5: return "ba";
            case 6: return "ce";
            case 7: return "df";
            case 8: return "es";
            case 9: return "go";
            case 10: return "ma";
            case 11: return "mt";
            case 12: return "ms";
            case 13: return "mg";
            case 14: return "pa";
            case 15: return "pb";
            case 16: return "pr";
            case 17: return "pe";
            case 18: return "pi";
            case 19: return "rj";
            case 20: return "rn";
            case 21: return "rs";
            case 22: return "ro";
            case 23: return "rr";
            case 24: return "sc";
            case 25: return "se";
            case 26: return "sp";
            case 27: return "to";
            default: return "sp";
        }
    }

    /**
     * Realiza a extração estruturada de metadados a partir de um mapa de dados decodificado do JSON do DataJud.
     */
    public DatajudProcessDetails parseProcessData(Map<String, Object> sourceMap) {
        if (sourceMap == null || sourceMap.isEmpty()) {
            return null;
        }

        String rawCnj = (String) sourceMap.get("numeroProcesso");
        String cnjFormatted = CnjNumberValidator.format(rawCnj);
        String court = (String) sourceMap.get("tribunal");
        String degree = (String) sourceMap.get("grau");
        String filingDate = (String) sourceMap.get("dataAjuizamento");

        // Classe Processual
        Integer classCode = null;
        String className = null;
        Object classeObj = sourceMap.get("classe");
        if (classeObj instanceof Map) {
            Map<?, ?> classeMap = (Map<?, ?>) classeObj;
            Object codeObj = classeMap.get("codigo");
            if (codeObj instanceof Number) {
                classCode = ((Number) codeObj).intValue();
            }
            className = (String) classeMap.get("nome");
        }

        // Órgão Julgador
        String judgingBody = null;
        Object orgaoObj = sourceMap.get("orgaoJulgador");
        if (orgaoObj instanceof Map) {
            judgingBody = (String) ((Map<?, ?>) orgaoObj).get("nome");
        }

        // Assuntos TPU
        List<DatajudProcessDetails.SubjectItem> subjects = new ArrayList<>();
        Object assuntosObj = sourceMap.get("assuntos");
        if (assuntosObj instanceof List) {
            List<?> list = (List<?>) assuntosObj;
            for (Object item : list) {
                if (item instanceof Map) {
                    Map<?, ?> itemMap = (Map<?, ?>) item;
                    Integer sCode = null;
                    Object scObj = itemMap.get("codigo");
                    if (scObj instanceof Number) sCode = ((Number) scObj).intValue();
                    String sName = (String) itemMap.get("nome");
                    Boolean isPrinc = (Boolean) itemMap.get("principal");
                    subjects.add(new DatajudProcessDetails.SubjectItem(sCode, sName, isPrinc != null && isPrinc));
                }
            }
        }

        // Movimentos Processuais
        List<DatajudProcessDetails.MovementItem> movements = new ArrayList<>();
        Object movimentosObj = sourceMap.get("movimentos");
        if (movimentosObj instanceof List) {
            List<?> list = (List<?>) movimentosObj;
            for (Object item : list) {
                if (item instanceof Map) {
                    Map<?, ?> itemMap = (Map<?, ?>) item;
                    Integer mCode = null;
                    Object mcObj = itemMap.get("codigo");
                    if (mcObj instanceof Number) mCode = ((Number) mcObj).intValue();
                    String mName = (String) itemMap.get("nome");
                    String mDate = (String) itemMap.get("dataHora");
                    String complement = null;
                    movements.add(new DatajudProcessDetails.MovementItem(mCode, mName, mDate, complement));
                }
            }
        }

        // Polos e Partes
        List<DatajudProcessDetails.PartyItem> parties = new ArrayList<>();
        Object polosObj = sourceMap.get("polos");
        if (polosObj instanceof List) {
            List<?> list = (List<?>) polosObj;
            for (Object p : list) {
                if (p instanceof Map) {
                    Map<?, ?> poloMap = (Map<?, ?>) p;
                    String poloType = (String) poloMap.get("polo"); // ATIVO, PASSIVO
                    Object partesObj = poloMap.get("partes");
                    if (partesObj instanceof List) {
                        for (Object parte : (List<?>) partesObj) {
                            if (parte instanceof Map) {
                                Map<?, ?> parteMap = (Map<?, ?>) parte;
                                String pName = (String) parteMap.get("nome");
                                String pDoc = (String) parteMap.get("numeroDocumentoPrincipal");
                                List<String> lawyers = new ArrayList<>();
                                Object advsObj = parteMap.get("advogados");
                                if (advsObj instanceof List) {
                                    for (Object adv : (List<?>) advsObj) {
                                        if (adv instanceof Map) {
                                            String advName = (String) ((Map<?, ?>) adv).get("nome");
                                            if (advName != null) lawyers.add(advName);
                                        }
                                    }
                                }
                                parties.add(new DatajudProcessDetails.PartyItem(poloType, pName, pDoc, lawyers));
                            }
                        }
                    }
                }
            }
        }

        return new DatajudProcessDetails(
                cnjFormatted,
                court,
                degree,
                classCode,
                className,
                judgingBody,
                filingDate,
                subjects,
                parties,
                movements
        );
    }
}
