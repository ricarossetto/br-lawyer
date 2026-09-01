/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.matching;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes unitários para o normalizador de nomes empresariais e pessoas físicas.
 *
 * @author BR-LAWYER Team
 */
public class LegalEntityNormalizerTest {

    @Test
    public void testNormalizeCompanyName() {
        String raw = "ACME Comércio e Serviços de Tecnologia LTDA - ME";
        String normalized = LegalEntityNormalizer.normalizeCompanyName(raw);
        assertEquals("ACME TECNOLOGIA", normalized);

        String raw2 = "SILVA & SANTOS SOCIEDADE DE ADVOGADOS - EIRELI";
        String normalized2 = LegalEntityNormalizer.normalizeCompanyName(raw2);
        assertEquals("SILVA SANTOS ADVOGADOS", normalized2);

        String raw3 = "BANCO DO BRASIL S/A";
        String normalized3 = LegalEntityNormalizer.normalizeCompanyName(raw3);
        assertEquals("BANCO BRASIL", normalized3);
    }

    @Test
    public void testNormalizePersonName() {
        String raw = "  José   Antônio   d'Ávila   ";
        String norm = LegalEntityNormalizer.normalizePersonName(raw);
        assertEquals("JOSE ANTONIO D AVILA", norm);
    }
}
