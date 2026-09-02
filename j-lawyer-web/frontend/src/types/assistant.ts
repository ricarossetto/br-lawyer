import { LegalPromptTemplate } from './documents';

export interface AiDraftingRequest {
  templateId: string;
  caseId?: string;
  customInstructions?: string;
  variables: Record<string, string>;
  includeFactsSummary?: boolean;
}

export interface AiDraftingResult {
  id: string;
  title: string;
  templateName: string;
  legalBase: string;
  markdownContent: string;
  extractedFactsCount: number;
  generatedDate: string;
  warnings?: string[];
}
