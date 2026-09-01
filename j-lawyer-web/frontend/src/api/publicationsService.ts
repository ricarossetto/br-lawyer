import { apiClient } from './client';
import {
  PublicationDetail,
  PublicationFilter,
  PublicationLinkRequest,
  PublicationOverview,
  PublicationPage,
  PublicationTreatRequest,
} from '../types/publications';

export const publicationsService = {
  async list(filter?: PublicationFilter): Promise<PublicationOverview[]> {
    const params = new URLSearchParams();
    if (filter?.status) params.append('status', filter.status);
    if (filter?.readStatus) params.append('readStatus', filter.readStatus);
    if (filter?.treatmentStatus) params.append('treatmentStatus', filter.treatmentStatus);
    if (filter?.courtCode) params.append('courtCode', filter.courtCode);
    if (filter?.processId) params.append('processId', filter.processId);
    if (filter?.cnjNumber) params.append('cnjNumber', filter.cnjNumber);
    if (filter?.assignedUser) params.append('assignedUser', filter.assignedUser);
    if (filter?.lawyerOab) params.append('lawyerOab', filter.lawyerOab);
    if (filter?.searchText) params.append('searchText', filter.searchText);
    if (filter?.fromDate) params.append('fromDate', filter.fromDate);
    if (filter?.toDate) params.append('toDate', filter.toDate);
    if (filter?.limit) params.append('limit', filter.limit.toString());

    const res = await apiClient.get<PublicationOverview[]>('/v8/publications', { params });
    return res.data;
  },

  async getPage(filter?: PublicationFilter): Promise<PublicationPage> {
    const params = new URLSearchParams();
    if (filter?.status) params.append('status', filter.status);
    if (filter?.readStatus) params.append('readStatus', filter.readStatus);
    if (filter?.treatmentStatus) params.append('treatmentStatus', filter.treatmentStatus);
    if (filter?.courtCode) params.append('courtCode', filter.courtCode);
    if (filter?.processId) params.append('processId', filter.processId);
    if (filter?.cnjNumber) params.append('cnjNumber', filter.cnjNumber);
    if (filter?.assignedUser) params.append('assignedUser', filter.assignedUser);
    if (filter?.lawyerOab) params.append('lawyerOab', filter.lawyerOab);
    if (filter?.searchText) params.append('searchText', filter.searchText);
    if (filter?.fromDate) params.append('fromDate', filter.fromDate);
    if (filter?.toDate) params.append('toDate', filter.toDate);
    params.append('page', (filter?.page ?? 0).toString());
    params.append('pageSize', (filter?.pageSize ?? 25).toString());

    const res = await apiClient.get<PublicationPage>('/v8/publications/page', { params });
    return res.data;
  },

  async getById(id: string): Promise<PublicationDetail> {
    const res = await apiClient.get<PublicationDetail>(`/v8/publications/${encodeURIComponent(id)}`);
    return res.data;
  },

  async markRead(id: string, read: boolean = true): Promise<PublicationDetail> {
    const res = await apiClient.post<PublicationDetail>(
      `/v8/publications/${encodeURIComponent(id)}/mark-read`,
      null,
      { params: { read } }
    );
    return res.data;
  },

  async linkCase(id: string, request: PublicationLinkRequest): Promise<PublicationDetail> {
    const res = await apiClient.post<PublicationDetail>(
      `/v8/publications/${encodeURIComponent(id)}/link-case`,
      request
    );
    return res.data;
  },

  async unlinkCase(id: string): Promise<PublicationDetail> {
    const res = await apiClient.post<PublicationDetail>(
      `/v8/publications/${encodeURIComponent(id)}/unlink-case`,
      null
    );
    return res.data;
  },

  async treat(id: string, request: PublicationTreatRequest): Promise<PublicationDetail> {
    const res = await apiClient.post<PublicationDetail>(
      `/v8/publications/${encodeURIComponent(id)}/treat`,
      request
    );
    return res.data;
  },

  async archive(id: string, reason?: string): Promise<PublicationDetail> {
    const res = await apiClient.post<PublicationDetail>(
      `/v8/publications/${encodeURIComponent(id)}/archive`,
      null,
      { params: reason ? { reason } : undefined }
    );
    return res.data;
  },

  async assign(id: string, user: string): Promise<PublicationDetail> {
    const res = await apiClient.post<PublicationDetail>(
      `/v8/publications/${encodeURIComponent(id)}/assign`,
      null,
      { params: { user } }
    );
    return res.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/v8/publications/${encodeURIComponent(id)}`);
  },
};
