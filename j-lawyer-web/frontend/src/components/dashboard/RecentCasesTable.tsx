import React from 'react';
import { Briefcase, ArrowRight, User } from 'lucide-react';
import { RestfulCaseOverviewV8 } from '../../types/cases';
import { Badge } from '../common/Badge';
import { formatCNJ, formatDate, formatBRL } from '../../utils/formatters';

interface RecentCasesTableProps {
  cases: RestfulCaseOverviewV8[];
  isLoading?: boolean;
  onSelectCase: (caseId: string) => void;
  onViewAll: () => void;
}

export const RecentCasesTable: React.FC<RecentCasesTableProps> = ({
  cases,
  isLoading = false,
  onSelectCase,
  onViewAll,
}) => {
  return (
    <div className="bg-[#0F1115] border border-white/10 rounded-2xl overflow-hidden shadow-[0_0_30px_-10px_rgba(247,147,26,0.1)]">
      <div className="px-5 py-4 border-b border-white/10 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Briefcase className="h-4 w-4 text-[#F7931A]" />
          <h3 className="text-sm font-semibold text-slate-100 font-heading">Processos Recentes em Andamento</h3>
        </div>
        <button
          onClick={onViewAll}
          className="text-xs font-medium text-[#F7931A] hover:text-[#FFD600] flex items-center gap-1 cursor-pointer transition-colors"
        >
          <span>Ver todos os processos</span>
          <ArrowRight className="h-3.5 w-3.5" />
        </button>
      </div>

      <div className="overflow-x-auto">
        {isLoading ? (
          <div className="p-8 text-center text-slate-400 text-xs">Carregando processos do servidor...</div>
        ) : cases.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-xs">
            Nenhum processo em andamento encontrado no banco de dados.
          </div>
        ) : (
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 text-[11px] font-semibold text-slate-400 uppercase tracking-wider bg-[#030304]/60 font-mono">
                <th className="py-2.5 px-4">Número CNJ / Pasta</th>
                <th className="py-2.5 px-4">Título / Ação</th>
                <th className="py-2.5 px-4">Área / Ramo</th>
                <th className="py-2.5 px-4">Responsável</th>
                <th className="py-2.5 px-4">Valor da Causa</th>
                <th className="py-2.5 px-4">Atualização</th>
                <th className="py-2.5 px-4 text-right">Ação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5 text-xs">
              {cases.slice(0, 7).map((c) => (
                <tr
                  key={c.id}
                  onClick={() => onSelectCase(c.id)}
                  className="hover:bg-white/5 transition-colors cursor-pointer group"
                >
                  <td className="py-3 px-4 font-mono font-medium text-slate-200 group-hover:text-[#FFD600] whitespace-nowrap">
                    {formatCNJ(c.fileNumber)}
                  </td>
                  <td className="py-3 px-4 font-medium text-slate-100 max-w-xs truncate">
                    {c.name}
                  </td>
                  <td className="py-3 px-4 text-slate-400">
                    <Badge variant="mono" size="sm">
                      {c.subjectField || 'Geral'}
                    </Badge>
                  </td>
                  <td className="py-3 px-4 text-slate-300 flex items-center gap-1.5 whitespace-nowrap">
                    <User className="h-3 w-3 text-slate-500" />
                    <span>{c.lawyer || 'Não atribuído'}</span>
                  </td>
                  <td className="py-3 px-4 font-mono font-bold text-[#FFD600] whitespace-nowrap">
                    {formatBRL(c.claimValue)}
                  </td>
                  <td className="py-3 px-4 text-slate-400 font-mono whitespace-nowrap">
                    {formatDate(c.dateChanged)}
                  </td>
                  <td className="py-3 px-4 text-right">
                    <span className="text-slate-400 group-hover:text-[#F7931A] font-medium text-[11px] inline-flex items-center gap-1 transition-colors">
                      Abrir <ArrowRight className="h-3 w-3" />
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};