/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.providers;

import com.jdimension.jlawyer.domain.enrichment.model.*;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Testes unitários para o provedor de dados sintéticos Mock.
 *
 * @author BR-LAWYER Team
 */
public class MockRegistryProviderTest {

    @Test
    public void testMockCompanyLookup() throws Exception {
        MockRegistryProvider provider = new MockRegistryProvider();
        ProviderConfig config = new ProviderConfig("mock", "Mock Provider", true, 1, null);

        CompanyRegistryResult company = provider.lookupCompany("00000000000191", config);
        assertNotNull(company);
        assertEquals("00.000.000/0001-91", company.getCnpj());
        assertEquals("EMPRESA MODELO BRASILEIRA LTDA", company.getLegalName());
        assertEquals(CompanyRegistryResult.CorporateStatus.ATIVA, company.getStatus());
        assertNotNull(company.getAddress());
        assertEquals("01310-100", company.getAddress().getCep());
        assertEquals("3550308", company.getAddress().getIbgeCityCode());

        assertNotNull(company.getMembers());
        assertEquals(2, company.getMembers().size());
        assertEquals("CARLOS EDUARDO SILVA", company.getMembers().get(0).getName());
        assertEquals("Sócio-Administrador", company.getMembers().get(0).getQualificationDescription());
    }

    @Test
    public void testMockPersonLookup() throws Exception {
        MockRegistryProvider provider = new MockRegistryProvider();
        ProviderConfig config = new ProviderConfig("mock", "Mock Provider", true, 1, null);

        PersonRegistryResult person = provider.lookupPerson("12345678909", null, config);
        assertNotNull(person);
        assertEquals("ROBERTO ALVES DE OLIVEIRA", person.getFullName());
        assertEquals(PersonRegistryResult.PersonStatus.REGULAR, person.getStatus());
    }

    @Test
    public void testMockAddressLookup() throws Exception {
        MockRegistryProvider provider = new MockRegistryProvider();
        ProviderConfig config = new ProviderConfig("mock", "Mock Provider", true, 1, null);

        AddressResult address = provider.lookupAddress("01310100", config);
        assertNotNull(address);
        assertEquals("01310-100", address.getCep());
        assertEquals("Avenida Paulista", address.getStreet());
        assertEquals("São Paulo", address.getCity());
        assertEquals("SP", address.getState());
        assertEquals("3550308", address.getIbgeCityCode());
    }

    @Test
    public void testMockBankingLookup() {
        MockRegistryProvider provider = new MockRegistryProvider();
        ProviderConfig config = new ProviderConfig("mock", "Mock Provider", true, 1, null);

        List<BankingInstitutionResult> banks = provider.listBanks(config);
        assertNotNull(banks);
        assertTrue(banks.size() >= 10);

        BankingInstitutionResult bb = provider.findBank("001", config);
        assertNotNull(bb);
        assertEquals("00000000", bb.getIspb());
        assertTrue(bb.isPixParticipant());
    }

    @Test
    public void testMockProfessionalLookup() throws Exception {
        MockRegistryProvider provider = new MockRegistryProvider();
        ProviderConfig config = new ProviderConfig("mock", "Mock Provider", true, 1, null);

        ProfessionalRegistrationResult oab = provider.lookupProfessional("123456", "SP", config);
        assertNotNull(oab);
        assertEquals("123456", oab.getRegistrationNumber());
        assertEquals("SP", oab.getState());
        assertEquals("OAB/SP 123456", oab.getFormattedOab());
        assertEquals(ProfessionalRegistrationResult.ProfessionalStatus.REGULAR, oab.getStatus());
    }
}
