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
 * Testes unitários para o codificador fonético do português brasileiro (Metaphone-PT).
 *
 * @author BR-LAWYER Team
 */
public class PortugueseMetaphoneTest {

    @Test
    public void testPhoneticEquivalence() {
        // Variações comuns que devem gerar chaves fonéticas idênticas
        assertEquals(PortugueseMetaphone.encode("Souza"), PortugueseMetaphone.encode("Sousa"));
        assertEquals(PortugueseMetaphone.encode("Luiz"), PortugueseMetaphone.encode("Luís"));
        assertEquals(PortugueseMetaphone.encode("Tereza"), PortugueseMetaphone.encode("Theresa"));
        assertEquals(PortugueseMetaphone.encode("Felipe"), PortugueseMetaphone.encode("Philippe"));
        assertEquals(PortugueseMetaphone.encode("Lucas"), PortugueseMetaphone.encode("Lukas"));
        assertEquals(PortugueseMetaphone.encode("Cícero"), PortugueseMetaphone.encode("Sícero"));
    }

    @Test
    public void testCompoundPhrase() {
        String p1 = PortugueseMetaphone.encodePhrase("João da Silva Souza");
        String p2 = PortugueseMetaphone.encodePhrase("Joao da Silva Sousa");
        assertEquals(p1, p2);
    }

    @Test
    public void testEmptyAndNull() {
        assertEquals("", PortugueseMetaphone.encode(null));
        assertEquals("", PortugueseMetaphone.encode("   "));
    }
}
