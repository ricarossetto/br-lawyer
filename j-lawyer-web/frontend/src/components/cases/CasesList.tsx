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
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 bg-[#0F0F0F] border border-[#262626] p-5 rounded-none">
        {/* Filter Tabs */}
        <div className="flex items-center gap-1.5 bg-[#0A0A0A] p-1 rounded-none border border-[#262626]">
          <button
            onClick={() => { setFilter('open'); setOffset(0); }}
            className={cn(
              'px-3.5 py-1.5 text-[10px] font-mono uppercase tracking-wider rounded-none transition-colors cursor-pointer',
              filter === 'open' ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold' : 'text-[#737373] hover:text-[#FAFAFA]'
            )}
          >
            Em Andamento
          </button>
          <button
            onClick={() => { setFilter('all'); setOffset(0); }}
            className={cn(
              'px-3.5 py-1.5 text-[10px] font-mono uppercase tracking-wider rounded-none transition-colors cursor-pointer',
              filter === 'all' ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold' : 'text-[#737373] hover:text-[#FAFAFA]'
            )}
          >
            Todos os Processos
          </button>
          <button
            onClick={() => { setFilter('closed'); setOffset(0); }}
            className={cn(
              'px-3.5 py-1.5 text-[10px] font-mono uppercase tracking-wider rounded-none transition-colors cursor-pointer',
              filter === 'closed' ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold' : 'text-[#737373] hover:text-[#FAFAFA]'
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
      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-[#262626] text-[10px] font-bold text-[#737373] uppercase tracking-wider bg-[#0A0A0A] font-mono select-none">
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
            <tbody className="divide-y divide-[#262626] text-xs">
              {isLoading ? (
                <tr>
                  <td colSpan={8} className="py-8 text-center text-[#737373]">
                    <svg className="animate-spin h-5 w-5 text-[#FF3D00] mx-auto mb-2" viewBox="0 0 24 24" fill="none">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                    </svg>
                    <span className="font-mono text-xs">Carregando processos do servidor WildFly...</span>
                  </td>
                </tr>
              ) : cases.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-8 text-center text-[#737373] font-mono text-xs">
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
                        'dense-table-row cursor-pointer group hover:bg-[#141414] transition-colors',
                        isSelected && 'selected'
                      )}
                    >
                      <td className="py-3 px-4 font-mono font-bold text-[#FAFAFA] group-hover:text-[#FF3D00] whitespace-nowrap">
                        {formatCNJ(c.fileNumber)}
                      </td>
                      <td className="py-3 px-4 font-bold text-[#FAFAFA] max-w-sm truncate">
                        {c.name}
                      </td>
                      <td className="py-3 px-4 text-[#737373] whitespace-nowrap">
                        <Badge variant="mono" size="sm">
                          {c.subjectField || 'Geral'}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-[#FAFAFA] flex items-center gap-1.5 whitespace-nowrap">
                        <User className="h-3 w-3 text-[#737373]" />
                        <span className="font-mono text-[11px]">{c.lawyer || '—'}</span>
                      </td>
                      <td className="py-3 px-4 font-mono font-bold text-[#FAFAFA] whitespace-nowrap">
                        {formatBRL(c.claimValue)}
                      </td>
                      <td className="py-3 px-4 whitespace-nowrap">
                        <Badge variant={c.archived ? 'neutral' : 'active'} size="sm">
                          {c.archived ? 'Arquivado' : 'Ativo'}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-[#737373] font-mono whitespace-nowrap text-[11px]">
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
        <div className="px-6 py-3 border-t border-[#262626] bg-[#0A0A0A] flex items-center justify-between text-xs text-[#737373] font-mono">
          <span>
            Mostrando <span className="font-mono font-bold text-[#FAFAFA]">{cases.length}</span> de{' '}
            <span className="font-mono font-bold text-[#FAFAFA]">{total}</span> processos
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