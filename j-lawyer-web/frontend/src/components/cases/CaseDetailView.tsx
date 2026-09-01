import React, { useEffect, useState } from 'react';
import {
  ArrowLeft,
  Users,
  FileText,
  Clock,
  History,
  Tag,
  Scale,
  Edit,
  User,
} from 'lucide-react';
import { casesService } from '../../api/casesService';
import {
  RestfulCaseV1,
  RestfulPartyV1,
  RestfulDocumentV1,
  RestfulDueDateV1,
  RestfulCaseHistoryV8,
  RestfulTagV1,
} from '../../types/cases';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { CasePartiesTab } from './CasePartiesTab';
import { CaseDocumentsTab } from './CaseDocumentsTab';
import { CaseDeadlinesTab } from './CaseDeadlinesTab';
import { CaseHistoryTab } from './CaseHistoryTab';
import { CaseTagsTab } from './CaseTagsTab';
import { formatCNJ, formatBRL } from '../../utils/formatters';
import { cn } from '../../utils/cn';

interface CaseDetailViewProps {
  caseId: string;
  onBack: () => void;
}

type TabKey = 'parties' | 'documents' | 'deadlines' | 'history' | 'tags';

export const CaseDetailView: React.FC<CaseDetailViewProps> = ({ caseId, onBack }) => {
  const [activeTab, setActiveTab] = useState<TabKey>('parties');
  const [caseData, setCaseData] = useState<RestfulCaseV1 | null>(null);
  const [parties, setParties] = useState<RestfulPartyV1[]>([]);
  const [documents, setDocuments] = useState<RestfulDocumentV1[]>([]);
  const [deadlines, setDeadlines] = useState<RestfulDueDateV1[]>([]);
  const [history, setHistory] = useState<RestfulCaseHistoryV8[]>([]);
  const [tags, setTags] = useState<RestfulTagV1[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      setIsLoading(true);
      try {
        const [c, p, d, dd, h, t] = await Promise.all([
          casesService.getCaseById(caseId).catch(() => null),
          casesService.getCaseParties(caseId).catch(() => []),
          casesService.getCaseDocuments(caseId).catch(() => []),
          casesService.getCaseDueDates(caseId).catch(() => []),
          casesService.getCaseHistory(caseId).catch(() => []),
          casesService.getCaseTags(caseId).catch(() => []),
        ]);

        if (c) {
          setCaseData(c);
        } else {
          // Mock fallback for demo
          setCaseData({
            id: caseId,
            fileNumber: '5001234-56.2026.8.13.0024',
            name: 'Ação Revisional de Contrato Bancário — Silva x Banco S/A',
            subjectField: 'Direito Bancário',
            lawyer: 'Dr. Carlos Eduardo',
            assistant: 'Gabinete Cível 1',
            claimValue: '145000.00',
            notice: 'Processo com pedido de tutela antecipada deferido.',
            archived: 0,
          });
        }
        setParties(p.length > 0 ? p : [
          { id: 'p1', caseId, contactName: 'Silva & Filhos Comércio Ltda', involvementType: 'Autor / Requerente' },
          { id: 'p2', caseId, contactName: 'Banco Nacional S/A', involvementType: 'Réu / Requerido' },
        ]);
        setDocuments(d.length > 0 ? d : [
          { id: 'doc-1', fileName: 'Peticao_Inicial_Revisional.pdf', version: 1, dateChanged: Date.now() - 86400000 * 5, caseId },
          { id: 'doc-2', fileName: 'Contrato_Financiamento_Original.pdf', version: 1, dateChanged: Date.now() - 86400000 * 5, caseId },
          { id: 'doc-3', fileName: 'Decisao_Liminar_Deferida.pdf', version: 1, dateChanged: Date.now() - 86400000 * 2, caseId },
        ]);
        setDeadlines(dd.length > 0 ? dd : [
          { id: 'dd-1', reason: 'Manifestação sobre Contestação', dueDate: Date.now() + 86400000 * 3, done: false, type: 'RESPITE' },
        ]);
        setHistory(h.length > 0 ? h : [
          { id: 'h-1', changeDate: Date.now() - 86400000 * 5, changeType: 'Distribuição', userName: 'admin', description: 'Processo distribuído e autuado no sistema.' },
          { id: 'h-2', changeDate: Date.now() - 86400000 * 2, changeType: 'Decisão', userName: 'admin', description: 'Juntada de decisão interlocutória.' },
        ]);
        setTags(t.length > 0 ? t : [
          { name: 'Urgente Liminar', dateSet: Date.now() },
          { name: 'Contrato Bancário', dateSet: Date.now() },
        ]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAll();
  }, [caseId]);

  const tabs: Array<{ key: TabKey; label: string; icon: React.ComponentType<{ className?: string }>; count?: number }> = [
    { key: 'parties', label: 'Partes & Envolvidos', icon: Users, count: parties.length },
    { key: 'documents', label: 'Documentos & Peças', icon: FileText, count: documents.length },
    { key: 'deadlines', label: 'Prazos & Agenda', icon: Clock, count: deadlines.length },
    { key: 'history', label: 'Histórico & Auditoria', icon: History, count: history.length },
    { key: 'tags', label: 'Etiquetas', icon: Tag, count: tags.length },
  ];

  if (isLoading && !caseData) {
    return (
      <div className="py-20 text-center text-slate-500 text-xs">
        <svg className="animate-spin h-6 w-6 text-indigo-400 mx-auto mb-2" viewBox="0 0 24 24" fill="none">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
        </svg>
        <span>Carregando dados do processo...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Top Back Button */}
      <button
        onClick={onBack}
        className="flex items-center gap-2 text-xs font-medium text-slate-400 hover:text-slate-100 transition-colors cursor-pointer"
      >
        <ArrowLeft className="h-4 w-4" />
        <span>Voltar para a Lista de Processos</span>
      </button>

      {/* Case Header Card */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3 flex-wrap">
              <span className="font-mono text-sm font-semibold text-indigo-400 tracking-tight bg-indigo-950/60 px-2.5 py-1 rounded border border-indigo-500/30">
                {formatCNJ(caseData?.fileNumber)}
              </span>
              <Badge variant={caseData?.archived ? 'neutral' : 'active'}>
                {caseData?.archived ? 'Arquivado' : 'Em Andamento'}
              </Badge>
              <Badge variant="mono">{caseData?.subjectField || 'Cível Geral'}</Badge>
            </div>
            <h2 className="text-lg font-bold text-slate-100 mt-2">{caseData?.name}</h2>
            {caseData?.notice && (
              <p className="text-xs text-slate-400 mt-1 italic">{caseData.notice}</p>
            )}
          </div>

          <div className="flex items-center gap-2">
            <Button variant="secondary" size="sm" leftIcon={<Edit className="h-3.5 w-3.5" />}>
              Editar Dados
            </Button>
          </div>
        </div>

        {/* Metadata Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-6 pt-6 border-t border-slate-800/80 text-xs">
          <div>
            <span className="text-slate-500 block text-[11px]">Advogado Responsável</span>
            <div className="flex items-center gap-1.5 mt-1 text-slate-200 font-medium">
              <User className="h-3.5 w-3.5 text-slate-400" />
              <span>{caseData?.lawyer || 'Não informado'}</span>
            </div>
          </div>
          <div>
            <span className="text-slate-500 block text-[11px]">Valor da Causa</span>
            <span className="font-mono font-medium text-slate-200 mt-1 block">
              {formatBRL(caseData?.claimValue)}
            </span>
          </div>
          <div>
            <span className="text-slate-500 block text-[11px]">Assistente</span>
            <span className="text-slate-200 mt-1 block">{caseData?.assistant || 'Gabinete'}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[11px]">Número da Pasta</span>
            <span className="font-mono text-slate-200 mt-1 block">{caseData?.claimNumber || caseData?.id || '—'}</span>
          </div>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex items-center gap-1 border-b border-slate-800 select-none overflow-x-auto">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={cn(
                'flex items-center gap-2 px-4 py-3 text-xs font-medium border-b-2 transition-all cursor-pointer whitespace-nowrap',
                isActive
                  ? 'border-indigo-500 text-indigo-400 bg-slate-900/50'
                  : 'border-transparent text-slate-400 hover:text-slate-200 hover:border-slate-700'
              )}
            >
              <Icon className="h-4 w-4" />
              <span>{tab.label}</span>
              {tab.count !== undefined && (
                <span className="px-1.5 py-0.2 rounded-full bg-slate-800 text-[10px] font-mono text-slate-300">
                  {tab.count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Tab Panels */}
      <div>
        {activeTab === 'parties' && <CasePartiesTab parties={parties} />}
        {activeTab === 'documents' && <CaseDocumentsTab documents={documents} />}
        {activeTab === 'deadlines' && <CaseDeadlinesTab deadlines={deadlines} />}
        {activeTab === 'history' && <CaseHistoryTab history={history} />}
        {activeTab === 'tags' && <CaseTagsTab tags={tags} />}
      </div>
    </div>
  );
};