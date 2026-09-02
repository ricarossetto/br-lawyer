import React, { useState } from 'react';
import {
  X,
  Scale,
  Calendar,
  Clock,
  User,
  CheckCircle2,
  AlertTriangle,
  Briefcase,
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
      <div className="fixed inset-0 z-40 bg-black/80 transition-opacity" onClick={onClose} />
      <div className="fixed inset-y-0 right-0 z-50 w-full max-w-xl bg-[#0F0F0F] border-l border-[#262626] flex flex-col animate-drawer-in text-xs rounded-none">
        {/* Header */}
        <div className="px-6 py-4 border-b border-[#262626] flex items-center justify-between bg-[#0A0A0A] shrink-0">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="h-7 w-7 bg-[#141414] border border-[#262626] flex items-center justify-center text-[#FAFAFA] shrink-0">
              <Scale className="h-4 w-4" />
            </div>
            <div className="flex flex-col truncate">
              <div className="flex items-center gap-2">
                <span className="font-bold text-[#FAFAFA] truncate font-heading tracking-tight">
                  {publication.courtCode || 'Tribunal'}
                </span>
                <Badge
                  variant={
                    publication.status === 'NEW'
                      ? 'active'
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
                  <Badge variant="active">Não Lida</Badge>
                )}
              </div>
              <span className="text-[11px] text-[#737373] font-mono truncate">
                {publication.cnjNumber || 'Sem NPU vinculado'}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2 shrink-0">
            <button
              onClick={onClose}
              className="p-1.5 rounded-none text-[#737373] hover:text-[#FAFAFA] hover:bg-[#1A1A1A] transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>

        {/* Legal Disclaimer */}
        <div className="px-6 py-2.5 bg-amber-950/30 border-b border-amber-600/40 flex items-center gap-2 text-amber-300 text-[11px] shrink-0">
          <AlertTriangle className="h-3.5 w-3.5 text-amber-500 shrink-0" />
          <span>
            A leitura e triagem internas <strong>não geram ciência oficial</strong> nos autos
            judiciais.
          </span>
        </div>

        {/* Scrollable Body */}
        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          {/* Metadata Grid */}
          <div className="grid grid-cols-2 gap-3 p-4 bg-[#0A0A0A] border border-[#262626] rounded-none">
            <div>
              <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Disponibilização
              </span>
              <div className="text-[#FAFAFA] font-mono mt-0.5">
                {publication.availabilityDate
                  ? format(new Date(publication.availabilityDate), 'dd/MM/yyyy', { locale: ptBR })
                  : '—'}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Publicação Oficial
              </span>
              <div className="text-[#FAFAFA] font-mono mt-0.5">
                {publication.publicationDate
                  ? format(new Date(publication.publicationDate), 'dd/MM/yyyy', { locale: ptBR })
                  : '—'}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Destinatário / Parte
              </span>
              <div className="text-[#FAFAFA] truncate mt-0.5">
                {publication.recipient || '—'}
              </div>
            </div>

            <div>
              <span className="text-[10px] uppercase font-bold text-[#737373] font-mono tracking-wider">
                Advogado / OAB
              </span>
              <div className="text-[#FAFAFA] truncate mt-0.5 font-mono">
                {publication.lawyerName || '—'}{' '}
                {publication.lawyerOab ? `(${publication.lawyerOab})` : ''}
              </div>
            </div>
          </div>

          {/* Linked Process Card */}
          <div className="p-4 bg-[#0A0A0A] border border-[#262626] rounded-none space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-bold text-[#FAFAFA] flex items-center gap-1.5 font-heading">
                <Briefcase className="h-3.5 w-3.5 text-[#FAFAFA]" />
                Processo Vinculado
              </span>
              {publication.processId && (
                <button
                  onClick={() => onUnlink(publication.id)}
                  className="text-[10px] text-rose-400 hover:text-rose-300 flex items-center gap-1 transition-colors font-mono uppercase"
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
                  <div className="font-bold text-[#FAFAFA] text-xs font-mono">
                    {publication.caseFileNumber || 'Pasta Processual'}
                  </div>
                  <div className="text-[11px] text-[#737373]">
                    {publication.caseName || 'Título do processo'}
                  </div>
                  {publication.linkProvenance && (
                    <span className="text-[10px] text-[#525252] font-mono">
                      Vínculo: {publication.linkProvenance}{' '}
                      {publication.linkConfidence ? `(${Math.round(publication.linkConfidence * 100)}%)` : ''}
                    </span>
                  )}
                </div>
                {onOpenCase && (
                  <button
                    onClick={() => onOpenCase(publication.processId!)}
                    className="px-3 py-1.5 rounded-none bg-[#1A1A1A] hover:bg-[#262626] text-[#FAFAFA] border border-[#262626] font-mono text-[10px] uppercase tracking-wider font-bold flex items-center gap-1 transition-colors"
                  >
                    <span>Abrir Autos</span>
                    <ExternalLink className="h-3 w-3" />
                  </button>
                )}
              </div>
            ) : (
              <div className="text-[#737373] text-[11px] flex items-center justify-between">
                <span>Nenhum processo vinculado automaticamente.</span>
                <span className="text-[10px] text-[#FF3D00] font-mono uppercase tracking-wider font-bold">
                  Vincular na triagem
                </span>
              </div>
            )}
          </div>

          {/* Suggested Deadlines Ribbon (if detected) */}
          {publication.suggestedDueDate && (
            <div className="p-4 bg-[#141414] border border-[#FF3D00]/40 rounded-none flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <Clock className="h-4 w-4 text-[#FF3D00] shrink-0" />
                <div>
                  <div className="font-bold text-[#FAFAFA]">
                    Prazo Sugerido:{' '}
                    <span className="text-[#FF3D00] font-mono">
                      {format(new Date(publication.suggestedDueDate), 'dd/MM/yyyy', { locale: ptBR })}
                    </span>
                  </div>
                  <div className="text-[11px] text-[#737373]">
                    {publication.suggestedDeadlineDays
                      ? `${publication.suggestedDeadlineDays} dias sugeridos pelo teor`
                      : 'Data estimada pela intimação'}
                  </div>
                </div>
              </div>
              <Badge variant="active">Heurística</Badge>
            </div>
          )}

          {/* Publication Full Content / Teor Integral */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono flex items-center gap-1.5">
                <FileText className="h-3.5 w-3.5 text-[#737373]" />
                Teor Integral da Publicação
              </span>
              <button
                onClick={handleCopyContent}
                className="px-3 py-1 rounded-none bg-[#141414] hover:bg-[#1A1A1A] text-[#FAFAFA] text-[10px] font-mono uppercase tracking-wider border border-[#262626] flex items-center gap-1.5 transition-colors cursor-pointer"
              >
                {copied ? <Check className="h-3 w-3 text-emerald-400" /> : <Copy className="h-3 w-3" />}
                {copied ? 'Copiado!' : 'Copiar Texto'}
              </button>
            </div>
            <div className="p-4 bg-[#0A0A0A] border border-[#262626] rounded-none text-[#FAFAFA] leading-relaxed font-sans text-xs whitespace-pre-wrap max-h-64 overflow-y-auto select-text selection:bg-[#FF3D00] selection:text-[#0A0A0A]">
              {publication.content || publication.rawContent || publication.snippet || 'Sem conteúdo'}
            </div>
          </div>

          {/* Linked Tasks Section */}
          {publication.linkedTasks && publication.linkedTasks.length > 0 && (
            <div className="space-y-2">
              <span className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono flex items-center gap-1.5">
                <ListTodo className="h-3.5 w-3.5 text-[#FAFAFA]" />
                Tarefas Geradas ({publication.linkedTasks.length})
              </span>
              <div className="space-y-1.5">
                {publication.linkedTasks.map((t) => (
                  <div
                    key={t.id}
                    onClick={() => onOpenTask && onOpenTask(t.id)}
                    className="p-3 bg-[#0A0A0A] border border-[#262626] hover:border-[#737373] rounded-none flex items-center justify-between cursor-pointer transition-colors"
                  >
                    <div className="flex items-center gap-2 truncate">
                      <div
                        className={`h-2 w-2 rounded-none shrink-0 ${
                          t.priority === 'URGENT'
                            ? 'bg-rose-500'
                            : t.priority === 'HIGH'
                            ? 'bg-[#FF3D00]'
                            : 'bg-[#737373]'
                        }`}
                      />
                      <span className="font-bold text-[#FAFAFA] truncate">{t.title}</span>
                    </div>
                    <div className="flex items-center gap-2 shrink-0">
                      <Badge variant={t.status === 'DONE' ? 'green' : 'active'}>{t.status}</Badge>
                      {t.dueDate && (
                        <span className="text-[10px] text-[#737373] font-mono">
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
              <span className="text-[10px] font-bold text-[#737373] uppercase tracking-wider font-mono flex items-center gap-1.5">
                <History className="h-3.5 w-3.5 text-[#737373]" />
                Trilha de Auditoria
              </span>
              <div className="p-3.5 bg-[#0A0A0A] border border-[#262626] rounded-none space-y-2 max-h-40 overflow-y-auto">
                {publication.events.map((ev) => (
                  <div key={ev.id} className="flex items-start gap-2 text-[11px]">
                    <div className="h-1.5 w-1.5 bg-[#FF3D00] mt-1.5 shrink-0" />
                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-[#FAFAFA]">{ev.eventType}</span>
                        <span className="text-[10px] text-[#737373] font-mono">
                          {format(new Date(ev.createdAt), 'dd/MM/yy HH:mm')}
                        </span>
                      </div>
                      <p className="text-[#737373] text-[11px]">{ev.eventDescription}</p>
                      <span className="text-[10px] text-[#525252] font-mono">Por: {ev.actorPrincipal}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Action Footer */}
        <div className="p-4 border-t border-[#262626] bg-[#0A0A0A] flex items-center justify-between gap-2 shrink-0">
          <div className="flex items-center gap-2">
            <button
              onClick={() => onToggleRead(publication.id, !isRead)}
              className="px-3.5 py-2 rounded-none bg-[#141414] hover:bg-[#1A1A1A] text-[#FAFAFA] border border-[#262626] font-mono text-[10px] uppercase tracking-wider font-bold flex items-center gap-1.5 transition-colors cursor-pointer"
            >
              {isRead ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4 text-[#FF3D00]" />}
              <span>{isRead ? 'Marcar Não Lida' : 'Marcar como Lida'}</span>
            </button>

            <button
              onClick={() => onArchive(publication.id)}
              className="px-3.5 py-2 rounded-none bg-[#141414] hover:bg-[#1A1A1A] text-[#737373] hover:text-[#FAFAFA] border border-[#262626] font-mono text-[10px] uppercase tracking-wider font-bold flex items-center gap-1.5 transition-colors cursor-pointer"
            >
              <Archive className="h-4 w-4" />
              <span>Arquivar</span>
            </button>
          </div>

          <button
            onClick={() => setIsTreatModalOpen(true)}
            className="px-5 py-2 rounded-none bg-[#FF3D00] hover:bg-[#E03600] text-[#0A0A0A] font-bold font-mono text-[10px] uppercase tracking-wider flex items-center gap-1.5 transition-colors cursor-pointer"
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
