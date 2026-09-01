export interface RestfulCalendarEventV8 {
  id: string;
  caseId?: string;
  caseNumber?: string;
  caseName?: string;
  summary: string;
  start: number;
  end?: number;
  assignee?: string;
  author?: string;
  type: 'followup' | 'respite' | 'event';
  done: boolean;
  location?: string;
}