/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.client.e2e;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runner Master da Suíte Completa de Testes E2E do BR-LAWYER.
 * Executa todos os Tiers (Tier 1 a Tier 4) cobrindo as 17 funcionalidades do sistema.
 *
 * Execução via Maven:
 *   mvn test -pl j-lawyer-client -Dtest=com.jdimension.jlawyer.client.e2e.BrazilianE2ETestSuite
 *
 * @author BR-LAWYER Team
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    Tier1FeatureCoverageTest.class,
    Tier2BoundaryCornerCaseTest.class,
    Tier3CrossFeatureIntegrationTest.class,
    Tier4BrazilianLegalWorkflowE2ETest.class
})
public class BrazilianE2ETestSuite {
    // Master suite holder - no methods needed
}
