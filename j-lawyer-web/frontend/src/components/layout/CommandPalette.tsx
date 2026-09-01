import React, { useEffect, useState } from 'react';
import { Search, Briefcase, FileText, User, ArrowRight, CornerDownLeft, Sparkles } from 'lucide-react';
import { searchService } from '../../api/searchService';
import { RestfulSearchHitV8 } from '../../types/search';
import { formatCNJ } from '../../utils/formatters';

interface CommandPaletteProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectCase: (caseId: string) => void;
}

export const CommandPalette: React.FC<CommandPaletteProps> = ({
  isOpen,
  onClose,
  onSelectCase,
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
          // Open
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
    }, 250);

    return () => clearTimeout(timer);
  }, [query, isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto flex items-start justify-center pt-20 p-4">
      {/* Backdrop */}
      <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs" onClick={onClose} />

      {/* Modal Box */}
      <div className="relative w-full max-w-2xl bg-slate-900 border border-slate-800 rounded-xl shadow-2xl overflow-hidden z-10">
        {/* Search Input Bar */}
        <div className="px-4 py-3 border-b border-slate-800 flex items-center gap-3">
          <Search className="h-4 w-4 text-indigo-400 shrink-0" />
          <input
            autoFocus
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Digite o número CNJ, cliente, palavra-chave ou comando..."
            className="w-full bg-transparent text-sm text-slate-100 placeholder-slate-500 focus:outline-none"
          />
          {isLoading && (
            <svg className="animate-spin h-4 w-4 text-indigo-400 shrink-0" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
            </svg>
          )}
        </div>

        {/* Results Stream */}
        <div className="max-h-96 overflow-y-auto p-2">
          {query.trim() === '' ? (
            <div className="p-6 text-center text-slate-500 text-xs">
              <Sparkles className="h-6 w-6 mx-auto mb-2 text-slate-600" />
              <p>Busque em tempo real por NPU CNJ, nome das partes ou conteúdo integral de peças.</p>
              <p className="mt-1 font-mono text-[11px] text-slate-600">Ex: 5001234 ou "revisional"</p>
            </div>
          ) : results.length === 0 && !isLoading ? (
            <div className="p-6 text-center text-slate-500 text-xs">
              Nenhum resultado encontrado para "{query}".
            </div>
          ) : (
            <div className="space-y-1">
              {results.map((hit) => {
                const targetCaseId = hit.archiveFileId || hit.caseId || (hit.entityType === 'case' ? hit.id : undefined);
                const displayTitle = hit.archiveFileNumber
                  ? `${hit.archiveFileNumber} — ${hit.archiveFileName || hit.fileName || ''}`
                  : (hit.title || hit.fileName || hit.id);
                const displaySummary = hit.snippet || hit.summary;
                const Icon = hit.fileName ? FileText : Briefcase;

                return (
                  <button
                    key={hit.id}
                    onClick={() => {
                      if (targetCaseId) {
                        onSelectCase(targetCaseId);
                        onClose();
                      }
                    }}
                    className="w-full px-3 py-2.5 rounded-lg flex items-center justify-between hover:bg-slate-800/80 transition-colors text-left group cursor-pointer"
                  >
                    <div className="flex items-center gap-3 overflow-hidden">
                      <div className="p-1.5 rounded-md bg-slate-800 text-slate-400 group-hover:text-indigo-400 group-hover:bg-indigo-950/50 transition-colors">
                        <Icon className="h-4 w-4" />
                      </div>
                      <div className="truncate">
                        <div className="text-xs font-medium text-slate-200 group-hover:text-white truncate">
                          {formatCNJ(displayTitle)}
                        </div>
                        {displaySummary && (
                          <div className="text-[11px] text-slate-400 truncate mt-0.5">{displaySummary}</div>
                        )}
                      </div>
                    </div>
                    <ArrowRight className="h-3.5 w-3.5 text-slate-600 group-hover:text-indigo-400 transition-colors shrink-0 ml-2" />
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-4 py-2 border-t border-slate-800/80 bg-slate-950/60 flex items-center justify-between text-[10px] text-slate-500 font-mono">
          <div className="flex items-center gap-3">
            <span>↑↓ navegar</span>
            <span>↵ selecionar</span>
            <span>esc fechar</span>
          </div>
          <span>Índice Lucene v8</span>
        </div>
      </div>
    </div>
  );
};