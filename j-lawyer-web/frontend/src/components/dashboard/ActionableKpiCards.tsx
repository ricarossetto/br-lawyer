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
    urgent: 'text-rose-400 bg-rose-500/10 border-rose-500/30',
    warning: 'text-amber-400 bg-amber-500/10 border-amber-500/30',
    amber: 'text-amber-300 bg-amber-500/10 border-amber-500/30',
    active: 'text-indigo-400 bg-indigo-500/10 border-indigo-500/30',
    success: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30',
    neutral: 'text-slate-400 bg-slate-800/40 border-slate-700/40',
  };

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
      {cards.map((card, idx) => {
        const Icon = card.icon;
        const style = colorStyles[card.accent as keyof typeof colorStyles];

        return (
          <div
            key={idx}
            onClick={card.onClick}
            className={`p-4 bg-slate-900 border border-slate-800 rounded-xl shadow-xs transition-all duration-150 select-none ${
              card.onClick ? 'hover:border-slate-700 hover:bg-slate-900/90 cursor-pointer' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <span className="text-xs font-medium text-slate-400">{card.title}</span>
              <div className={`p-2 rounded-lg border ${style}`}>
                <Icon className="h-4 w-4" />
              </div>
            </div>

            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-2xl font-bold text-slate-100 font-mono tracking-tight">
                {card.value}
              </span>
            </div>

            <p className="mt-1 text-[11px] text-slate-500 truncate">{card.subtitle}</p>
          </div>
        );
      })}
    </div>
  );
};