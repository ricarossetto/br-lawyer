export type ContactType = 'INDIVIDUAL' | 'COMPANY' | 'COURT' | 'AUTHORITY' | 'OTHER';
export type ContactRole = 'CLIENT' | 'OPPOSING_PARTY' | 'LAWYER' | 'WITNESS' | 'EXPERT' | 'PARTNER' | 'OTHER';

export interface BrazilianQSAItem {
  name: string;
  role: string;
  country?: string;
  legalRepresentative?: string;
}

export interface BrazilianRegistryEnrichment {
  provider: 'BrasilAPI' | 'ViaCEP' | 'Internal';
  status: 'CURRENT' | 'FOUND' | 'CACHED';
  enrichedAt: string;
  corporateName?: string;
  tradeName?: string;
  statusText?: string;
  shareCapital?: number;
  mainCnae?: string;
  secondaryCnaes?: string[];
  qsa?: BrazilianQSAItem[];
  street?: string;
  number?: string;
  complement?: string;
  neighborhood?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  conflictHints?: string[];
}

export interface RestfulClientContact {
  id: string;
  type: ContactType;
  role: ContactRole;
  name: string;
  tradeName?: string;
  documentNumber?: string; // CPF or Alphanumeric CNPJ
  oabNumber?: string;
  email?: string;
  phone?: string;
  cellphone?: string;
  zipCode?: string;
  street?: string;
  number?: string;
  complement?: string;
  neighborhood?: string;
  city?: string;
  state?: string;
  notes?: string;
  casesCount?: number;
  totalClaimValue?: number;
  createdDate?: string;
  lastUpdated?: string;
  enrichment?: BrazilianRegistryEnrichment;
  hasConflictHint?: boolean;
}

export interface ClientContactCreateUpdateRequest {
  id?: string;
  type: ContactType;
  role: ContactRole;
  name: string;
  tradeName?: string;
  documentNumber?: string;
  oabNumber?: string;
  email?: string;
  phone?: string;
  cellphone?: string;
  zipCode?: string;
  street?: string;
  number?: string;
  complement?: string;
  neighborhood?: string;
  city?: string;
  state?: string;
  notes?: string;
}
