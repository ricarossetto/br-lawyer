import React, { useState, useEffect } from 'react';
import {
  X,
  ListTodo,
  Calendar,
  Clock,
  User,
  Briefcase,
  AlertCircle,
  Tag,
  CheckCircle2,
} from 'lucide-react';
import { TaskCategory, TaskCreateUpdateRequest, TaskDetail, TaskPriority, TaskStatus } from '../../types/tasks';
import { useAuth } from '../../context/AuthContext';
import { format } from 'date-fns';

interface TaskCreateEditModalProps {
  isOpen: boolean;
  onClose: () => void;
  task?: TaskDetail | null;
  initialProcessId?: string;
  initialPublicationId?: string;
  onSave: (task: TaskCreateUpdateRequest, syncCalendar: boolean) => Promise<void>;
  isSubmitting?: boolean;
}

export const TaskCreateEditModal: React.FC<TaskCreateEditModalProps> = ({
  isOpen,
  onClose,
  task,
  initialProcessId,
  initialPublicationId,
  onSave,
  isSubmitting = false,
}) => {
  const { session } = useAuth();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [assignedUser, setAssignedUser] = useState('');
  const [priority, setPriority] = useState<TaskPriority>('NORMAL');
  const [category, setCategory] = useState<TaskCategory>('PETICAO');
  const [status, setStatus] = useState<TaskStatus>('TODO');
  const [dueDate, setDueDate] = useState('');
  const [dueTime, setDueTime] = useState('18:00');
  const [estimatedMinutes, setEstimatedMinutes] = useState<number | undefined>(undefined);
  const [syncCalendar, setSyncCalendar] = useState(true);
  const [notes, setNotes] = useState('');

  useEffect(() => {
    if (task) {
      setTitle(task.title || '');
      setDescription(task.description || '');
      setAssignedUser(task.assignedUser || session?.username || 'admin');
      setPriority(task.priority || 'NORMAL');
      setCategory(task.category || 'PETICAO');
      setStatus(task.status || 'TODO');
      setDueDate(task.dueDate ? format(new Date(task.dueDate), 'yyyy-MM-dd') : '');
      setDueTime(task.dueTime || '18:00');
      setEstimatedMinutes(task.estimatedMinutes);
      setNotes(task.notes || '');
      setSyncCalendar(true);
    } else {
      setTitle('');
      setDescription('');
      setAssignedUser(session?.username || 'admin');
      setPriority('NORMAL');
      setCategory('PETICAO');
      setStatus('TODO');
      const d = new Date();
      d.setDate(d.getDate() + 3);
      setDueDate(format(d, 'yyyy-MM-dd'));
      setDueTime('18:00');
      setEstimatedMinutes(undefined);
      setNotes('');
      setSyncCalendar(true);
    }
  }, [task, session, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    let dueDateEpoch: number | undefined = undefined;
    if (dueDate) {
      const [year, month, day] = dueDate.split('-').map(Number);
      const dateObj = new Date(year, month - 1, day, 12, 0, 0);
      dueDateEpoch = dateObj.getTime();
    }

    const payload: TaskCreateUpdateRequest = {
      id: task?.id,
      title: title.trim(),
      description: description.trim() || undefined,
      processId: task?.processId || initialProcessId,
      publicationId: task?.publicationId || initialPublicationId,
      assignedUser: assignedUser.trim() || session?.username || 'admin',
      priority,
      category,
      status,
      dueDate: dueDateEpoch,
      dueTime: dueTime.trim() || undefined,
      estimatedMinutes: estimatedMinutes ? Number(estimatedMinutes) : undefined,
      notes: notes.trim() || undefined,
    };

    await onSave(payload, syncCalendar);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150">
      <div className="bg-slate-900 border border-slate-700 rounded-xl shadow-2xl w-full max-w-xl max-h-[90vh] flex flex-col overflow-hidden text-xs">
        {/* Header */}
        <div className="px-5 py-4 border-b border-slate-800 flex items-center justify-between bg-slate-950/60">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400">
              <ListTodo className="h-4 w-4" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-slate-100">
                {task ? 'Editar Tarefa / Providência' : 'Nova Tarefa Jurídica'}
              </h2>
              <p className="text-[11px] text-slate-400">
                {task
                  ? 'Atualize os detalhes, prazos e responsabilidade'
                  : 'Cadastre uma nova atividade operacional vinculada ao workflow'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-5 space-y-4">
          {/* Title */}
          <div className="space-y-1">
            <label className="text-[11px] font-medium text-slate-300">Título da Tarefa *</label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Ex: Elaborar minuta de Recurso Especial..."
              className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
            />
          </div>

          {/* Description */}
          <div className="space-y-1">
            <label className="text-[11px] font-medium text-slate-300">Descrição / Instruções</label>
            <textarea
              rows={3}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Descreva as orientações para a execução da tarefa..."
              className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
            />
          </div>

          {/* User & Priority */}
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300">Responsável</label>
              <input
                type="text"
                value={assignedUser}
                onChange={(e) => setAssignedUser(e.target.value)}
                placeholder="Usuário atribuído..."
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              />
            </div>

            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300">Prioridade</label>
              <select
                value={priority}
                onChange={(e) => setPriority(e.target.value as TaskPriority)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              >
                <option value="URGENT">Urgente (Prazo Fatal)</option>
                <option value="HIGH">Alta Prioridade</option>
                <option value="NORMAL">Normal</option>
                <option value="LOW">Baixa Prioridade</option>
              </select>
            </div>
          </div>

          {/* Category & Status */}
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300">Categoria</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value as TaskCategory)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              >
                <option value="PRAZO_FATAL">Prazo Fatal</option>
                <option value="PETICAO">Petição / Peça</option>
                <option value="AUDIENCIA">Audiência</option>
                <option value="DILIGENCIA">Diligência Externa</option>
                <option value="REUNIAO">Reunião com Cliente</option>
                <option value="ANALISE">Análise Processual</option>
                <option value="ADMINISTRATIVO">Administrativo</option>
                <option value="OUTROS">Outros</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300">Status</label>
              <select
                value={status}
                onChange={(e) => setStatus(e.target.value as TaskStatus)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              >
                <option value="TODO">A Fazer (TODO)</option>
                <option value="IN_PROGRESS">Em Andamento (IN_PROGRESS)</option>
                <option value="WAITING">Aguardando Terceiro / Órgão (WAITING)</option>
                <option value="DONE">Concluída (DONE)</option>
                <option value="CANCELLED">Cancelada (CANCELLED)</option>
              </select>
            </div>
          </div>

          {/* Due Date & Time & Estimation */}
          <div className="grid grid-cols-3 gap-3">
            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300 flex items-center gap-1">
                <Calendar className="h-3 w-3 text-indigo-400" />
                Data de Vencimento
              </label>
              <input
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              />
            </div>

            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300 flex items-center gap-1">
                <Clock className="h-3 w-3 text-slate-400" />
                Horário Limite
              </label>
              <input
                type="text"
                value={dueTime}
                onChange={(e) => setDueTime(e.target.value)}
                placeholder="18:00"
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              />
            </div>

            <div className="space-y-1">
              <label className="text-[11px] font-medium text-slate-300">Tempo Estimado (min)</label>
              <input
                type="number"
                min="0"
                step="15"
                value={estimatedMinutes ?? ''}
                onChange={(e) => setEstimatedMinutes(e.target.value ? Number(e.target.value) : undefined)}
                placeholder="Ex: 60"
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-xs"
              />
            </div>
          </div>

          {/* Sync Calendar */}
          <div className="flex items-center gap-2 pt-1">
            <input
              type="checkbox"
              id="syncCalendarTask"
              checked={syncCalendar}
              onChange={(e) => setSyncCalendar(e.target.checked)}
              className="rounded border-slate-700 bg-slate-950 text-indigo-600 focus:ring-indigo-500 h-3.5 w-3.5"
            />
            <label htmlFor="syncCalendarTask" className="text-slate-300 text-[11px] cursor-pointer">
              Sincronizar com a Agenda & Controle de Prazos do j-lawyer
            </label>
          </div>

          {/* Footer Buttons */}
          <div className="pt-3 border-t border-slate-800 flex items-center justify-end gap-2.5">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2 rounded-lg text-slate-300 hover:bg-slate-800 transition-colors font-medium"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-medium transition-colors shadow-sm flex items-center gap-1.5 disabled:opacity-50"
            >
              <CheckCircle2 className="h-4 w-4" />
              <span>{isSubmitting ? 'Salvando...' : task ? 'Salvar Alterações' : 'Criar Tarefa'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
