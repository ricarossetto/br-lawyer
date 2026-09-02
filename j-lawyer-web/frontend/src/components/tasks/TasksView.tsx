import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  ListTodo,
  Columns,
  List,
  Search,
  Plus,
  RotateCw,
  Filter,
  User,
  Calendar,
  AlertTriangle,
} from 'lucide-react';
import { tasksService } from '../../api/tasksService';
import {
  KanbanBoard,
  TaskCreateUpdateRequest,
  TaskDetail,
  TaskFilter,
  TaskPriority,
  TaskStatus,
  TaskStatusChangeRequest,
} from '../../types/tasks';
import { TasksList } from './TasksList';
import { TasksKanban } from './TasksKanban';
import { TaskInspectorDrawer } from './TaskInspectorDrawer';
import { TaskCreateEditModal } from './TaskCreateEditModal';

interface TasksViewProps {
  onSelectCase?: (caseId: string) => void;
  onSelectPublication?: (pubId: string) => void;
  initialProcessId?: string;
}

export const TasksView: React.FC<TasksViewProps> = ({
  onSelectCase,
  onSelectPublication,
  initialProcessId,
}) => {
  const queryClient = useQueryClient();

  const [viewMode, setViewMode] = useState<'KANBAN' | 'LIST'>('KANBAN');
  const [searchText, setSearchText] = useState('');
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [selectedPriority, setSelectedPriority] = useState<string>('');
  const [assignedUser, setAssignedUser] = useState<string>('');
  const [quickDeadline, setQuickDeadline] = useState<'ALL' | 'OVERDUE' | 'TODAY'>('ALL');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(25);

  // Selected task for drawer / editing
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [editingTask, setEditingTask] = useState<TaskDetail | null>(null);
  const [isNewTaskModalOpen, setIsNewTaskModalOpen] = useState(false);

  // Filter for List query
  const listFilter: TaskFilter = {
    page,
    pageSize,
    searchText: searchText.trim() || undefined,
    status: selectedStatus || undefined,
    priority: selectedPriority || undefined,
    assignedUser: assignedUser.trim() || undefined,
    overdue: quickDeadline === 'OVERDUE' ? true : undefined,
    dueToday: quickDeadline === 'TODAY' ? true : undefined,
    processId: initialProcessId,
  };

  // Query: Task Page (List mode)
  const {
    data: pageData,
    isLoading: isLoadingList,
    isFetching: isFetchingList,
    refetch: refetchList,
  } = useQuery({
    queryKey: ['tasks', listFilter],
    queryFn: () => tasksService.getPage(listFilter),
    enabled: viewMode === 'LIST',
  });

  // Query: Kanban Board (Kanban mode)
  const {
    data: kanbanBoard,
    isLoading: isLoadingKanban,
    isFetching: isFetchingKanban,
    refetch: refetchKanban,
  } = useQuery({
    queryKey: ['tasks-kanban', assignedUser, initialProcessId],
    queryFn: () => tasksService.getKanban(assignedUser.trim() || undefined, initialProcessId),
    enabled: viewMode === 'KANBAN',
  });

  // Query: Selected Task Details
  const { data: selectedTaskDetail } = useQuery({
    queryKey: ['task', selectedTaskId],
    queryFn: () => (selectedTaskId ? tasksService.getById(selectedTaskId) : null),
    enabled: !!selectedTaskId,
  });

  // Mutations
  const invalidateAllTasks = () => {
    queryClient.invalidateQueries({ queryKey: ['tasks'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-kanban'] });
    queryClient.invalidateQueries({ queryKey: ['task', selectedTaskId] });
    queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
  };

  const createMutation = useMutation({
    mutationFn: ({ task, syncCalendar }: { task: TaskCreateUpdateRequest; syncCalendar: boolean }) =>
      tasksService.create(task, syncCalendar),
    onSuccess: () => invalidateAllTasks(),
  });

  const updateMutation = useMutation({
    mutationFn: ({
      id,
      task,
      syncCalendar,
    }: {
      id: string;
      task: TaskCreateUpdateRequest;
      syncCalendar: boolean;
    }) => tasksService.update(id, task, syncCalendar),
    onSuccess: () => invalidateAllTasks(),
  });

  const changeStatusMutation = useMutation({
    mutationFn: ({ id, req }: { id: string; req: TaskStatusChangeRequest }) =>
      tasksService.changeStatus(id, req),
    onMutate: async ({ id, req }) => {
      // Cancel outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['tasks-kanban'] });
      const prevKanban = queryClient.getQueryData<KanbanBoard>(['tasks-kanban', assignedUser, initialProcessId]);

      // Optimistic update for Kanban
      if (prevKanban) {
        let movedTask: any = null;
        const newCols = prevKanban.columns.map((col) => {
          const matching = col.tasks.find((t) => t.id === id);
          if (matching) {
            movedTask = { ...matching, status: req.newStatus };
            return { ...col, count: col.count - 1, tasks: col.tasks.filter((t) => t.id !== id) };
          }
          return col;
        });

        if (movedTask) {
          const finalCols = newCols.map((col) => {
            if (col.status === req.newStatus) {
              return { ...col, count: col.count + 1, tasks: [movedTask, ...col.tasks] };
            }
            return col;
          });
          queryClient.setQueryData(['tasks-kanban', assignedUser, initialProcessId], {
            ...prevKanban,
            columns: finalCols,
          });
        }
      }

      return { prevKanban };
    },
    onError: (_err, _vars, context) => {
      // Rollback on error
      if (context?.prevKanban) {
        queryClient.setQueryData(['tasks-kanban', assignedUser, initialProcessId], context.prevKanban);
      }
    },
    onSettled: () => invalidateAllTasks(),
  });

  const addCommentMutation = useMutation({
    mutationFn: ({ id, text }: { id: string; text: string }) => tasksService.addComment(id, text),
    onSuccess: () => invalidateAllTasks(),
  });

  const addChecklistMutation = useMutation({
    mutationFn: ({ id, title }: { id: string; title: string }) => tasksService.addChecklistItem(id, title),
    onSuccess: () => invalidateAllTasks(),
  });

  const toggleChecklistMutation = useMutation({
    mutationFn: ({ id, itemId, done }: { id: string; itemId: string; done: boolean }) =>
      tasksService.updateChecklistItem(id, itemId, { done }),
    onSuccess: () => invalidateAllTasks(),
  });

  const deleteChecklistMutation = useMutation({
    mutationFn: ({ id, itemId }: { id: string; itemId: string }) =>
      tasksService.deleteChecklistItem(id, itemId),
    onSuccess: () => invalidateAllTasks(),
  });

  const deleteTaskMutation = useMutation({
    mutationFn: (id: string) => tasksService.delete(id),
    onSuccess: () => {
      setSelectedTaskId(null);
      invalidateAllTasks();
    },
  });

  const handleSaveTask = async (task: TaskCreateUpdateRequest, syncCalendar: boolean) => {
    if (task.id) {
      await updateMutation.mutateAsync({ id: task.id, task, syncCalendar });
    } else {
      await createMutation.mutateAsync({ task, syncCalendar });
    }
    setEditingTask(null);
    setIsNewTaskModalOpen(false);
  };

  const isFetching = viewMode === 'KANBAN' ? isFetchingKanban : isFetchingList;

  return (
    <div className="space-y-4">
      {/* Top Header & Mode Toggle Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-[#0F1115] border border-white/10 p-5 rounded-2xl shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)]">
        <div>
          <h1 className="text-sm font-semibold text-slate-100 flex items-center gap-2 font-heading">
            <ListTodo className="h-4 w-4 text-[#F7931A]" />
            Gestão de Tarefas & Providências Jurídicas
          </h1>
          <p className="text-[11px] text-slate-400 mt-0.5 font-sans">
            Acompanhamento operacional, prazos fatais e fluxo visual de trabalho
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          {/* View Mode Switcher */}
          <div className="p-1 bg-[#030304] border border-white/10 rounded-full flex items-center gap-1">
            <button
              onClick={() => setViewMode('KANBAN')}
              className={`px-3.5 py-1 rounded-full text-xs font-medium flex items-center gap-1.5 transition-all ${
                viewMode === 'KANBAN'
                  ? 'bg-[#F7931A]/20 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Columns className="h-3.5 w-3.5" />
              <span>Quadro Kanban</span>
            </button>
            <button
              onClick={() => setViewMode('LIST')}
              className={`px-3.5 py-1 rounded-full text-xs font-medium flex items-center gap-1.5 transition-all ${
                viewMode === 'LIST'
                  ? 'bg-[#F7931A]/20 text-[#F7931A] border border-[#F7931A]/40 shadow-[0_0_12px_rgba(247,147,26,0.25)] font-semibold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <List className="h-3.5 w-3.5" />
              <span>Lista Densa</span>
            </button>
          </div>

          <button
            onClick={() => (viewMode === 'KANBAN' ? refetchKanban() : refetchList())}
            disabled={isFetching}
            className="p-2 rounded-full bg-white/5 hover:bg-white/10 text-slate-300 border border-white/10 transition-colors disabled:opacity-50 hover:border-[#F7931A]/40"
            title="Atualizar tarefas"
          >
            <RotateCw className={`h-3.5 w-3.5 ${isFetching ? 'animate-spin text-[#F7931A]' : ''}`} />
          </button>

          <button
            onClick={() => {
              setEditingTask(null);
              setIsNewTaskModalOpen(true);
            }}
            className="px-4 py-2 rounded-full bg-gradient-to-r from-[#EA580C] to-[#F7931A] text-white text-xs font-semibold shadow-[0_0_20px_-5px_rgba(234,88,12,0.5)] flex items-center gap-1.5 transition-all hover:scale-[1.02]"
          >
            <Plus className="h-3.5 w-3.5" />
            <span>Nova Tarefa</span>
          </button>
        </div>
      </div>

      {/* Filter Ribbon */}
      <div className="bg-[#0F1115] border border-white/10 rounded-2xl p-4 space-y-3 shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)] text-xs">
        <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-500" />
            <input
              type="text"
              value={searchText}
              onChange={(e) => {
                setSearchText(e.target.value);
                setPage(0);
              }}
              placeholder="Buscar título, processo ou CNJ..."
              className="w-full pl-9 pr-3 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs"
            />
          </div>

          {/* User */}
          <div className="relative">
            <User className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-500" />
            <input
              type="text"
              value={assignedUser}
              onChange={(e) => {
                setAssignedUser(e.target.value);
                setPage(0);
              }}
              placeholder="Filtrar por Responsável..."
              className="w-full pl-9 pr-3 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs"
            />
          </div>

          {/* Status (active in list view) */}
          <select
            value={selectedStatus}
            onChange={(e) => {
              setSelectedStatus(e.target.value);
              setPage(0);
            }}
            className="w-full px-3 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 focus:outline-none focus:border-[#F7931A] transition-colors text-xs"
          >
            <option value="">Todos os Status</option>
            <option value="TODO">A Fazer</option>
            <option value="IN_PROGRESS">Em Andamento</option>
            <option value="WAITING">Aguardando Terceiro</option>
            <option value="DONE">Concluídas</option>
            <option value="CANCELLED">Canceladas</option>
          </select>

          {/* Quick Urgency / Due Date */}
          <div className="flex items-center gap-1.5 bg-[#030304] p-1 border border-white/10 rounded-full">
            <button
              onClick={() => { setQuickDeadline('ALL'); setPage(0); }}
              className={`flex-1 py-1 rounded-full font-medium text-[11px] transition-all ${
                quickDeadline === 'ALL'
                  ? 'bg-white/10 text-white font-semibold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Todas
            </button>
            <button
              onClick={() => { setQuickDeadline('TODAY'); setPage(0); }}
              className={`flex-1 py-1 rounded-full font-medium text-[11px] transition-all ${
                quickDeadline === 'TODAY'
                  ? 'bg-amber-600/30 text-amber-200 border border-amber-500/40 shadow-[0_0_10px_rgba(245,158,11,0.25)] font-semibold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Hoje
            </button>
            <button
              onClick={() => { setQuickDeadline('OVERDUE'); setPage(0); }}
              className={`flex-1 py-1 rounded-full font-medium text-[11px] transition-all ${
                quickDeadline === 'OVERDUE'
                  ? 'bg-rose-600/30 text-rose-200 border border-rose-500/40 shadow-[0_0_10px_rgba(244,63,94,0.25)] font-semibold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Atrasadas
            </button>
          </div>
        </div>
      </div>

      {/* Main Content Area: Kanban vs List */}
      {viewMode === 'KANBAN' ? (
        <TasksKanban
          board={kanbanBoard || null}
          isLoading={isLoadingKanban}
          onSelectTask={(id) => setSelectedTaskId(id)}
          onStatusChange={async (id, newStatus) => {
            await changeStatusMutation.mutateAsync({ id, req: { newStatus, syncCalendar: true } });
          }}
          onNewTask={() => {
            setEditingTask(null);
            setIsNewTaskModalOpen(true);
          }}
          onSelectCase={onSelectCase}
        />
      ) : (
        <TasksList
          pageData={pageData || null}
          isLoading={isLoadingList}
          page={page}
          pageSize={pageSize}
          onPageChange={setPage}
          onSelectTask={(id) => setSelectedTaskId(id)}
          onChangeStatus={async (id, req) => {
            await changeStatusMutation.mutateAsync({ id, req });
          }}
          onSelectCase={onSelectCase}
        />
      )}

      {/* Task Inspector Drawer */}
      <TaskInspectorDrawer
        task={selectedTaskDetail || null}
        isOpen={!!selectedTaskId}
        onClose={() => setSelectedTaskId(null)}
        onChangeStatus={async (id, req) => {
          await changeStatusMutation.mutateAsync({ id, req });
        }}
        onAddComment={async (id, text) => {
          await addCommentMutation.mutateAsync({ id, text });
        }}
        onAddChecklistItem={async (id, title) => {
          await addChecklistMutation.mutateAsync({ id, title });
        }}
        onToggleChecklistItem={async (id, itemId, done) => {
          await toggleChecklistMutation.mutateAsync({ id, itemId, done });
        }}
        onDeleteChecklistItem={async (id, itemId) => {
          await deleteChecklistMutation.mutateAsync({ id, itemId });
        }}
        onEditTask={(task) => {
          setEditingTask(task);
          setIsNewTaskModalOpen(true);
        }}
        onDeleteTask={async (id) => {
          await deleteTaskMutation.mutateAsync(id);
        }}
        onOpenCase={onSelectCase}
        onOpenPublication={onSelectPublication}
      />

      {/* Task Create / Edit Modal */}
      <TaskCreateEditModal
        isOpen={isNewTaskModalOpen}
        onClose={() => {
          setIsNewTaskModalOpen(false);
          setEditingTask(null);
        }}
        task={editingTask}
        initialProcessId={initialProcessId}
        onSave={handleSaveTask}
        isSubmitting={createMutation.isPending || updateMutation.isPending}
      />
    </div>
  );
};
