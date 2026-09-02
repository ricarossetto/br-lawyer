import React from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  LayoutDashboard,
  Briefcase,
  Scale,
  ListTodo,
  Calendar,
  Users,
  FileText,
  DollarSign,
  Clock,
  Sparkles,
  Settings,
  LogOut,
  ChevronLeft,
  ChevronRight,
  User,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { workflowService } from '../../api/workflowService';
import { cn } from '../../utils/cn';

export type NavItemKey =
  | 'dashboard'
  | 'cases'
  | 'publications'
  | 'tasks'
  | 'calendar'
  | 'clients'
  | 'documents'
  | 'finance'
  | 'timesheets'
  | 'assistant'
  | 'settings';

interface SidebarProps {
  currentView: NavItemKey;
  onNavigate: (view: NavItemKey) => void;
  isCollapsed: boolean;
  onToggleCollapse: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentView,
  onNavigate,
  isCollapsed,
  onToggleCollapse,
}) => {
  const { session, logout, isAdmin } = useAuth();

  // Query workflow dashboard for real dynamic operational badges
  const { data: dashboard } = useQuery({
    queryKey: ['workflow-dashboard'],
    queryFn: () => workflowService.getDashboard(),
    staleTime: 1000 * 30, // 30s
    refetchInterval: 1000 * 60, // 1 min poll
  });

  const untreatedPubs = dashboard?.totalUntreatedPublications ?? 0;
  const overdueTasks = dashboard?.totalOverdueTasks ?? 0;
  const dueTodayTasks = dashboard?.totalDueTodayTasks ?? 0;

  const navItems: Array<{
    key: NavItemKey;
    label: string;
    icon: React.ComponentType<{ className?: string }>;
    isFunctional: boolean;
    badge?: number | string;
    badgeColor?: 'red' | 'amber' | 'blue' | 'gray';
  }> = [
    { key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, isFunctional: true },
    { key: 'cases', label: 'Processos', icon: Briefcase, isFunctional: true },
    {
      key: 'publications',
      label: 'Publicações',
      icon: Scale,
      isFunctional: true,
      badge: untreatedPubs > 0 ? untreatedPubs : undefined,
      badgeColor: untreatedPubs > 0 ? 'amber' : 'gray',
    },
    {
      key: 'tasks',
      label: 'Tarefas',
      icon: ListTodo,
      isFunctional: true,
      badge:
        overdueTasks > 0
          ? `${overdueTasks} atrasada(s)`
          : dueTodayTasks > 0
          ? `${dueTodayTasks} hoje`
          : undefined,
      badgeColor: overdueTasks > 0 ? 'red' : 'amber',
    },
    { key: 'calendar', label: 'Agenda', icon: Calendar, isFunctional: false },
    { key: 'clients', label: 'Clientes', icon: Users, isFunctional: false },
    { key: 'documents', label: 'Documentos', icon: FileText, isFunctional: false },
    { key: 'finance', label: 'Financeiro', icon: DollarSign, isFunctional: false },
    { key: 'timesheets', label: 'Apontamentos', icon: Clock, isFunctional: false },
    { key: 'assistant', label: 'Assistente IA', icon: Sparkles, isFunctional: false },
  ];

  return (
    <aside
      className={cn(
        'h-screen bg-[#0A0A0A] border-r border-[#262626] flex flex-col justify-between transition-all duration-150 select-none z-30',
        isCollapsed ? 'w-16' : 'w-60'
      )}
    >
      {/* Brand Header */}
      <div>
        <div className="h-14 px-4 flex items-center justify-between border-b border-[#262626]">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-7 w-7 bg-[#141414] border border-[#262626] flex items-center justify-center shrink-0">
              <img src="/icons/atrium-emblem.svg" alt="BR-LAWYER" className="h-4 w-4" />
            </div>
            {!isCollapsed && (
              <div className="flex flex-col">
                <span className="font-heading font-black text-sm text-[#FAFAFA] tracking-tight leading-tight">BR-LAWYER</span>
                <span className="text-[9px] font-bold text-[#737373] font-mono tracking-widest uppercase">ATRIUM LEGAL</span>
              </div>
            )}
          </div>
          <button
            onClick={onToggleCollapse}
            className="p-1 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#141414] transition-colors"
            title={isCollapsed ? 'Expandir Sidebar' : 'Recolher Sidebar'}
          >
            {isCollapsed ? <ChevronRight className="h-4 w-4" /> : <ChevronLeft className="h-4 w-4" />}
          </button>
        </div>

        {/* Navigation List */}
        <nav className="p-2 space-y-1 mt-2">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = currentView === item.key;
            return (
              <button
                key={item.key}
                onClick={() => onNavigate(item.key)}
                className={cn(
                  'w-full flex items-center justify-between px-3 py-2.5 rounded-none text-xs transition-colors duration-150 group border-l-2',
                  isActive
                    ? 'border-[#FF3D00] bg-[#141414] text-[#FAFAFA] font-bold'
                    : 'border-transparent text-[#737373] hover:text-[#FAFAFA] hover:bg-[#141414] font-medium'
                )}
                title={isCollapsed ? item.label : undefined}
              >
                <div className="flex items-center gap-3 truncate">
                  <Icon
                    className={cn(
                      'h-4 w-4 shrink-0',
                      isActive ? 'text-[#FF3D00]' : 'text-[#737373] group-hover:text-[#FAFAFA]'
                    )}
                  />
                  {!isCollapsed && (
                    <div className="flex items-center gap-1.5 truncate">
                      <span className="truncate">{item.label}</span>
                      {!item.isFunctional && (
                        <span className="text-[9px] font-mono text-[#525252] uppercase tracking-wider">
                          breve
                        </span>
                      )}
                    </div>
                  )}
                </div>

                {!isCollapsed && item.badge !== undefined && (
                  <span
                    className={cn(
                      'px-1.5 py-0.2 rounded-none font-mono text-[9px] font-bold uppercase tracking-wider shrink-0 border',
                      item.badgeColor === 'red'
                        ? 'bg-rose-950/30 text-rose-400 border-rose-600/40'
                        : 'bg-[#1A1A1A] text-[#FF3D00] border-[#262626]'
                    )}
                  >
                    {item.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>

      {/* User Footer */}
      <div className="p-3 border-t border-[#262626] bg-[#0A0A0A]">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-7 w-7 rounded-none bg-[#141414] border border-[#262626] flex items-center justify-center text-[#FAFAFA] shrink-0 font-mono text-xs font-bold">
              {session?.username?.[0]?.toUpperCase() || 'U'}
            </div>
            {!isCollapsed && (
              <div className="flex flex-col truncate">
                <span className="text-xs font-semibold text-[#FAFAFA] truncate">
                  {session?.username || 'Usuário'}
                </span>
                <span className="text-[10px] text-[#737373] font-mono uppercase tracking-wider truncate">
                  {isAdmin ? 'Administrador' : 'Advogado'}
                </span>
              </div>
            )}
          </div>
          {!isCollapsed && (
            <button
              onClick={logout}
              className="p-1 rounded-none text-[#737373] hover:text-rose-400 hover:bg-[#141414] transition-colors"
              title="Encerrar Sessão"
            >
              <LogOut className="h-3.5 w-3.5" />
            </button>
          )}
        </div>
      </div>
    </aside>
  );
};