import { apiClient } from './client';
import { RestfulDocumentVaultItem, LegalPromptTemplate } from '../types/documents';

export const documentsService = {
  async listVaultDocuments(params?: { category?: string; query?: string }): Promise<RestfulDocumentVaultItem[]> {
    try {
      const p = new URLSearchParams();
      if (params?.category) p.append('category', params.category);
      if (params?.query) p.append('q', params.query);
      const res = await apiClient.get<RestfulDocumentVaultItem[]>(`/v8/documents/vault?${p.toString()}`);
      return res.data || [];
    } catch {
      return [
        {
          id: 'doc-vault-001',
          name: 'Petição Inicial - Ação Ordinária Tributária.pdf',
          fileName: 'peticao_inicial_tributario_5001234.pdf',
          category: 'PECA',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          contactName: 'EMPRESA TESTE BR-LAWYER LTDA.',
          fileSize: 245000,
          mimeType: 'application/pdf',
          version: 1,
          tags: ['Tributário', 'Inicial', 'TRF4', 'PIS/COFINS'],
          uploadedBy: 'admin',
          createdDate: '2026-08-25T11:00:00Z',
          lastUpdated: '2026-08-25T11:00:00Z',
          hasOcrText: true,
          ocrSnippet: 'EXCELENTÍSSIMO SENHOR DOUTOR JUIZ FEDERAL DA VARA FEDERAL DA SUBSEÇÃO JUDICIÁRIA DE SANTO ÂNGELO/RS...',
        },
        {
          id: 'doc-vault-002',
          name: 'Procuração Ad Judicia et Extra - Assinada.pdf',
          fileName: 'procuracao_empresa_teste.pdf',
          category: 'PROCURACAO',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          contactName: 'EMPRESA TESTE BR-LAWYER LTDA.',
          fileSize: 112000,
          mimeType: 'application/pdf',
          version: 1,
          tags: ['Procuração', 'Poderes Especiais'],
          uploadedBy: 'admin',
          createdDate: '2026-08-20T16:40:00Z',
          lastUpdated: '2026-08-20T16:40:00Z',
        },
        {
          id: 'doc-vault-003',
          name: 'Contestação União Federal - Juntada em 01/09/2026.pdf',
          fileName: 'contestacao_uniao_federal.pdf',
          category: 'PECA',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          contactName: 'PROCURADORIA-GERAL DA FAZENDA NACIONAL',
          fileSize: 520000,
          mimeType: 'application/pdf',
          version: 1,
          tags: ['Contestação', 'Fazenda Nacional', 'Prazo Réplica'],
          uploadedBy: 'DataJud Scraper',
          createdDate: '2026-09-01T08:30:00Z',
          lastUpdated: '2026-09-01T08:30:00Z',
          hasOcrText: true,
          ocrSnippet: 'A UNIÃO FEDERAL, por seu Procurador da Fazenda Nacional, vem apresentar CONTESTAÇÃO com preliminares de mérito...',
        },
      ];
    }
  },

  getPromptTemplates(): LegalPromptTemplate[] {
    return [
      {
        id: 'tmpl-peticao-inicial',
        name: 'Petição Inicial Cível (Art. 319 CPC)',
        category: 'PETICAO_INICIAL',
        legalBase: 'Art. 319 do CPC/2015',
        description: 'Estrutura canônica de petição inicial com endereçamento, qualificação das partes, fatos, fundamentos jurídicos e pedidos líquidos.',
        placeholders: [
          'VARA',
          'COMARCA',
          'NOME_AUTOR',
          'QUALIFICACAO_AUTOR',
          'NOME_REU',
          'QUALIFICACAO_REU',
          'DOS_FATOS',
          'DOS_FUNDAMENTOS',
          'DOS_PEDIDOS',
          'VALOR_CAUSA',
        ],
        isOfficial: true,
        templateText: `EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO DA {{VARA}} DA COMARCA DE {{COMARCA}}

{{NOME_AUTOR}}, {{QUALIFICACAO_AUTOR}}, por seu advogado subscrito (procuração anexa), vem, respeitosamente, à presença de Vossa Excelência, propor a presente

AÇÃO ORDINÁRIA DE COBRANÇA C/C INDENIZAÇÃO

em face de {{NOME_REU}}, {{QUALIFICACAO_REU}}, pelos fatos e fundamentos jurídicos a seguir expostos:

I - DOS FATOS
{{DOS_FATOS}}

II - DO DIREITO E FUNDAMENTAÇÃO JURÍDICA
{{DOS_FUNDAMENTOS}}

III - DOS PEDIDOS
Ante o exposto, requer a Vossa Excelência:
a) A citação do Réu para, querendo, responder aos termos da presente no prazo legal;
b) A total procedência dos pedidos para condenar o Réu ao pagamento do valor principal atualizado;
c) A condenação do Réu em honorários advocatícios sucumbenciais no percentual de 20% (Art. 85, § 2º do CPC) e custas processuais.

Dá-se à causa o valor de R$ {{VALOR_CAUSA}}.

Nestes termos,
Pede deferimento.

{{COMARCA}}, data do protocolo.
Advogado(a) - OAB/UF`,
      },
      {
        id: 'tmpl-contestacao',
        name: 'Contestação Cível com Preliminares (Art. 337 CPC)',
        category: 'CONTESTACAO',
        legalBase: 'Art. 337 do CPC/2015',
        description: 'Modelo de defesa com arguição preliminar de incompetência, inépcia, ilegitimidade e impugnação ao mérito.',
        placeholders: [
          'NUMERO_PROCESSO',
          'VARA',
          'NOME_AUTOR',
          'NOME_REU',
          'PRELIMINARES',
          'DO_MERITO',
          'PEDIDOS_DEFESA',
        ],
        isOfficial: true,
        templateText: `EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO DA {{VARA}}
PROCESSO Nº {{NUMERO_PROCESSO}}

{{NOME_REU}}, já qualificado nos autos da ação em epígrafe proposta por {{NOME_AUTOR}}, vem apresentar

CONTESTAÇÃO

com base nas seguintes razões:

I - PRELIMINARMENTE (Art. 337 do CPC)
{{PRELIMINARES}}

II - DO MÉRITO E IMPUGNAÇÃO ESPECÍFICA
{{DO_MERITO}}

III - DOS REQUERIMENTOS
Requer o acolhimento da matéria preliminar com a extinção do processo sem resolução do mérito, ou, sucessivamente, a total improcedência dos pedidos formulados pelo Autor, com a condenação em custas e honorários sucumbenciais.

Nestes termos,
Pede deferimento.`,
      },
      {
        id: 'tmpl-embargos-declaracao',
        name: 'Embargos de Declaração (Art. 1.022 CPC)',
        category: 'EMBARGOS',
        legalBase: 'Art. 1.022 do CPC/2015',
        description: 'Peça recursal para sanar omissão, obscuridade, contradição ou erro material na decisão judicial, com interrupção de prazo.',
        placeholders: [
          'NUMERO_PROCESSO',
          'OMISSAO_CONTRADICAO',
          'PREQUESTIONAMENTO',
        ],
        isOfficial: true,
        templateText: `EXCELENTÍSSIMO(A) SENHOR(A) DOUTOR(A) JUIZ(A) DE DIREITO
PROCESSO Nº {{NUMERO_PROCESSO}}

Vem, tempestivamente, com fulcro no Art. 1.022 do CPC, opor

EMBARGOS DE DECLARAÇÃO

em face da r. decisão interlocutória / sentença proferida, pelos motivos a seguir:

I - DA TEMPESTIVIDADE E EFEITO INTERRUPTIVO (Art. 1.026 do CPC)
Os presentes embargos são opostos dentro do prazo de 5 (cinco) dias úteis.

II - DA OMISSÃO / CONTRADIÇÃO APONTADA
{{OMISSAO_CONTRADICAO}}

III - DO PEDIDO DE ACLARAMENTO
Requer o acolhimento dos presentes embargos para suprir o vício indicado.

Nestes termos,
Pede deferimento.`,
      },
    ];
  },
};
