/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.financial;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculadora Canônica de Honorários e Prestação de Contas (RPV, Precatórios e Ações Judiciais).
 *
 * Implementa as regras financeiras do escritório jurídico brasileiro:
 * 1. Honorários Contratuais = (Valor Bruto - Despesas Dedutíveis) * Alíquota Contratual
 * 2. Total Escritório = Honorários Contratuais + Honorários Sucumbenciais
 * 3. Líquido Cliente = Valor Bruto - Total Escritório - Retenções Tributárias - Despesas Reembolsáveis
 * 4. Máquina de Estados: PENDENTE -> LIQUIDADO -> REPASSADO
 *
 * @author BR-LAWYER Team
 */
public class BrazilianLegalFinancialCalculator {

    public RpvCalculationResult calculateRpvStatement(BigDecimal grossAmount,
                                                      BigDecimal contractualFeeRate,
                                                      BigDecimal succumbingFees,
                                                      BigDecimal taxWithholding,
                                                      BigDecimal reimbursableExpenses,
                                                      RpvCalculationResult.FinancialStatus currentStatus) {
        if (grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor bruto da RPV não pode ser nulo ou negativo");
        }

        BigDecimal rate = contractualFeeRate != null ? contractualFeeRate : BigDecimal.ZERO;
        BigDecimal succumbing = succumbingFees != null ? succumbingFees : BigDecimal.ZERO;
        BigDecimal tax = taxWithholding != null ? taxWithholding : BigDecimal.ZERO;
        BigDecimal expenses = reimbursableExpenses != null ? reimbursableExpenses : BigDecimal.ZERO;

        // 1. Calcular Honorários Contratuais
        BigDecimal contractualAmount = grossAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        // 2. Calcular Total do Escritório
        BigDecimal totalOffice = contractualAmount.add(succumbing).setScale(2, RoundingMode.HALF_UP);

        // 3. Calcular Líquido do Cliente
        // Líquido = Bruto - Honorários Contratuais - Retenções - Despesas Reembolsadas
        BigDecimal netClient = grossAmount
                .subtract(contractualAmount)
                .subtract(tax)
                .subtract(expenses)
                .setScale(2, RoundingMode.HALF_UP);

        if (netClient.compareTo(BigDecimal.ZERO) < 0) {
            netClient = BigDecimal.ZERO;
        }

        return new RpvCalculationResult(
                grossAmount,
                rate,
                contractualAmount,
                succumbing,
                totalOffice,
                tax,
                expenses,
                netClient,
                currentStatus != null ? currentStatus : RpvCalculationResult.FinancialStatus.PENDENTE
        );
    }
}
