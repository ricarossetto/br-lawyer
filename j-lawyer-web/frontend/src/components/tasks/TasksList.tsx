import React from 'react';
import {
  CheckSquare,
  Square,
  Calendar,
  Briefcase,
  User,
  MessageSquare,
  ChevronLeft,
  ChevronRight,
  ListTodo,
} from 'lucide-react';
import { TaskOverview, TaskPage, TaskPriority, TaskStatus, TaskStatusChangeRequest } from '../../types/tasks';
import { Badge } from '../common/Badge';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface TasksListProps {
  pageData: TaskPage | null;
  isLoading: boolean;
  page: number;
  pageSize: number;
  onPageChange: (newPage: number) => void;
  onSelectTask: (taskId: string) => void;
  onChangeStatus: (id: string, req: TaskStatusChangeRequest) => Promise<void>;
  onSelectCase?: (caseId: string) => void;
}

export const TasksList: React.FC<TasksListProps> = ({
  pageData,
  isLoading,
  page,
  pageSize,
  onPageChange,
  onSelectTask,
  onChangeStatus,
  onSelectCase,
}) => {
  if (isLoading) {
    return (
      <div className="p-16 flex flex-col items-center justify-center text-slate-400 space-y-3 bg-[#0F1115] border border-white/10 rounded-2xl">
        <div className="h-6 w-6 border-2 border-[#F7931A] border-t-transparent rounded-full animate-spin" />
        <span className="text-xs">Carregando lista de tarefas...</span>
      </div>
    );
  }

  if (!pageData || pageData.items.length === 0) {
    return (
      <div className="p-16 text-center text-slate-400 space-y-2 bg-[#0F1115] border border-white/10 rounded-2xl">
        <ListTodo className="h-8 w-8 mx-auto text-slate-600" />
        <div className="text-xs font-medium text-slate-300">Nenhuma tarefa encontrada</div>
        <p className="text-[11px] text-slate-500 max-w-sm mx-auto font-sans">
          Não há tarefas ou providências correspondentes aos filtros selecionados.
        </p>
      </div>
    );
  }

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

  const getStatusBadge = (status: TaskStatus) => {
    switch (status) {
      case 'TODO':
        return <Badge variant="active">A Fazer</Badge>;
      case 'IN_PROGRESS':
        return <Badge variant="yellow">Em Andamento</Badge>;
      case 'WAITING':
        return <Badge variant="mono">Aguardando</Badge>;
      case 'DONE':
        return <Badge variant="green">Concluída</Badge>;
      case 'CANCELLED':
        return <Badge variant="neutral">Cancelada</Badge>;
    }
  };

  const totalPages = Math.ceil(pageData.total / pageSize);

  return (
    <div className="bg-[#0F1115] border border-white/10 rounded-2xl overflow-hidden shadow-[0_0_30px_-10px_rgba(247,147,26,0.1)]">
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse text-xs">
          <thead>
            <tr className="border-b border-white/10 bg-[#030304]/60 text-[11px] font-semibold text-slate-400 uppercase tracking-wider font-mono select-none">
              <th className="py-2.5 px-3 w-10 text-center">OK</th>
              <th className="py-2.5 px-3">Prioridade</th>
              <th className="py-2.5 px-3 min-w-[240px]">Título da Tarefa / Providência</th>
              <th className="py-2.5 px-3">Processo / CNJ</th>
              <th className="py-2.5 px-3">Responsável</th>
              <th className="py-2.5 px-3">Categoria</th>
              <th className="py-2.5 px-3">Vencimento</th>
              <th className="py-2.5 px-3 text-center">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            {pageData.items.map((task) => {
              const isDone = task.status === 'DONE';

              return (
                <tr
                  key={task.id}
                  onClick={() => onSelectTask(task.id)}
                  className={`group hover:bg-white/5 cursor-pointer transition-colors ${
                    isDone ? 'opacity-60' : ''
                  }`}
                >
                  {/* Status Toggle Checkbox */}
                  <td
                    className="py-2.5 px-3 text-center"
                    onClick={(e) => {
                      e.stopPropagation();
                      onChangeStatus(task.id, {
                        newStatus: isDone ? 'TODO' : 'DONE',
                        syncCalendar: true,
                      });
                    }}
                  >
                    <button
                      className="p-1 rounded-full text-slate-400 hover:text-[#F7931A] transition-colors"
                      title={isDone ? 'Reabrir tarefa' : 'Marcar como concluída'}
                    >
                      {isDone ? (
                        <CheckSquare className="h-4 w-4 text-emerald-400" />
                      ) : (
                        <Square className="h-4 w-4 text-slate-600 group-hover:text-slate-400" />
                      )}
                    </button>
                  </td>

                  {/* Priority */}
                  <td className="py-2.5 px-3 whitespace-nowrap">
                    {getPriorityBadge(task.priority)}
                  </td>

                  {/* Title */}
                  <td className="py-2.5 px-3">
                    <div
                      className={`font-semibold text-slate-100 ${
                        isDone ? 'line-through text-slate-400' : ''
                      }`}
                    >
                      {task.title}
                    </div>
                    {task.checklistTotalCount > 0 && (
                      <div className="text-[10px] text-slate-400 mt-0.5 flex items-center gap-2 font-mono">
                        <span>
                          Checklist: {task.checklistDoneCount}/{task.checklistTotalCount}
                        </span>
                        {task.commentCount > 0 && (
                          <span>• {task.commentCount} comentário(s)</span>
                        )}
                      </div>
                    )}
                  </td>

                  {/* Process / CNJ */}
                  <td className="py-2.5 px-3">
                    {task.caseFileNumber || task.cnjNumber ? (
                      <div
                        className="text-[#FFD600] font-mono text-[11px] hover:underline transition-colors flex items-center gap-1 truncate max-w-[180px]"
                        onClick={(e) => {
                          if (task.processId && onSelectCase) {
                            e.stopPropagation();
                            onSelectCase(task.processId);
                          }
                        }}
                      >
                        <Briefcase className="h-3 w-3 text-[#FFD600] shrink-0" />
                        <span className="truncate">{task.caseFileNumber || task.cnjNumber}</span>
                      </div>
                    ) : (
                      <span className="text-slate-600 text-[11px] font-mono">—</span>
                    )}
                  </td>

                  {/* Assigned User */}
                  <td className="py-2.5 px-3 whitespace-nowrap">
                    {task.assignedUser ? (
                      <div className="flex items-center gap-1.5 text-slate-300">
                        <div className="h-4.5 w-4.5 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-[9px] text-slate-300 font-bold">
                          {task.assignedUser.slice(0, 2).toUpperCase()}
                        </div>
                        <span className="text-[11px]">{task.assignedUser}</span>
                      </div>
                    ) : (
                      <span className="text-slate-600 text-[11px]">—</span>
                    )}
                  </td>

                  {/* Category */}
                  <td className="py-2.5 px-3 whitespace-nowrap">
                    <span className="px-2 py-0.5 rounded bg-slate-800 border border-slate-700 text-slate-300 font-mono text-[10px]">
                      {task.category || 'GERAL'}
                    </span>
                  </td>

                  {/* Due Date */}
                  <td className="py-2.5 px-3 whitespace-nowrap font-mono text-[11px]">
                    {task.dueDate ? (
                      <div>
                        <div
                          className={`font-semibold ${
                            task.overdue
                              ? 'text-rose-400'
                              : task.dueToday
                              ? 'text-amber-400'
                              : 'text-slate-300'
                          }`}
                        >
                          {format(new Date(task.dueDate), 'dd/MM/yyyy')}
                        </div>
                        {task.overdue && (
                          <span className="text-[10px] text-rose-400 font-bold uppercase">
                            Atrasada
                          </span>
                        )}
                        {task.dueToday && !task.overdue && (
                          <span className="text-[10px] text-amber-400 font-bold uppercase">
                            Hoje
                          </span>
                        )}
                      </div>
                    ) : (
                      <span className="text-slate-600">—</span>
                    )}
                  </td>

                  {/* Status */}
                  <td className="py-2.5 px-3 text-center whitespace-nowrap">
                    {getStatusBadge(task.status)}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Pagination Footer */}
      {pageData.total > 0 && (
        <div className="px-4 py-3 border-t border-slate-800 bg-slate-950/60 flex items-center justify-between text-xs text-slate-400">
          <div>
            Mostrando{' '}
            <span className="font-semibold text-slate-200">
              {page * pageSize + 1}–{Math.min((page + 1) * pageSize, pageData.total)}
            </span>{' '}
            de <span className="font-semibold text-slate-200">{pageData.total}</span> tarefas
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => onPageChange(Math.max(0, page - 1))}
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
              onClick={() => onPageChange(Math.min(totalPages - 1, page + 1))}
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
  );
};
