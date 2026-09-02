/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.financial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Resultado do cálculo e prestação de contas de Requisição de Pequeno Valor (RPV) e Precatórios.
 *
 * @author BR-LAWYER Team
 */
public final class RpvCalculationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum FinancialStatus {
        PENDENTE,
        LIQUIDADO,
        REPASSADO
    }

    private final BigDecimal grossAmount;              // Valor Bruto Total
    private final BigDecimal contractualFeeRate;        // Alíquota de Honorários Contratuais (ex: 0.30 para 30%)
    private final BigDecimal contractualFeeAmount;      // Valor dos Honorários Contratuais
    private final BigDecimal succumbingFeeAmount;       // Honorários Sucumbenciais
    private final BigDecimal totalOfficeFees;           // Total do Escritório (Contratuais + Sucumbenciais)
    private final BigDecimal taxWithholdingAmount;      // Retenção Tributária (IRRF / RRA)
    private final BigDecimal reimbursableExpenses;      // Despesas e Custas Reembolsáveis
    private final BigDecimal netClientAmount;           // Valor Líquido a Repassar ao Cliente
    private final FinancialStatus status;               // Status atual

    public RpvCalculationResult(BigDecimal grossAmount,
                                BigDecimal contractualFeeRate,
                                BigDecimal contractualFeeAmount,
                                BigDecimal succumbingFeeAmount,
                                BigDecimal totalOfficeFees,
                                BigDecimal taxWithholdingAmount,
                                BigDecimal reimbursableExpenses,
                                BigDecimal netClientAmount,
                                FinancialStatus status) {
        this.grossAmount = grossAmount != null ? grossAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.contractualFeeRate = contractualFeeRate != null ? contractualFeeRate : BigDecimal.ZERO;
        this.contractualFeeAmount = contractualFeeAmount != null ? contractualFeeAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.succumbingFeeAmount = succumbingFeeAmount != null ? succumbingFeeAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.totalOfficeFees = totalOfficeFees != null ? totalOfficeFees.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.taxWithholdingAmount = taxWithholdingAmount != null ? taxWithholdingAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.reimbursableExpenses = reimbursableExpenses != null ? reimbursableExpenses.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.netClientAmount = netClientAmount != null ? netClientAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.status = status != null ? status : FinancialStatus.PENDENTE;
    }

    public BigDecimal getGrossAmount() { return grossAmount; }
    public BigDecimal getContractualFeeRate() { return contractualFeeRate; }
    public BigDecimal getContractualFeeAmount() { return contractualFeeAmount; }
    public BigDecimal getSuccumbingFeeAmount() { return succumbingFeeAmount; }
    public BigDecimal getTotalOfficeFees() { return totalOfficeFees; }
    public BigDecimal getTaxWithholdingAmount() { return taxWithholdingAmount; }
    public BigDecimal getReimbursableExpenses() { return reimbursableExpenses; }
    public BigDecimal getNetClientAmount() { return netClientAmount; }
    public FinancialStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "RpvCalculationResult{" +
                "grossAmount=" + grossAmount +
                ", contractualFeeAmount=" + contractualFeeAmount +
                ", succumbingFeeAmount=" + succumbingFeeAmount +
                ", totalOfficeFees=" + totalOfficeFees +
                ", netClientAmount=" + netClientAmount +
                ", status=" + status +
                '}';
    }
}
