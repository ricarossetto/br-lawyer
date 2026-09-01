import React from 'react';
import { Search, Sun, Moon, Bell, ShieldCheck } from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';
import { useAuth } from '../../context/AuthContext';

interface TopbarProps {
  title: string;
  subtitle?: string;
  onOpenSearch: () => void;
}

export const Topbar: React.FC<TopbarProps> = ({ title, subtitle, onOpenSearch }) => {
  const { theme, toggleTheme } = useTheme();
  const { session } = useAuth();

  return (
    <header className="h-14 bg-slate-900/90 backdrop-blur border-b border-slate-800 px-6 flex items-center justify-between shrink-0 select-none">
      {/* Title / Breadcrumb */}
      <div>
        <h1 className="text-sm font-semibold text-slate-100">{title}</h1>
        {subtitle && <p className="text-xs text-slate-400">{subtitle}</p>}
      </div>

      {/* Center Command Bar Trigger */}
      <button
        onClick={onOpenSearch}
        className="flex items-center gap-3 px-3.5 py-1.5 bg-slate-950 border border-slate-800 hover:border-slate-700 rounded-md text-xs text-slate-400 hover:text-slate-200 transition-all w-80 shadow-xs cursor-pointer"
      >
        <Search className="h-3.5 w-3.5 text-slate-400" />
        <span className="flex-1 text-left">Buscar processos, contatos, notas...</span>
        <kbd className="px-1.5 py-0.5 text-[10px] font-mono bg-slate-800 text-slate-300 rounded border border-slate-700">
          Ctrl+K
        </kbd>
      </button>

      {/* Actions */}
      <div className="flex items-center gap-2">
        {/* Backend Connected Indicator */}
        <div className="flex items-center gap-1.5 px-2.5 py-1 bg-emerald-500/10 border border-emerald-500/30 rounded text-[11px] font-medium text-emerald-400">
          <ShieldCheck className="h-3.5 w-3.5" />
          <span>WildFly REST v8</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-1.5 rounded-md text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors cursor-pointer"
          title={theme === 'dark' ? 'Mudar para tema claro' : 'Mudar para tema escuro'}
        >
          {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        </button>

        {/* Notifications */}
        <button
          className="p-1.5 rounded-md text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors relative cursor-pointer"
          title="Notificações & Prazos"
        >
          <Bell className="h-4 w-4" />
          <span className="absolute top-1 right-1 h-2 w-2 rounded-full bg-indigo-500 ring-2 ring-slate-900" />
        </button>
      </div>
    </header>
  );
};