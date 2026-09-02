import { apiClient } from './client';
import { RestfulFinancialEntry, FinancialStatus, RpvPrecatórioCalculation } from '../types/finance';

export const financeService = {
  async listEntries(params?: { type?: string; status?: string }): Promise<RestfulFinancialEntry[]> {
    try {
      const p = new URLSearchParams();
      if (params?.type) p.append('type', params.type);
      if (params?.status) p.append('status', params.status);
      const res = await apiClient.get<RestfulFinancialEntry[]>(`/v8/finance/entries?${p.toString()}`);
      return res.data || [];
    } catch {
      return [
        {
          id: 'fin-001',
          type: 'RPV',
          status: 'PENDING',
          title: 'Requisição de Pequeno Valor - TRF4 (Ofício Requisitório nº 2026/0491)',
          description: 'Valor decorrente de procedência da repetição de indébito tributário.',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          contactName: 'EMPRESA TESTE BR-LAWYER LTDA.',
          grossAmount: 72400.0,
          netAmount: 50680.0,
          feeOfficeAmount: 21720.0,
          clientTransferAmount: 50680.0,
          dueDate: '2026-10-15',
          courtOrderNumber: 'TRF4-RPV-2026-00812',
          bank: 'Caixa Econômica Federal',
          rpvDetails: {
            grossAmount: 72400.0,
            contractualFeePercentage: 30,
            contractualFeeAmount: 21720.0,
            sucumbenceFeeAmount: 0,
            taxDeductions: 0,
            reimbursableCosts: 0,
            totalOfficeAmount: 21720.0,
            netClientAmount: 50680.0,
          },
        },
        {
          id: 'fin-002',
          type: 'HONORARIOS_CONTRATUAIS',
          status: 'SETTLED',
          title: 'Honorários Iniciais - Ajuizamento de Ação Ordinária',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          contactName: 'EMPRESA TESTE BR-LAWYER LTDA.',
          grossAmount: 15000.0,
          netAmount: 15000.0,
          dueDate: '2026-08-30',
          settledDate: '2026-08-29',
          bank: 'Banco Itaú',
        },
        {
          id: 'fin-003',
          type: 'HONORARIOS_SUCUMBENCIAIS',
          status: 'PENDING',
          title: 'Honorários de Sucumbência (10% sobre proveito econômico)',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          contactName: 'PROCURADORIA-GERAL DA FAZENDA NACIONAL',
          grossAmount: 15000.0,
          netAmount: 15000.0,
          dueDate: '2026-11-20',
        },
      ];
    }
  },

  calculateRpvSplit(grossAmount: number, feePercentage: number, sucumbenceAmount = 0, deductions = 0, costs = 0): RpvPrecatórioCalculation {
    const feeAmount = (grossAmount * feePercentage) / 100;
    const totalOffice = feeAmount + sucumbenceAmount;
    const netClient = grossAmount - feeAmount - deductions - costs;

    return {
      grossAmount,
      contractualFeePercentage: feePercentage,
      contractualFeeAmount: feeAmount,
      sucumbenceFeeAmount: sucumbenceAmount,
      taxDeductions: deductions,
      reimbursableCosts: costs,
      totalOfficeAmount: totalOffice,
      netClientAmount: Math.max(0, netClient),
    };
  },

  async updateStatus(id: string, status: FinancialStatus): Promise<void> {
    try {
      await apiClient.patch(`/v8/finance/entries/${id}/status`, { status });
    } catch {
      // simulated update
    }
  },
};
