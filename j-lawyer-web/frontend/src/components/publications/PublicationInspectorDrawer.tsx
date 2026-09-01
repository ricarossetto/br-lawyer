import React, { useState } from 'react';
import {
  X,
  Scale,
  Calendar,
  Clock,
  User,
  CheckCircle2,
  AlertTriangle,
  FileText,
  ExternalLink,
  Link as LinkIcon,
  Unlink,
  Archive,
  Eye,
  EyeOff,
  Copy,
  Check,
  Tag,
  ShieldCheck,
  History,
  ListTodo,
} from 'lucide-react';
import { PublicationDetail, PublicationTreatRequest } from '../../types/publications';
import { Badge } from '../common/Badge';
import { TreatPublicationModal } from './TreatPublicationModal';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface PublicationInspectorDrawerProps {
  publication: PublicationDetail | null;
  isOpen: boolean;
  onClose: () => void;
  onToggleRead: (id: string, read: boolean) => Promise<void>;
  onTreat: (id: string, req: PublicationTreatRequest) => Promise<void>;
  onArchive: (id: string) => Promise<void>;
  onUnlink: (id: string) => Promise<void>;
  onOpenCase?: (caseId: string) => void;
  onOpenTask?: (taskId: string) => void;
  isLoading?: boolean;
}

