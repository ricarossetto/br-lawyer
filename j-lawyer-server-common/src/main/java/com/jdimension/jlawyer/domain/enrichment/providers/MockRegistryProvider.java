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
import com.jdimension.jlawyer.domain.enrichment.spi.*;
import com.jdimension.jlawyer.domain.legal.cnj.BrazilianDocumentValidator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Provedor de dados cadastrais sintéticos para testes automatizados e ambiente offline.
 * Gera dados matematicamente válidos para CNPJ, CPF, CEP, OAB e Bancos sem realizar chamadas de rede.
 *
 * @author BR-LAWYER Team
 */
public class MockRegistryProvider implements CompanyRegistryProvider, PersonRegistryProvider, AddressRegistryProvider, ProfessionalRegistryProvider, BankingDirectoryProvider {

    public static final String PROVIDER_ID = "mock";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayName() {
        return "Provedor Sintético / Mock (Offline & Testes)";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        ProviderCapabilities cap = new ProviderCapabilities();
        cap.setSupportsCpf(true);
        cap.setSupportsCnpj(true);
        cap.setSupportsQsa(true);
        cap.setSupportsAddress(true);
        cap.setSupportsCnae(true);
        cap.setSupportsCorporateStatus(true);
        cap.setSupportsProfessionalRegistration(true);
        cap.setSupportsBanking(true);
        cap.setRequiresCredentials(false);
        cap.setSelfHostable(true);
        cap.setOfficialGovSource(false);
        return cap;
    }

    @Override
    public boolean testConnection(ProviderConfig config) {
        return true;
    }

    @Override
    public CompanyRegistryResult lookupCompany(String cnpj, ProviderConfig config) throws Exception {
        String clean = cnpj != null ? cnpj.replaceAll("[^a-zA-Z0-9]", "").toUpperCase() : "00000000000191";
        
        CompanyRegistryResult res = new CompanyRegistryResult();
        res.setCnpj(BrazilianDocumentValidator.formatCnpj(clean));
        res.setCleanCnpj(clean);
        res.setLegalName("EMPRESA MODELO BRASILEIRA LTDA");
        res.setTradeName("MODELO TECNOLOGIA");
        res.setStatus(CompanyRegistryResult.CorporateStatus.ATIVA);
        res.setStatusDescription("ATIVA");
        res.setStatusDate(DATE_FORMAT.parse("2010-05-15"));
        res.setOpeningDate(DATE_FORMAT.parse("2010-05-15"));
        res.setEstablishmentType(clean.endsWith("000191") ? CompanyRegistryResult.EstablishmentType.MATRIZ : CompanyRegistryResult.EstablishmentType.FILIAL);
        res.setLegalNatureCode("2062");
        res.setLegalNatureDescription("Sociedade Empresária Limitada");
        res.setCompanySize("EPP");
        res.setCapitalSocial(new BigDecimal("250000.00"));
        res.setSimplesOptant(true);
        res.setSimplesOptionDate(DATE_FORMAT.parse("2015-01-01"));
        res.setMeiopting(false);
        res.setMainCnaeCode("6201501");
        res.setMainCnaeDescription("Desenvolvimento de programas de computador sob encomenda");

        List<CompanyRegistryResult.CnaeEntry> secCnaes = new ArrayList<>();
        secCnaes.add(new CompanyRegistryResult.CnaeEntry("6202300", "Desenvolvimento e licenciamento de programas de computador customizáveis"));
        secCnaes.add(new CompanyRegistryResult.CnaeEntry("6209100", "Suporte técnico, manutenção e outros serviços em tecnologia da informação"));
        res.setSecondaryCnaes(secCnaes);

        AddressResult addr = new AddressResult();
        addr.setCep("01310-100");
        addr.setStreet("Avenida Paulista");
        addr.setStreetType("Avenida");
        addr.setNumber("1000");
        addr.setComplement("Andar 14");
        addr.setNeighborhood("Bela Vista");
        addr.setCity("São Paulo");
        addr.setState("SP");
        addr.setIbgeCityCode("3550308");
        addr.setDdd("11");
        addr.setLatitude(-23.561414);
        addr.setLongitude(-46.655881);
        addr.getProvenance().setProviderId(PROVIDER_ID);
        addr.getProvenance().setProviderName(getDisplayName());
        res.setAddress(addr);

        res.setPhones(Arrays.asList("(11) 3456-7890", "(11) 98765-4321"));
        res.setEmails(Arrays.asList("contato@modelotecnologia.com.br", "fiscal@modelotecnologia.com.br"));

        // QSA com sócios e administradores
        List<CompanyMemberResult> members = new ArrayList<>();
        
        CompanyMemberResult m1 = new CompanyMemberResult();
        m1.setName("CARLOS EDUARDO SILVA");
        m1.setIdentifier("***.456.789-**");
        m1.setMemberType(CompanyMemberResult.MemberType.PESSOA_FISICA);
        m1.setQualificationCode("49");
        m1.setQualificationDescription("Sócio-Administrador");
        m1.setAgeGroup("ENTRE_41_A_50");
        m1.setEntryDate(DATE_FORMAT.parse("2010-05-15"));
        m1.setCapitalPercentage(60.0);
        members.add(m1);

        CompanyMemberResult m2 = new CompanyMemberResult();
        m2.setName("ANA PAULA PEREIRA DE SOUZA");
        m2.setIdentifier("***.123.987-**");
        m2.setMemberType(CompanyMemberResult.MemberType.PESSOA_FISICA);
        m2.setQualificationCode("22");
        m2.setQualificationDescription("Sócio");
        m2.setAgeGroup("ENTRE_31_A_40");
        m2.setEntryDate(DATE_FORMAT.parse("2015-08-20"));
        m2.setCapitalPercentage(40.0);
        members.add(m2);

        res.setMembers(members);

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "Synthetic Fixture Engine");
        prov.setConfidenceScore(1.0);
        prov.addFieldProvenance("legalName", res.getLegalName());
        prov.addFieldProvenance("status", res.getStatusDescription());
        prov.addFieldProvenance("address", addr.getFullAddress());
        res.setProvenance(prov);

