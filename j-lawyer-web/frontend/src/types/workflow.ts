import { PublicationOverview } from './publications';
import { TaskOverview } from './tasks';

export interface WorkflowDashboard {
  totalNewPublications: number;
  totalUnreadPublications: number;
  totalUntreatedPublications: number;

  totalOpenTasks: number;
  totalOverdueTasks: number;
  totalDueTodayTasks: number;
  totalDueNext7DaysTasks: number;
  totalMyOpenTasks: number;

  urgentTasksCount: number;
  highTasksCount: number;
  normalTasksCount: number;
  lowTasksCount: number;

  todoCount: number;
  inProgressCount: number;
  waitingCount: number;
  doneRecentlyCount: number;

  urgentPublications: PublicationOverview[];
  urgentOverdueTasks: TaskOverview[];
  todayTasks: TaskOverview[];
}
