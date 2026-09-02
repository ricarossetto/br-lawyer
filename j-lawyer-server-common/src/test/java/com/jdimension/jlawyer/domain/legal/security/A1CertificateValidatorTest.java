/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.security;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Testes Unitários do Validador de Certificados Digitais ICP-Brasil A1.
 *
 * @author BR-LAWYER Team
 */
public class A1CertificateValidatorTest {

    private A1CertificateValidator validator;

    @Before
    public void setUp() {
        validator = new A1CertificateValidator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBytesThrowsException() throws Exception {
        validator.validateAndExtractInfo(null, "senha".toCharArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPasswordThrowsException() throws Exception {
        validator.validateAndExtractInfo(new byte[]{1, 2, 3}, null);
    }

    @Test(expected = Exception.class)
    public void testCorruptedBytesThrowsException() throws Exception {
        validator.validateAndExtractInfo(new byte[]{1, 2, 3, 4, 5}, "senha".toCharArray());
    }
}
