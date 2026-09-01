import React from 'react';
import { Clock, Briefcase, Calendar, FileCheck, ArrowUpRight } from 'lucide-react';
import { cn } from '../../utils/cn';

interface KpiCardProps {
  title: string;
  value: string | number;
  subtitle: string;
  icon: React.ComponentType<{ className?: string }>;
  accentColor: 'urgent' | 'warning' | 'active' | 'success';
  onClick?: () => void;
}

export const ActionableKpiCards: React.FC<{
  activeCasesCount?: number;
  upcomingEventsCount?: number;
  onNavigateToCases: () => void;
  onNavigateToCalendar: () => void;
}> = ({ activeCasesCount = 0, upcomingEventsCount = 0, onNavigateToCases, onNavigateToCalendar }) => {
  const cards: KpiCardProps[] = [
    {
      title: 'Prazos a Vencer (7 Dias)',
      value: upcomingEventsCount,
      subtitle: upcomingEventsCount > 0 ? `${upcomingEventsCount} cadastrados no servidor` : 'Nenhum prazo pendente',
      icon: Clock,
      accentColor: upcomingEventsCount > 0 ? 'urgent' : 'success',
      onClick: onNavigateToCalendar,
    },
    {
      title: 'Processos Ativos',
      value: activeCasesCount,
      subtitle: activeCasesCount > 0 ? `${activeCasesCount} processos em andamento` : 'Nenhum processo ativo',
      icon: Briefcase,
      accentColor: 'active',
      onClick: onNavigateToCases,
    },
    {
      title: 'Agenda e Compromissos',
      value: upcomingEventsCount,
      subtitle: 'Sincronizado com WildFly',
      icon: Calendar,
      accentColor: 'warning',
      onClick: onNavigateToCalendar,
    },
    {
      title: 'Status da Conexão',
      value: 'Online',
      subtitle: 'WildFly EJB & MariaDB Ativos',
      icon: FileCheck,
      accentColor: 'success',
    },
  ];

  const colorStyles = {
    urgent: 'text-red-400 bg-red-500/10 border-red-500/30',
    warning: 'text-amber-400 bg-amber-500/10 border-amber-500/30',
    active: 'text-indigo-400 bg-indigo-500/10 border-indigo-500/30',
    success: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30',
  };

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      {cards.map((card, idx) => {
        const Icon = card.icon;
        return (
          <div
            key={idx}
            onClick={card.onClick}
            className={cn(
              'p-4 bg-slate-900 border border-slate-800 rounded-xl hover:border-slate-700 transition-all select-none',
              card.onClick && 'cursor-pointer group hover:bg-slate-900/90'
            )}
          >
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-medium text-slate-400">{card.title}</span>
              <div className={cn('p-1.5 rounded-lg border', colorStyles[card.accentColor])}>
                <Icon className="h-4 w-4" />
              </div>
            </div>
            <div className="flex items-baseline justify-between">
              <span className="text-2xl font-bold text-slate-100 tracking-tight">{card.value}</span>
              {card.onClick && (
                <ArrowUpRight className="h-4 w-4 text-slate-600 group-hover:text-indigo-400 transition-colors" />
              )}
            </div>
            <p className="text-[11px] text-slate-500 mt-1">{card.subtitle}</p>
          </div>
        );
      })}
    </div>
  );
};