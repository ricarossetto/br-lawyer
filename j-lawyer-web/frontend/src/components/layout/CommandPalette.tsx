import React, { useEffect, useState } from 'react';
import {
  Search,
  Briefcase,
  FileText,
  User,
  ArrowRight,
  CornerDownLeft,
  Sparkles,
  Scale,
  ListTodo,
  Calendar,
  Plus,
  LayoutDashboard,
} from 'lucide-react';
import { searchService } from '../../api/searchService';
import { RestfulSearchHitV8 } from '../../types/search';
import { formatCNJ } from '../../utils/formatters';
import { NavItemKey } from './Sidebar';

interface CommandPaletteProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectCase: (caseId: string) => void;
  onNavigate?: (view: NavItemKey) => void;
  onOpenNewTask?: () => void;
}

export const CommandPalette: React.FC<CommandPaletteProps> = ({
  isOpen,
  onClose,
  onSelectCase,
  onNavigate,
  onOpenNewTask,
}) => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<RestfulSearchHitV8[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        if (isOpen) {
          onClose();
        } else {
          setQuery('');
          setResults([]);
        }
      }
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  useEffect(() => {
    if (!query.trim() || !isOpen) {
      setResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      setIsLoading(true);
      try {
        const hits = await searchService.searchFulltext(query, 15);
        setResults(hits || []);
      } catch (err) {
        setResults([]);
      } finally {
        setIsLoading(false);
      }
    }, 200);

    return () => clearTimeout(timer);
  }, [query, isOpen]);

  if (!isOpen) return null;

  const quickNavigations: Array<{
    label: string;
    description: string;
    icon: React.ComponentType<{ className?: string }>;
    action: () => void;
  }> = [
    {
      label: 'Criar Nova Tarefa / Providência',
      description: 'Cadastre um novo prazo ou atividade operacional',
      icon: Plus,
      action: () => {
        onClose();
        if (onOpenNewTask) onOpenNewTask();
      },
    },
    {
      label: 'Inbox de Publicações & Intimações',
      description: 'Triagem e tratamento de publicações oficiais dos tribunais',
      icon: Scale,
      action: () => {
        onClose();
        if (onNavigate) onNavigate('publications');
      },
    },
    {
      label: 'Quadro de Tarefas & Kanban',
      description: 'Acompanhamento do fluxo operacional e prazos',
      icon: ListTodo,
      action: () => {
        onClose();
        if (onNavigate) onNavigate('tasks');
      },
    },
    {
      label: 'Central Diária de Comando (Cockpit)',
      description: 'Métricas executivas, alertas críticos e fila do dia',
      icon: LayoutDashboard,
      action: () => {
        onClose();
        if (onNavigate) onNavigate('dashboard');
      },
    },
    {
      label: 'Gestão de Processos Judiciais',
      description: 'Autos digitais, histórico e documentos',
      icon: Briefcase,
      action: () => {
        onClose();
        if (onNavigate) onNavigate('cases');
      },
    },
    {
      label: 'Prazos & Agenda Processual',
      description: 'Audiências, perícias e prazos fatais',
      icon: Calendar,
      action: () => {
        onClose();
        if (onNavigate) onNavigate('calendar');
      },
    },
  ];

  const filteredNavigations = query.trim()
    ? quickNavigations.filter((n) =>
        n.label.toLowerCase().includes(query.toLowerCase()) ||
        n.description.toLowerCase().includes(query.toLowerCase())
      )
    : quickNavigations;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto flex items-start justify-center pt-20 p-4">
      {/* Backdrop */}
      <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs" onClick={onClose} />

      {/* Modal Box */}
      <div className="relative w-full max-w-2xl bg-slate-900 border border-slate-800 rounded-xl shadow-2xl overflow-hidden z-10 text-xs">
        {/* Search Input Bar */}
        <div className="px-4 py-3 border-b border-slate-800 flex items-center gap-3 bg-slate-950/60">
          <Search className="h-4 w-4 text-indigo-400 shrink-0" />
          <input
            autoFocus
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Buscar por CNJ, processo, publicação, comando ou atalho..."
            className="w-full bg-transparent text-xs text-slate-100 placeholder-slate-500 focus:outline-none"
          />
          {isLoading && (
            <svg
              className="animate-spin h-4 w-4 text-indigo-400 shrink-0"
              viewBox="0 0 24 24"
              fill="none"
            >
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
            </svg>
          )}
        </div>

        {/* Results Stream */}
        <div className="max-h-96 overflow-y-auto p-2 space-y-3">
          {/* Quick Actions / Shortcuts */}
          {filteredNavigations.length > 0 && (
            <div className="space-y-1">
              <div className="px-3 py-1 text-[10px] uppercase font-semibold text-slate-500 tracking-wider">
                Ações Rápidas & Navegação
              </div>
              {filteredNavigations.map((nav, idx) => {
                const Icon = nav.icon;
                return (
                  <button
                    key={idx}
                    onClick={nav.action}
                    className="w-full p-2.5 rounded-lg bg-slate-950/40 hover:bg-indigo-600/15 border border-slate-800/80 hover:border-indigo-500/40 flex items-center justify-between text-left transition-all duration-150 group"
                  >
                    <div className="flex items-center gap-3">
                      <div className="p-1.5 rounded-md bg-slate-800 group-hover:bg-indigo-600/30 text-slate-400 group-hover:text-indigo-300 transition-colors">
                        <Icon className="h-4 w-4" />
                      </div>
                      <div>
                        <div className="font-semibold text-slate-200 group-hover:text-indigo-200 text-xs">
                          {nav.label}
                        </div>
                        <div className="text-[11px] text-slate-500">{nav.description}</div>
                      </div>
                    </div>
                    <CornerDownLeft className="h-3.5 w-3.5 text-slate-600 group-hover:text-indigo-400" />
                  </button>
                );
              })}
            </div>
          )}

          {/* Search Hits Stream */}
          {query.trim() !== '' && (
            <div className="space-y-1 pt-1 border-t border-slate-800/60">
              <div className="px-3 py-1 text-[10px] uppercase font-semibold text-slate-500 tracking-wider">
                Resultados de Busca Textual & Processos ({results.length})
              </div>
              {results.length === 0 && !isLoading ? (
                <div className="p-4 text-center text-slate-500 text-xs italic">
                  Nenhum processo ou documento encontrado para "{query}".
                </div>
              ) : (
                results.map((hit) => (
                  <button
                    key={hit.id}
                    onClick={() => {
                      onClose();
                      onSelectCase(hit.id);
                    }}
                    className="w-full p-2.5 rounded-lg bg-slate-950/60 hover:bg-slate-800 border border-slate-800 flex items-center justify-between text-left transition-colors group"
                  >
                    <div className="flex items-start gap-3 truncate">
                      <div className="mt-0.5 p-1 rounded bg-slate-800 text-slate-400 group-hover:text-indigo-400">
                        {hit.entityType === 'document' ? (
                          <FileText className="h-3.5 w-3.5" />
                        ) : (
                          <Briefcase className="h-3.5 w-3.5" />
                        )}
                      </div>
                      <div className="truncate">
                        <div className="font-semibold text-slate-200 group-hover:text-indigo-300 text-xs truncate">
                          {hit.title || hit.fileName || hit.archiveFileName || 'Item encontrado'}
                        </div>
                        {(hit.archiveFileNumber || hit.summary) && (
                          <div className="text-[11px] text-slate-400 font-mono truncate">
                            {formatCNJ(hit.archiveFileNumber || hit.summary)}
                          </div>
                        )}
                        {hit.snippet && (
                          <div className="text-[10px] text-slate-500 line-clamp-1 mt-0.5">
                            {hit.snippet}
                          </div>
                        )}
                      </div>
                    </div>
                    <ArrowRight className="h-3.5 w-3.5 text-slate-600 group-hover:text-slate-300 shrink-0 ml-2" />
                  </button>
                ))
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-4 py-2 border-t border-slate-800 bg-slate-950/80 flex items-center justify-between text-[11px] text-slate-500">
          <span>Use as setas para navegar, Enter para selecionar</span>
          <span className="font-mono">ESC para fechar</span>
        </div>
      </div>
    </div>
  );
};