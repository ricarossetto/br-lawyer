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
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Testes Unitários do Cliente de Discovery do DataJud e Parser do DJEN.
 *
 * @author BR-LAWYER Team
 */
public class DatajudProcessDiscoveryClientTest {

    private DatajudProcessDiscoveryClient client;
    private DjenPublicationParser djenParser;

    @Before
    public void setUp() {
        client = new DatajudProcessDiscoveryClient();
        djenParser = new DjenPublicationParser();
    }

    @Test
    public void testBuildSearchQueryPayload() {
        String cnj = "5001234-56.2023.4.04.7100";
        String payload = client.buildSearchQueryPayload(cnj);

        assertNotNull(payload);
        assertTrue("Deve conter a query match", payload.contains("\"numeroProcesso\": \"50012345620234047100\""));
    }

    @Test
    public void testResolveDatajudEndpoints() {
        // TRF4 (J=4, TR=04)
        CnjNumber cnjTrf4 = new CnjNumber("5001234", "56", 2023, 4, 4, "7100");
        String endpointTrf4 = client.resolveDatajudEndpoint(cnjTrf4);
        assertEquals("https://api-publica.datajud.cnj.jus.br/api_publica_trf4/_search", endpointTrf4);

        // TJSP (J=8, TR=26)
        CnjNumber cnjTjsp = new CnjNumber("1002345", "67", 2023, 8, 26, "0100");
        String endpointTjsp = client.resolveDatajudEndpoint(cnjTjsp);
        assertEquals("https://api-publica.datajud.cnj.jus.br/api_publica_tjsp/_search", endpointTjsp);

        // STJ (J=3)
        CnjNumber cnjStj = new CnjNumber("0001234", "89", 2023, 3, 0, "0000");
        String endpointStj = client.resolveDatajudEndpoint(cnjStj);
        assertEquals("https://api-publica.datajud.cnj.jus.br/api_publica_stj/_search", endpointStj);
    }

    @Test
    public void testParseDatajudJsonData() {
        Map<String, Object> mockSource = new HashMap<>();
        mockSource.put("numeroProcesso", "50012345620234047100");
        mockSource.put("tribunal", "TRF4");
        mockSource.put("grau", "G1");
        mockSource.put("dataAjuizamento", "2023-05-10T14:30:00.000Z");

        Map<String, Object> classeMap = new HashMap<>();
        classeMap.put("codigo", 7);
        classeMap.put("nome", "Procedimento Comum Cível");
        mockSource.put("classe", classeMap);

        Map<String, Object> orgaoMap = new HashMap<>();
        orgaoMap.put("nome", "1ª Vara Federal de Porto Alegre");
        mockSource.put("orgaoJulgador", orgaoMap);

        List<Map<String, Object>> assuntos = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("codigo", 6017);
        a1.put("nome", "Benefício Previdenciário");
        a1.put("principal", true);
        assuntos.add(a1);
        mockSource.put("assuntos", assuntos);

        List<Map<String, Object>> movimentos = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("codigo", 26);
        m1.put("nome", "Distribuído por sorteio");
        m1.put("dataHora", "2023-05-10T14:30:00.000Z");
        movimentos.add(m1);
        mockSource.put("movimentos", movimentos);

        DatajudProcessDetails details = client.parseProcessData(mockSource);

        assertNotNull(details);
        assertEquals("5001234-56.2023.4.04.7100", details.getCnjNumber());
        assertEquals("TRF4", details.getCourt());
        assertEquals("G1", details.getDegree());
        assertEquals(Integer.valueOf(7), details.getClassCode());
        assertEquals("Procedimento Comum Cível", details.getClassName());
        assertEquals("1ª Vara Federal de Porto Alegre", details.getJudgingBody());
        assertEquals(1, details.getSubjects().size());
        assertEquals(Integer.valueOf(6017), details.getSubjects().get(0).getCode());
        assertEquals(1, details.getMovements().size());
        assertEquals("Distribuído por sorteio", details.getMovements().get(0).getName());
    }

    @Test
    public void testDjenPublicationParser() {
        String rawText = "PODER JUDICIÁRIO - TRIBUNAL DE JUSTIÇA DE SÃO PAULO\n" +
                "Processo Digital nº: 1002345-10.2023.8.26.0100\n" +
                "Classe: Procedimento Comum Cível\n" +
                "Autor: João da Silva. Advogado: Dr. Carlos Advogado OAB/SP 123456.\n" +
                "Fica a parte autora intimada para manifestação sobre a contestação no prazo legal.";

        DjenPublicationParser.DjenParsedPublication parsed = djenParser.parse(
                "pub-123",
                rawText,
                "TJSP",
                "2023-08-15",
                Collections.singletonList("João da Silva")
        );

        assertNotNull(parsed);
        assertEquals("1002345-10.2023.8.26.0100", parsed.getCnjNumber());
        assertEquals("TJSP", parsed.getTribunal());
        assertEquals(1, parsed.getExtractedOabs().size());
        assertEquals("OAB/SP 123456", parsed.getExtractedOabs().get(0));
        assertEquals(1, parsed.getRecipients().size());
    }
}
