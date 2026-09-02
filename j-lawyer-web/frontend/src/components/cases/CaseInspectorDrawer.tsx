import React, { useEffect, useState } from 'react';
import {
  Briefcase,
  Users,
  FileText,
  Clock,
  ExternalLink,
  Tag,
  Scale,
  Calendar,
} from 'lucide-react';
import { Drawer } from '../common/Drawer';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { RestfulCaseOverviewV8, RestfulPartyV1, RestfulDueDateV1 } from '../../types/cases';
import { casesService } from '../../api/casesService';
import { formatCNJ, formatDate, formatBRL } from '../../utils/formatters';

interface CaseInspectorDrawerProps {
  selectedCase: RestfulCaseOverviewV8 | null;
  onClose: () => void;
  onOpenFullDetail: (caseId: string) => void;
}

export const CaseInspectorDrawer: React.FC<CaseInspectorDrawerProps> = ({
  selectedCase,
  onClose,
  onOpenFullDetail,
}) => {
  const [parties, setParties] = useState<RestfulPartyV1[]>([]);
  const [dueDates, setDueDates] = useState<RestfulDueDateV1[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!selectedCase) return;

    const fetchDetails = async () => {
      setIsLoading(true);
      try {
        const [partiesData, dueDatesData] = await Promise.all([
          casesService.getCaseParties(selectedCase.id).catch(() => []),
          casesService.getCaseDueDates(selectedCase.id).catch(() => []),
        ]);
        setParties(partiesData || []);
        setDueDates(dueDatesData || []);
      } catch (err) {
        setParties([]);
        setDueDates([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDetails();
  }, [selectedCase]);

  if (!selectedCase) return null;

  return (
    <Drawer
      isOpen={!!selectedCase}
      onClose={onClose}
      title={selectedCase.name}
      subtitle={`NPU CNJ: ${formatCNJ(selectedCase.fileNumber)}`}
      width="lg"
    >
      <div className="space-y-6">
        {/* Action Header */}
        <div className="flex items-center justify-between p-4 bg-[#0A0A0A] border border-[#262626] rounded-none">
          <div className="flex items-center gap-2">
            <Badge variant={selectedCase.archived ? 'neutral' : 'active'}>
              {selectedCase.archived ? 'Arquivado' : 'Em Andamento'}
            </Badge>
            <Badge variant="mono">{selectedCase.subjectField || 'Cível'}</Badge>
          </div>
          <Button
            variant="primary"
            size="xs"
            rightIcon={<ExternalLink className="h-3.5 w-3.5" />}
            onClick={() => {
              onOpenFullDetail(selectedCase.id);
              onClose();
            }}
          >
            Abrir Detalhe Completo
          </Button>
        </div>

        {/* Key Metadata Grid */}
        <div className="grid grid-cols-2 gap-3 text-xs">
          <div className="p-3 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <span className="text-[#737373] block text-[10px] uppercase font-mono tracking-wider font-bold">Advogado Responsável</span>
            <span className="font-mono text-[#FAFAFA] mt-0.5 block">{selectedCase.lawyer || 'Não atribuído'}</span>
          </div>
          <div className="p-3 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <span className="text-[#737373] block text-[10px] uppercase font-mono tracking-wider font-bold">Valor da Causa</span>
            <span className="font-mono font-bold text-[#FAFAFA] mt-0.5 block">{formatBRL(selectedCase.claimValue)}</span>
          </div>
          <div className="p-3 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <span className="text-[#737373] block text-[10px] uppercase font-mono tracking-wider font-bold">Última Alteração</span>
            <span className="font-mono text-[#FAFAFA] mt-0.5 block">{formatDate(selectedCase.dateChanged)}</span>
          </div>
          <div className="p-3 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <span className="text-[#737373] block text-[10px] uppercase font-mono tracking-wider font-bold">Assistente</span>
            <span className="text-[#FAFAFA] mt-0.5 block font-mono">{selectedCase.assistant || 'Gabinete Geral'}</span>
          </div>
        </div>

        {/* Parties Quick List */}
        <div>
          <div className="flex items-center gap-1.5 text-xs font-bold text-[#FAFAFA] mb-2 font-heading tracking-tight">
            <Users className="h-3.5 w-3.5 text-[#FAFAFA]" />
            <span>Polos e Partes Envolvidas</span>
          </div>
          <div className="space-y-1.5">
            {parties.map((p) => (
              <div key={p.id} className="p-3 bg-[#0A0A0A] border border-[#262626] rounded-none flex items-center justify-between text-xs">
                <span className="font-bold text-[#FAFAFA]">{p.contactName || p.contact}</span>
                <Badge variant="neutral" size="sm">{p.involvementType || 'Interessado'}</Badge>
              </div>
            ))}
          </div>
        </div>

        {/* Deadlines Quick List */}
        <div>
          <div className="flex items-center gap-1.5 text-xs font-bold text-[#FAFAFA] mb-2 font-heading tracking-tight">
            <Clock className="h-3.5 w-3.5 text-[#FF3D00]" />
            <span>Prazos Processuais Vinculados</span>
          </div>
          <div className="space-y-1.5">
            {dueDates.length === 0 ? (
              <p className="text-xs text-[#737373] font-mono italic p-3 bg-[#0A0A0A] border border-[#262626] rounded-none">Nenhum prazo pendente para este processo.</p>
            ) : (
              dueDates.map((d) => (
                <div key={d.id} className="p-3 bg-[#0A0A0A] border border-[#262626] rounded-none flex items-center justify-between text-xs">
                  <div className="truncate pr-2">
                    <span className="font-bold text-[#FAFAFA] block truncate">{d.reason}</span>
                    <span className="text-[10px] text-[#737373] font-mono">{formatDate(d.dueDate)}</span>
                  </div>
                  <Badge variant={d.done ? 'green' : 'yellow'} size="sm">
                    {d.done ? 'Cumprido' : 'Pendente'}
                  </Badge>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </Drawer>
  );
};