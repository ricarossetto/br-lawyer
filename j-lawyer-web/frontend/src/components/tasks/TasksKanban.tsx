import React, { useState } from 'react';
import {
  ListTodo,
  Calendar,
  Clock,
  User,
  Briefcase,
  CheckSquare,
  MessageSquare,
  AlertCircle,
  Plus,
  CheckCircle2,
  AlertTriangle,
} from 'lucide-react';
import { KanbanBoard, TaskOverview, TaskPriority, TaskStatus } from '../../types/tasks';
import { Badge } from '../common/Badge';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface TasksKanbanProps {
  board: KanbanBoard | null;
  isLoading?: boolean;
  onSelectTask: (taskId: string) => void;
  onStatusChange: (taskId: string, newStatus: TaskStatus) => Promise<void>;
  onNewTask?: () => void;
  onSelectCase?: (caseId: string) => void;
}

const COLUMN_CONFIG: Array<{
  status: TaskStatus;
  title: string;
  badgeVariant: 'blue' | 'yellow' | 'purple' | 'green';
  borderColor: string;
}> = [
  { status: 'TODO', title: 'A Fazer', badgeVariant: 'blue', borderColor: 'border-indigo-500/40' },
  {
    status: 'IN_PROGRESS',
    title: 'Em Andamento',
    badgeVariant: 'yellow',
    borderColor: 'border-amber-500/40',
  },
  {
    status: 'WAITING',
    title: 'Aguardando',
    badgeVariant: 'purple',
    borderColor: 'border-purple-500/40',
  },
  {
    status: 'DONE',
    title: 'Concluído',
    badgeVariant: 'green',
    borderColor: 'border-emerald-500/40',
  },
];

