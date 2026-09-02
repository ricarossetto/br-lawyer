import { apiClient } from './client';
import { AiDraftingRequest, AiDraftingResult } from '../types/assistant';
import { documentsService } from './documentsService';

export const aiService = {
  async generateLegalDraft(req: AiDraftingRequest): Promise<AiDraftingResult> {
    try {
      const res = await apiClient.post<AiDraftingResult>('/v8/ai/draft', req);
      return res.data;
    } catch {
      // Local assistance engine utilizing Brazilian Legal Prompt Catalog
      const templates = documentsService.getPromptTemplates();
      const template = templates.find((t) => t.id === req.templateId) || templates[0];

      let generated = template.templateText;
      for (const [key, val] of Object.entries(req.variables)) {
        const placeholder = new RegExp(`{{${key}}}`, 'g');
        generated = generated.replace(placeholder, val || `[${key} NÃO INFORMADO]`);
      }

      if (req.customInstructions) {
        generated += `\n\n/* OBSERVAÇÕES E DIRETRIZES ESPECÍFICAS INFORMADAS PELO ADVOGADO */\n/* ${req.customInstructions} */`;
      }

      return {
        id: `draft-${Date.now()}`,
        title: `Minuta Gerada - ${template.name}`,
        templateName: template.name,
        legalBase: template.legalBase,
        markdownContent: generated,
        extractedFactsCount: Object.keys(req.variables).length,
        generatedDate: new Date().toISOString(),
        warnings: [
          'Esta minuta foi elaborada com assistência de inteligência jurídica. A conferência profissional e assinatura são de responsabilidade privativa do(a) advogado(a) (Lei nº 8.906/1994 - Estatuto da OAB).',
        ],
      };
    }
  },
};
