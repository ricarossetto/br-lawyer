import React from 'react';
import {
  LayoutDashboard,
  Briefcase,
  Calendar,
  FileText,
  Settings,
  Scale,
  LogOut,
  ChevronLeft,
  ChevronRight,
  User,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { cn } from '../../utils/cn';

export type NavItemKey = 'dashboard' | 'cases' | 'calendar' | 'documents' | 'settings';

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

  const navItems: Array<{ key: NavItemKey; label: string; icon: React.ComponentType<{ className?: string }> }> = [
    { key: 'dashboard', label: 'Central Diária', icon: LayoutDashboard },
    { key: 'cases', label: 'Processos', icon: Briefcase },
    { key: 'calendar', label: 'Prazos & Agenda', icon: Calendar },
    { key: 'documents', label: 'Documentos', icon: FileText },
    { key: 'settings', label: 'Configurações', icon: Settings },
  ];

  return (
    <aside
      className={cn(
        'h-screen bg-slate-950 border-r border-slate-800 flex flex-col justify-between transition-all duration-200 select-none z-30',
        isCollapsed ? 'w-16' : 'w-60'
      )}
    >
      {/* Brand Header */}
      <div>
        <div className="h-14 px-4 flex items-center justify-between border-b border-slate-800">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-8 w-8 rounded-lg bg-indigo-600/20 border border-indigo-500/40 flex items-center justify-center text-indigo-400 shrink-0">
              <Scale className="h-4 w-4" />
            </div>
            {!isCollapsed && (
              <div className="flex flex-col">
                <span className="font-semibold text-xs text-slate-100 tracking-wider">BR-LAWYER</span>
                <span className="text-[10px] text-slate-400 font-mono">MINERAL UI</span>
              </div>
            )}
          </div>
          <button
            onClick={onToggleCollapse}
            className="p-1 rounded-md text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
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
                  'w-full flex items-center gap-3 px-3 py-2 rounded-md text-xs font-medium transition-all duration-150',
                  isActive
                    ? 'bg-indigo-600/15 text-indigo-300 border border-indigo-500/30'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900 border border-transparent'
                )}
                title={isCollapsed ? item.label : undefined}
              >
                <Icon className={cn('h-4 w-4 shrink-0', isActive ? 'text-indigo-400' : 'text-slate-400')} />
                {!isCollapsed && <span>{item.label}</span>}
              </button>
            );
          })}
        </nav>
      </div>

      {/* User Footer */}
      <div className="p-3 border-t border-slate-800 bg-slate-950/60">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-8 w-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300 shrink-0">
              <User className="h-4 w-4" />
            </div>
            {!isCollapsed && (
              <div className="flex flex-col truncate">
                <span className="text-xs font-medium text-slate-200 truncate">{session?.principal || 'Usuário'}</span>
                <span className="text-[10px] text-slate-400 truncate font-mono">
                  {isAdmin ? 'Administrador' : 'Advogado'}
                </span>
              </div>
            )}
          </div>
          {!isCollapsed && (
            <button
              onClick={() => logout()}
              className="p-1.5 rounded-md text-slate-400 hover:text-red-400 hover:bg-slate-900 transition-colors"
              title="Encerrar Sessão"
            >
              <LogOut className="h-4 w-4" />
            </button>
          )}
        </div>
      </div>
    </aside>
  );
};