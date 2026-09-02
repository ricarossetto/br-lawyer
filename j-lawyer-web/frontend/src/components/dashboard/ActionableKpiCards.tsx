import React from 'react';
import {
  Clock,
  Briefcase,
  Calendar,
  Scale,
  ListTodo,
  AlertTriangle,
  FileCheck,
  CheckCircle2,
} from 'lucide-react';
import { WorkflowDashboard } from '../../types/workflow';

interface ActionableKpiCardsProps {
  dashboard: WorkflowDashboard | null;
  activeCasesCount?: number;
  onNavigateToPublications?: () => void;
  onNavigateToTasks?: () => void;
  onNavigateToCases?: () => void;
  onNavigateToCalendar?: () => void;
}

export const ActionableKpiCards: React.FC<ActionableKpiCardsProps> = ({
  dashboard,
  activeCasesCount = 0,
  onNavigateToPublications,
  onNavigateToTasks,
  onNavigateToCases,
  onNavigateToCalendar,
}) => {
  const untreatedPubs = dashboard?.totalUntreatedPublications ?? 0;
  const overdueTasks = dashboard?.totalOverdueTasks ?? 0;
  const dueTodayTasks = dashboard?.totalDueTodayTasks ?? 0;
  const openTasks = dashboard?.totalOpenTasks ?? 0;
  const myOpenTasks = dashboard?.totalMyOpenTasks ?? 0;

  const cards = [
    {
      title: 'Publicações Pendentes',
      value: untreatedPubs,
      subtitle: `${dashboard?.totalNewPublications ?? 0} novas / não lidas`,
      icon: Scale,
      accent: untreatedPubs > 0 ? 'warning' : 'success',
      onClick: onNavigateToPublications,
    },
    {
      title: 'Tarefas Atrasadas',
      value: overdueTasks,
      subtitle: overdueTasks > 0 ? 'Exigem providência imediata' : 'Nenhuma tarefa em atraso',
      icon: AlertTriangle,
      accent: overdueTasks > 0 ? 'urgent' : 'success',
      onClick: onNavigateToTasks,
    },
    {
      title: 'Vencimento Hoje',
      value: dueTodayTasks,
      subtitle: 'Prazos e providências do dia',
      icon: Clock,
      accent: dueTodayTasks > 0 ? 'amber' : 'neutral',
      onClick: onNavigateToTasks,
    },
    {
      title: 'Minhas Tarefas Abertas',
      value: myOpenTasks,
      subtitle: `De ${openTasks} tarefas ativas no escritório`,
      icon: ListTodo,
      accent: 'active',
      onClick: onNavigateToTasks,
    },
  ];

  const colorStyles = {
    urgent: 'text-rose-500 bg-rose-950/20 border-rose-600/30',
    warning: 'text-[#FF3D00] bg-[#1A1A1A] border-[#FF3D00]/40',
    amber: 'text-[#FF3D00] bg-[#1A1A1A] border-[#262626]',
    active: 'text-[#FAFAFA] bg-[#141414] border-[#262626]',
    success: 'text-emerald-400 bg-emerald-950/20 border-emerald-600/30',
    neutral: 'text-[#737373] bg-[#141414] border-[#262626]',
  };

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, idx) => {
        const Icon = card.icon;
        const style = colorStyles[card.accent as keyof typeof colorStyles];

        return (
          <div
            key={idx}
            onClick={card.onClick}
            className={`p-6 bg-[#0F0F0F] border border-[#262626] rounded-none transition-colors duration-150 select-none group ${
              card.onClick ? 'hover:border-[#737373] cursor-pointer' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-mono uppercase tracking-wider text-[#737373]">{card.title}</span>
              <div className={`p-2 rounded-none border ${style}`}>
                <Icon className="h-4 w-4" />
              </div>
            </div>

            <div className="mt-4 flex items-baseline gap-2">
              <span className="text-4xl font-extrabold text-[#FAFAFA] font-mono tracking-tight group-hover:text-[#FF3D00] transition-colors">
                {card.value}
              </span>
            </div>

            <p className="mt-1.5 text-xs text-[#737373] truncate font-sans">{card.subtitle}</p>
          </div>
        );
      })}
    </div>
  );
};