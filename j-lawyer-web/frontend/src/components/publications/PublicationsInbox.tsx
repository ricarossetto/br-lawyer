import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Scale,
  Search,
  Filter,
  CheckCircle2,
  Clock,
  Eye,
  EyeOff,
  Briefcase,
  AlertCircle,
  ChevronLeft,
  ChevronRight,
  RotateCw,
  FileCheck,
  Building,
  User,
  ExternalLink,
} from 'lucide-react';
import { publicationsService } from '../../api/publicationsService';
import { PublicationFilter, PublicationOverview, PublicationTreatRequest } from '../../types/publications';
import { Badge } from '../common/Badge';
import { PublicationInspectorDrawer } from './PublicationInspectorDrawer';
import { TreatPublicationModal } from './TreatPublicationModal';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface PublicationsInboxProps {
  onSelectCase?: (caseId: string) => void;
  onSelectTask?: (taskId: string) => void;
  initialProcessId?: string;
}

export const PublicationsInbox: React.FC<PublicationsInboxProps> = ({
  onSelectCase,
  onSelectTask,
  initialProcessId,
}) => {
  const queryClient = useQueryClient();

  // Filter state
  const [activeTab, setActiveTab] = useState<'ALL' | 'NEW' | 'UNREAD' | 'ANALYZING' | 'TREATED' | 'ARCHIVED'>('ALL');
  const [searchText, setSearchText] = useState('');
  const [courtCode, setCourtCode] = useState('');
  const [assignedUser, setAssignedUser] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(25);

  // Selected for drawer & quick treat
  const [selectedPubId, setSelectedPubId] = useState<string | null>(null);
  const [quickTreatPub, setQuickTreatPub] = useState<PublicationOverview | null>(null);

  // Build filter object
  const filter: PublicationFilter = {
    page,
    pageSize,
    searchText: searchText.trim() || undefined,
    courtCode: courtCode.trim() || undefined,
    assignedUser: assignedUser.trim() || undefined,
    processId: initialProcessId,
  };

  if (activeTab === 'NEW') filter.status = 'NEW';
  else if (activeTab === 'UNREAD') filter.readStatus = 'UNREAD';
  else if (activeTab === 'ANALYZING') filter.status = 'ANALYZING';
  else if (activeTab === 'TREATED') filter.treatmentStatus = 'TREATED';
  else if (activeTab === 'ARCHIVED') filter.status = 'ARCHIVED';

  // Query: Publications Page
  const { data: pageData, isLoading, isFetching, refetch } = useQuery({
    queryKey: ['publications', filter],
    queryFn: () => publicationsService.getPage(filter),
  });

  // Query: Selected Publication Detail
  const { data: selectedPubDetail, isLoading: isLoadingDetail } = useQuery({
    queryKey: ['publication', selectedPubId],
    queryFn: () => (selectedPubId ? publicationsService.getById(selectedPubId) : null),
    enabled: !!selectedPubId,
  });

  // Mutations
  const markReadMutation = useMutation({
    mutationFn: ({ id, read }: { id: string; read: boolean }) => publicationsService.markRead(id, read),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['publications'] });
      queryClient.invalidateQueries({ queryKey: ['publication', selectedPubId] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
  });

  const treatMutation = useMutation({
    mutationFn: ({ id, request }: { id: string; request: PublicationTreatRequest }) =>
      publicationsService.treat(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['publications'] });
      queryClient.invalidateQueries({ queryKey: ['publication', selectedPubId] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
  });

  const archiveMutation = useMutation({
    mutationFn: (id: string) => publicationsService.archive(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['publications'] });
      queryClient.invalidateQueries({ queryKey: ['publication', selectedPubId] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
  });

  const unlinkMutation = useMutation({
    mutationFn: (id: string) => publicationsService.unlinkCase(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['publications'] });
      queryClient.invalidateQueries({ queryKey: ['publication', selectedPubId] });
    },
  });

  const handleRowClick = (pub: PublicationOverview) => {
    setSelectedPubId(pub.id);
    if (pub.readStatus === 'UNREAD') {
      markReadMutation.mutate({ id: pub.id, read: true });
    }
  };

  const getCourtBadgeColor = (court?: string) => {
    if (!court) return 'gray';
    if (court.startsWith('STF') || court.startsWith('STJ') || court.startsWith('TST')) return 'purple';
    if (court.startsWith('TRF')) return 'blue';
    if (court.startsWith('TRT')) return 'green';
    return 'yellow';
  };

  const totalPages = pageData ? Math.ceil(pageData.total / pageSize) : 0;

  return (
    <div className="space-y-4">
      {/* Top Header & Metrics Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-[#0F0F0F] border border-[#262626] p-6 rounded-none">
        <div>
          <h1 className="text-sm font-bold text-[#FAFAFA] flex items-center gap-2 font-heading tracking-tight">
            <Scale className="h-4 w-4 text-[#FAFAFA]" />
            Inbox de Publicações & Intimações Judiciais
          </h1>
          <p className="text-xs text-[#737373] mt-0.5 font-sans">
            Triagem operacional de comunicações processuais, diários de justiça e intimações
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => refetch()}
            disabled={isFetching}
            className="px-4 py-2 rounded-none bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] text-[10px] font-mono uppercase tracking-wider font-bold border border-[#262626] flex items-center gap-1.5 transition-colors disabled:opacity-50 cursor-pointer"
            title="Atualizar lista de publicações"
          >
            <RotateCw className={`h-3.5 w-3.5 ${isFetching ? 'animate-spin text-[#FF3D00]' : ''}`} />
            <span>Atualizar</span>
          </button>
        </div>
      </div>

      {/* Filter Ribbon & Tab Switcher */}
      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none p-5 space-y-4">
        {/* Status Tabs */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs border-b border-[#262626]">
          <button
            onClick={() => { setActiveTab('ALL'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-none font-mono text-[10px] uppercase tracking-wider transition-colors ${
              activeTab === 'ALL'
                ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold'
                : 'text-[#737373] hover:text-[#FAFAFA]'
            }`}
          >
            Todas
          </button>
          <button
            onClick={() => { setActiveTab('NEW'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-none font-mono text-[10px] uppercase tracking-wider transition-colors flex items-center gap-1.5 ${
              activeTab === 'NEW'
                ? 'bg-[#1A1A1A] text-[#FF3D00] border border-[#FF3D00]/40 font-bold'
                : 'text-[#737373] hover:text-[#FAFAFA]'
            }`}
          >
            <span>Novas</span>
            <span className="px-1.5 py-0.2 rounded-none bg-[#141414] text-[#FF3D00] text-[9px] font-mono font-bold border border-[#262626]">
              Recentes
            </span>
          </button>
          <button
            onClick={() => { setActiveTab('UNREAD'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-none font-mono text-[10px] uppercase tracking-wider transition-colors ${
              activeTab === 'UNREAD'
                ? 'bg-[#1A1A1A] text-[#FF3D00] border border-[#FF3D00]/40 font-bold'
                : 'text-[#737373] hover:text-[#FAFAFA]'
            }`}
          >
            Não Lidas
          </button>
          <button
            onClick={() => { setActiveTab('ANALYZING'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-none font-mono text-[10px] uppercase tracking-wider transition-colors ${
              activeTab === 'ANALYZING'
                ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold'
                : 'text-[#737373] hover:text-[#FAFAFA]'
            }`}
          >
            Em Análise
          </button>
          <button
            onClick={() => { setActiveTab('TREATED'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-none font-mono text-[10px] uppercase tracking-wider transition-colors ${
              activeTab === 'TREATED'
                ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold'
                : 'text-[#737373] hover:text-[#FAFAFA]'
            }`}
          >
            Tratadas
          </button>
          <button
            onClick={() => { setActiveTab('ARCHIVED'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-none font-mono text-[10px] uppercase tracking-wider transition-colors ${
              activeTab === 'ARCHIVED'
                ? 'bg-[#1A1A1A] text-[#737373] border border-[#262626] font-bold'
                : 'text-[#737373] hover:text-[#FAFAFA]'
            }`}
          >
            Arquivadas
          </button>
        </div>

        {/* Filter Inputs Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
          {/* Search Input */}
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-3.5 w-3.5 text-[#737373]" />
            <input
              type="text"
              value={searchText}
              onChange={(e) => { setSearchText(e.target.value); setPage(0); }}
              placeholder="Buscar por teor, CNJ, advogado, destinatário..."
              className="w-full pl-9 pr-3 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors text-xs"
            />
          </div>

          {/* Court Filter */}
          <div className="relative">
            <Building className="absolute left-3 top-2.5 h-3.5 w-3.5 text-[#737373]" />
            <input
              type="text"
              value={courtCode}
              onChange={(e) => { setCourtCode(e.target.value); setPage(0); }}
              placeholder="Filtrar por Tribunal (ex: STJ, TJSP, TRF3)..."
              className="w-full pl-9 pr-3 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors text-xs uppercase font-mono"
            />
          </div>

          {/* Assigned User Filter */}
          <div className="relative">
            <User className="absolute left-3 top-2.5 h-3.5 w-3.5 text-[#737373]" />
            <input
              type="text"
              value={assignedUser}
              onChange={(e) => { setAssignedUser(e.target.value); setPage(0); }}
              placeholder="Filtrar por Responsável..."
              className="w-full pl-9 pr-3 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors text-xs"
            />
          </div>
        </div>
      </div>

      {/* Publications Table Card */}
      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none">
        {isLoading ? (
          <div className="p-12 flex flex-col items-center justify-center text-slate-400 space-y-3">
            <div className="h-6 w-6 border-2 border-[#F7931A] border-t-transparent rounded-full animate-spin" />
            <span className="text-xs">Carregando publicações do servidor...</span>
          </div>
        ) : !pageData || pageData.items.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-2">
            <FileCheck className="h-8 w-8 mx-auto text-slate-600" />
            <div className="text-xs font-medium text-slate-300">Nenhuma publicação encontrada</div>
            <p className="text-[11px] text-slate-500 max-w-sm mx-auto">
              Nenhuma comunicação processual corresponde aos filtros selecionados.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="border-b border-[#262626] bg-[#0A0A0A] text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono">
                  <th className="py-2.5 px-3">Data</th>
                  <th className="py-2.5 px-3">Tribunal</th>
                  <th className="py-2.5 px-3">Processo / CNJ</th>
                  <th className="py-2.5 px-3">Destinatário & OAB</th>
                  <th className="py-2.5 px-3 min-w-[280px]">Ementa / Teor</th>
                  <th className="py-2.5 px-3">Responsável</th>
                  <th className="py-2.5 px-3 text-center">Status</th>
                  <th className="py-2.5 px-3 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#262626]">
                {pageData.items.map((pub) => {
                  const isUnread = pub.readStatus === 'UNREAD';
                  const isTreated = pub.treatmentStatus === 'TREATED';

                  return (
                    <tr
                      key={pub.id}
                      onClick={() => handleRowClick(pub)}
                      className={`group hover:bg-[#141414] cursor-pointer transition-colors ${
                        isUnread ? 'bg-[#141414] font-medium' : ''
                      }`}
                    >
                      {/* Date */}
                      <td className="py-2.5 px-3 whitespace-nowrap text-[#FAFAFA] font-mono text-[11px]">
                        <div>
                          {pub.availabilityDate
                            ? format(new Date(pub.availabilityDate), 'dd/MM/yyyy')
                            : '—'}
                        </div>
                        {pub.suggestedDueDate && (
                          <div className="text-[10px] text-[#FF3D00] flex items-center gap-1 font-mono font-bold">
                            <Clock className="h-3 w-3" />
                            Prazo: {format(new Date(pub.suggestedDueDate), 'dd/MM')}
                          </div>
                        )}
                      </td>

                      {/* Court */}
                      <td className="py-2.5 px-3 whitespace-nowrap">
                        <Badge variant={getCourtBadgeColor(pub.courtCode)}>
                          {pub.courtCode || 'TRIBUNAL'}
                        </Badge>
                      </td>

                      {/* Process / CNJ */}
                      <td className="py-2.5 px-3">
                        <div className="font-mono font-bold text-[#FAFAFA] text-[11px] group-hover:text-[#FF3D00] transition-colors">
                          {pub.cnjNumber || 'Sem NPU'}
                        </div>
                        {pub.processId ? (
                          <div className="text-[10px] text-[#737373] font-mono flex items-center gap-1">
                            <Briefcase className="h-3 w-3" />
                            <span>{pub.caseFileNumber || 'Vinculado'}</span>
                          </div>
                        ) : (
                          <span className="text-[10px] text-[#525252] italic font-mono">Não vinculado</span>
                        )}
                      </td>

                      {/* Recipient / Lawyer */}
                      <td className="py-2.5 px-3">
                        <div className="text-[#FAFAFA] font-medium truncate max-w-[140px]">
                          {pub.recipient || '—'}
                        </div>
                        <div className="text-[10px] text-[#737373] truncate max-w-[140px] font-mono">
                          {pub.lawyerName || '—'}{' '}
                          {pub.lawyerOab ? `(${pub.lawyerOab})` : ''}
                        </div>
                      </td>

                      {/* Snippet */}
                      <td className="py-2.5 px-3 text-[#737373] text-[11px]">
                        <p className="line-clamp-2 leading-tight font-sans">
                          {pub.snippet || 'Sem resumo disponível'}
                        </p>
                      </td>

                      {/* Assigned User */}
                      <td className="py-2.5 px-3 whitespace-nowrap text-[#FAFAFA]">
                        {pub.assignedUser ? (
                          <span className="px-2 py-0.5 rounded-none bg-[#141414] border border-[#262626] text-[10px] font-mono">
                            {pub.assignedUser}
                          </span>
                        ) : (
                          <span className="text-[#525252] text-[11px]">—</span>
                        )}
                      </td>

                      {/* Status */}
                      <td className="py-2.5 px-3 text-center whitespace-nowrap">
                        {isTreated ? (
                          <Badge variant="green">Tratada</Badge>
                        ) : isUnread ? (
                          <Badge variant="active">Nova / Não Lida</Badge>
                        ) : (
                          <Badge variant="yellow">Pendente</Badge>
                        )}
                      </td>

                      {/* Quick Actions */}
                      <td className="py-2.5 px-3 text-right whitespace-nowrap" onClick={(e) => e.stopPropagation()}>
                        <div className="flex items-center justify-end gap-1.5">
                          <button
                            onClick={() =>
                              markReadMutation.mutate({
                                id: pub.id,
                                read: isUnread ? true : false,
                              })
                            }
                            className="p-1.5 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors"
                            title={isUnread ? 'Marcar como lida' : 'Marcar como não lida'}
                          >
                            {isUnread ? <Eye className="h-3.5 w-3.5" /> : <EyeOff className="h-3.5 w-3.5" />}
                          </button>

                          <button
                            onClick={() => setQuickTreatPub(pub)}
                            className="px-3 py-1 bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold text-[10px] font-mono uppercase tracking-wider flex items-center gap-1 transition-colors cursor-pointer"
                          >
                            <CheckCircle2 className="h-3 w-3" />
                            <span>Tratar</span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination Footer */}
        {pageData && pageData.total > 0 && (
          <div className="px-6 py-3 border-t border-[#262626] bg-[#0A0A0A] flex items-center justify-between text-xs text-[#737373]">
            <div>
              Mostrando{' '}
              <span className="font-mono font-bold text-[#FAFAFA]">
                {page * pageSize + 1}–{Math.min((page + 1) * pageSize, pageData.total)}
              </span>{' '}
              de <span className="font-mono font-bold text-[#FAFAFA]">{pageData.total}</span> publicações
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="p-1 rounded-none bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] border border-[#262626] disabled:opacity-40 transition-colors"
                title="Página anterior"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>

              <span className="font-mono text-[11px] text-[#FAFAFA]">
                {page + 1} / {Math.max(1, totalPages)}
              </span>

              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="p-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 disabled:opacity-40 transition-colors"
                title="Próxima página"
              >
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Publication Inspector Drawer */}
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
          await archiveMutation.mutateAsync(id);
        }}
        onUnlink={async (id) => {
          await unlinkMutation.mutateAsync(id);
        }}
        onOpenCase={onSelectCase}
        onOpenTask={onSelectTask}
        isLoading={isLoadingDetail}
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
