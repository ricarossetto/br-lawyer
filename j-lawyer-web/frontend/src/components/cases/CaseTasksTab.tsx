import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ListTodo, Plus, CheckSquare, Square, Calendar, Clock, User, AlertTriangle } from 'lucide-react';
import { tasksService } from '../../api/tasksService';
import { TaskCreateUpdateRequest, TaskDetail, TaskPriority, TaskStatus, TaskStatusChangeRequest } from '../../types/tasks';
import { Badge } from '../common/Badge';
import { TaskInspectorDrawer } from '../tasks/TaskInspectorDrawer';
import { TaskCreateEditModal } from '../tasks/TaskCreateEditModal';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface CaseTasksTabProps {
  caseId: string;
  cnjNumber?: string;
}

export const CaseTasksTab: React.FC<CaseTasksTabProps> = ({ caseId, cnjNumber }) => {
  const queryClient = useQueryClient();
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [isNewTaskModalOpen, setIsNewTaskModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<TaskDetail | null>(null);

  const { data: tasks, isLoading } = useQuery({
    queryKey: ['case-tasks', caseId],
    queryFn: () => tasksService.list({ processId: caseId }),
  });

  const { data: selectedTaskDetail } = useQuery({
    queryKey: ['task', selectedTaskId],
    queryFn: () => (selectedTaskId ? tasksService.getById(selectedTaskId) : null),
    enabled: !!selectedTaskId,
  });

  const createMutation = useMutation({
    mutationFn: ({ task, syncCalendar }: { task: TaskCreateUpdateRequest; syncCalendar: boolean }) =>
      tasksService.create(task, syncCalendar),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['case-tasks', caseId] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['case-tasks', caseId] });
      queryClient.invalidateQueries({ queryKey: ['task', selectedTaskId] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
  });

  const changeStatusMutation = useMutation({
    mutationFn: ({ id, req }: { id: string; req: TaskStatusChangeRequest }) =>
      tasksService.changeStatus(id, req),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['case-tasks', caseId] });
      queryClient.invalidateQueries({ queryKey: ['task', selectedTaskId] });
      queryClient.invalidateQueries({ queryKey: ['workflow-dashboard'] });
    },
  });

  const getPriorityBadge = (priority: TaskPriority) => {
    switch (priority) {
      case 'URGENT':
        return <Badge variant="red">Urgente</Badge>;
      case 'HIGH':
        return <Badge variant="yellow">Alta</Badge>;
      case 'NORMAL':
        return <Badge variant="active">Normal</Badge>;
      case 'LOW':
        return <Badge variant="neutral">Baixa</Badge>;
    }
  };

  if (isLoading) {
    return (
      <div className="p-12 flex flex-col items-center justify-center text-slate-400 space-y-3">
        <div className="h-6 w-6 border-2 border-[#F7931A] border-t-transparent rounded-full animate-spin" />
        <span className="text-xs">Carregando tarefas vinculadas ao processo...</span>
      </div>
    );
  }

  return (
    <div className="space-y-4 text-xs">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-[10px] font-bold uppercase font-mono tracking-wider text-[#737373]">
            Tarefas & Providências ({tasks?.length || 0})
          </span>
        </div>

        <button
          onClick={() => {
            setEditingTask(null);
            setIsNewTaskModalOpen(true);
          }}
          className="px-4 py-2 rounded-none bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold font-mono text-[10px] uppercase tracking-wider flex items-center gap-1.5 transition-colors cursor-pointer"
        >
          <Plus className="h-3.5 w-3.5" />
          <span>Nova Tarefa</span>
        </button>
      </div>

      {!tasks || tasks.length === 0 ? (
        <div className="p-12 text-center text-[#737373] space-y-2 bg-[#0A0A0A] border border-[#262626] rounded-none">
          <ListTodo className="h-8 w-8 mx-auto text-[#525252]" />
          <div className="text-xs font-bold text-[#FAFAFA]">Nenhuma tarefa cadastrada</div>
          <p className="text-[11px] text-[#737373] max-w-sm mx-auto font-sans">
            Crie tarefas ou providências judiciais diretamente para este processo.
          </p>
        </div>
      ) : (
        <div className="space-y-2.5">
          {tasks.map((task) => {
            const isDone = task.status === 'DONE';

            return (
              <div
                key={task.id}
                onClick={() => setSelectedTaskId(task.id)}
                className={`p-4 bg-[#0A0A0A] border rounded-none hover:border-[#737373] cursor-pointer transition-colors space-y-2 group ${
                  isDone ? 'border-[#262626] opacity-60' : 'border-[#262626]'
                }`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        changeStatusMutation.mutate({
                          id: task.id,
                          req: { newStatus: isDone ? 'TODO' : 'DONE', syncCalendar: true },
                        });
                      }}
                      className="text-[#737373] hover:text-[#FAFAFA] transition-colors cursor-pointer"
                    >
                      {isDone ? (
                        <CheckSquare className="h-4 w-4 text-[#FAFAFA]" />
                      ) : (
                        <Square className="h-4 w-4 text-[#525252] group-hover:text-[#FAFAFA]" />
                      )}
                    </button>

                    {getPriorityBadge(task.priority)}

                    <span
                      className={`font-bold text-[#FAFAFA] text-xs ${
                        isDone ? 'line-through text-[#737373]' : ''
                      }`}
                    >
                      {task.title}
                    </span>
                  </div>

                  <div className="flex items-center gap-2 font-mono text-[11px]">
                    {task.dueDate && (
                      <span
                        className={`${
                          task.overdue
                            ? 'text-[#FF3D00] font-bold'
                            : task.dueToday
                            ? 'text-amber-400 font-bold'
                            : 'text-[#737373]'
                        }`}
                      >
                        {format(new Date(task.dueDate), 'dd/MM/yyyy')}
                      </span>
                    )}

                    <Badge
                      variant={
                        task.status === 'DONE'
                          ? 'green'
                          : task.status === 'IN_PROGRESS'
                          ? 'yellow'
                          : 'blue'
                      }
                    >
                      {task.status}
                    </Badge>
                  </div>
                </div>

                <div className="flex items-center justify-between text-[11px] text-[#737373] pt-1 border-t border-[#262626]">
                  <span className="uppercase font-mono text-[10px]">{task.category || 'GERAL'}</span>
                  <span className="font-mono text-[10px]">Responsável: {task.assignedUser || 'Não atribuído'}</span>
                </div>
              </div>
            );
          })}
        </div>
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
        onEditTask={(task) => {
          setEditingTask(task);
          setIsNewTaskModalOpen(true);
        }}
        onDeleteTask={async (id) => {
          await tasksService.delete(id);
          setSelectedTaskId(null);
          queryClient.invalidateQueries({ queryKey: ['case-tasks', caseId] });
        }}
      />

      {/* Task Create / Edit Modal */}
      <TaskCreateEditModal
        isOpen={isNewTaskModalOpen}
        onClose={() => {
          setIsNewTaskModalOpen(false);
          setEditingTask(null);
        }}
        task={editingTask}
        initialProcessId={caseId}
        onSave={async (t, sync) => {
          if (t.id) {
            await updateMutation.mutateAsync({ id: t.id, task: t, syncCalendar: sync });
          } else {
            await createMutation.mutateAsync({ task: t, syncCalendar: sync });
          }
        }}
      />
    </div>
  );
};
