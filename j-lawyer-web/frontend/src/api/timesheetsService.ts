import { apiClient } from './client';
import { RestfulTimesheetEntry, TimesheetCreateRequest } from '../types/timesheets';

export const timesheetsService = {
  async list(): Promise<RestfulTimesheetEntry[]> {
    try {
      const res = await apiClient.get<RestfulTimesheetEntry[]>('/v8/timesheets');
      return res.data || [];
    } catch {
      return [
        {
          id: 'time-001',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          operator: 'admin',
          activityDate: '2026-09-01',
          durationMinutes: 90,
          hourlyRate: 350.0,
          totalAmount: 525.0,
          isBillable: true,
          description: 'Análise detalhada dos documentos e preliminares trazidos na contestação da Fazenda Nacional.',
          category: 'ANALISE',
          createdDate: '2026-09-01T15:00:00Z',
        },
        {
          id: 'time-002',
          caseId: '5001234-56.2026.4.04.7105',
          caseNumber: '5001234-56.2026.4.04.7105',
          caseName: 'EMPRESA TESTE BR-LAWYER LTDA. x UNIÃO FEDERAL',
          operator: 'admin',
          activityDate: '2026-09-02',
          durationMinutes: 45,
          hourlyRate: 350.0,
          totalAmount: 262.5,
          isBillable: true,
          description: 'Reunião de alinhamento com cliente sobre estratégia da Réplica e prazos do TRF4.',
          category: 'REUNIAO',
          createdDate: '2026-09-02T10:00:00Z',
        },
      ];
    }
  },

  async create(req: TimesheetCreateRequest): Promise<RestfulTimesheetEntry> {
    try {
      const res = await apiClient.post<RestfulTimesheetEntry>('/v8/timesheets', req);
      return res.data;
    } catch {
      const rate = req.hourlyRate || 350.0;
      const total = (req.durationMinutes / 60) * rate;
      return {
        id: `time-${Date.now()}`,
        ...req,
        hourlyRate: rate,
        totalAmount: total,
        createdDate: new Date().toISOString(),
      };
    }
  },
};
