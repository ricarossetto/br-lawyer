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
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-[#0F1115] border border-white/10 p-5 rounded-2xl shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)]">
        <div>
          <h1 className="text-sm font-semibold text-slate-100 flex items-center gap-2 font-heading">
            <Scale className="h-4 w-4 text-[#F7931A]" />
            Inbox de Publicações & Intimações Judiciais
          </h1>
          <p className="text-[11px] text-slate-400 mt-0.5 font-sans">
            Triagem operacional de comunicações processuais, diários de justiça e intimações
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => refetch()}
            disabled={isFetching}
            className="px-3.5 py-1.5 rounded-full bg-white/5 hover:bg-white/10 text-slate-300 text-xs font-medium border border-white/10 flex items-center gap-1.5 transition-all disabled:opacity-50 hover:border-[#F7931A]/40"
            title="Atualizar lista de publicações"
          >
            <RotateCw className={`h-3.5 w-3.5 ${isFetching ? 'animate-spin text-[#F7931A]' : ''}`} />
            <span>Atualizar</span>
          </button>
        </div>
      </div>

      {/* Filter Ribbon & Tab Switcher */}
      <div className="bg-[#0F1115] border border-white/10 rounded-2xl p-4 space-y-3 shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)]">
        {/* Status Tabs */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs border-b border-white/10">
          <button
            onClick={() => { setActiveTab('ALL'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-full font-medium transition-all ${
              activeTab === 'ALL'
                ? 'bg-[#F7931A]/15 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
            }`}
          >
            Todas
          </button>
          <button
            onClick={() => { setActiveTab('NEW'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-full font-medium transition-all flex items-center gap-1.5 ${
              activeTab === 'NEW'
                ? 'bg-[#F7931A]/15 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
            }`}
          >
            <span>Novas</span>
            <span className="px-1.5 py-0.2 rounded-full bg-[#F7931A]/30 text-[#F7931A] text-[10px] font-mono font-bold">
              Recentes
            </span>
          </button>
          <button
            onClick={() => { setActiveTab('UNREAD'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-full font-medium transition-all ${
              activeTab === 'UNREAD'
                ? 'bg-[#F7931A]/15 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
            }`}
          >
            Não Lidas
          </button>
          <button
            onClick={() => { setActiveTab('ANALYZING'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-full font-medium transition-all ${
              activeTab === 'ANALYZING'
                ? 'bg-[#F7931A]/15 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
            }`}
          >
            Em Análise
          </button>
          <button
            onClick={() => { setActiveTab('TREATED'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-full font-medium transition-all ${
              activeTab === 'TREATED'
                ? 'bg-[#F7931A]/15 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
            }`}
          >
            Tratadas
          </button>
          <button
            onClick={() => { setActiveTab('ARCHIVED'); setPage(0); }}
            className={`px-3.5 py-1.5 rounded-full font-medium transition-all ${
              activeTab === 'ARCHIVED'
                ? 'bg-[#F7931A]/15 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
            }`}
          >
            Arquivadas
          </button>
        </div>

        {/* Filter Inputs Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
          {/* Search Input */}
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-500" />
            <input
              type="text"
              value={searchText}
              onChange={(e) => { setSearchText(e.target.value); setPage(0); }}
              placeholder="Buscar por teor, CNJ, advogado, destinatário..."
              className="w-full pl-9 pr-3 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs"
            />
          </div>

          {/* Court Filter */}
          <div className="relative">
            <Building className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-500" />
            <input
              type="text"
              value={courtCode}
              onChange={(e) => { setCourtCode(e.target.value); setPage(0); }}
              placeholder="Filtrar por Tribunal (ex: STJ, TJSP, TRF3)..."
              className="w-full pl-9 pr-3 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs uppercase font-mono"
            />
          </div>

          {/* Assigned User Filter */}
          <div className="relative">
            <User className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-500" />
            <input
              type="text"
              value={assignedUser}
              onChange={(e) => { setAssignedUser(e.target.value); setPage(0); }}
              placeholder="Filtrar por Responsável..."
              className="w-full pl-9 pr-3 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs"
            />
          </div>
        </div>
      </div>

      {/* Publications Table Card */}
      <div className="bg-[#0F1115] border border-white/10 rounded-2xl overflow-hidden shadow-[0_0_30px_-10px_rgba(247,147,26,0.1)]">
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
                <tr className="border-b border-white/10 bg-[#030304]/70 text-[11px] font-semibold text-slate-400 uppercase tracking-wider font-mono">
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
              <tbody className="divide-y divide-white/5">
                {pageData.items.map((pub) => {
                  const isUnread = pub.readStatus === 'UNREAD';
                  const isTreated = pub.treatmentStatus === 'TREATED';

                  return (
                    <tr
                      key={pub.id}
                      onClick={() => handleRowClick(pub)}
                      className={`group hover:bg-white/5 cursor-pointer transition-colors ${
                        isUnread ? 'bg-[#F7931A]/5 font-medium' : ''
                      }`}
                    >
                      {/* Date */}
                      <td className="py-2.5 px-3 whitespace-nowrap text-slate-300 font-mono text-[11px]">
                        <div>
                          {pub.availabilityDate
                            ? format(new Date(pub.availabilityDate), 'dd/MM/yyyy')
                            : '—'}
                        </div>
                        {pub.suggestedDueDate && (
                          <div className="text-[10px] text-[#FFD600] flex items-center gap-1 font-semibold">
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
                        <div className="font-mono text-slate-200 text-[11px] group-hover:text-[#FFD600] transition-colors">
                          {pub.cnjNumber || 'Sem NPU'}
                        </div>
                        {pub.processId ? (
                          <div className="text-[10px] text-[#FFD600] flex items-center gap-1">
                            <Briefcase className="h-3 w-3" />
                            <span>{pub.caseFileNumber || 'Vinculado'}</span>
                          </div>
                        ) : (
                          <span className="text-[10px] text-slate-500 italic">Não vinculado</span>
                        )}
                      </td>

                      {/* Recipient / Lawyer */}
                      <td className="py-2.5 px-3">
                        <div className="text-slate-200 truncate max-w-[140px]">
                          {pub.recipient || '—'}
                        </div>
                        <div className="text-[10px] text-slate-400 truncate max-w-[140px]">
                          {pub.lawyerName || '—'}{' '}
                          {pub.lawyerOab ? `(${pub.lawyerOab})` : ''}
                        </div>
                      </td>

                      {/* Snippet */}
                      <td className="py-2.5 px-3 text-slate-400 text-[11px]">
                        <p className="line-clamp-2 leading-tight">
                          {pub.snippet || 'Sem resumo disponível'}
                        </p>
                      </td>

                      {/* Assigned User */}
                      <td className="py-2.5 px-3 whitespace-nowrap text-slate-300">
                        {pub.assignedUser ? (
                          <span className="px-2 py-0.5 rounded-full bg-white/5 border border-white/10 text-[10px] font-mono">
                            {pub.assignedUser}
                          </span>
                        ) : (
                          <span className="text-slate-600 text-[11px]">—</span>
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
                            className="p-1 rounded-md text-slate-400 hover:text-slate-200 hover:bg-white/10 transition-colors"
                            title={isUnread ? 'Marcar como lida' : 'Marcar como não lida'}
                          >
                            {isUnread ? <Eye className="h-3.5 w-3.5" /> : <EyeOff className="h-3.5 w-3.5" />}
                          </button>

                          <button
                            onClick={() => setQuickTreatPub(pub)}
                            className="px-3 py-1 rounded-full bg-emerald-600 hover:bg-emerald-500 text-white font-medium text-[11px] flex items-center gap-1 shadow-[0_0_10px_rgba(16,185,129,0.3)] transition-all hover:scale-[1.03]"
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
          <div className="px-4 py-3 border-t border-white/10 bg-[#030304]/40 flex items-center justify-between text-xs text-slate-400">
            <div>
              Mostrando{' '}
              <span className="font-semibold text-slate-200">
                {page * pageSize + 1}–{Math.min((page + 1) * pageSize, pageData.total)}
              </span>{' '}
              de <span className="font-semibold text-slate-200">{pageData.total}</span> publicações
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="p-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 disabled:opacity-40 transition-colors"
                title="Página anterior"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>

              <span className="font-mono text-[11px]">
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
