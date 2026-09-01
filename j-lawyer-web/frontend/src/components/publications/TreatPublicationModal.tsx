import React, { useState, useEffect } from 'react';
import {
  X,
  CheckCircle2,
  Calendar,
  Clock,
  User,
  AlertTriangle,
  FileCheck,
  Briefcase,
  Layers,
} from 'lucide-react';
import { PublicationDetail, PublicationOverview, PublicationTreatRequest } from '../../types/publications';
import { TaskCategory, TaskPriority } from '../../types/tasks';
import { useAuth } from '../../context/AuthContext';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface TreatPublicationModalProps {
  isOpen: boolean;
  onClose: () => void;
  publication: PublicationOverview | PublicationDetail | null;
  onConfirmTreat: (request: PublicationTreatRequest) => Promise<void>;
  isSubmitting?: boolean;
}

export const TreatPublicationModal: React.FC<TreatPublicationModalProps> = ({
  isOpen,
  onClose,
  publication,
  onConfirmTreat,
  isSubmitting = false,
}) => {
  const { session } = useAuth();
  const [treatmentType, setTreatmentType] = useState<'WITH_TASK' | 'NO_TASK'>('WITH_TASK');

  // Task form fields
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskAssignedUser, setTaskAssignedUser] = useState('');
  const [taskPriority, setTaskPriority] = useState<TaskPriority>('HIGH');
  const [taskCategory, setTaskCategory] = useState<TaskCategory>('PRAZO_FATAL');
  const [taskDueDate, setTaskDueDate] = useState('');
  const [taskDueTime, setTaskDueTime] = useState('18:00');
  const [syncCalendar, setSyncCalendar] = useState(true);
  const [notes, setNotes] = useState('');

  useEffect(() => {
    if (publication) {
      const defaultTitle = publication.cnjNumber
        ? `[Prazo/Intimação] Providência ${publication.courtCode ? `(${publication.courtCode})` : ''} — ${publication.cnjNumber}`
        : `[Intimação] Análise de publicação ${publication.courtCode ? `(${publication.courtCode})` : ''}`;

      setTaskTitle(defaultTitle);
      setTaskDescription(
        publication.snippet
          ? `Publicação disponibilizada em ${
              publication.availabilityDate
                ? format(new Date(publication.availabilityDate), 'dd/MM/yyyy', { locale: ptBR })
                : 'data recente'
            }.\n\nTrecho: "${publication.snippet}"`
          : ''
      );
      setTaskAssignedUser(publication.assignedUser || session?.username || 'admin');
      setTaskPriority('HIGH');
      setTaskCategory('PRAZO_FATAL');

      // Suggested due date (if provided by publication heuristics)
      if (publication.suggestedDueDate) {
        const d = new Date(publication.suggestedDueDate);
        setTaskDueDate(format(d, 'yyyy-MM-dd'));
      } else {
        // Default 5 business days / 7 calendar days
        const d = new Date();
        d.setDate(d.getDate() + 5);
        setTaskDueDate(format(d, 'yyyy-MM-dd'));
      }
      setTaskDueTime('18:00');
      setSyncCalendar(true);
      setNotes('');
      setTreatmentType('WITH_TASK');
    }
  }, [publication, session]);

  if (!isOpen || !publication) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    let dueDateEpoch: number | undefined = undefined;
    if (treatmentType === 'WITH_TASK' && taskDueDate) {
      const [year, month, day] = taskDueDate.split('-').map(Number);
      const dateObj = new Date(year, month - 1, day, 12, 0, 0);
      dueDateEpoch = dateObj.getTime();
    }

    const request: PublicationTreatRequest = {
      action: 'MARK_TREATED',
      user: session?.username || 'admin',
      notes: notes.trim() || undefined,
      createTask: treatmentType === 'WITH_TASK',
      taskTitle: treatmentType === 'WITH_TASK' ? taskTitle.trim() : undefined,
      taskDescription: treatmentType === 'WITH_TASK' ? taskDescription.trim() : undefined,
      taskAssignedUser: treatmentType === 'WITH_TASK' ? taskAssignedUser : undefined,
      taskPriority: treatmentType === 'WITH_TASK' ? taskPriority : undefined,
      taskCategory: treatmentType === 'WITH_TASK' ? taskCategory : undefined,
      taskDueDate: dueDateEpoch,
      taskDueTime: treatmentType === 'WITH_TASK' ? taskDueTime : undefined,
      syncCalendar: treatmentType === 'WITH_TASK' ? syncCalendar : false,
    };

    await onConfirmTreat(request);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150">
      <div className="bg-slate-900 border border-slate-700 rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col overflow-hidden">
        {/* Header */}
        <div className="px-5 py-4 border-b border-slate-800 flex items-center justify-between bg-slate-950/60">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
              <FileCheck className="h-4 w-4" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-slate-100">
                Tratamento de Publicação Judicial
              </h2>
              <p className="text-[11px] text-slate-400">
                Defina o fluxo operacional e providências para esta intimação
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

        {/* Content */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-5 space-y-4 text-xs">
          {/* Context Snippet */}
          <div className="p-3 bg-slate-950 border border-slate-800 rounded-lg space-y-1.5">
            <div className="flex items-center justify-between">
              <span className="font-semibold text-slate-200">
                {publication.courtCode || 'Tribunal'} — {publication.cnjNumber || 'Processo não vinculado'}
              </span>
              {publication.suggestedDeadlineDays && (
                <span className="px-2 py-0.5 rounded bg-amber-500/10 border border-amber-500/30 text-amber-300 text-[10px] font-mono">
                  Prazo sugerido: {publication.suggestedDeadlineDays} dias
                </span>
              )}
            </div>
            {publication.snippet && (
              <p className="text-slate-400 text-[11px] line-clamp-2 italic">
                "{publication.snippet}"
              </p>
            )}
          </div>

          {/* Treatment Decision Mode */}
          <div className="space-y-1.5">
            <label className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider">
              Ação de Tratamento
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setTreatmentType('WITH_TASK')}
                className={`p-3 rounded-lg border text-left transition-all flex items-start gap-2.5 ${
                  treatmentType === 'WITH_TASK'
                    ? 'bg-indigo-600/15 border-indigo-500/50 text-indigo-200'
                    : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
                }`}
              >
                <div
                  className={`mt-0.5 h-4 w-4 rounded-full border flex items-center justify-center shrink-0 ${
                    treatmentType === 'WITH_TASK'
                      ? 'border-indigo-400 bg-indigo-500'
                      : 'border-slate-600'
                  }`}
                >
                  {treatmentType === 'WITH_TASK' && (
                    <div className="h-1.5 w-1.5 rounded-full bg-white" />
                  )}
                </div>
                <div>
                  <div className="font-semibold text-xs text-slate-200">
                    Tratar e Criar Tarefa / Prazo
                  </div>
                  <div className="text-[11px] text-slate-400 mt-0.5">
                    Gera providência operacional no Kanban e sincroniza com a agenda
                  </div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => setTreatmentType('NO_TASK')}
                className={`p-3 rounded-lg border text-left transition-all flex items-start gap-2.5 ${
                  treatmentType === 'NO_TASK'
                    ? 'bg-emerald-600/15 border-emerald-500/50 text-emerald-200'
                    : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
                }`}
              >
                <div
                  className={`mt-0.5 h-4 w-4 rounded-full border flex items-center justify-center shrink-0 ${
                    treatmentType === 'NO_TASK'
                      ? 'border-emerald-400 bg-emerald-500'
                      : 'border-slate-600'
                  }`}
                >
                  {treatmentType === 'NO_TASK' && (
                    <div className="h-1.5 w-1.5 rounded-full bg-white" />
                  )}
                </div>
                <div>
                  <div className="font-semibold text-xs text-slate-200">
                    Apenas Marcar como Tratada
                  </div>
                  <div className="text-[11px] text-slate-400 mt-0.5">
                    Ciência tomada sem necessidade de criar nova providência ou prazo
                  </div>
                </div>
              </button>
            </div>
          </div>

          {/* Conditional Task Fields */}
          {treatmentType === 'WITH_TASK' && (
            <div className="space-y-3 pt-2 border-t border-slate-800">
              <div className="space-y-1">
                <label className="text-[11px] font-medium text-slate-300">
                  Título da Providência / Tarefa *
                </label>
                <input
                  type="text"
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors"
                  placeholder="Ex: Elaborar Contestação sobre Intimação STJ..."
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <label className="text-[11px] font-medium text-slate-300">Responsável</label>
                  <div className="relative">
                    <input
                      type="text"
                      value={taskAssignedUser}
                      onChange={(e) => setTaskAssignedUser(e.target.value)}
                      className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors"
                      placeholder="Usuário responsável..."
                    />
                  </div>
                </div>

                <div className="space-y-1">
                  <label className="text-[11px] font-medium text-slate-300">Prioridade</label>
                  <select
                    value={taskPriority}
                    onChange={(e) => setTaskPriority(e.target.value as TaskPriority)}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors"
                  >
                    <option value="URGENT">Urgente (Prazo Fatal)</option>
                    <option value="HIGH">Alta Prioridade</option>
                    <option value="NORMAL">Normal</option>
                    <option value="LOW">Baixa Prioridade</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div className="space-y-1">
                  <label className="text-[11px] font-medium text-slate-300">Categoria</label>
                  <select
                    value={taskCategory}
                    onChange={(e) => setTaskCategory(e.target.value as TaskCategory)}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors"
                  >
                    <option value="PRAZO_FATAL">Prazo Fatal</option>
                    <option value="PETICAO">Petição / Peça</option>
                    <option value="AUDIENCIA">Audiência</option>
                    <option value="DILIGENCIA">Diligência Externa</option>
                    <option value="ANALISE">Análise Processual</option>
                    <option value="OUTROS">Outros</option>
                  </select>
                </div>

                <div className="space-y-1">
                  <label className="text-[11px] font-medium text-slate-300 flex items-center gap-1">
                    <Calendar className="h-3 w-3 text-indigo-400" />
                    Data de Vencimento
                  </label>
                  <input
                    type="date"
                    value={taskDueDate}
                    onChange={(e) => setTaskDueDate(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-[11px] font-medium text-slate-300 flex items-center gap-1">
                    <Clock className="h-3 w-3 text-slate-400" />
                    Horário Limite
                  </label>
                  <input
                    type="text"
                    value={taskDueTime}
                    onChange={(e) => setTaskDueTime(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors"
                    placeholder="18:00"
                  />
                </div>
              </div>

              <div className="flex items-center gap-2 pt-1">
                <input
                  type="checkbox"
                  id="syncCalendar"
                  checked={syncCalendar}
                  onChange={(e) => setSyncCalendar(e.target.checked)}
                  className="rounded border-slate-700 bg-slate-950 text-indigo-600 focus:ring-indigo-500 h-3.5 w-3.5"
                />
                <label htmlFor="syncCalendar" className="text-slate-300 text-[11px] cursor-pointer">
                  Sincronizar com a Agenda & Controle de Prazos do j-lawyer
                </label>
              </div>
            </div>
          )}

          {/* Notes / Observation */}
          <div className="space-y-1 pt-2 border-t border-slate-800">
            <label className="text-[11px] font-medium text-slate-300">
              Observações / Despacho Interno (Opcional)
            </label>
            <textarea
              rows={2}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors"
              placeholder="Anotação de triagem sobre esta publicação..."
            />
          </div>

          {/* Important Notice */}
          <div className="p-2.5 bg-amber-500/5 border border-amber-500/20 rounded-lg flex items-start gap-2 text-amber-300 text-[11px]">
            <AlertTriangle className="h-4 w-4 shrink-0 mt-0.5 text-amber-400" />
            <span>
              <strong>Atenção:</strong> O tratamento interno no BR-LAWYER organiza a rotina do
              escritório e <strong>não substitui</strong> a ciência formal nos sistemas dos tribunais
              (PJe, e-SAJ, Projudi, etc.).
            </span>
          </div>

          {/* Buttons */}
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
              <span>{isSubmitting ? 'Salvando...' : 'Confirmar Tratamento'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
