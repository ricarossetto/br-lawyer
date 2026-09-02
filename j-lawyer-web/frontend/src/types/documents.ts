export interface LegalPromptTemplate {
  id: string;
  name: string;
  category: 'PETICAO_INICIAL' | 'CONTESTACAO' | 'RECURSO' | 'EMBARGOS' | 'PARECER' | 'CONTRATO';
  description: string;
  legalBase: string; // e.g. "Art. 319 CPC", "Art. 337 CPC", "Art. 1.009 CPC"
  templateText: string;
  placeholders: string[]; // e.g. ["VARA", "COMARCA", "NOME_AUTOR", "QUALIFICACAO_AUTOR", "NOME_REU", "NUMERO_PROCESSO", "FATOS", "PEDIDOS"]
  isOfficial?: boolean;
}

export interface RestfulDocumentVaultItem {
  id: string;
  name: string;
  fileName: string;
  category: 'PECA' | 'PROCURACAO' | 'CONTRATO' | 'PROVA' | 'DECISAO' | 'OUTROS';
  caseId?: string;
  caseNumber?: string;
  caseName?: string;
  contactId?: string;
  contactName?: string;
  fileSize: number; // bytes
  mimeType: string;
  version: number;
  tags?: string[];
  uploadedBy: string;
  createdDate: string;
  lastUpdated: string;
  hasOcrText?: boolean;
  ocrSnippet?: string;
}
