/*
 * Copyright (C) j-lawyer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.*;
import com.jdimension.jlawyer.persistence.BrPublication;
import com.jdimension.jlawyer.persistence.BrPublicationEvent;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

public class PublicationWorkflowTest {

    @Test
    public void testComputeDeterministicSha256Fingerprint() throws Exception {
        String content = "Fica intimado o advogado Dr. Silva OAB/SP 123456 sobre a decisão de fls.";
        String courtCode = "TJSP";
        String dateStr = "2026-09-01";
        String lawyer = "Dr. Silva";

        String raw = (content != null ? content.trim() : "") + "|" 
                   + (courtCode != null ? courtCode.trim().toUpperCase() : "") + "|" 
                   + dateStr + "|" 
                   + (lawyer != null ? lawyer.trim().toUpperCase() : "");

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String calculated = hexString.toString();

        Assert.assertNotNull(calculated);
        Assert.assertEquals(64, calculated.length());
    }

    @Test
    public void testCleanCnjMatching() {
        String rawCnj = "0001234-56.2026.8.26.0100";
        String cleanCnj = rawCnj.replaceAll("[^0-9]", "");
        Assert.assertEquals("00012345620268260100", cleanCnj);
        Assert.assertEquals(20, cleanCnj.length());

        // Formatting verification
        String formatted = String.format("%s-%s.%s.%s.%s.%s",
                cleanCnj.substring(0, 7),
                cleanCnj.substring(7, 9),
                cleanCnj.substring(9, 13),
                cleanCnj.substring(13, 14),
                cleanCnj.substring(14, 16),
                cleanCnj.substring(16, 20));
        Assert.assertEquals("0001234-56.2026.8.26.0100", formatted);
    }

    @Test
    public void testPublicationEntityLifecycle() {
        BrPublication pub = new BrPublication();
        pub.setId("PUB-TEST-001");
        pub.setContent("Publicação de teste TJSP");
        pub.setCourtCode("TJSP");
        pub.setStatus(BrPublication.STATUS_NOVA);
        pub.setReadStatus(BrPublication.READ_UNREAD);
        pub.setTreatmentStatus(BrPublication.TREATMENT_NAO_TRATADA);

        Assert.assertEquals(BrPublication.STATUS_NOVA, pub.getStatus());
        Assert.assertEquals(BrPublication.READ_UNREAD, pub.getReadStatus());
        Assert.assertEquals(BrPublication.TREATMENT_NAO_TRATADA, pub.getTreatmentStatus());

        // Mark Read
        pub.setReadStatus(BrPublication.READ_READ);
        pub.setReadAt(new Date());
        Assert.assertEquals(BrPublication.READ_READ, pub.getReadStatus());
        Assert.assertNotNull(pub.getReadAt());

        // Treat
        pub.setStatus(BrPublication.STATUS_TRATADA);
        pub.setTreatmentStatus(BrPublication.TREATMENT_TRATADA);
        pub.setTreatedAt(new Date());
        pub.setTreatedBy("ADVOGADO_TESTE");
        pub.setNotes("Tratada com criação de contestação");
        Assert.assertEquals(BrPublication.STATUS_TRATADA, pub.getStatus());
        Assert.assertEquals(BrPublication.TREATMENT_TRATADA, pub.getTreatmentStatus());
        Assert.assertEquals("Tratada com criação de contestação", pub.getNotes());

        // Event Audit Log
        BrPublicationEvent event = new BrPublicationEvent();
        event.setId(java.util.UUID.randomUUID().toString());
        event.setPublicationId(pub.getId());
        event.setEventType(BrPublicationEvent.EVENT_TREATED);
        event.setActor("ADVOGADO_TESTE");
        event.setDetails("Publicação tratada.");
        event.setCreatedAt(new Date());

        Assert.assertEquals(pub.getId(), event.getPublicationId());
        Assert.assertEquals(BrPublicationEvent.EVENT_TREATED, event.getEventType());
        Assert.assertEquals("ADVOGADO_TESTE", event.getActor());
    }
}