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
    <header className="h-14 bg-[#030304]/85 backdrop-blur-md border-b border-white/10 px-6 flex items-center justify-between shrink-0 select-none z-20">
      {/* Title / Breadcrumb */}
      <div>
        <h1 className="text-sm font-semibold text-slate-100 font-heading">{title}</h1>
        {subtitle && <p className="text-xs text-slate-400 font-sans">{subtitle}</p>}
      </div>

      {/* Center Command Bar Trigger */}
      <button
        onClick={onOpenSearch}
        className="flex items-center gap-3 px-4 py-1.5 bg-[#0F1115] border border-white/10 hover:border-[#F7931A]/50 rounded-full text-xs text-slate-400 hover:text-slate-100 transition-all w-80 shadow-xs hover:shadow-[0_0_15px_-4px_rgba(247,147,26,0.3)] cursor-pointer group"
      >
        <Search className="h-3.5 w-3.5 text-slate-400 group-hover:text-[#F7931A] transition-colors" />
        <span className="flex-1 text-left truncate">Buscar processos, contatos, notas...</span>
        <kbd className="px-2 py-0.5 text-[10px] font-mono bg-[#181B20] text-[#FFD600] rounded-full border border-white/10 shadow-inner">
          Ctrl+K
        </kbd>
      </button>

      {/* Actions */}
      <div className="flex items-center gap-2.5">
        {/* Backend Connected Indicator */}
        <div className="flex items-center gap-1.5 px-3 py-1 bg-emerald-500/10 border border-emerald-500/30 rounded-full text-[11px] font-medium text-emerald-400 shadow-[0_0_10px_-2px_rgba(16,185,129,0.2)]">
          <ShieldCheck className="h-3.5 w-3.5" />
          <span>WildFly REST v8</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-2 rounded-full text-slate-400 hover:text-[#FFD600] hover:bg-white/5 transition-colors cursor-pointer border border-transparent hover:border-white/10"
          title={theme === 'dark' ? 'Mudar para tema claro' : 'Mudar para tema escuro'}
        >
          {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        </button>

        {/* Notifications */}
        <button
          className="p-2 rounded-full text-slate-400 hover:text-[#F7931A] hover:bg-white/5 transition-colors relative cursor-pointer border border-transparent hover:border-white/10"
          title="Notificações & Prazos"
        >
          <Bell className="h-4 w-4" />
          <span className="absolute top-1.5 right-1.5 h-2 w-2 rounded-full bg-[#F7931A] ring-2 ring-[#030304] animate-pulse" />
        </button>
      </div>
    </header>
  );
};