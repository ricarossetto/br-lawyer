import { apiClient } from './client';
import { RestfulCalendarEventV8 } from '../types/calendar';

export const calendarService = {
  async getEvents(from?: string, to?: string, type = 'all', status = 'all', limit = 100): Promise<RestfulCalendarEventV8[]> {
    const params = new URLSearchParams({ type, status, limit: limit.toString() });
    if (from) params.append('from', from);
    if (to) params.append('to', to);
    const response = await apiClient.get<RestfulCalendarEventV8[]>(`/v8/calendar/events?${params.toString()}`);
    return response.data;
  },

  async deleteEvent(id: string): Promise<void> {
    await apiClient.delete(`/v8/calendar/events/${id}`);
  },
};