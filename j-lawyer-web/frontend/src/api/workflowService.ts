import { apiClient } from './client';
import { WorkflowDashboard } from '../types/workflow';

export const workflowService = {
  async getDashboard(): Promise<WorkflowDashboard> {
    const res = await apiClient.get<WorkflowDashboard>('/v8/workflow/dashboard');
    return res.data;
  },
};