export const TasksKanban: React.FC<TasksKanbanProps> = ({
  board,
  isLoading = false,
  onSelectTask,
  onStatusChange,
  onNewTask,
  onSelectCase,
}) => {
  const [draggingTaskId, setDraggingTaskId] = useState<string | null>(null);
  const [dragOverColumn, setDragOverColumn] = useState<TaskStatus | null>(null);

  const handleDragStart = (e: React.DragEvent, taskId: string) => {
    setDraggingTaskId(taskId);
    e.dataTransfer.setData('text/plain', taskId);
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleDragOver = (e: React.DragEvent, status: TaskStatus) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    if (dragOverColumn !== status) {
      setDragOverColumn(status);
    }
  };

  const handleDragLeave = (e: React.DragEvent, status: TaskStatus) => {
    e.preventDefault();
    if (dragOverColumn === status) {
      setDragOverColumn(null);
    }
  };

  const handleDrop = async (e: React.DragEvent, targetStatus: TaskStatus) => {
    e.preventDefault();
    setDragOverColumn(null);
    const taskId = e.dataTransfer.getData('text/plain') || draggingTaskId;
    setDraggingTaskId(null);

    if (taskId) {
      await onStatusChange(taskId, targetStatus);
    }
  };

  if (isLoading) {
    return (
      <div className="p-16 flex flex-col items-center justify-center text-slate-400 space-y-3">
        <div className="h-6 w-6 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin" />
        <span className="text-xs">Carregando quadro operacional Kanban...</span>
      </div>
    );
  }

  // Map columns by status from board
  const columnsMap = new Map<TaskStatus, TaskOverview[]>();
  COLUMN_CONFIG.forEach((col) => columnsMap.set(col.status, []));

  if (board?.columns) {
    board.columns.forEach((c) => {
      if (columnsMap.has(c.status)) {
        columnsMap.set(c.status, c.tasks || []);
      }
    });
  }

  const getPriorityBadge = (priority: TaskPriority) => {
    switch (priority) {
      case 'URGENT':
        return <Badge variant="red">Urgente</Badge>;
      case 'HIGH':
        return <Badge variant="yellow">Alta</Badge>;
      case 'NORMAL':
        return <Badge variant="blue">Normal</Badge>;
      case 'LOW':
        return <Badge variant="gray">Baixa</Badge>;
    }
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 items-start select-none">
      {COLUMN_CONFIG.map((col) => {
        const tasks = columnsMap.get(col.status) || [];
        const isColumnDragOver = dragOverColumn === col.status;

        return (
          <div
            key={col.status}
            onDragOver={(e) => handleDragOver(e, col.status)}
            onDragLeave={(e) => handleDragLeave(e, col.status)}
            onDrop={(e) => handleDrop(e, col.status)}
            className={`bg-slate-900 border rounded-xl flex flex-col max-h-[calc(100vh-220px)] transition-all duration-150 ${
              isColumnDragOver
                ? 'border-indigo-500 bg-slate-900/90 shadow-lg shadow-indigo-500/10 ring-2 ring-indigo-500/20'
                : 'border-slate-800'
            }`}
          >
            {/* Column Header */}
            <div className="p-3 border-b border-slate-800 bg-slate-950/60 rounded-t-xl flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="font-semibold text-xs text-slate-200">{col.title}</span>
                <span className="px-2 py-0.5 rounded-full bg-slate-800 text-slate-400 font-mono text-[10px] font-bold">
                  {tasks.length}
                </span>
              </div>

              {col.status === 'TODO' && onNewTask && (
                <button
                  onClick={onNewTask}
                  className="p-1 rounded text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
                  title="Criar tarefa nesta coluna"
                >
                  <Plus className="h-3.5 w-3.5" />
                </button>
              )}
            </div>

            {/* Column Cards Container */}
            <div className="p-2.5 space-y-2.5 overflow-y-auto flex-1 min-h-[140px]">
              {tasks.length === 0 ? (
                <div className="p-6 text-center text-slate-500 text-[11px] border border-dashed border-slate-800/80 rounded-lg">
                  Nenhuma tarefa nesta coluna
                </div>
              ) : (
                tasks.map((task) => {
                  const isDragging = draggingTaskId === task.id;

                  return (
                    <div
                      key={task.id}
                      draggable
                      onDragStart={(e) => handleDragStart(e, task.id)}
                      onClick={() => onSelectTask(task.id)}
                      className={`p-3 bg-slate-950 border rounded-lg hover:border-slate-700 cursor-grab active:cursor-grabbing transition-all duration-150 group shadow-xs space-y-2 ${
                        isDragging ? 'opacity-40 scale-98 border-indigo-500' : 'border-slate-800'
                      }`}
                    >
                      {/* Card Top: Priority & Category */}
                      <div className="flex items-center justify-between gap-1">
                        <div className="flex items-center gap-1.5">
                          {getPriorityBadge(task.priority)}
                          <span className="text-[10px] uppercase font-mono text-slate-400 truncate max-w-[100px]">
                            {task.category || 'TAREFA'}
                          </span>
                        </div>

                        {task.overdue && (
                          <span className="px-1.5 py-0.2 rounded bg-rose-500/20 text-rose-300 border border-rose-500/30 text-[9px] font-bold uppercase">
                            Atrasada
                          </span>
                        )}
                        {task.dueToday && !task.overdue && (
                          <span className="px-1.5 py-0.2 rounded bg-amber-500/20 text-amber-300 border border-amber-500/30 text-[9px] font-bold uppercase">
                            Hoje
                          </span>
                        )}
                      </div>

                      {/* Card Title */}
                      <div className="font-semibold text-xs text-slate-100 group-hover:text-indigo-300 transition-colors leading-snug">
                        {task.title}
                      </div>

                      {/* Linked Process (if any) */}
                      {(task.caseFileNumber || task.cnjNumber) && (
                        <div
                          className="flex items-center gap-1.5 text-[10px] text-slate-400 truncate hover:text-indigo-300 transition-colors"
                          onClick={(e) => {
                            if (task.processId && onSelectCase) {
                              e.stopPropagation();
                              onSelectCase(task.processId);
                            }
                          }}
                        >
                          <Briefcase className="h-3 w-3 text-indigo-400 shrink-0" />
                          <span className="font-mono truncate">
                            {task.caseFileNumber || task.cnjNumber}
                          </span>
                        </div>
                      )}

                      {/* Card Footer: Due Date, User, Counters */}
                      <div className="pt-2 border-t border-slate-900 flex items-center justify-between text-[10px] text-slate-400">
                        {/* Due Date */}
                        <div className="flex items-center gap-1">
                          <Calendar
                            className={`h-3 w-3 ${
                              task.overdue
                                ? 'text-rose-400'
                                : task.dueToday
                                ? 'text-amber-400'
                                : 'text-slate-500'
                            }`}
                          />
                          <span
                            className={`font-mono ${
                              task.overdue
                                ? 'text-rose-400 font-bold'
                                : task.dueToday
                                ? 'text-amber-400 font-bold'
                                : 'text-slate-400'
                            }`}
                          >
                            {task.dueDate ? format(new Date(task.dueDate), 'dd/MM') : '—'}
                          </span>
                        </div>

                        {/* Checklist & Comments Counters */}
                        <div className="flex items-center gap-2">
                          {task.checklistTotalCount > 0 && (
                            <div className="flex items-center gap-0.5" title="Itens do checklist">
                              <CheckSquare className="h-3 w-3 text-slate-500" />
                              <span>
                                {task.checklistDoneCount}/{task.checklistTotalCount}
                              </span>
                            </div>
                          )}

                          {task.commentCount > 0 && (
                            <div className="flex items-center gap-0.5" title="Comentários">
                              <MessageSquare className="h-3 w-3 text-slate-500" />
                              <span>{task.commentCount}</span>
                            </div>
                          )}

                          {/* Assigned User Initials */}
                          <div
                            className="h-4.5 w-4.5 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-[9px] text-slate-300 font-bold"
                            title={`Responsável: ${task.assignedUser || 'Não atribuído'}`}
                          >
                            {task.assignedUser ? task.assignedUser.slice(0, 2).toUpperCase() : '??'}
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
