import { apiClient } from './client';
import { RestfulSearchHitV8 } from '../types/search';

export const searchService = {
  async searchFulltext(query: string, maxDocs = 20): Promise<RestfulSearchHitV8[]> {
    if (!query.trim()) return [];
    const params = new URLSearchParams({ query: query.trim(), maxDocs: maxDocs.toString() });
    const response = await apiClient.get<RestfulSearchHitV8[]>(`/v8/search/fulltext?${params.toString()}`);
    return response.data;
  },
};