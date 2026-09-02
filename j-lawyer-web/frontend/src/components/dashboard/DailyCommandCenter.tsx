import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  AlertTriangle,
  RotateCw,
  Scale,
  ListTodo,
  Clock,
  Briefcase,
  CheckCircle2,
  Square,
  CheckSquare,
  ChevronRight,
  ExternalLink,
  Flame,
  Calendar,
} from 'lucide-react';
import { workflowService } from '../../api/workflowService';
import { casesService } from '../../api/casesService';
import { tasksService } from '../../api/tasksService';
import { publicationsService } from '../../api/publicationsService';
import { ActionableKpiCards } from './ActionableKpiCards';
import { RecentCasesTable } from './RecentCasesTable';
import { Badge } from '../common/Badge';
import { TreatPublicationModal } from '../publications/TreatPublicationModal';
import { TaskInspectorDrawer } from '../tasks/TaskInspectorDrawer';
import { PublicationInspectorDrawer } from '../publications/PublicationInspectorDrawer';
import { PublicationOverview, PublicationTreatRequest } from '../../types/publications';
import { TaskDetail, TaskOverview, TaskStatusChangeRequest } from '../../types/tasks';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface DailyCommandCenterProps {
  onSelectCase: (caseId: string) => void;
  onNavigateToCases: () => void;
  onNavigateToCalendar: () => void;
  onNavigateToPublications: () => void;
  onNavigateToTasks: () => void;
}

