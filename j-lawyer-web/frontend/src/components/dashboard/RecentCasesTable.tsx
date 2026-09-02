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
    <div className="bg-[#0F0F0F] border border-[#262626] rounded-none">
      <div className="px-6 py-4 border-b border-[#262626] flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Briefcase className="h-4 w-4 text-[#FAFAFA]" />
          <h3 className="text-sm font-bold text-[#FAFAFA] font-heading tracking-tight">Processos Recentes em Andamento</h3>
        </div>
        <button
          onClick={onViewAll}
          className="text-xs font-mono uppercase tracking-wider text-[#FF3D00] hover:text-[#E03600] flex items-center gap-1 cursor-pointer transition-colors"
        >
          <span>Ver todos os processos</span>
          <ArrowRight className="h-3.5 w-3.5" />
        </button>
      </div>

      <div className="overflow-x-auto">
        {isLoading ? (
          <div className="p-8 text-center text-[#737373] text-xs font-mono">Carregando processos do servidor...</div>
        ) : cases.length === 0 ? (
          <div className="p-8 text-center text-[#737373] text-xs font-mono">
            Nenhum processo em andamento encontrado no banco de dados.
          </div>
        ) : (
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-[#262626] text-[10px] font-bold text-[#737373] uppercase tracking-wider bg-[#0A0A0A] font-mono">
                <th className="py-2.5 px-4">Número CNJ / Pasta</th>
                <th className="py-2.5 px-4">Título / Ação</th>
                <th className="py-2.5 px-4">Área / Ramo</th>
                <th className="py-2.5 px-4">Responsável</th>
                <th className="py-2.5 px-4">Valor da Causa</th>
                <th className="py-2.5 px-4">Atualização</th>
                <th className="py-2.5 px-4 text-right">Ação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#262626] text-xs font-sans">
              {cases.slice(0, 7).map((c) => (
                <tr
                  key={c.id}
                  onClick={() => onSelectCase(c.id)}
                  className="hover:bg-[#141414] transition-colors cursor-pointer group"
                >
                  <td className="py-3 px-4 font-mono font-bold text-[#FAFAFA] group-hover:text-[#FF3D00] whitespace-nowrap">
                    {formatCNJ(c.fileNumber)}
                  </td>
                  <td className="py-3 px-4 font-semibold text-[#FAFAFA] max-w-xs truncate">
                    {c.name}
                  </td>
                  <td className="py-3 px-4 text-[#737373]">
                    <Badge variant="mono" size="sm">
                      {c.subjectField || 'Geral'}
                    </Badge>
                  </td>
                  <td className="py-3 px-4 text-[#FAFAFA] flex items-center gap-1.5 whitespace-nowrap">
                    <User className="h-3 w-3 text-[#737373]" />
                    <span>{c.lawyer || 'Não atribuído'}</span>
                  </td>
                  <td className="py-3 px-4 font-mono font-bold text-[#FAFAFA] whitespace-nowrap">
                    {formatBRL(c.claimValue)}
                  </td>
                  <td className="py-3 px-4 text-[#737373] font-mono whitespace-nowrap">
                    {formatDate(c.dateChanged)}
                  </td>
                  <td className="py-3 px-4 text-right">
                    <span className="text-[#737373] group-hover:text-[#FF3D00] font-mono uppercase tracking-wider text-[10px] font-bold inline-flex items-center gap-1 transition-colors">
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