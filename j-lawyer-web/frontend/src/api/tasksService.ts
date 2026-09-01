import { apiClient } from './client';
import {
  KanbanBoard,
  TaskChecklistItem,
  TaskComment,
  TaskCreateUpdateRequest,
  TaskDetail,
  TaskFilter,
  TaskOverview,
  TaskPage,
  TaskStatusChangeRequest,
} from '../types/tasks';

export const tasksService = {
  async list(filter?: TaskFilter): Promise<TaskOverview[]> {
    const params = new URLSearchParams();
    if (filter?.status) params.append('status', filter.status);
    if (filter?.assignedUser) params.append('assignedUser', filter.assignedUser);
    if (filter?.processId) params.append('processId', filter.processId);
    if (filter?.priority) params.append('priority', filter.priority);
    if (filter?.category) params.append('category', filter.category);
    if (filter?.overdue !== undefined) params.append('overdue', filter.overdue.toString());
    if (filter?.dueToday !== undefined) params.append('dueToday', filter.dueToday.toString());
    if (filter?.fromDueDate) params.append('fromDueDate', filter.fromDueDate);
    if (filter?.toDueDate) params.append('toDueDate', filter.toDueDate);
    if (filter?.searchText) params.append('searchText', filter.searchText);
    if (filter?.limit) params.append('limit', filter.limit.toString());

    const res = await apiClient.get<TaskOverview[]>('/v8/tasks', { params });
    return res.data;
  },

  async getPage(filter?: TaskFilter): Promise<TaskPage> {
    const params = new URLSearchParams();
    if (filter?.status) params.append('status', filter.status);
    if (filter?.assignedUser) params.append('assignedUser', filter.assignedUser);
    if (filter?.processId) params.append('processId', filter.processId);
    if (filter?.priority) params.append('priority', filter.priority);
    if (filter?.category) params.append('category', filter.category);
    if (filter?.overdue !== undefined) params.append('overdue', filter.overdue.toString());
    if (filter?.dueToday !== undefined) params.append('dueToday', filter.dueToday.toString());
    if (filter?.fromDueDate) params.append('fromDueDate', filter.fromDueDate);
    if (filter?.toDueDate) params.append('toDueDate', filter.toDueDate);
    if (filter?.searchText) params.append('searchText', filter.searchText);
    params.append('page', (filter?.page ?? 0).toString());
    params.append('pageSize', (filter?.pageSize ?? 25).toString());

    const res = await apiClient.get<TaskPage>('/v8/tasks/page', { params });
    return res.data;
  },

  async getKanban(assignedUser?: string, processId?: string): Promise<KanbanBoard> {
    const params = new URLSearchParams();
    if (assignedUser) params.append('assignedUser', assignedUser);
    if (processId) params.append('processId', processId);

    const res = await apiClient.get<KanbanBoard>('/v8/tasks/kanban', { params });
    return res.data;
  },

  async getById(id: string): Promise<TaskDetail> {
    const res = await apiClient.get<TaskDetail>(`/v8/tasks/${encodeURIComponent(id)}`);
    return res.data;
  },

  async create(task: TaskCreateUpdateRequest, syncCalendar: boolean = true): Promise<TaskDetail> {
    const res = await apiClient.post<TaskDetail>('/v8/tasks', task, {
      params: { syncCalendar },
    });
    return res.data;
  },

  async update(
    id: string,
    task: TaskCreateUpdateRequest,
    syncCalendar: boolean = true
  ): Promise<TaskDetail> {
    const res = await apiClient.put<TaskDetail>(`/v8/tasks/${encodeURIComponent(id)}`, task, {
      params: { syncCalendar },
    });
    return res.data;
  },

  async changeStatus(id: string, request: TaskStatusChangeRequest): Promise<TaskDetail> {
    const res = await apiClient.post<TaskDetail>(
      `/v8/tasks/${encodeURIComponent(id)}/status`,
      request
    );
    return res.data;
  },

  async assign(id: string, user: string): Promise<TaskDetail> {
    const res = await apiClient.post<TaskDetail>(
      `/v8/tasks/${encodeURIComponent(id)}/assign`,
      null,
      { params: { user } }
    );
    return res.data;
  },

  async addComment(id: string, text: string): Promise<TaskComment> {
    const res = await apiClient.post<TaskComment>(
      `/v8/tasks/${encodeURIComponent(id)}/comments`,
      null,
      { params: { text } }
    );
    return res.data;
  },

  async getComments(id: string): Promise<TaskComment[]> {
    const res = await apiClient.get<TaskComment[]>(`/v8/tasks/${encodeURIComponent(id)}/comments`);
    return res.data;
  },

  async addChecklistItem(id: string, title: string, order: number = 0): Promise<TaskChecklistItem> {
    const res = await apiClient.post<TaskChecklistItem>(
      `/v8/tasks/${encodeURIComponent(id)}/checklist`,
      null,
      { params: { title, order } }
    );
    return res.data;
  },

  async updateChecklistItem(
    id: string,
    itemId: string,
    item: { title?: string; done?: boolean; itemOrder?: number }
  ): Promise<TaskChecklistItem> {
    const res = await apiClient.put<TaskChecklistItem>(
      `/v8/tasks/${encodeURIComponent(id)}/checklist/${encodeURIComponent(itemId)}`,
      item
    );
    return res.data;
  },

  async deleteChecklistItem(id: string, itemId: string): Promise<void> {
    await apiClient.delete(
      `/v8/tasks/${encodeURIComponent(id)}/checklist/${encodeURIComponent(itemId)}`
    );
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/v8/tasks/${encodeURIComponent(id)}`);
  },
};
