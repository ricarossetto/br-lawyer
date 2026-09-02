export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'WAITING' | 'DONE' | 'CANCELLED';
export type TaskPriority = 'URGENT' | 'HIGH' | 'NORMAL' | 'LOW';
export type TaskCategory =
  | 'PRAZO_FATAL'
  | 'PETICAO'
  | 'AUDIENCIA'
  | 'DILIGENCIA'
  | 'REUNIAO'
  | 'ANALISE'
  | 'ADMINISTRATIVO'
  | 'OUTROS';

export interface TaskChecklistItem {
  id: string;
  taskId: string;
  title: string;
  done: boolean;
  itemOrder: number;
  completedAt?: number;
  completedBy?: string;
  createdAt: number;
}

export interface TaskComment {
  id: string;
  taskId: string;
  userName?: string;
  authorUser?: string;
  commentText: string;
  createdAt: number;
}

export interface TaskOverview {
  id: string;
  title: string;
  processId?: string;
  caseFileNumber?: string;
  caseName?: string;
  cnjNumber?: string;
  publicationId?: string;
  calendarEventId?: string;
  assignedUser?: string;
  createdBy?: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate?: number;
  dueTime?: string;
  overdue: boolean;
  dueToday: boolean;
  category: TaskCategory;
  checklistTotalCount: number;
  checklistDoneCount: number;
  commentCount: number;
  estimatedMinutes?: number;
  actualMinutes?: number;
  createdAt?: number;
  completedAt?: number;
}

export interface TaskDetail extends TaskOverview {
  description?: string;
  completedBy?: string;
  notes?: string;
  updatedAt?: number;
  comments: TaskComment[];
  checklistItems: TaskChecklistItem[];
}

export interface TaskPage {
  total: number;
  page: number;
  pageSize: number;
  items: TaskOverview[];
}

export interface KanbanColumn {
  status: TaskStatus;
  title: string;
  count: number;
  tasks: TaskOverview[];
}

export interface KanbanBoard {
  totalTasks: number;
  columns: KanbanColumn[];
}

export interface TaskFilter {
  status?: string;
  assignedUser?: string;
  processId?: string;
  priority?: string;
  category?: string;
  overdue?: boolean;
  dueToday?: boolean;
  fromDueDate?: string;
  toDueDate?: string;
  searchText?: string;
  page?: number;
  pageSize?: number;
  limit?: number;
}

export interface TaskStatusChangeRequest {
  newStatus: TaskStatus;
  notes?: string;
  user?: string;
  syncCalendar?: boolean;
}

export interface TaskCreateUpdateRequest {
  id?: string;
  title: string;
  description?: string;
  processId?: string;
  publicationId?: string;
  assignedUser?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: number;
  dueTime?: string;
  estimatedMinutes?: number;
  actualMinutes?: number;
  category?: TaskCategory;
  notes?: string;
}
