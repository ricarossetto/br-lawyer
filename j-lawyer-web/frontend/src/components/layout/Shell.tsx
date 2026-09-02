import React, { useState } from 'react';
import { Sidebar, NavItemKey } from './Sidebar';
import { Topbar } from './Topbar';
import { CommandPalette } from './CommandPalette';

interface ShellProps {
  currentView: NavItemKey;
  onNavigate: (view: NavItemKey) => void;
  title: string;
  subtitle?: string;
  children: React.ReactNode;
  onSelectCaseFromSearch: (caseId: string) => void;
  onOpenNewTask?: () => void;
}

export const Shell: React.FC<ShellProps> = ({
  currentView,
  onNavigate,
  title,
  subtitle,
  children,
  onSelectCaseFromSearch,
  onOpenNewTask,
}) => {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-[#030304] text-slate-100 selection:bg-[#F7931A]/30 selection:text-white relative font-sans">
      {/* Ambient background glows */}
      <div className="pointer-events-none fixed -top-24 right-1/4 w-96 h-96 bg-[#EA580C]/5 rounded-full blur-3xl animate-glow-pulse" />
      <div className="pointer-events-none fixed -bottom-24 left-1/3 w-80 h-80 bg-[#F7931A]/5 rounded-full blur-3xl animate-glow-pulse" />

      {/* Sidebar */}
      <Sidebar
        currentView={currentView}
        onNavigate={onNavigate}
        isCollapsed={isSidebarCollapsed}
        onToggleCollapse={() => setIsSidebarCollapsed((prev) => !prev)}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Topbar
          title={title}
          subtitle={subtitle}
          onOpenSearch={() => setIsSearchOpen(true)}
        />
        <main className="flex-1 overflow-y-auto p-6 bg-[#030304] bg-grid-pattern relative">
          {children}
        </main>
      </div>

      {/* Global Command Palette */}
      <CommandPalette
        isOpen={isSearchOpen}
        onClose={() => setIsSearchOpen(false)}
        onSelectCase={onSelectCaseFromSearch}
        onNavigate={onNavigate}
        onOpenNewTask={onOpenNewTask}
      />
    </div>
  );
};