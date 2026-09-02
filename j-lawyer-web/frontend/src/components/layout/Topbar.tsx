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
    <header className="h-14 bg-[#0A0A0A] border-b border-[#262626] px-6 flex items-center justify-between shrink-0 select-none z-20">
      {/* Title / Breadcrumb */}
      <div>
        <h1 className="text-sm font-bold text-[#FAFAFA] font-heading tracking-tight">{title}</h1>
        {subtitle && <p className="text-xs text-[#737373] font-sans">{subtitle}</p>}
      </div>

      {/* Center Command Bar Trigger */}
      <button
        onClick={onOpenSearch}
        className="flex items-center gap-3 px-4 py-1.5 bg-[#141414] border border-[#262626] hover:border-[#737373] rounded-none text-xs text-[#737373] hover:text-[#FAFAFA] transition-colors w-80 cursor-pointer group"
      >
        <Search className="h-3.5 w-3.5 text-[#737373] group-hover:text-[#FAFAFA] transition-colors" />
        <span className="flex-1 text-left truncate">Buscar processos, contatos, notas...</span>
        <kbd className="px-2 py-0.5 text-[10px] font-mono bg-[#1F1F1F] text-[#FAFAFA] rounded-none border border-[#262626]">
          Ctrl+K
        </kbd>
      </button>

      {/* Actions */}
      <div className="flex items-center gap-2">
        {/* Backend Connected Indicator */}
        <div className="flex items-center gap-1.5 px-3 py-1 bg-[#141414] border border-[#262626] rounded-none text-[10px] font-mono font-bold uppercase tracking-wider text-emerald-400">
          <ShieldCheck className="h-3.5 w-3.5" />
          <span>WildFly REST v8</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-2 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#141414] transition-colors cursor-pointer border border-transparent hover:border-[#262626]"
          title={theme === 'dark' ? 'Mudar para tema claro' : 'Mudar para tema escuro'}
        >
          {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        </button>

        {/* Notifications */}
        <button
          className="p-2 rounded-none text-[#737373] hover:text-[#FF3D00] hover:bg-[#141414] transition-colors relative cursor-pointer border border-transparent hover:border-[#262626]"
          title="Notificações & Prazos"
        >
          <Bell className="h-4 w-4" />
          <span className="absolute top-1.5 right-1.5 h-1.5 w-1.5 bg-[#FF3D00]" />
        </button>
      </div>
    </header>
  );
};