import { apiClient } from './client';
import {
  RestfulCaseOverviewV8,
  RestfulCasePageV8,
  RestfulCaseV1,
  RestfulPartyV1,
  RestfulDocumentV1,
  RestfulDueDateV1,
  RestfulCaseHistoryV8,
  RestfulTagV1,
  DocumentPreviewPdfResponse,
  RestfulDocumentContentV1,
} from '../types/cases';

export const casesService = {
  async getCasesPage(offset = 0, limit = 50, filter = 'all', q = ''): Promise<RestfulCasePageV8> {
    const params = new URLSearchParams({
      offset: offset.toString(),
      limit: limit.toString(),
      filter,
    });
    if (q.trim()) {
      params.append('q', q.trim());
    }
    const response = await apiClient.get<RestfulCasePageV8>(`/v8/cases/page?${params.toString()}`);
    return response.data;
  },

  async getActiveCases(): Promise<RestfulCaseOverviewV8[]> {
    const response = await apiClient.get<RestfulCaseOverviewV8[]>('/v8/cases/list/active');
    return response.data;
  },

  async getCaseById(id: string): Promise<RestfulCaseV1> {
    const response = await apiClient.get<RestfulCaseV1>(`/v1/cases/${id}`);
    return response.data;
  },

  async getCaseParties(id: string): Promise<RestfulPartyV1[]> {
    const response = await apiClient.get<RestfulPartyV1[]>(`/v1/cases/${id}/parties`);
    return response.data;
  },

  async getCaseDocuments(id: string): Promise<RestfulDocumentV1[]> {
    const response = await apiClient.get<RestfulDocumentV1[]>(`/v1/cases/${id}/documents/with-tags`);
    return response.data;
  },

  async getDocumentPreviewPdf(documentId: string): Promise<DocumentPreviewPdfResponse> {
    const response = await apiClient.get<DocumentPreviewPdfResponse>(`/v8/cases/document/${documentId}/preview-pdf`);
    return response.data;
  },

  async getDocumentContent(documentId: string): Promise<RestfulDocumentContentV1> {
    const response = await apiClient.get<RestfulDocumentContentV1>(`/v1/cases/document/${documentId}/content`);
    return response.data;
  },

  async updateDocumentContent(documentId: string, base64content: string): Promise<void> {
    await apiClient.put(`/v8/cases/document/${documentId}/content`, { base64content });
  },

  async getCaseDueDates(id: string): Promise<RestfulDueDateV1[]> {
    const response = await apiClient.get<RestfulDueDateV1[]>(`/v1/cases/${id}/duedates`);
    return response.data;
  },

  async getCaseHistory(id: string): Promise<RestfulCaseHistoryV8[]> {
    const response = await apiClient.get<RestfulCaseHistoryV8[]>(`/v8/cases/${id}/history`);
    return response.data;
  },

  async getCaseTags(id: string): Promise<RestfulTagV1[]> {
    const response = await apiClient.get<RestfulTagV1[]>(`/v1/cases/${id}/tags`);
    return response.data;
  },
};