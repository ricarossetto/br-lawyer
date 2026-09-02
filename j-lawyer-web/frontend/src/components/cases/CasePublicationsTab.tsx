import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Scale, Eye, EyeOff, CheckCircle2, Clock, Plus, ExternalLink, FileCheck } from 'lucide-react';
import { publicationsService } from '../../api/publicationsService';
import { PublicationDetail, PublicationOverview, PublicationTreatRequest } from '../../types/publications';
import { Badge } from '../common/Badge';
import { PublicationInspectorDrawer } from '../publications/PublicationInspectorDrawer';
import { TreatPublicationModal } from '../publications/TreatPublicationModal';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface CasePublicationsTabProps {
  caseId: string;
  cnjNumber?: string;
}

export const CasePublicationsTab: React.FC<CasePublicationsTabProps> = ({ caseId, cnjNumber }) => {
  const queryClient = useQueryClient();
  const [selectedPubId, setSelectedPubId] = useState<string | null>(null);
  const [quickTreatPub, setQuickTreatPub] = useState<PublicationOverview | null>(null);

  const { data: publications, isLoading } = useQuery({
    queryKey: ['case-publications', caseId],
    queryFn: () => publicationsService.list({ processId: caseId }),
  });

  const { data: selectedPubDetail } = useQuery({
    queryKey: ['publication', selectedPubId],
    queryFn: () => (selectedPubId ? publicationsService.getById(selectedPubId) : null),
    enabled: !!selectedPubId,
  });

  const treatMutation = useMutation({
    mutationFn: ({ id, request }: { id: string; request: PublicationTreatRequest }) =>
      publicationsService.treat(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['case-publications', caseId] });
      queryClient.invalidateQueries({ queryKey: ['publication', selectedPubId] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
  });

  const markReadMutation = useMutation({
    mutationFn: ({ id, read }: { id: string; read: boolean }) =>
      publicationsService.markRead(id, read),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['case-publications', caseId] });
    },
  });

  if (isLoading) {
    return (
      <div className="p-12 flex flex-col items-center justify-center text-slate-400 space-y-3">
        <div className="h-6 w-6 border-2 border-[#F7931A] border-t-transparent rounded-full animate-spin" />
        <span className="text-xs">Carregando publicações vinculadas aos autos...</span>
      </div>
    );
  }

  if (!publications || publications.length === 0) {
    return (
      <div className="p-12 text-center text-slate-400 space-y-2 bg-[#0F1115] border border-white/10 rounded-2xl">
        <Scale className="h-8 w-8 mx-auto text-slate-600" />
        <div className="text-xs font-medium text-slate-300">Nenhuma publicação vinculada</div>
        <p className="text-[11px] text-slate-500 max-w-sm mx-auto">
          Este processo ainda não possui publicações ou intimações associadas.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4 text-xs">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-[10px] font-bold uppercase font-mono tracking-wider text-[#737373]">
            Publicações Judiciais ({publications.length})
          </span>
          {cnjNumber && (
            <span className="text-[10px] text-[#FAFAFA] font-mono font-bold">CNJ: {cnjNumber}</span>
          )}
        </div>
      </div>

      <div className="space-y-3">
        {publications.map((pub) => {
          const isUnread = pub.readStatus === 'UNREAD';
          const isTreated = pub.treatmentStatus === 'TREATED';

          return (
            <div
              key={pub.id}
              onClick={() => setSelectedPubId(pub.id)}
              className={`p-4 bg-[#0A0A0A] border rounded-none hover:border-[#737373] cursor-pointer transition-colors space-y-2.5 group ${
                isUnread ? 'border-[#FF3D00] bg-[#141414]' : 'border-[#262626]'
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Badge variant={pub.courtCode ? 'active' : 'neutral'}>
                    {pub.courtCode || 'DJ'}
                  </Badge>
                  <span className="font-mono text-[#737373] text-[10px]">
                    {pub.availabilityDate
                      ? format(new Date(pub.availabilityDate), 'dd/MM/yyyy')
                      : 'Data recente'}
                  </span>
                  {isUnread && <Badge variant="active">Nova / Não Lida</Badge>}
                  {isTreated ? <Badge variant="green">Tratada</Badge> : <Badge variant="yellow">Pendente</Badge>}
                </div>

                <div className="flex items-center gap-1" onClick={(e) => e.stopPropagation()}>
                  <button
                    onClick={() => setQuickTreatPub(pub)}
                    className="px-3 py-1 bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold text-[10px] font-mono uppercase tracking-wider flex items-center gap-1 transition-colors cursor-pointer"
                  >
                    <CheckCircle2 className="h-3.5 w-3.5" />
                    <span>Tratar</span>
                  </button>
                </div>
              </div>

              <p className="text-[#FAFAFA] text-xs line-clamp-2 leading-relaxed font-sans">
                {pub.snippet || 'Sem resumo disponível'}
              </p>

              {pub.suggestedDueDate && (
                <div className="text-[10px] text-[#FF3D00] flex items-center gap-1 font-mono pt-1.5 border-t border-[#262626] font-bold">
                  <Clock className="h-3 w-3" />
                  <span>
                    Prazo Heurístico:{' '}
                    {format(new Date(pub.suggestedDueDate), 'dd/MM/yyyy', { locale: ptBR })}
                  </span>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Inspector Drawer */}
      <PublicationInspectorDrawer
        publication={selectedPubDetail || null}
        isOpen={!!selectedPubId}
        onClose={() => setSelectedPubId(null)}
        onToggleRead={async (id, read) => {
          await markReadMutation.mutateAsync({ id, read });
        }}
        onTreat={async (id, req) => {
          await treatMutation.mutateAsync({ id, request: req });
        }}
        onArchive={async (id) => {
          await publicationsService.archive(id);
          queryClient.invalidateQueries({ queryKey: ['case-publications', caseId] });
        }}
        onUnlink={async (id) => {
          await publicationsService.unlinkCase(id);
          queryClient.invalidateQueries({ queryKey: ['case-publications', caseId] });
        }}
      />

      {/* Quick Treat Modal */}
      <TreatPublicationModal
        isOpen={!!quickTreatPub}
        onClose={() => setQuickTreatPub(null)}
        publication={quickTreatPub}
        onConfirmTreat={async (request) => {
          if (quickTreatPub) {
            await treatMutation.mutateAsync({ id: quickTreatPub.id, request });
          }
        }}
      />
    </div>
  );
};