        return res;
    }

    @Override
    public PersonRegistryResult lookupPerson(String cpf, Date birthDate, ProviderConfig config) throws Exception {
        String clean = cpf != null ? cpf.replaceAll("[^0-9]", "") : "12345678909";

        PersonRegistryResult res = new PersonRegistryResult();
        res.setCpf(BrazilianDocumentValidator.formatCpf(clean));
        res.setCleanCpf(clean);
        res.setFullName("ROBERTO ALVES DE OLIVEIRA");
        res.setSocialName("");
        res.setBirthDate(birthDate != null ? birthDate : DATE_FORMAT.parse("1985-03-22"));
        res.setStatus(PersonRegistryResult.PersonStatus.REGULAR);
        res.setStatusDescription("REGULAR");
        res.setStatusDate(new Date());
        res.setMotherName("TERESA ALVES DE OLIVEIRA");
        res.setNationality("BRASILEIRA");
        res.setGender("MASCULINO");
        res.setResidentAbroad(false);

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "Synthetic Fixture Engine");
        prov.setConfidenceScore(1.0);
        prov.addFieldProvenance("fullName", res.getFullName());
        prov.addFieldProvenance("status", res.getStatusDescription());
        res.setProvenance(prov);

        return res;
    }

    @Override
    public AddressResult lookupAddress(String cep, ProviderConfig config) throws Exception {
        String clean = cep != null ? cep.replaceAll("[^0-9]", "") : "01310100";

        AddressResult addr = new AddressResult();
        addr.setCep(BrazilianDocumentValidator.formatCep(clean));
        addr.setStreet("Avenida Paulista");
        addr.setStreetType("Avenida");
        addr.setNeighborhood("Bela Vista");
        addr.setCity("São Paulo");
        addr.setState("SP");
        addr.setIbgeCityCode("3550308");
        addr.setDdd("11");
        addr.setSiafiCode("7107");
        addr.setLatitude(-23.561414);
        addr.setLongitude(-46.655881);

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "Synthetic Fixture Engine");
        prov.setConfidenceScore(1.0);
        addr.setProvenance(prov);

        return addr;
    }

    @Override
    public ProfessionalRegistrationResult lookupProfessional(String registrationNumber, String state, ProviderConfig config) throws Exception {
        String cleanNum = registrationNumber != null ? registrationNumber.replaceAll("[^0-9]", "") : "123456";
        String uf = state != null ? state.toUpperCase().trim() : "SP";

        ProfessionalRegistrationResult res = new ProfessionalRegistrationResult();
        res.setRegistrationNumber(cleanNum);
        res.setState(uf);
        res.setRegistrationType(ProfessionalRegistrationResult.RegistrationType.ADVOGADO);
        res.setStatus(ProfessionalRegistrationResult.ProfessionalStatus.REGULAR);
        res.setFullName("DRA. FERNANDA MARTINS COELHO");
        res.setSubSection("São Paulo - Capital");
        res.setRegistrationDate(DATE_FORMAT.parse("2012-04-10"));

        RegistryProvenance prov = new RegistryProvenance(PROVIDER_ID, getDisplayName(), "Synthetic CNA Registry Engine");
        prov.setConfidenceScore(1.0);
        res.setProvenance(prov);

        return res;
    }

    @Override
    public List<ProfessionalRegistrationResult> searchByName(String fullName, String state, ProviderConfig config) throws Exception {
        List<ProfessionalRegistrationResult> list = new ArrayList<>();
        list.add(lookupProfessional("123456", state != null ? state : "SP", config));
        return list;
    }

    @Override
    public List<BankingInstitutionResult> listBanks(ProviderConfig config) {
        List<BankingInstitutionResult> banks = new ArrayList<>();
        banks.add(new BankingInstitutionResult("00000000", "001", "BCO DO BRASIL S.A.", "BANCO DO BRASIL S.A.", true));
        banks.add(new BankingInstitutionResult("00360305", "104", "CAIXA ECONOMICA FEDERAL", "CAIXA ECONOMICA FEDERAL", true));
        banks.add(new BankingInstitutionResult("60746948", "237", "BCO BRADESCO S.A.", "BANCO BRADESCO S.A.", true));
        banks.add(new BankingInstitutionResult("60701190", "341", "ITAÚ UNIBANCO S.A.", "ITAÚ UNIBANCO S.A.", true));
        banks.add(new BankingInstitutionResult("90400888", "033", "BCO SANTANDER (BRASIL) S.A.", "BANCO SANTANDER (BRASIL) S.A.", true));
        banks.add(new BankingInstitutionResult("18236120", "260", "NU PAGAMENTOS - IP", "NU PAGAMENTOS S.A. - INSTITUIÇÃO DE PAGAMENTO", true));
        banks.add(new BankingInstitutionResult("41696665", "077", "BANCO INTER", "BANCO INTER S.A.", true));
        banks.add(new BankingInstitutionResult("30306294", "208", "BANCO BTG PACTUAL S.A.", "BANCO BTG PACTUAL S.A.", true));
        banks.add(new BankingInstitutionResult("00038166", "756", "BANCOOB", "BANCO COOPERATIVO SICOOB S.A. - BANCOOB", true));
        banks.add(new BankingInstitutionResult("01181521", "748", "BANCO COOPERATIVO SICREDI S.A.", "BANCO COOPERATIVO SICREDI S.A.", true));
        return banks;
    }

    @Override
    public BankingInstitutionResult findBank(String codeOrIspb, ProviderConfig config) {
        if (codeOrIspb == null || codeOrIspb.trim().isEmpty()) return null;
        String clean = codeOrIspb.trim();
        for (BankingInstitutionResult b : listBanks(config)) {
            if (clean.equals(b.getCompeCode()) || clean.equals(b.getIspb())) {
                return b;
            }
        }
        return null;
    }
}
