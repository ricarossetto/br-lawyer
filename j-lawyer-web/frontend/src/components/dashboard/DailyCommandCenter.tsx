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
      <div className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-xs space-y-3 p-4 text-xs">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3">
          <div className="flex items-center gap-2">
            <div className="h-7 w-7 rounded-lg bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400">
              <ListTodo className="h-3.5 w-3.5" />
            </div>
            <div>
              <h2 className="font-semibold text-slate-100 text-xs">Minha Fila de Trabalho Prioritária</h2>
              <p className="text-[11px] text-slate-400">
                Ações imediatas selecionadas pelo motor de workflow para o dia de hoje
              </p>
            </div>
          </div>

          {/* Queue Filter Tabs */}
          <div className="flex items-center gap-1 bg-slate-950 p-1 border border-slate-800 rounded-lg">
            <button
              onClick={() => setActiveQueueTab('URGENT_TASKS')}
              className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition-colors flex items-center gap-1.5 ${
                activeQueueTab === 'URGENT_TASKS'
                  ? 'bg-rose-600/30 text-rose-200 border border-rose-500/40'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <span>Atrasadas & Urgentes</span>
              <span className="px-1.5 py-0.2 rounded-full bg-rose-500/30 text-rose-200 font-mono text-[10px] font-bold">
                {urgentTasks.length}
              </span>
            </button>

            <button
              onClick={() => setActiveQueueTab('TODAY_TASKS')}
              className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition-colors flex items-center gap-1.5 ${
                activeQueueTab === 'TODAY_TASKS'
                  ? 'bg-amber-600/30 text-amber-200 border border-amber-500/40'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <span>Vencem Hoje</span>
              <span className="px-1.5 py-0.2 rounded-full bg-amber-500/30 text-amber-200 font-mono text-[10px] font-bold">
                {todayTasks.length}
              </span>
            </button>

            <button
              onClick={() => setActiveQueueTab('PENDING_PUBS')}
              className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition-colors flex items-center gap-1.5 ${
                activeQueueTab === 'PENDING_PUBS'
                  ? 'bg-indigo-600/30 text-indigo-200 border border-indigo-500/40'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <span>Publicações Pendentes</span>
              <span className="px-1.5 py-0.2 rounded-full bg-indigo-500/30 text-indigo-200 font-mono text-[10px] font-bold">
                {urgentPubs.length}
              </span>
            </button>
          </div>
        </div>

        {/* Tab Content: Urgent Tasks */}
        {activeQueueTab === 'URGENT_TASKS' && (
          <div className="space-y-2">
            {urgentTasks.length === 0 ? (
              <div className="p-8 text-center text-slate-500 space-y-1">
                <CheckCircle2 className="h-6 w-6 text-emerald-400 mx-auto" />
                <div className="font-semibold text-slate-300">Nenhuma tarefa atrasada ou urgente!</div>
                <p className="text-[11px] text-slate-500">Todas as providências prioritárias estão em dia.</p>
              </div>
            ) : (
              urgentTasks.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTaskId(t.id)}
                  className="p-3 bg-slate-950 border border-slate-800 hover:border-slate-700 rounded-lg flex items-center justify-between gap-3 cursor-pointer transition-colors group"
                >
                  <div className="flex items-center gap-3 truncate">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        changeStatusMutation.mutate({ id: t.id, req: { newStatus: 'DONE', syncCalendar: true } });
                      }}
                      className="p-1 text-slate-500 hover:text-emerald-400 transition-colors"
                      title="Marcar como concluída"
                    >
                      <Square className="h-4 w-4" />
                    </button>

                    <div className="truncate">
                      <div className="font-semibold text-slate-100 group-hover:text-indigo-300 transition-colors truncate">
                        {t.title}
                      </div>
                      <div className="flex items-center gap-2 mt-0.5 text-[10px] text-slate-400">
                        {t.caseFileNumber && (
                          <span className="font-mono text-indigo-400">{t.caseFileNumber}</span>
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
                      <span className="px-2 py-0.5 rounded bg-rose-500/20 text-rose-300 border border-rose-500/30 text-[10px] font-bold">
                        {t.overdue ? 'ATRASADA' : 'URGENTE'}
                      </span>
                      {t.dueDate && (
                        <div className="text-[10px] text-slate-400 mt-0.5">
                          {format(new Date(t.dueDate), 'dd/MM/yyyy')}
                        </div>
                      )}
                    </div>

                    <ChevronRight className="h-4 w-4 text-slate-600 group-hover:text-slate-300 transition-colors" />
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
              <div className="p-8 text-center text-slate-500 space-y-1">
                <Calendar className="h-6 w-6 text-slate-500 mx-auto" />
                <div className="font-semibold text-slate-300">Nenhuma tarefa agendada para hoje.</div>
                <p className="text-[11px] text-slate-500">Confira a agenda para os próximos dias.</p>
              </div>
            ) : (
              todayTasks.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTaskId(t.id)}
                  className="p-3 bg-slate-950 border border-slate-800 hover:border-slate-700 rounded-lg flex items-center justify-between gap-3 cursor-pointer transition-colors group"
                >
                  <div className="flex items-center gap-3 truncate">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        changeStatusMutation.mutate({ id: t.id, req: { newStatus: 'DONE', syncCalendar: true } });
                      }}
                      className="p-1 text-slate-500 hover:text-emerald-400 transition-colors"
                      title="Marcar como concluída"
                    >
                      <Square className="h-4 w-4" />
                    </button>

                    <div className="truncate">
                      <div className="font-semibold text-slate-100 group-hover:text-indigo-300 transition-colors truncate">
                        {t.title}
                      </div>
                      <div className="flex items-center gap-2 mt-0.5 text-[10px] text-slate-400">
                        {t.caseFileNumber && (
                          <span className="font-mono text-indigo-400">{t.caseFileNumber}</span>
                        )}
                        <span>•</span>
                        <span className="uppercase">{t.category}</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 shrink-0">
                    <span className="px-2 py-0.5 rounded bg-amber-500/20 text-amber-300 border border-amber-500/30 text-[10px] font-bold">
                      HOJE {t.dueTime ? `(${t.dueTime})` : ''}
                    </span>
                    <ChevronRight className="h-4 w-4 text-slate-600 group-hover:text-slate-300 transition-colors" />
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
              <div className="p-8 text-center text-slate-500 space-y-1">
                <CheckCircle2 className="h-6 w-6 text-emerald-400 mx-auto" />
                <div className="font-semibold text-slate-300">Todas as publicações foram triadas!</div>
                <p className="text-[11px] text-slate-500">Nenhuma intimação pendente de análise.</p>
              </div>
            ) : (
              urgentPubs.map((pub) => (
                <div
                  key={pub.id}
                  onClick={() => setSelectedPubId(pub.id)}
                  className="p-3 bg-slate-950 border border-slate-800 hover:border-slate-700 rounded-lg flex items-center justify-between gap-3 cursor-pointer transition-colors group"
                >
                  <div className="flex items-center gap-3 truncate">
                    <div className="p-2 rounded bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 shrink-0 font-bold text-[10px]">
                      {pub.courtCode || 'DJ'}
                    </div>

                    <div className="truncate">
                      <div className="font-semibold text-slate-100 group-hover:text-indigo-300 transition-colors truncate">
                        {pub.cnjNumber || pub.recipient || 'Publicação Judicial'}
                      </div>
                      <p className="text-[11px] text-slate-400 truncate line-clamp-1 mt-0.5">
                        {pub.snippet || 'Sem resumo disponível'}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0" onClick={(e) => e.stopPropagation()}>
                    <button
                      onClick={() => setQuickTreatPub(pub)}
                      className="px-2.5 py-1 rounded bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-300 border border-emerald-500/30 font-medium text-[11px] flex items-center gap-1 transition-colors"
                    >
                      <CheckCircle2 className="h-3 w-3" />
                      <span>Tratar</span>
                    </button>

                    <ChevronRight className="h-4 w-4 text-slate-600 group-hover:text-slate-300 transition-colors" />
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