export const PublicationInspectorDrawer: React.FC<PublicationInspectorDrawerProps> = ({
  publication,
  isOpen,
  onClose,
  onToggleRead,
  onTreat,
  onArchive,
  onUnlink,
  onOpenCase,
  onOpenTask,
  isLoading = false,
}) => {
  const [isTreatModalOpen, setIsTreatModalOpen] = useState(false);
  const [copied, setCopied] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isOpen || !publication) return null;

  const handleCopyContent = () => {
    const textToCopy = publication.content || publication.rawContent || publication.snippet || '';
    navigator.clipboard.writeText(textToCopy);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleTreatConfirm = async (request: PublicationTreatRequest) => {
    setIsSubmitting(true);
    try {
      await onTreat(publication.id, request);
    } finally {
      setIsSubmitting(false);
    }
  };

  const isTreated = publication.treatmentStatus === 'TREATED' || publication.status === 'TREATED';
  const isRead = publication.readStatus === 'READ';

  return (
    <>
      <div className="fixed inset-0 z-40 bg-black/50 backdrop-blur-xs" onClick={onClose} />
      <div className="fixed inset-y-0 right-0 z-50 w-full max-w-xl bg-slate-900 border-l border-slate-800 shadow-2xl flex flex-col animate-in slide-in-from-right duration-200 text-xs">
        {/* Header */}
        <div className="px-5 py-4 border-b border-slate-800 flex items-center justify-between bg-slate-950/70 shrink-0">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-8 w-8 rounded-lg bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400 shrink-0">
              <Scale className="h-4 w-4" />
            </div>
            <div className="flex flex-col truncate">
              <div className="flex items-center gap-2">
                <span className="font-semibold text-slate-100 truncate">
                  {publication.courtCode || 'Tribunal'}
                </span>
                <Badge
                  variant={
                    publication.status === 'NEW'
                      ? 'blue'
                      : publication.status === 'TREATED'
                      ? 'green'
                      : publication.status === 'ARCHIVED'
                      ? 'gray'
                      : 'yellow'
                  }
                >
                  {publication.status}
                </Badge>
                {isRead ? (
                  <Badge variant="gray">Lida</Badge>
                ) : (
                  <Badge variant="blue">Não Lida</Badge>
                )}
              </div>
              <span className="text-[11px] text-slate-400 font-mono truncate">
                {publication.cnjNumber || 'Sem NPU vinculado'}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2 shrink-0">
            <button
              onClick={onClose}
              className="p-1 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>

        {/* Legal Disclaimer */}
        <div className="px-5 py-2 bg-amber-500/5 border-b border-amber-500/15 flex items-center gap-2 text-amber-300 text-[11px] shrink-0">
          <AlertTriangle className="h-3.5 w-3.5 text-amber-400 shrink-0" />
          <span>
            A leitura e triagem internas <strong>não geram ciência oficial</strong> nos autos
            judiciais.
          </span>
        </div>

        {/* Scrollable Body */}
        <div className="flex-1 overflow-y-auto p-5 space-y-5">
          {/* Metadata Grid */}
          <div className="grid grid-cols-2 gap-3 p-3 bg-slate-950/60 border border-slate-800 rounded-lg">
            <div>
              <span className="text-[10px] uppercase font-semibold text-slate-500 tracking-wider">
                Disponibilização
              </span>
              <div className="text-slate-200 font-mono mt-0.5">
                {publication.availabilityDate
                  ? format(new Date(publication.availabilityDate), 'dd/MM/yyyy', { locale: ptBR })
                  : '—'}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-semibold text-slate-500 tracking-wider">
                Publicação Oficial
              </span>
              <div className="text-slate-200 font-mono mt-0.5">
                {publication.publicationDate
                  ? format(new Date(publication.publicationDate), 'dd/MM/yyyy', { locale: ptBR })
                  : '—'}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-semibold text-slate-500 tracking-wider">
                Destinatário / Parte
              </span>
              <div className="text-slate-200 truncate mt-0.5">
                {publication.recipient || '—'}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-semibold text-slate-500 tracking-wider">
                Advogado / OAB
              </span>
              <div className="text-slate-200 truncate mt-0.5">
                {publication.lawyerName || '—'}{' '}
                {publication.lawyerOab ? `(${publication.lawyerOab})` : ''}
              </div>
            </div>
          </div>

          {/* Linked Process Card */}
          <div className="p-3 bg-slate-950 border border-slate-800 rounded-lg space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-semibold text-slate-300 flex items-center gap-1.5">
                <Briefcase className="h-3.5 w-3.5 text-indigo-400" />
                Processo Vinculado
              </span>
              {publication.processId && (
                <button
                  onClick={() => onUnlink(publication.id)}
                  className="text-[10px] text-red-400 hover:text-red-300 flex items-center gap-1 transition-colors"
                  title="Desvincular do processo"
                >
                  <Unlink className="h-3 w-3" />
                  Desvincular
                </button>
              )}
            </div>

            {publication.processId ? (
              <div className="flex items-center justify-between pt-1">
                <div>
                  <div className="font-semibold text-slate-200 text-xs">
                    {publication.caseFileNumber || 'Pasta Processual'}
                  </div>
                  <div className="text-[11px] text-slate-400">
                    {publication.caseName || 'Título do processo'}
                  </div>
                  {publication.linkProvenance && (
                    <span className="text-[10px] text-slate-500 font-mono">
                      Vínculo: {publication.linkProvenance}{' '}
                      {publication.linkConfidence ? `(${Math.round(publication.linkConfidence * 100)}%)` : ''}
                    </span>
                  )}
                </div>
                {onOpenCase && (
                  <button
                    onClick={() => onOpenCase(publication.processId!)}
                    className="px-2.5 py-1.5 rounded bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-300 border border-indigo-500/30 font-medium flex items-center gap-1 transition-colors"
                  >
                    <span>Abrir Autos</span>
                    <ExternalLink className="h-3 w-3" />
                  </button>
                )}
              </div>
            ) : (
              <div className="text-slate-400 text-[11px] flex items-center justify-between">
                <span>Nenhum processo vinculado automaticamente.</span>
                <span className="text-[10px] text-indigo-400 font-medium">
                  Vincular na triagem
                </span>
              </div>
            )}
          </div>

          {/* Suggested Deadlines Ribbon (if detected) */}
          {publication.suggestedDueDate && (
            <div className="p-3 bg-indigo-950/40 border border-indigo-500/30 rounded-lg flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Clock className="h-4 w-4 text-indigo-400 shrink-0" />
                <div>
                  <div className="font-semibold text-slate-200">
                    Prazo Sugerido:{' '}
                    {format(new Date(publication.suggestedDueDate), 'dd/MM/yyyy', { locale: ptBR })}
                  </div>
                  <div className="text-[11px] text-indigo-300">
                    {publication.suggestedDeadlineDays
                      ? `${publication.suggestedDeadlineDays} dias sugeridos pelo teor`
                      : 'Data estimada pela intimação'}
                  </div>
                </div>
              </div>
              <Badge variant="purple">Heurística</Badge>
            </div>
          )}

          {/* Publication Full Content / Teor Integral */}
          <div className="space-y-1.5">
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider flex items-center gap-1.5">
                <FileText className="h-3.5 w-3.5 text-slate-400" />
                Teor Integral da Publicação
              </span>
              <button
                onClick={handleCopyContent}
                className="px-2 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-[11px] flex items-center gap-1 transition-colors"
              >
                {copied ? <Check className="h-3 w-3 text-emerald-400" /> : <Copy className="h-3 w-3" />}
                {copied ? 'Copiado!' : 'Copiar Texto'}
              </button>
            </div>
            <div className="p-4 bg-slate-950 border border-slate-800 rounded-lg text-slate-200 leading-relaxed font-sans text-xs whitespace-pre-wrap max-h-64 overflow-y-auto select-text selection:bg-indigo-500/30">
              {publication.content || publication.rawContent || publication.snippet || 'Sem conteúdo'}
            </div>
          </div>

          {/* Linked Tasks Section */}
          {publication.linkedTasks && publication.linkedTasks.length > 0 && (
            <div className="space-y-2">
              <span className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider flex items-center gap-1.5">
                <ListTodo className="h-3.5 w-3.5 text-emerald-400" />
                Tarefas Geradas a partir desta Publicação ({publication.linkedTasks.length})
              </span>
              <div className="space-y-1.5">
                {publication.linkedTasks.map((t) => (
                  <div
                    key={t.id}
                    onClick={() => onOpenTask && onOpenTask(t.id)}
                    className="p-2.5 bg-slate-950 border border-slate-800 hover:border-slate-700 rounded-lg flex items-center justify-between cursor-pointer transition-colors"
                  >
                    <div className="flex items-center gap-2 truncate">
                      <div
                        className={`h-2 w-2 rounded-full shrink-0 ${
                          t.priority === 'URGENT'
                            ? 'bg-rose-500'
                            : t.priority === 'HIGH'
                            ? 'bg-amber-500'
                            : 'bg-indigo-500'
                        }`}
                      />
                      <span className="font-semibold text-slate-200 truncate">{t.title}</span>
                    </div>
                    <div className="flex items-center gap-2 shrink-0">
                      <Badge variant={t.status === 'DONE' ? 'green' : 'blue'}>{t.status}</Badge>
                      {t.dueDate && (
                        <span className="text-[10px] text-slate-400 font-mono">
                          {format(new Date(t.dueDate), 'dd/MM/yyyy')}
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Audit Log / Event History */}
          {publication.events && publication.events.length > 0 && (
            <div className="space-y-2">
              <span className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider flex items-center gap-1.5">
                <History className="h-3.5 w-3.5 text-slate-400" />
                Trilha de Auditoria & Eventos
              </span>
              <div className="p-3 bg-slate-950/60 border border-slate-800 rounded-lg space-y-2 max-h-40 overflow-y-auto">
                {publication.events.map((ev) => (
                  <div key={ev.id} className="flex items-start gap-2 text-[11px]">
                    <div className="h-1.5 w-1.5 rounded-full bg-slate-500 mt-1.5 shrink-0" />
                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <span className="font-semibold text-slate-300">{ev.eventType}</span>
                        <span className="text-[10px] text-slate-500 font-mono">
                          {format(new Date(ev.createdAt), 'dd/MM/yy HH:mm')}
                        </span>
                      </div>
                      <p className="text-slate-400 text-[11px]">{ev.eventDescription}</p>
                      <span className="text-[10px] text-slate-500">Por: {ev.actorPrincipal}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Action Footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-950 flex items-center justify-between gap-2 shrink-0">
          <div className="flex items-center gap-2">
            <button
              onClick={() => onToggleRead(publication.id, !isRead)}
              className="px-3 py-2 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-700 font-medium flex items-center gap-1.5 transition-colors"
            >
              {isRead ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4 text-indigo-400" />}
              <span>{isRead ? 'Marcar Não Lida' : 'Marcar como Lida'}</span>
            </button>

            <button
              onClick={() => onArchive(publication.id)}
              className="px-3 py-2 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-400 hover:text-slate-200 border border-slate-700 font-medium flex items-center gap-1.5 transition-colors"
            >
              <Archive className="h-4 w-4" />
              <span>Arquivar</span>
            </button>
          </div>

          <button
            onClick={() => setIsTreatModalOpen(true)}
            className="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-medium shadow-sm flex items-center gap-1.5 transition-colors"
          >
            <CheckCircle2 className="h-4 w-4" />
            <span>{isTreated ? 'Revisar Tratamento' : 'Tratar Publicação'}</span>
          </button>
        </div>
      </div>

      {/* Triage / Treat Modal */}
      <TreatPublicationModal
        isOpen={isTreatModalOpen}
        onClose={() => setIsTreatModalOpen(false)}
        publication={publication}
        onConfirmTreat={handleTreatConfirm}
        isSubmitting={isSubmitting}
      />
    </>
  );
};
