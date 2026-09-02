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
      <div className="fixed inset-0 z-40 bg-black/60 backdrop-blur-sm transition-opacity" onClick={onClose} />
      <div className="fixed inset-y-0 right-0 z-50 w-full max-w-xl bg-[#0F1115] border-l border-white/10 shadow-[0_0_50px_-10px_rgba(247,147,26,0.15)] flex flex-col animate-drawer-in text-xs">
        {/* Header */}
        <div className="px-6 py-4 border-b border-white/10 flex items-center justify-between bg-[#030304]/80 shrink-0">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-8 w-8 rounded-xl bg-[#EA580C]/15 border border-[#F7931A]/40 flex items-center justify-center text-[#F7931A] shrink-0 shadow-[0_0_12px_rgba(247,147,26,0.25)]">
              <ListTodo className="h-4 w-4" />
            </div>
            <div className="flex flex-col truncate">
              <span className="font-semibold text-slate-100 truncate text-sm font-heading">
                {task.title}
              </span>
              <div className="flex items-center gap-2 mt-0.5">
                <span className="text-[10px] uppercase font-mono text-slate-400">
                  {task.category || 'TAREFA'}
                </span>
                {task.overdue && (
                  <span className="px-2 py-0.5 rounded-full bg-rose-500/20 text-rose-300 border border-rose-500/30 text-[10px] font-bold font-mono">
                    ATRASADA
                  </span>
                )}
                {task.dueToday && (
                  <span className="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-300 border border-amber-500/30 text-[10px] font-bold font-mono">
                    VENCE HOJE
                  </span>
                )}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-1.5 shrink-0">
            <button
              onClick={() => onEditTask(task)}
              className="p-1.5 rounded-full text-slate-400 hover:text-slate-200 hover:bg-white/10 transition-colors"
              title="Editar tarefa"
            >
              <Edit className="h-4 w-4" />
            </button>
            <button
              onClick={onClose}
              className="p-1.5 rounded-full text-slate-400 hover:text-slate-200 hover:bg-white/10 transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          {/* Status & Priority Control Row */}
          <div className="grid grid-cols-2 gap-3 p-4 bg-[#030304] border border-white/10 rounded-2xl">
            <div className="space-y-1">
              <label className="text-[10px] uppercase font-semibold text-slate-500 font-mono tracking-wider">
                Status da Tarefa
              </label>
              <select
                value={task.status}
                onChange={handleStatusSelect}
                className="w-full px-3 py-1.5 bg-[#0F1115] border border-white/10 rounded-xl text-slate-100 font-medium focus:outline-none focus:border-[#F7931A] transition-colors text-xs"
              >
                <option value="TODO">A Fazer</option>
                <option value="IN_PROGRESS">Em Andamento</option>
                <option value="WAITING">Aguardando Terceiro</option>
                <option value="DONE">Concluída</option>
                <option value="CANCELLED">Cancelada</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-[10px] uppercase font-semibold text-slate-500 font-mono tracking-wider">
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
            <div className="p-4 bg-[#030304] border border-white/10 rounded-2xl flex items-center justify-between">
              <div className="flex items-center gap-2.5 truncate">
                <Briefcase className="h-4 w-4 text-[#FFD600] shrink-0" />
                <div className="truncate">
                  <span className="text-[10px] uppercase font-semibold text-slate-500 font-mono tracking-wider">
                    Processo Vinculado
                  </span>
                  <div className="font-semibold text-slate-200 truncate font-mono">
                    {task.caseFileNumber || task.cnjNumber || 'Sem processo vinculado'}
                  </div>
                  {task.caseName && (
                    <div className="text-[11px] text-slate-400 truncate">{task.caseName}</div>
                  )}
                </div>
              </div>

              {task.processId && onOpenCase && (
                <button
                  onClick={() => onOpenCase(task.processId!)}
                  className="px-3 py-1.5 rounded-full bg-[#F7931A]/15 hover:bg-[#F7931A]/25 text-[#F7931A] border border-[#F7931A]/40 font-medium text-[11px] flex items-center gap-1 transition-all shrink-0"
                >
                  <span>Ver Autos</span>
                  <ExternalLink className="h-3 w-3" />
                </button>
              )}
            </div>

            {/* Publication of Origin */}
            {task.publicationId && (
              <div className="p-4 bg-[#030304] border border-white/10 rounded-2xl flex items-center justify-between">
                <div className="flex items-center gap-2.5 truncate">
                  <FileText className="h-4 w-4 text-emerald-400 shrink-0" />
                  <div className="truncate">
                    <span className="text-[10px] uppercase font-semibold text-slate-500 font-mono tracking-wider">
                      Publicação de Origem
                    </span>
                    <div className="font-medium text-slate-200 truncate">
                      Intimação / Diário de Justiça
                    </div>
                  </div>
                </div>

                {onOpenPublication && (
                  <button
                    onClick={() => onOpenPublication(task.publicationId!)}
                    className="px-3 py-1.5 rounded-full bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-300 border border-emerald-500/30 font-medium text-[11px] flex items-center gap-1 transition-colors shrink-0"
                  >
                    <span>Ver Publicação</span>
                    <ChevronRight className="h-3 w-3" />
                  </button>
                )}
              </div>
            )}
          </div>

          {/* Dates & Assignment Grid */}
          <div className="grid grid-cols-2 gap-3 p-4 bg-[#030304] border border-white/10 rounded-2xl">
            <div>
              <span className="text-[10px] uppercase font-semibold text-slate-500 font-mono tracking-wider">
                Data de Vencimento
              </span>
              <div
                className={`font-mono text-xs font-semibold mt-0.5 ${
                  task.overdue
                    ? 'text-rose-400'
                    : task.dueToday
                    ? 'text-amber-400'
                    : 'text-slate-200'
                }`}
              >
                {task.dueDate ? format(new Date(task.dueDate), 'dd/MM/yyyy', { locale: ptBR }) : 'Sem data'}{' '}
                {task.dueTime ? `às ${task.dueTime}` : ''}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-semibold text-slate-500 font-mono tracking-wider">
                Responsável
              </span>
              <div className="text-slate-200 font-medium mt-0.5 flex items-center gap-1.5">
                <div className="h-5 w-5 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-[10px] text-slate-300 font-mono font-bold">
                  {task.assignedUser ? task.assignedUser.slice(0, 2).toUpperCase() : '??'}
                </div>
                <span>{task.assignedUser || 'Não atribuído'}</span>
              </div>
            </div>
          </div>

          {/* Description */}
          {task.description && (
            <div className="space-y-1">
              <span className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider font-mono">
                Descrição / Instruções
              </span>
              <div className="p-4 bg-[#030304] border border-white/10 rounded-2xl text-slate-200 leading-relaxed whitespace-pre-wrap font-sans">
                {task.description}
              </div>
            </div>
          )}

          {/* Checklist Section */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider font-mono flex items-center gap-1.5">
                <CheckSquare className="h-3.5 w-3.5 text-[#FFD600]" />
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
                    className="p-2.5 bg-[#030304] border border-white/10 hover:border-[#F7931A]/40 rounded-xl flex items-center justify-between group transition-colors"
                  >
                    <div
                      onClick={() => onToggleChecklistItem(task.id, item.id, !item.done)}
                      className="flex items-center gap-2 cursor-pointer flex-1"
                    >
                      {item.done ? (
                        <CheckSquare className="h-4 w-4 text-emerald-400 shrink-0" />
                      ) : (
                        <Square className="h-4 w-4 text-slate-500 hover:text-slate-300 shrink-0" />
                      )}
                      <span
                        className={`text-xs ${
                          item.done ? 'line-through text-slate-500' : 'text-slate-200'
                        }`}
                      >
                        {item.title}
                      </span>
                    </div>

                    <button
                      onClick={() => onDeleteChecklistItem(task.id, item.id)}
                      className="opacity-0 group-hover:opacity-100 p-1 text-slate-500 hover:text-rose-400 transition-all"
                      title="Excluir item"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  </div>
                ))
              ) : (
                <div className="text-slate-500 text-[11px] p-3 bg-[#030304] rounded-xl border border-white/10 italic">
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
                className="flex-1 px-3.5 py-1.5 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs"
              />
              <button
                type="submit"
                disabled={isSubmittingChecklist || !newChecklistTitle.trim()}
                className="px-3.5 py-1.5 bg-white/5 hover:bg-white/10 text-slate-200 font-medium rounded-full border border-white/10 flex items-center gap-1 transition-colors disabled:opacity-40"
              >
                <Plus className="h-3.5 w-3.5" />
                <span>Adicionar</span>
              </button>
            </form>
          </div>

          {/* Comments Section */}
          <div className="space-y-2">
            <span className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider font-mono flex items-center gap-1.5">
              <MessageSquare className="h-3.5 w-3.5 text-[#FFD600]" />
              Comentários & Histórico ({task.comments?.length || 0})
            </span>

            {/* Comments List */}
            <div className="space-y-2 max-h-48 overflow-y-auto">
              {task.comments && task.comments.length > 0 ? (
                task.comments.map((c) => (
                  <div key={c.id} className="p-3.5 bg-[#030304] border border-white/10 rounded-xl space-y-1">
                    <div className="flex items-center justify-between text-[10px] text-slate-500">
                      <span className="font-semibold text-slate-300">{c.authorUser}</span>
                      <span className="font-mono">
                        {format(new Date(c.createdAt), 'dd/MM/yy HH:mm')}
                      </span>
                    </div>
                    <p className="text-slate-200 text-xs whitespace-pre-wrap font-sans">{c.commentText}</p>
                  </div>
                ))
              ) : (
                <div className="text-slate-500 text-[11px] p-3 bg-[#030304] rounded-xl border border-white/10 italic">
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
                className="w-full px-3.5 py-2 bg-[#030304] border border-white/10 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-[#F7931A] focus:ring-1 focus:ring-[#F7931A]/30 transition-all text-xs"
              />
              <div className="flex justify-end">
                <button
                  type="submit"
                  disabled={isSubmittingComment || !newComment.trim()}
                  className="px-4 py-1.5 bg-[#F7931A] hover:bg-[#EA580C] text-white font-semibold rounded-full shadow-[0_0_12px_rgba(247,147,26,0.3)] flex items-center gap-1.5 transition-all disabled:opacity-40 text-xs hover:scale-[1.02]"
                >
                  <Send className="h-3 w-3" />
                  <span>Enviar Comentário</span>
                </button>
              </div>
            </form>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 border-t border-white/10 bg-[#030304] flex items-center justify-between shrink-0">
          <button
            onClick={() => onDeleteTask(task.id)}
            className="px-4 py-2 rounded-full bg-white/5 hover:bg-rose-950/40 text-slate-400 hover:text-rose-400 border border-white/10 hover:border-rose-800/40 font-medium flex items-center gap-1.5 transition-colors"
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
            className={`px-5 py-2 rounded-full font-semibold shadow-[0_0_15px_rgba(16,185,129,0.3)] flex items-center gap-1.5 transition-all hover:scale-[1.02] ${
              isDone
                ? 'bg-white/10 hover:bg-white/20 text-slate-200'
                : 'bg-emerald-600 hover:bg-emerald-500 text-white'
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
