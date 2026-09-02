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
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80">
      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none w-full max-w-lg overflow-hidden flex flex-col max-h-[90vh] animate-modal-pop">
        {/* Header */}
        <div className="px-6 py-4 border-b border-[#262626] flex items-center justify-between bg-[#0A0A0A] shrink-0">
          <div className="flex items-center gap-2.5">
            <div className="h-7 w-7 bg-[#141414] border border-[#262626] flex items-center justify-center text-[#FAFAFA]">
              <FileCheck className="h-4 w-4" />
            </div>
            <div>
              <h2 className="text-sm font-bold text-[#FAFAFA] font-heading tracking-tight">
                Tratamento de Publicação Judicial
              </h2>
              <p className="text-[11px] text-[#737373] font-sans">
                Defina o fluxo operacional e providências para esta intimação
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        {/* Content */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-4 text-xs">
          {/* Context Snippet */}
          <div className="p-3.5 bg-[#0A0A0A] border border-[#262626] rounded-none space-y-1.5">
            <div className="flex items-center justify-between">
              <span className="font-bold text-[#FAFAFA] font-mono">
                {publication.courtCode || 'Tribunal'} — {publication.cnjNumber || 'Processo não vinculado'}
              </span>
              {publication.suggestedDeadlineDays && (
                <span className="px-2.5 py-0.5 rounded-none bg-[#141414] border border-[#FF3D00]/40 text-[#FF3D00] text-[10px] font-mono font-bold uppercase tracking-wider">
                  Prazo sugerido: {publication.suggestedDeadlineDays} dias
                </span>
              )}
            </div>
            {publication.snippet && (
              <p className="text-[#737373] text-[11px] line-clamp-2 italic font-sans">
                "{publication.snippet}"
              </p>
            )}
          </div>

          {/* Treatment Decision Mode */}
          <div className="space-y-1.5">
            <label className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono">
              Ação de Tratamento
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setTreatmentType('WITH_TASK')}
                className={`p-3.5 rounded-none border text-left transition-colors flex items-start gap-2.5 cursor-pointer ${
                  treatmentType === 'WITH_TASK'
                    ? 'bg-[#1A1A1A] border-[#FF3D00] text-[#FAFAFA]'
                    : 'bg-[#0A0A0A] border-[#262626] text-[#737373] hover:border-[#737373]'
                }`}
              >
                <div
                  className={`mt-0.5 h-3.5 w-3.5 rounded-none border flex items-center justify-center shrink-0 ${
                    treatmentType === 'WITH_TASK'
                      ? 'border-[#FF3D00] bg-[#FF3D00]'
                      : 'border-[#525252]'
                  }`}
                >
                  {treatmentType === 'WITH_TASK' && (
                    <div className="h-1.5 w-1.5 bg-[#0A0A0A]" />
                  )}
                </div>
                <div>
                  <div className="font-bold text-xs text-[#FAFAFA]">
                    Tratar e Criar Tarefa / Prazo
                  </div>
                  <div className="text-[11px] text-[#737373] mt-0.5 font-sans">
                    Gera providência operacional no Kanban e sincroniza com a agenda
                  </div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => setTreatmentType('NO_TASK')}
                className={`p-3.5 rounded-none border text-left transition-colors flex items-start gap-2.5 cursor-pointer ${
                  treatmentType === 'NO_TASK'
                    ? 'bg-[#1A1A1A] border-emerald-500 text-[#FAFAFA]'
                    : 'bg-[#0A0A0A] border-[#262626] text-[#737373] hover:border-[#737373]'
                }`}
              >
                <div
                  className={`mt-0.5 h-3.5 w-3.5 rounded-none border flex items-center justify-center shrink-0 ${
                    treatmentType === 'NO_TASK'
                      ? 'border-emerald-400 bg-emerald-500'
                      : 'border-[#525252]'
                  }`}
                >
                  {treatmentType === 'NO_TASK' && (
                    <div className="h-1.5 w-1.5 bg-[#0A0A0A]" />
                  )}
                </div>
                <div>
                  <div className="font-bold text-xs text-[#FAFAFA]">
                    Apenas Marcar como Tratada
                  </div>
                  <div className="text-[11px] text-[#737373] mt-0.5 font-sans">
                    Ciência tomada sem necessidade de criar nova providência ou prazo
                  </div>
                </div>
              </button>
            </div>
          </div>

          {/* Conditional Task Fields */}
          {treatmentType === 'WITH_TASK' && (
            <div className="space-y-3 pt-2 border-t border-[#262626]">
              <div className="space-y-1">
                <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373]">
                  Título da Providência / Tarefa *
                </label>
                <input
                  type="text"
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors"
                  placeholder="Ex: Elaborar Contestação sobre Intimação STJ..."
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373]">Responsável</label>
                  <div className="relative">
                    <input
                      type="text"
                      value={taskAssignedUser}
                      onChange={(e) => setTaskAssignedUser(e.target.value)}
                      className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors"
                      placeholder="Usuário responsável..."
                    />
                  </div>
                </div>

                <div className="space-y-1">
                  <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373]">Prioridade</label>
                  <select
                    value={taskPriority}
                    onChange={(e) => setTaskPriority(e.target.value as TaskPriority)}
                    className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] focus:outline-none focus:border-[#FF3D00] transition-colors font-mono text-xs"
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
                  <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373]">Categoria</label>
                  <select
                    value={taskCategory}
                    onChange={(e) => setTaskCategory(e.target.value as TaskCategory)}
                    className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] focus:outline-none focus:border-[#FF3D00] transition-colors font-mono text-xs"
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
                  <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373] flex items-center gap-1">
                    <Calendar className="h-3 w-3 text-[#FAFAFA]" />
                    Data de Vencimento
                  </label>
                  <input
                    type="date"
                    value={taskDueDate}
                    onChange={(e) => setTaskDueDate(e.target.value)}
                    className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] focus:outline-none focus:border-[#FF3D00] transition-colors font-mono text-xs"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373] flex items-center gap-1">
                    <Clock className="h-3 w-3 text-[#737373]" />
                    Horário Limite
                  </label>
                  <input
                    type="text"
                    value={taskDueTime}
                    onChange={(e) => setTaskDueTime(e.target.value)}
                    className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] focus:outline-none focus:border-[#FF3D00] transition-colors font-mono text-xs"
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
                  className="rounded-none border-[#262626] bg-[#0A0A0A] text-[#FF3D00] focus:ring-0 h-3.5 w-3.5 accent-[#FF3D00]"
                />
                <label htmlFor="syncCalendar" className="text-[#FAFAFA] text-[11px] cursor-pointer">
                  Sincronizar com a Agenda & Controle de Prazos do j-lawyer
                </label>
              </div>
            </div>
          )}

          {/* Notes / Observation */}
          <div className="space-y-1 pt-2 border-t border-[#262626]">
            <label className="text-[10px] font-mono uppercase tracking-wider text-[#737373]">
              Observações / Despacho Interno (Opcional)
            </label>
            <textarea
              rows={2}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-3.5 py-2 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] placeholder-[#737373] focus:outline-none focus:border-[#FF3D00] transition-colors text-xs font-sans"
              placeholder="Anotação de triagem sobre esta publicação..."
            />
          </div>

          {/* Important Notice */}
          <div className="p-3 bg-amber-950/30 border border-amber-600/40 rounded-none flex items-start gap-2 text-amber-300 text-[11px]">
            <AlertTriangle className="h-4 w-4 shrink-0 mt-0.5 text-amber-500" />
            <span>
              <strong>Atenção:</strong> O tratamento interno no BR-LAWYER organiza a rotina do
              escritório e <strong>não substitui</strong> a ciência formal nos sistemas dos tribunais
              (PJe, e-SAJ, Projudi, etc.).
            </span>
          </div>

          {/* Buttons */}
          <div className="pt-3 border-t border-[#262626] flex items-center justify-end gap-2.5">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors font-mono uppercase tracking-wider text-xs font-semibold cursor-pointer"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2 rounded-none bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold font-mono uppercase tracking-wider text-xs transition-colors flex items-center gap-1.5 disabled:opacity-50 cursor-pointer"
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
