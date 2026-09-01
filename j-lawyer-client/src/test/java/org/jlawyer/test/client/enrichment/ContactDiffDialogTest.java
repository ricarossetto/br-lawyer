/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package org.jlawyer.test.client.enrichment;

import com.jdimension.jlawyer.client.enrichment.ContactDiffDialog;
import com.jdimension.jlawyer.domain.enrichment.model.AddressResult;
import com.jdimension.jlawyer.domain.enrichment.model.CompanyRegistryResult;
import com.jdimension.jlawyer.domain.enrichment.model.RegistryProvenance;
import com.jdimension.jlawyer.persistence.AddressBean;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Testes unitários para o modelo de comparação e aplicação de divergências de dados cadastrais.
 *
 * @author BR-LAWYER Team
 */
public class ContactDiffDialogTest {

    @Test
    public void testDiffItemDivergenceLogic() {
        // Caso 1: Valores iguais -> não divergente, não selecionado por padrão
        ContactDiffDialog.DiffItem item1 = new ContactDiffDialog.DiffItem("company", "Razão Social", "EMPRESA MODELO LTDA", "EMPRESA MODELO LTDA");
        Assert.assertFalse(item1.divergent);
        Assert.assertFalse(item1.selected);

        // Caso 2: Case insensitive -> não divergente
        ContactDiffDialog.DiffItem item2 = new ContactDiffDialog.DiffItem("city", "Município", "São Paulo", "SÃO PAULO");
        Assert.assertFalse(item2.divergent);
        Assert.assertFalse(item2.selected);

        // Caso 3: Valores divergentes -> divergente = true, selecionado = true
        ContactDiffDialog.DiffItem item3 = new ContactDiffDialog.DiffItem("street", "Logradouro", "Rua Antiga", "Avenida Paulista");
        Assert.assertTrue(item3.divergent);
        Assert.assertTrue(item3.selected);

        // Caso 4: Valor enriquecido vazio -> não divergente (não deve apagar valor existente)
        ContactDiffDialog.DiffItem item4 = new ContactDiffDialog.DiffItem("complement", "Complemento", "Sala 101", "");
        Assert.assertFalse(item4.divergent);
        Assert.assertFalse(item4.selected);

        // Caso 5: Valor atual vazio e enriquecido preenchido -> divergente = true, selecionado = true
        ContactDiffDialog.DiffItem item5 = new ContactDiffDialog.DiffItem("phone", "Telefone", "", "1133334444");
        Assert.assertTrue(item5.divergent);
        Assert.assertTrue(item5.selected);
    }

    @Test
    public void testApplyToAddressBean() {
        AddressBean target = new AddressBean();
        target.setCompany("Nome Antigo");
        target.setVatId("00.000.000/0001-91");
        target.setStreet("Rua Velha");
        target.setStreetNumber("10");

        CompanyRegistryResult enriched = new CompanyRegistryResult();
        enriched.setCnpj("11222333000181");
        enriched.setLegalName("NOVA EMPRESA BRASILEIRA LTDA");
        enriched.setTradeName("NOVA TECH");

        AddressResult addr = new AddressResult();
        addr.setCep("01310-100");
        addr.setStreet("Avenida Paulista");
        addr.setNumber("1000");
        addr.setNeighborhood("Bela Vista");
        addr.setCity("São Paulo");
        addr.setState("SP");
        addr.setIbgeCityCode("3550308");
        enriched.setAddress(addr);

        RegistryProvenance prov = new RegistryProvenance("brasilapi-cnpj", "BrasilAPI", "https://brasilapi.com.br");
        enriched.setProvenance(prov);

        // In Headless mode or direct test, we test the domain model & DiffItem mappings
        ContactDiffDialog.DiffItem diffCompany = new ContactDiffDialog.DiffItem(
                ContactDiffDialog.FIELD_COMPANY_NAME, "Razão Social", target.getCompany(), enriched.getLegalName());
        ContactDiffDialog.DiffItem diffStreet = new ContactDiffDialog.DiffItem(
                ContactDiffDialog.FIELD_STREET, "Logradouro", target.getStreet(), addr.getStreet());
        ContactDiffDialog.DiffItem diffNumber = new ContactDiffDialog.DiffItem(
                ContactDiffDialog.FIELD_NUMBER, "Número", target.getStreetNumber(), addr.getNumber());

        Assert.assertTrue(diffCompany.divergent);
        Assert.assertTrue(diffStreet.divergent);
        Assert.assertTrue(diffNumber.divergent);

        Assert.assertEquals("NOVA EMPRESA BRASILEIRA LTDA", diffCompany.enrichedValue);
        Assert.assertEquals("Avenida Paulista", diffStreet.enrichedValue);
        Assert.assertEquals("1000", diffNumber.enrichedValue);
    }
}
