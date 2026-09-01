import React, { useEffect, useState } from 'react';
import { CriticalAlertRibbon } from './CriticalAlertRibbon';
import { ActionableKpiCards } from './ActionableKpiCards';
import { RecentCasesTable } from './RecentCasesTable';
import { casesService } from '../../api/casesService';
import { calendarService } from '../../api/calendarService';
import { RestfulCaseOverviewV8 } from '../../types/cases';
import { RestfulCalendarEventV8 } from '../../types/calendar';
import { AlertCircle, RefreshCw } from 'lucide-react';
import { Button } from '../common/Button';

interface DailyCommandCenterProps {
  onSelectCase: (caseId: string) => void;
  onNavigateToCases: () => void;
  onNavigateToCalendar: () => void;
}

export const DailyCommandCenter: React.FC<DailyCommandCenterProps> = ({
  onSelectCase,
  onNavigateToCases,
  onNavigateToCalendar,
}) => {
  const [recentCases, setRecentCases] = useState<RestfulCaseOverviewV8[]>([]);
  const [activeCasesCount, setActiveCasesCount] = useState<number>(0);
  const [upcomingEvents, setUpcomingEvents] = useState<RestfulCalendarEventV8[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadDashboardData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const [casesPage, events] = await Promise.all([
        casesService.getCasesPage(0, 10, 'open'),
        calendarService.getEvents(undefined, undefined, 'all', 'open', 50).catch(() => []),
      ]);
      setRecentCases(casesPage.items || []);
      setActiveCasesCount(casesPage.total || (casesPage.items ? casesPage.items.length : 0));
      setUpcomingEvents(events || []);
    } catch (err: any) {
      setError(err?.message || 'Falha ao sincronizar dados com o servidor BR-LAWYER.');
      setRecentCases([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  return (
    <div className="space-y-6">
      {error && (
        <div className="p-4 bg-red-950/40 border border-red-800 rounded-xl flex items-center justify-between">
          <div className="flex items-center gap-3 text-red-200 text-sm">
            <AlertCircle className="h-5 w-5 text-red-400 shrink-0" />
            <span>{error}</span>
          </div>
          <Button variant="secondary" size="xs" onClick={loadDashboardData} leftIcon={<RefreshCw className="h-3.5 w-3.5" />}>
            Tentar novamente
          </Button>
        </div>
      )}

      {/* 1. Critical Alert Ribbon */}
      <CriticalAlertRibbon events={upcomingEvents} onSelectCase={onSelectCase} />

      {/* 2. Actionable KPI Metrics */}
      <ActionableKpiCards
        activeCasesCount={activeCasesCount}
        upcomingEventsCount={upcomingEvents.length}
        onNavigateToCases={onNavigateToCases}
        onNavigateToCalendar={onNavigateToCalendar}
      />

      {/* 3. Priority Work Queue & Recent Cases */}
      <RecentCasesTable
        cases={recentCases}
        isLoading={isLoading}
        onSelectCase={onSelectCase}
        onViewAll={onNavigateToCases}
      />
    </div>
  );
};