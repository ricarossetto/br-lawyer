export interface RestfulTimesheetEntry {
  id: string;
  caseId?: string;
  caseNumber?: string;
  caseName?: string;
  taskId?: string;
  taskTitle?: string;
  operator: string;
  activityDate: string;
  durationMinutes: number;
  hourlyRate: number;
  totalAmount: number;
  isBillable: boolean;
  description: string;
  category: 'AUDIENCIA' | 'PETICAO' | 'REUNIAO' | 'PESQUISA' | 'ANALISE' | 'DILIGENCIA';
  createdDate: string;
}

export interface TimesheetCreateRequest {
  caseId?: string;
  taskId?: string;
  operator: string;
  activityDate: string;
  durationMinutes: number;
  hourlyRate?: number;
  isBillable: boolean;
  description: string;
  category: 'AUDIENCIA' | 'PETICAO' | 'REUNIAO' | 'PESQUISA' | 'ANALISE' | 'DILIGENCIA';
}
