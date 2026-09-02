import React, { useState } from 'react';
import {
  X,
  ListTodo,
  Calendar,
  Clock,
  User,
  CheckCircle2,
  AlertTriangle,
  Briefcase,
  FileText,
  MessageSquare,
  Plus,
  Trash2,
  Edit,
  CheckSquare,
  Square,
  Send,
  ExternalLink,
  ChevronRight,
} from 'lucide-react';
import { TaskCategory, TaskDetail, TaskPriority, TaskStatus, TaskStatusChangeRequest } from '../../types/tasks';
import { Badge } from '../common/Badge';
import { useAuth } from '../../context/AuthContext';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface TaskInspectorDrawerProps {
  task: TaskDetail | null;
  isOpen: boolean;
  onClose: () => void;
  onChangeStatus: (id: string, req: TaskStatusChangeRequest) => Promise<void>;
  onAddComment: (id: string, text: string) => Promise<void>;
  onAddChecklistItem: (id: string, title: string) => Promise<void>;
  onToggleChecklistItem: (id: string, itemId: string, done: boolean) => Promise<void>;
  onDeleteChecklistItem: (id: string, itemId: string) => Promise<void>;
  onEditTask: (task: TaskDetail) => void;
  onDeleteTask: (id: string) => Promise<void>;
  onOpenCase?: (caseId: string) => void;
  onOpenPublication?: (pubId: string) => void;
}

