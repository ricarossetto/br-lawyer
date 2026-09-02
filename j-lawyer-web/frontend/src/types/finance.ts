export type FinancialEntryType = 'HONORARIOS_CONTRATUAIS' | 'HONORARIOS_SUCUMBENCIAIS' | 'RPV' | 'PRECATORIO' | 'CUSTAS_REEMBOLSAVEIS' | 'DESPESA_OPERACIONAL';
export type FinancialStatus = 'PENDING' | 'SETTLED' | 'TRANSFERRED' | 'CANCELLED';

export interface RpvPrecatórioCalculation {
  grossAmount: number;
  contractualFeePercentage: number; // e.g. 30%
  contractualFeeAmount: number;
  sucumbenceFeeAmount: number;
  taxDeductions: number;
  reimbursableCosts: number;
  totalOfficeAmount: number;
  netClientAmount: number;
}

export interface RestfulFinancialEntry {
  id: string;
  type: FinancialEntryType;
  status: FinancialStatus;
  title: string;
  description?: string;
  caseId?: string;
  caseNumber?: string;
  caseName?: string;
  contactId?: string;
  contactName?: string;
  grossAmount: number;
  netAmount: number;
  feeOfficeAmount?: number;
  clientTransferAmount?: number;
  dueDate: string;
  settledDate?: string;
  transferredDate?: string;
  courtOrderNumber?: string; // Número do Ofício Requisitório / RPV
  bank?: string; // e.g. "Caixa Econômica Federal", "Banco do Brasil"
  accountDetails?: string;
  notes?: string;
  rpvDetails?: RpvPrecatórioCalculation;
}
