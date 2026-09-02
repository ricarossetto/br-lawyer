import React, { useEffect, useState } from 'react';
import {
  Briefcase,
  Search,
  Filter,
  User,
  Clock,
  ChevronLeft,
  ChevronRight,
  Plus,
  ArrowUpDown,
} from 'lucide-react';
import { casesService } from '../../api/casesService';
import { RestfulCaseOverviewV8 } from '../../types/cases';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { CaseInspectorDrawer } from './CaseInspectorDrawer';
import { formatCNJ, formatDate, formatBRL } from '../../utils/formatters';
import { cn } from '../../utils/cn';

interface CasesListProps {
  onSelectCase: (caseId: string) => void;
}

export const CasesList: React.FC<CasesListProps> = ({ onSelectCase }) => {
  const [cases, setCases] = useState<RestfulCaseOverviewV8[]>([]);
  const [total, setTotal] = useState(0);
  const [offset, setOffset] = useState(0);
  const [limit] = useState(25);
  const [filter, setFilter] = useState<'all' | 'open' | 'closed'>('open');
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [selectedCaseForDrawer, setSelectedCaseForDrawer] = useState<RestfulCaseOverviewV8 | null>(null);

  const [error, setError] = useState<string | null>(null);

  const fetchCases = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const page = await casesService.getCasesPage(offset, limit, filter, searchQuery);
      setCases(page.items || []);
      setTotal(page.total || 0);
    } catch (err: any) {
      setError(err?.message || 'Erro ao carregar processos do servidor.');
      setCases([]);
      setTotal(0);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchCases();
    }, 200);
    return () => clearTimeout(timer);
  }, [offset, filter, searchQuery]);

  return (
    <div className="space-y-4">
      {/* Control Ribbon (Search, Filters, New Case Button) */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 bg-[#0F1115] border border-white/10 p-4 rounded-2xl shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)]">
        {/* Filter Tabs */}
        <div className="flex items-center gap-1.5 bg-[#030304] p-1 rounded-full border border-white/10">
          <button
            onClick={() => { setFilter('open'); setOffset(0); }}
            className={cn(
              'px-3.5 py-1 text-xs font-medium rounded-full transition-all cursor-pointer',
              filter === 'open' ? 'bg-[#F7931A]/20 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold' : 'text-slate-400 hover:text-slate-200'
            )}
          >
            Em Andamento
          </button>
          <button
            onClick={() => { setFilter('all'); setOffset(0); }}
            className={cn(
              'px-3.5 py-1 text-xs font-medium rounded-full transition-all cursor-pointer',
              filter === 'all' ? 'bg-[#F7931A]/20 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold' : 'text-slate-400 hover:text-slate-200'
            )}
          >
            Todos os Processos
          </button>
          <button
            onClick={() => { setFilter('closed'); setOffset(0); }}
            className={cn(
              'px-3.5 py-1 text-xs font-medium rounded-full transition-all cursor-pointer',
              filter === 'closed' ? 'bg-[#F7931A]/20 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold' : 'text-slate-400 hover:text-slate-200'
            )}
          >
            Arquivados
          </button>
        </div>

        {/* Search & Actions */}
        <div className="flex items-center gap-2.5">
          <Input
            type="text"
            value={searchQuery}
            onChange={(e) => { setSearchQuery(e.target.value); setOffset(0); }}
            placeholder="Filtrar por NPU, título, advogado..."
            leftIcon={<Search className="h-3.5 w-3.5" />}
            className="w-72"
          />
          <Button variant="primary" size="sm" leftIcon={<Plus className="h-3.5 w-3.5" />}>
            Novo Processo
          </Button>
        </div>
      </div>

      {/* Dense Table */}
      <div className="bg-[#0F1115] border border-white/10 rounded-2xl overflow-hidden shadow-[0_0_30px_-10px_rgba(247,147,26,0.1)]">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 text-[11px] font-semibold text-slate-400 uppercase tracking-wider bg-[#030304]/60 font-mono select-none">
                <th className="py-2.5 px-4">Número CNJ / Pasta</th>
                <th className="py-2.5 px-4">Ação / Título</th>
                <th className="py-2.5 px-4">Área</th>
                <th className="py-2.5 px-4">Advogado</th>
                <th className="py-2.5 px-4">Valor Causa</th>
                <th className="py-2.5 px-4">Status</th>
                <th className="py-2.5 px-4">Atualização</th>
                <th className="py-2.5 px-4 text-right">Ação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5 text-xs">
              {isLoading ? (
                <tr>
                  <td colSpan={8} className="py-8 text-center text-slate-500">
                    <svg className="animate-spin h-5 w-5 text-[#F7931A] mx-auto mb-2" viewBox="0 0 24 24" fill="none">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                    </svg>
                    <span>Carregando processos do servidor WildFly...</span>
                  </td>
                </tr>
              ) : cases.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-8 text-center text-slate-500">
                    Nenhum processo encontrado com os filtros selecionados.
                  </td>
                </tr>
              ) : (
                cases.map((c) => {
                  const isSelected = selectedCaseForDrawer?.id === c.id;
                  return (
                    <tr
                      key={c.id}
                      onClick={() => setSelectedCaseForDrawer(c)}
                      className={cn(
                        'dense-table-row cursor-pointer group hover:bg-white/5 transition-colors',
                        isSelected && 'selected'
                      )}
                    >
                      <td className="py-3 px-4 font-mono font-medium text-slate-200 group-hover:text-[#FFD600] whitespace-nowrap">
                        {formatCNJ(c.fileNumber)}
                      </td>
                      <td className="py-3 px-4 font-medium text-slate-100 max-w-sm truncate">
                        {c.name}
                      </td>
                      <td className="py-3 px-4 text-slate-400 whitespace-nowrap">
                        <Badge variant="mono" size="sm">
                          {c.subjectField || 'Geral'}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-slate-300 flex items-center gap-1.5 whitespace-nowrap">
                        <User className="h-3 w-3 text-slate-500" />
                        <span>{c.lawyer || '—'}</span>
                      </td>
                      <td className="py-3 px-4 font-mono font-bold text-[#FFD600] whitespace-nowrap">
                        {formatBRL(c.claimValue)}
                      </td>
                      <td className="py-3 px-4 whitespace-nowrap">
                        <Badge variant={c.archived ? 'neutral' : 'active'} size="sm">
                          {c.archived ? 'Arquivado' : 'Ativo'}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-slate-400 font-mono whitespace-nowrap">
                        {formatDate(c.dateChanged)}
                      </td>
                      <td className="py-3 px-4 text-right whitespace-nowrap">
                        <Button
                          variant="ghost"
                          size="xs"
                          onClick={(e) => {
                            e.stopPropagation();
                            onSelectCase(c.id);
                          }}
                        >
                          Detalhe
                        </Button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Footer */}
        <div className="px-5 py-3 border-t border-white/10 bg-[#030304]/40 flex items-center justify-between text-xs text-slate-400 font-mono">
          <span>
            Mostrando <span className="font-mono font-medium text-slate-200">{cases.length}</span> de{' '}
            <span className="font-mono font-medium text-slate-200">{total}</span> processos
          </span>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="xs"
              disabled={offset === 0}
              onClick={() => setOffset((prev) => Math.max(0, prev - limit))}
              leftIcon={<ChevronLeft className="h-3 w-3" />}
            >
              Anterior
            </Button>
            <Button
              variant="outline"
              size="xs"
              disabled={offset + limit >= total}
              onClick={() => setOffset((prev) => prev + limit)}
              rightIcon={<ChevronRight className="h-3 w-3" />}
            >
              Próxima
            </Button>
          </div>
        </div>
      </div>

      {/* Inspector Lateral (Right Drawer) */}
      <CaseInspectorDrawer
        selectedCase={selectedCaseForDrawer}
        onClose={() => setSelectedCaseForDrawer(null)}
        onOpenFullDetail={onSelectCase}
      />
    </div>
  );
};