export const TaskInspectorDrawer: React.FC<TaskInspectorDrawerProps> = ({
  task,
  isOpen,
  onClose,
  onChangeStatus,
  onAddComment,
  onAddChecklistItem,
  onToggleChecklistItem,
  onDeleteChecklistItem,
  onEditTask,
  onDeleteTask,
  onOpenCase,
  onOpenPublication,
}) => {
  const { session } = useAuth();
  const [newComment, setNewComment] = useState('');
  const [newChecklistTitle, setNewChecklistTitle] = useState('');
  const [isSubmittingComment, setIsSubmittingComment] = useState(false);
  const [isSubmittingChecklist, setIsSubmittingChecklist] = useState(false);

  if (!isOpen || !task) return null;

  const handleStatusSelect = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newStatus = e.target.value as TaskStatus;
    await onChangeStatus(task.id, {
      newStatus,
      user: session?.username || 'admin',
      syncCalendar: true,
    });
  };

  const handleCommentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    setIsSubmittingComment(true);
    try {
      await onAddComment(task.id, newComment.trim());
      setNewComment('');
    } finally {
      setIsSubmittingComment(false);
    }
  };

  const handleChecklistSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newChecklistTitle.trim()) return;
    setIsSubmittingChecklist(true);
    try {
      await onAddChecklistItem(task.id, newChecklistTitle.trim());
      setNewChecklistTitle('');
    } finally {
      setIsSubmittingChecklist(false);
    }
  };

  const isDone = task.status === 'DONE';

  return (
    <>
      <div className="fixed inset-0 z-40 bg-black/80 transition-opacity" onClick={onClose} />
      <div className="fixed inset-y-0 right-0 z-50 w-full max-w-xl bg-[#0F0F0F] border-l border-[#262626] flex flex-col animate-drawer-in text-xs rounded-none">
        {/* Header */}
        <div className="px-6 py-4 border-b border-[#262626] flex items-center justify-between bg-[#0A0A0A] shrink-0">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-7 w-7 bg-[#141414] border border-[#262626] flex items-center justify-center text-[#FAFAFA] shrink-0">
              <ListTodo className="h-4 w-4" />
            </div>
            <div className="flex flex-col truncate">
              <span className="font-bold text-[#FAFAFA] truncate text-sm font-heading tracking-tight">
                {task.title}
              </span>
              <div className="flex items-center gap-2 mt-0.5">
                <span className="text-[10px] uppercase font-mono tracking-wider text-[#737373]">
                  {task.category || 'TAREFA'}
                </span>
                {task.overdue && (
                  <span className="px-1.5 py-0.2 rounded-none bg-rose-950/40 text-rose-400 border border-rose-600/40 text-[9px] font-bold font-mono uppercase">
                    ATRASADA
                  </span>
                )}
                {task.dueToday && (
                  <span className="px-1.5 py-0.2 rounded-none bg-[#141414] text-[#FF3D00] border border-[#FF3D00]/40 text-[9px] font-bold font-mono uppercase">
                    VENCE HOJE
                  </span>
                )}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-1.5 shrink-0">
            <button
              onClick={() => onEditTask(task)}
              className="p-1.5 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors"
              title="Editar tarefa"
            >
              <Edit className="h-4 w-4" />
            </button>
            <button
              onClick={onClose}
              className="p-1.5 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          {/* Status & Priority Control Row */}
          <div className="grid grid-cols-2 gap-3 p-4 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <div className="space-y-1">
              <label className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Status da Tarefa
              </label>
              <select
                value={task.status}
                onChange={handleStatusSelect}
                className="w-full px-3 py-1.5 bg-[#141414] border border-[#262626] rounded-none text-[#FAFAFA] font-mono font-medium focus:outline-none focus:border-[#FF3D00] transition-colors text-xs"
              >
                <option value="TODO">A Fazer</option>
                <option value="IN_PROGRESS">Em Andamento</option>
                <option value="WAITING">Aguardando Terceiro</option>
                <option value="DONE">Concluída</option>
                <option value="CANCELLED">Cancelada</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Prioridade
              </label>
              <div className="pt-1.5 flex items-center gap-2">
                <Badge
                  variant={
                    task.priority === 'URGENT'
                      ? 'red'
                      : task.priority === 'HIGH'
                      ? 'yellow'
                      : task.priority === 'NORMAL'
                      ? 'active'
                      : 'neutral'
                  }
                >
                  {task.priority === 'URGENT'
                    ? 'Urgente'
                    : task.priority === 'HIGH'
                    ? 'Alta'
                    : task.priority === 'NORMAL'
                    ? 'Normal'
                    : 'Baixa'}
                </Badge>
              </div>
            </div>
          </div>

          {/* Context Links: Process & Publication */}
          <div className="space-y-2">
            {/* Linked Process */}
            <div className="p-4 bg-[#0A0A0A] border border-[#262626] rounded-none flex items-center justify-between">
              <div className="flex items-center gap-2.5 truncate">
                <Briefcase className="h-4 w-4 text-[#FAFAFA] shrink-0" />
                <div className="truncate">
                  <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                    Processo Vinculado
                  </span>
                  <div className="font-bold text-[#FAFAFA] truncate font-mono">
                    {task.caseFileNumber || task.cnjNumber || 'Sem processo vinculado'}
                  </div>
                  {task.caseName && (
                    <div className="text-[11px] text-[#737373] truncate">{task.caseName}</div>
                  )}
                </div>
              </div>

              {task.processId && onOpenCase && (
                <button
                  onClick={() => onOpenCase(task.processId!)}
                  className="px-3 py-1.5 rounded-none bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] border border-[#262626] font-mono text-[10px] uppercase tracking-wider font-bold flex items-center gap-1 transition-colors shrink-0"
                >
                  <span>Ver Autos</span>
                  <ExternalLink className="h-3 w-3" />
                </button>
              )}
            </div>

            {/* Publication of Origin */}
            {task.publicationId && (
              <div className="p-4 bg-[#0A0A0A] border border-[#262626] rounded-none flex items-center justify-between">
                <div className="flex items-center gap-2.5 truncate">
                  <FileText className="h-4 w-4 text-[#737373] shrink-0" />
                  <div className="truncate">
                    <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                      Publicação de Origem
                    </span>
                    <div className="font-medium text-[#FAFAFA] truncate">
                      Intimação / Diário de Justiça
                    </div>
                  </div>
                </div>

                {onOpenPublication && (
                  <button
                    onClick={() => onOpenPublication(task.publicationId!)}
                    className="px-3 py-1.5 rounded-none bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] border border-[#262626] font-mono text-[10px] uppercase tracking-wider font-bold flex items-center gap-1 transition-colors shrink-0"
                  >
                    <span>Ver Publicação</span>
                    <ChevronRight className="h-3 w-3" />
                  </button>
                )}
              </div>
            )}
          </div>

          {/* Dates & Assignment Grid */}
          <div className="grid grid-cols-2 gap-3 p-4 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <div>
              <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Data de Vencimento
              </span>
              <div
                className={`font-mono text-xs font-bold mt-0.5 ${
                  task.overdue
                    ? 'text-rose-400'
                    : task.dueToday
                    ? 'text-[#FF3D00]'
                    : 'text-[#FAFAFA]'
                }`}
              >
                {task.dueDate ? format(new Date(task.dueDate), 'dd/MM/yyyy', { locale: ptBR }) : 'Sem data'}{' '}
                {task.dueTime ? `às ${task.dueTime}` : ''}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Responsável
              </span>
              <div className="text-[#FAFAFA] font-medium mt-0.5 flex items-center gap-1.5">
                <div className="h-5 w-5 rounded-none bg-[#141414] border border-[#262626] flex items-center justify-center text-[9px] text-[#FAFAFA] font-mono font-bold">
                  {task.assignedUser ? task.assignedUser.slice(0, 2).toUpperCase() : '??'}
                </div>
                <span className="font-mono">{task.assignedUser || 'Não atribuído'}</span>
              </div>
            </div>
          </div>

          {/* Description */}
          {task.description && (
            <div className="space-y-1">
              <span className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono">
                Descrição / Instruções
              </span>
              <div className="p-4 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] leading-relaxed whitespace-pre-wrap font-sans">
                {task.description}
              </div>
            </div>
          )}

          {/* Checklist Section */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono flex items-center gap-1.5">
                <CheckSquare className="h-3.5 w-3.5 text-[#FAFAFA]" />
                Checklist / Subtarefas (
                {task.checklistItems?.filter((i) => i.done).length || 0}/
                {task.checklistItems?.length || 0})
              </span>
            </div>

            {/* Checklist Items List */}
            <div className="space-y-1.5">
              {task.checklistItems && task.checklistItems.length > 0 ? (
                task.checklistItems.map((item) => (
                  <div
                    key={item.id}
                    className="p-2.5 bg-[#0A0A0A] border border-[#262626] hover:border-[#737373] rounded-none flex items-center justify-between group transition-colors"
                  >
                    <div
                      onClick={() => onToggleChecklistItem(task.id, item.id, !item.done)}
                      className="flex items-center gap-2 cursor-pointer flex-1"
                    >
                      {item.done ? (
                        <CheckSquare className="h-4 w-4 text-emerald-400 shrink-0" />
                      ) : (
                        <Square className="h-4 w-4 text-[#525252] hover:text-[#FAFAFA] shrink-0" />
                      )}
                      <span
                        className={`text-xs ${
                          item.done ? 'line-through text-[#737373]' : 'text-[#FAFAFA]'
                        }`}
                      >
                        {item.title}
                      </span>
                    </div>

                    <button
                      onClick={() => onDeleteChecklistItem(task.id, item.id)}
                      className="opacity-0 group-hover:opacity-100 p-1 text-[#737373] hover:text-rose-400 transition-colors"
                      title="Excluir item"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  </div>
                ))
              ) : (
                <div className="text-[#737373] text-[11px] font-mono p-3 bg-[#0A0A0A] rounded-none border border-[#262626] italic">
                  Nenhum item adicionado ao checklist.
                </div>
              )}
            </div>

            {/* Add Checklist Input */}
            <form onSubmit={handleChecklistSubmit} className="flex items-center gap-2 pt-1">
              <input
                type="text"
                value={newChecklistTitle}
                onChange={(e) => setNewChecklistTitle(e.target.value)}
                placeholder="Adicionar item ao checklist..."
                className="flex-1 px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors text-xs"
              />
              <button
                type="submit"
                disabled={isSubmittingChecklist || !newChecklistTitle.trim()}
                className="px-3.5 py-2 bg-[#141414] hover:bg-[#1A1A1A] text-[#FAFAFA] font-mono text-[10px] uppercase tracking-wider font-bold rounded-none border border-[#262626] flex items-center gap-1 transition-colors disabled:opacity-40 cursor-pointer"
              >
                <Plus className="h-3.5 w-3.5" />
                <span>Adicionar</span>
              </button>
            </form>
          </div>

          {/* Comments Section */}
          <div className="space-y-2">
            <span className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono flex items-center gap-1.5">
              <MessageSquare className="h-3.5 w-3.5 text-[#737373]" />
              Comentários ({task.comments?.length || 0})
            </span>

            {/* Comments List */}
            <div className="space-y-2 max-h-48 overflow-y-auto">
              {task.comments && task.comments.length > 0 ? (
                task.comments.map((c) => (
                  <div key={c.id} className="p-3.5 bg-[#0A0A0A] border border-[#262626] rounded-none space-y-1">
                    <div className="flex items-center justify-between text-[10px] text-[#737373]">
                      <span className="font-bold text-[#FAFAFA] font-mono">{c.authorUser}</span>
                      <span className="font-mono">
                        {format(new Date(c.createdAt), 'dd/MM/yy HH:mm')}
                      </span>
                    </div>
                    <p className="text-[#FAFAFA] text-xs whitespace-pre-wrap font-sans">{c.commentText}</p>
                  </div>
                ))
              ) : (
                <div className="text-[#737373] text-[11px] font-mono p-3 bg-[#0A0A0A] rounded-none border border-[#262626] italic">
                  Nenhum comentário registrado.
                </div>
              )}
            </div>

            {/* Add Comment Input */}
            <form onSubmit={handleCommentSubmit} className="space-y-2 pt-1">
              <textarea
                rows={2}
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Escreva um comentário ou atualização sobre esta tarefa..."
                className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors text-xs font-sans"
              />
              <div className="flex justify-end">
                <button
                  type="submit"
                  disabled={isSubmittingComment || !newComment.trim()}
                  className="px-4 py-1.5 bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold font-mono text-[10px] uppercase tracking-wider rounded-none flex items-center gap-1.5 transition-colors disabled:opacity-40 cursor-pointer"
                >
                  <Send className="h-3 w-3" />
                  <span>Enviar Comentário</span>
                </button>
              </div>
            </form>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 border-t border-[#262626] bg-[#0A0A0A] flex items-center justify-between shrink-0">
          <button
            onClick={() => onDeleteTask(task.id)}
            className="px-4 py-2 rounded-none bg-[#141414] hover:bg-rose-950/40 text-[#737373] hover:text-rose-400 border border-[#262626] hover:border-rose-600/40 font-mono text-[10px] uppercase tracking-wider font-bold flex items-center gap-1.5 transition-colors cursor-pointer"
          >
            <Trash2 className="h-4 w-4" />
            <span>Excluir</span>
          </button>

          <button
            onClick={() =>
              onChangeStatus(task.id, {
                newStatus: isDone ? 'TODO' : 'DONE',
                user: session?.username || 'admin',
                syncCalendar: true,
              })
            }
            className={`px-5 py-2 rounded-none font-bold font-mono text-[10px] uppercase tracking-wider flex items-center gap-1.5 transition-colors cursor-pointer ${
              isDone
                ? 'bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] border border-[#262626]'
                : 'bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A]'
            }`}
          >
            <CheckCircle2 className="h-4 w-4" />
            <span>{isDone ? 'Reabrir Tarefa' : 'Concluir Tarefa'}</span>
          </button>
        </div>
      </div>
    </>
  );
};