export const DailyCommandCenter: React.FC<DailyCommandCenterProps> = ({
  onSelectCase,
  onNavigateToCases,
  onNavigateToCalendar,
  onNavigateToPublications,
  onNavigateToTasks,
}) => {
  const queryClient = useQueryClient();

  const [activeQueueTab, setActiveQueueTab] = useState<'URGENT_TASKS' | 'TODAY_TASKS' | 'PENDING_PUBS'>('URGENT_TASKS');
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [selectedPubId, setSelectedPubId] = useState<string | null>(null);
  const [quickTreatPub, setQuickTreatPub] = useState<PublicationOverview | null>(null);

  // Queries
  const { data: dashboard, isLoading: isLoadingDash, isFetching, refetch } = useQuery({
    queryKey: ['workflow-dashboard'],
    queryFn: () => workflowService.getDashboard(),
  });

  const { data: casesPage, isLoading: isLoadingCases } = useQuery({
    queryKey: ['cases-recent'],
    queryFn: () => casesService.getCasesPage(0, 8, 'open'),
  });

  const { data: selectedTaskDetail } = useQuery({
    queryKey: ['task', selectedTaskId],
    queryFn: () => (selectedTaskId ? tasksService.getById(selectedTaskId) : null),
    enabled: !!selectedTaskId,
  });

  const { data: selectedPubDetail } = useQuery({
    queryKey: ['publication', selectedPubId],
    queryFn: () => (selectedPubId ? publicationsService.getById(selectedPubId) : null),
    enabled: !!selectedPubId,
  });

  // Mutations
  const changeStatusMutation = useMutation({
    mutationFn: ({ id, req }: { id: string; req: TaskStatusChangeRequest }) =>
      tasksService.changeStatus(id, req),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      queryClient.invalidateQueries({ queryKey: ['tasks-kanban'] });
    },
  });

  const treatMutation = useMutation({
    mutationFn: ({ id, request }: { id: string; request: PublicationTreatRequest }) =>
      publicationsService.treat(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['publications'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
    },
  });

  const markReadMutation = useMutation({
    mutationFn: ({ id, read }: { id: string; read: boolean }) =>
      publicationsService.markRead(id, read),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['publications'] });
    },
  });

  const urgentTasks = dashboard?.urgentOverdueTasks || [];
  const todayTasks = dashboard?.todayTasks || [];
  const urgentPubs = dashboard?.urgentPublications || [];

  const hasCriticalAlerts = (dashboard?.totalOverdueTasks ?? 0) > 0 || (dashboard?.totalUntreatedPublications ?? 0) > 0;

  return (
    <div className="space-y-6">
      {/* Top Critical Alert Ribbon */}
      {hasCriticalAlerts && (
        <div className="p-3.5 bg-rose-950/30 border border-rose-800/60 rounded-xl flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs">
          <div className="flex items-center gap-2.5 text-rose-200">
            <Flame className="h-4 w-4 text-rose-400 shrink-0" />
            <span>
              <strong>Atenção Operacional:</strong> Você possui{' '}
              <strong>{dashboard?.totalOverdueTasks || 0} tarefa(s) atrasada(s)</strong> e{' '}
              <strong>{dashboard?.totalUntreatedPublications || 0} publicação(ões) pendente(s)</strong> de triagem.
            </span>
          </div>

          <div className="flex items-center gap-2">
            {dashboard?.totalUntreatedPublications ? (
              <button
                onClick={onNavigateToPublications}
                className="px-2.5 py-1 rounded bg-rose-900/60 hover:bg-rose-800/80 text-rose-200 border border-rose-700/60 font-medium transition-colors"
              >
                Triar Publicações
              </button>
            ) : null}

            {dashboard?.totalOverdueTasks ? (
              <button
                onClick={onNavigateToTasks}
                className="px-2.5 py-1 rounded bg-rose-900/60 hover:bg-rose-800/80 text-rose-200 border border-rose-700/60 font-medium transition-colors"
              >
                Ver Prazos Atrasados
              </button>
            ) : null}
          </div>
        </div>
      )}

      {/* Actionable KPI Cards */}
      <ActionableKpiCards
        dashboard={dashboard || null}
        activeCasesCount={casesPage?.total || 0}
        onNavigateToPublications={onNavigateToPublications}
        onNavigateToTasks={onNavigateToTasks}
        onNavigateToCases={onNavigateToCases}
        onNavigateToCalendar={onNavigateToCalendar}
      />

      {/* Minha Fila de Trabalho / Daily Work Queue */}
      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none space-y-4 p-6 text-xs">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-[#262626] pb-4">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 bg-[#141414] border border-[#262626] flex items-center justify-center text-[#FAFAFA]">
              <ListTodo className="h-4 w-4" />
            </div>
            <div>
              <h2 className="font-bold text-[#FAFAFA] text-sm font-heading tracking-tight">Fila de Trabalho Prioritária</h2>
              <p className="text-[11px] text-[#737373] font-sans">
                Ações imediatas selecionadas pelo motor de workflow para o dia de hoje
              </p>
            </div>
          </div>

          {/* Queue Filter Tabs */}
          <div className="flex items-center gap-1 bg-[#0A0A0A] p-1 border border-[#262626] rounded-none">
            <button
              onClick={() => setActiveQueueTab('URGENT_TASKS')}
              className={`px-3 py-1.5 rounded-none text-[10px] font-mono uppercase tracking-wider transition-colors flex items-center gap-1.5 ${
                activeQueueTab === 'URGENT_TASKS'
                  ? 'bg-[#1A1A1A] text-rose-400 border border-rose-600/40 font-bold'
                  : 'text-[#737373] hover:text-[#FAFAFA]'
              }`}
            >
              <span>Atrasadas & Urgentes</span>
              <span className="px-1.5 py-0.2 rounded-none bg-rose-950/40 text-rose-400 font-mono text-[9px] font-bold">
                {urgentTasks.length}
              </span>
            </button>

            <button
              onClick={() => setActiveQueueTab('TODAY_TASKS')}
              className={`px-3 py-1.5 rounded-none text-[10px] font-mono uppercase tracking-wider transition-colors flex items-center gap-1.5 ${
                activeQueueTab === 'TODAY_TASKS'
                  ? 'bg-[#1A1A1A] text-[#FF3D00] border border-[#FF3D00]/40 font-bold'
                  : 'text-[#737373] hover:text-[#FAFAFA]'
              }`}
            >
              <span>Vencem Hoje</span>
              <span className="px-1.5 py-0.2 rounded-none bg-[#141414] text-[#FF3D00] font-mono text-[9px] font-bold border border-[#262626]">
                {todayTasks.length}
              </span>
            </button>

            <button
              onClick={() => setActiveQueueTab('PENDING_PUBS')}
              className={`px-3 py-1.5 rounded-none text-[10px] font-mono uppercase tracking-wider transition-colors flex items-center gap-1.5 ${
                activeQueueTab === 'PENDING_PUBS'
                  ? 'bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-bold'
                  : 'text-[#737373] hover:text-[#FAFAFA]'
              }`}
            >
              <span>Publicações Pendentes</span>
              <span className="px-1.5 py-0.2 rounded-none bg-[#141414] text-[#FAFAFA] font-mono text-[9px] font-bold border border-[#262626]">
                {urgentPubs.length}
              </span>
            </button>
          </div>
        </div>

        {/* Tab Content: Urgent Tasks */}
        {activeQueueTab === 'URGENT_TASKS' && (
          <div className="space-y-2">
            {urgentTasks.length === 0 ? (
              <div className="p-8 text-center text-[#737373] space-y-1">
                <CheckCircle2 className="h-6 w-6 text-emerald-400 mx-auto" />
                <div className="font-semibold text-[#FAFAFA]">Nenhuma tarefa atrasada ou urgente!</div>
                <p className="text-[11px] text-[#737373]">Todas as providências prioritárias estão em dia.</p>
              </div>
            ) : (
              urgentTasks.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTaskId(t.id)}
                  className="p-3.5 bg-[#0A0A0A] border border-[#262626] hover:border-[#737373] rounded-none flex items-center justify-between gap-3 cursor-pointer transition-colors duration-150 group"
                >
                  <div className="flex items-center gap-3 truncate">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        changeStatusMutation.mutate({ id: t.id, req: { newStatus: 'DONE', syncCalendar: true } });
                      }}
                      className="p-1 text-[#737373] hover:text-emerald-400 transition-colors"
                      title="Marcar como concluída"
                    >
                      <Square className="h-4 w-4" />
                    </button>

                    <div className="truncate">
                      <div className="font-semibold text-[#FAFAFA] group-hover:text-[#FF3D00] transition-colors truncate">
                        {t.title}
                      </div>
                      <div className="flex items-center gap-2 mt-0.5 text-[10px] text-[#737373] font-mono">
                        {t.caseFileNumber && (
                          <span className="text-[#FAFAFA]">{t.caseFileNumber}</span>
                        )}
                        <span>•</span>
                        <span className="uppercase">{t.category}</span>
                        {t.assignedUser && (
                          <>
                            <span>•</span>
                            <span>{t.assignedUser}</span>
                          </>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 shrink-0">
                    <div className="text-right font-mono">
                      <span className="px-2 py-0.5 rounded-none bg-rose-950/30 text-rose-400 border border-rose-600/40 text-[10px] font-bold uppercase tracking-wider">
                        {t.overdue ? 'ATRASADA' : 'URGENTE'}
                      </span>
                      {t.dueDate && (
                        <div className="text-[10px] text-[#737373] mt-0.5">
                          {format(new Date(t.dueDate), 'dd/MM/yyyy')}
                        </div>
                      )}
                    </div>

                    <ChevronRight className="h-4 w-4 text-[#525252] group-hover:text-[#FAFAFA] transition-colors" />
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {/* Tab Content: Today Tasks */}
        {activeQueueTab === 'TODAY_TASKS' && (
          <div className="space-y-2">
            {todayTasks.length === 0 ? (
              <div className="p-8 text-center text-[#737373] space-y-1">
                <Calendar className="h-6 w-6 text-[#525252] mx-auto" />
                <div className="font-semibold text-[#FAFAFA]">Nenhuma tarefa agendada para hoje.</div>
                <p className="text-[11px] text-[#737373]">Confira a agenda para os próximos dias.</p>
              </div>
            ) : (
              todayTasks.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTaskId(t.id)}
                  className="p-3.5 bg-[#0A0A0A] border border-[#262626] hover:border-[#737373] rounded-none flex items-center justify-between gap-3 cursor-pointer transition-colors duration-150 group"
                >
                  <div className="flex items-center gap-3 truncate">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        changeStatusMutation.mutate({ id: t.id, req: { newStatus: 'DONE', syncCalendar: true } });
                      }}
                      className="p-1 text-[#737373] hover:text-emerald-400 transition-colors"
                      title="Marcar como concluída"
                    >
                      <Square className="h-4 w-4" />
                    </button>

                    <div className="truncate">
                      <div className="font-semibold text-[#FAFAFA] group-hover:text-[#FF3D00] transition-colors truncate">
                        {t.title}
                      </div>
                      <div className="flex items-center gap-2 mt-0.5 text-[10px] text-[#737373] font-mono">
                        {t.caseFileNumber && (
                          <span className="text-[#FAFAFA]">{t.caseFileNumber}</span>
                        )}
                        <span>•</span>
                        <span className="uppercase">{t.category}</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 shrink-0">
                    <span className="px-2 py-0.5 rounded-none bg-[#1A1A1A] text-[#FF3D00] border border-[#FF3D00]/40 text-[10px] font-mono font-bold uppercase tracking-wider">
                      HOJE {t.dueTime ? `(${t.dueTime})` : ''}
                    </span>
                    <ChevronRight className="h-4 w-4 text-[#525252] group-hover:text-[#FAFAFA] transition-colors" />
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {/* Tab Content: Pending Publications */}
        {activeQueueTab === 'PENDING_PUBS' && (
          <div className="space-y-2">
            {urgentPubs.length === 0 ? (
              <div className="p-8 text-center text-[#737373] space-y-1">
                <CheckCircle2 className="h-6 w-6 text-emerald-400 mx-auto" />
                <div className="font-semibold text-[#FAFAFA]">Todas as publicações foram triadas!</div>
                <p className="text-[11px] text-[#737373]">Nenhuma intimação pendente de análise.</p>
              </div>
            ) : (
              urgentPubs.map((pub) => (
                <div
                  key={pub.id}
                  onClick={() => setSelectedPubId(pub.id)}
                  className="p-3.5 bg-[#0A0A0A] border border-[#262626] hover:border-[#737373] rounded-none flex items-center justify-between gap-3 cursor-pointer transition-colors duration-150 group"
                >
                  <div className="flex items-center gap-3 truncate">
                    <div className="p-1.5 bg-[#141414] border border-[#262626] text-[#FAFAFA] shrink-0 font-mono font-bold text-[10px] uppercase">
                      {pub.courtCode || 'DJ'}
                    </div>

                    <div className="truncate">
                      <div className="font-semibold text-[#FAFAFA] group-hover:text-[#FF3D00] transition-colors truncate">
                        {pub.cnjNumber || pub.recipient || 'Publicação Judicial'}
                      </div>
                      <p className="text-[11px] text-[#737373] truncate line-clamp-1 mt-0.5 font-sans">
                        {pub.snippet || 'Sem resumo disponível'}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0" onClick={(e) => e.stopPropagation()}>
                    <button
                      onClick={() => setQuickTreatPub(pub)}
                      className="px-3 py-1 bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold text-[10px] font-mono uppercase tracking-wider flex items-center gap-1.5 transition-colors cursor-pointer"
                    >
                      <CheckCircle2 className="h-3.5 w-3.5" />
                      <span>Tratar</span>
                    </button>

                    <ChevronRight className="h-4 w-4 text-[#525252] group-hover:text-[#FAFAFA] transition-colors" />
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      {/* Recent Cases Section */}
      <RecentCasesTable
        cases={casesPage?.items || []}
        isLoading={isLoadingCases}
        onSelectCase={onSelectCase}
        onViewAll={onNavigateToCases}
      />

      {/* Task Inspector Drawer */}
      <TaskInspectorDrawer
        task={selectedTaskDetail || null}
        isOpen={!!selectedTaskId}
        onClose={() => setSelectedTaskId(null)}
        onChangeStatus={async (id, req) => {
          await changeStatusMutation.mutateAsync({ id, req });
        }}
        onAddComment={async (id, text) => {
          await tasksService.addComment(id, text);
          queryClient.invalidateQueries({ queryKey: ['task', id] });
        }}
        onAddChecklistItem={async (id, title) => {
          await tasksService.addChecklistItem(id, title);
          queryClient.invalidateQueries({ queryKey: ['task', id] });
        }}
        onToggleChecklistItem={async (id, itemId, done) => {
          await tasksService.updateChecklistItem(id, itemId, { done });
          queryClient.invalidateQueries({ queryKey: ['task', id] });
        }}
        onDeleteChecklistItem={async (id, itemId) => {
          await tasksService.deleteChecklistItem(id, itemId);
          queryClient.invalidateQueries({ queryKey: ['task', id] });
        }}
        onEditTask={() => {
          onNavigateToTasks();
        }}
        onDeleteTask={async (id) => {
          await tasksService.delete(id);
          setSelectedTaskId(null);
          queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
        }}
        onOpenCase={onSelectCase}
        onOpenPublication={(pubId) => {
          setSelectedPubId(pubId);
        }}
      />

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
          await publicationsService.archive(id);
          queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
        }}
        onUnlink={async (id) => {
          await publicationsService.unlinkCase(id);
          queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
        }}
        onOpenCase={onSelectCase}
        onOpenTask={(taskId) => {
          setSelectedTaskId(taskId);
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