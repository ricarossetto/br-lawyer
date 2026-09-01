import React, { useEffect, useState } from 'react';
import { CriticalAlertRibbon } from './CriticalAlertRibbon';
import { ActionableKpiCards } from './ActionableKpiCards';
import { RecentCasesTable } from './RecentCasesTable';
import { casesService } from '../../api/casesService';
import { RestfulCaseOverviewV8 } from '../../types/cases';

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
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchCases = async () => {
      try {
        const page = await casesService.getCasesPage(0, 10, 'open');
        setRecentCases(page.items);
      } catch (err) {
        // Fallback demo data for immediate offline showcase
        setRecentCases([
          {
            id: 'c-101',
            fileNumber: '5001234-56.2026.8.13.0024',
            name: 'Ação Revisional de Contrato Bancário — Silva x Banco S/A',
            subjectField: 'Direito Bancário',
            lawyer: 'Dr. Carlos Eduardo',
            claimValue: '145000.00',
            archived: false,
            dateChanged: Date.now() - 3600000 * 2,
          },
          {
            id: 'c-102',
            fileNumber: '0019876-12.2026.5.03.0001',
            name: 'Reclamação Trabalhista — Oliveira x Construtora Horizonte',
            subjectField: 'Direito do Trabalho',
            lawyer: 'Dra. Mariana Rios',
            claimValue: '87500.00',
            archived: false,
            dateChanged: Date.now() - 3600000 * 5,
          },
          {
            id: 'c-103',
            fileNumber: '1045678-90.2026.4.01.3800',
            name: 'Mandado de Segurança Tributário — Tech Solutions x Fazenda Nacional',
            subjectField: 'Direito Tributário',
            lawyer: 'Dr. Roberto Santos',
            claimValue: '320000.00',
            archived: false,
            dateChanged: Date.now() - 3600000 * 24,
          },
        ]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCases();
  }, []);

  return (
    <div className="space-y-6">
      {/* 1. Critical Alert Ribbon (Prazos Fatais D-0 / D-1) */}
      <CriticalAlertRibbon onSelectCase={onSelectCase} />

      {/* 2. Actionable KPI Metrics */}
      <ActionableKpiCards
        onNavigateToCases={onNavigateToCases}
        onNavigateToCalendar={onNavigateToCalendar}
      />

      {/* 3. Priority Work Queue & Recent Cases */}
      <RecentCasesTable
        cases={recentCases}
        onSelectCase={onSelectCase}
        onViewAll={onNavigateToCases}
      />
    </div>
  );
};