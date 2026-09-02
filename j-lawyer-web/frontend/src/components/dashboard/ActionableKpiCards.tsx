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
    urgent: 'text-rose-400 bg-rose-500/10 border-rose-500/30 shadow-[0_0_12px_rgba(244,63,94,0.2)]',
    warning: 'text-[#FFD600] bg-[#FFD600]/10 border-[#FFD600]/30 shadow-[0_0_12px_rgba(255,214,0,0.2)]',
    amber: 'text-amber-400 bg-amber-500/10 border-amber-500/30 shadow-[0_0_12px_rgba(245,158,11,0.2)]',
    active: 'text-[#F7931A] bg-[#EA580C]/15 border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)]',
    success: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30 shadow-[0_0_12px_rgba(16,185,129,0.2)]',
    neutral: 'text-slate-400 bg-white/5 border-white/10',
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
            className={`p-5 bg-[#0F1115] border border-white/10 rounded-2xl shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)] transition-all duration-200 select-none group ${
              card.onClick ? 'hover:-translate-y-1 hover:border-[#F7931A]/50 hover:shadow-[0_0_25px_-5px_rgba(247,147,26,0.25)] cursor-pointer' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <span className="text-xs font-medium text-slate-400 font-sans">{card.title}</span>
              <div className={`p-2.5 rounded-xl border transition-all duration-200 ${style}`}>
                <Icon className="h-4 w-4" />
              </div>
            </div>

            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-3xl font-bold text-white font-mono tracking-tight group-hover:text-[#FFD600] transition-colors">
                {card.value}
              </span>
            </div>

            <p className="mt-1 text-[11px] text-slate-400 truncate">{card.subtitle}</p>
          </div>
        );
      })}
    </div>
  );
};