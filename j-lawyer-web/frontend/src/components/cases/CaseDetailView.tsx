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
import { CasePublicationsTab } from './CasePublicationsTab';
import { CaseTasksTab } from './CaseTasksTab';
import { CaseTagsTab } from './CaseTagsTab';
import { formatCNJ, formatBRL } from '../../utils/formatters';
import { cn } from '../../utils/cn';

interface CaseDetailViewProps {
  caseId: string;
  onBack: () => void;
}

type TabKey = 'publications' | 'tasks' | 'parties' | 'documents' | 'deadlines' | 'history' | 'tags';

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
          setCaseData(null);
        }
        setParties(p || []);
        setDocuments(d || []);
        setDeadlines(dd || []);
        setHistory(h || []);
        setTags(t || []);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAll();
  }, [caseId]);

  const tabs: Array<{ key: TabKey; label: string; icon: React.ComponentType<{ className?: string }>; count?: number }> = [
    { key: 'publications', label: 'Publicações', icon: Scale },
    { key: 'tasks', label: 'Tarefas & Providências', icon: Clock },
    { key: 'parties', label: 'Partes & Envolvidos', icon: Users, count: parties.length },
    { key: 'documents', label: 'Documentos & Peças', icon: FileText, count: documents.length },
    { key: 'deadlines', label: 'Prazos & Agenda', icon: Clock, count: deadlines.length },
    { key: 'history', label: 'Histórico & Auditoria', icon: History, count: history.length },
    { key: 'tags', label: 'Etiquetas', icon: Tag, count: tags.length },
  ];

  if (isLoading && !caseData) {
    return (
      <div className="py-20 text-center text-slate-500 text-xs">
        <svg className="animate-spin h-6 w-6 text-[#F7931A] mx-auto mb-2" viewBox="0 0 24 24" fill="none">
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
        className="flex items-center gap-2 text-[10px] font-mono uppercase tracking-wider font-bold text-[#737373] hover:text-[#FAFAFA] transition-colors cursor-pointer"
      >
        <ArrowLeft className="h-4 w-4" />
        <span>Voltar para a Lista de Processos</span>
      </button>

      {/* Case Header Card */}
      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none p-6">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3 flex-wrap">
              <span className="font-mono text-sm font-bold text-[#FAFAFA] bg-[#141414] px-3 py-1 rounded-none border border-[#262626]">
                {formatCNJ(caseData?.fileNumber)}
              </span>
              <Badge variant={caseData?.archived ? 'neutral' : 'active'}>
                {caseData?.archived ? 'Arquivado' : 'Em Andamento'}
              </Badge>
              <Badge variant="mono">{caseData?.subjectField || 'Cível Geral'}</Badge>
            </div>
            <h2 className="text-xl font-bold text-[#FAFAFA] mt-3 font-heading tracking-tight">{caseData?.name}</h2>
            {caseData?.notice && (
              <p className="text-xs text-[#737373] mt-1 italic font-sans">{caseData.notice}</p>
            )}
          </div>

          <div className="flex items-center gap-2">
            <Button variant="secondary" size="sm" leftIcon={<Edit className="h-3.5 w-3.5" />}>
              Editar Dados
            </Button>
          </div>
        </div>

        {/* Metadata Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-6 pt-6 border-t border-[#262626] text-xs">
          <div>
            <span className="text-[#737373] block text-[10px] font-mono uppercase tracking-wider font-bold">Advogado Responsável</span>
            <div className="flex items-center gap-1.5 mt-1 text-[#FAFAFA] font-mono">
              <User className="h-3.5 w-3.5 text-[#737373]" />
              <span>{caseData?.lawyer || 'Não informado'}</span>
            </div>
          </div>
          <div>
            <span className="text-[#737373] block text-[10px] font-mono uppercase tracking-wider font-bold">Valor da Causa</span>
            <span className="font-mono font-bold text-[#FAFAFA] mt-1 block">
              {formatBRL(caseData?.claimValue)}
            </span>
          </div>
          <div>
            <span className="text-[#737373] block text-[10px] font-mono uppercase tracking-wider font-bold">Assistente</span>
            <span className="text-[#FAFAFA] mt-1 block font-mono">{caseData?.assistant || 'Gabinete'}</span>
          </div>
          <div>
            <span className="text-[#737373] block text-[10px] font-mono uppercase tracking-wider font-bold">Número da Pasta</span>
            <span className="font-mono text-[#FAFAFA] mt-1 block">{caseData?.claimNumber || caseData?.id || '—'}</span>
          </div>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex items-center gap-1 border-b border-[#262626] select-none overflow-x-auto">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={cn(
                'flex items-center gap-2 px-4 py-3 text-xs font-mono uppercase tracking-wider border-b-2 transition-colors cursor-pointer whitespace-nowrap rounded-none',
                isActive
                  ? 'border-[#FF3D00] text-[#FAFAFA] bg-[#141414] font-bold'
                  : 'border-transparent text-[#737373] hover:text-[#FAFAFA] hover:border-[#737373]'
              )}
            >
              <Icon className="h-3.5 w-3.5" />
              <span>{tab.label}</span>
              {tab.count !== undefined && (
                <span className="px-1.5 py-0.2 rounded-none bg-[#1A1A1A] border border-[#262626] text-[9px] font-mono text-[#FAFAFA]">
                  {tab.count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Tab Panels */}
      <div>
        {activeTab === 'publications' && <CasePublicationsTab caseId={caseId} cnjNumber={caseData?.fileNumber} />}
        {activeTab === 'tasks' && <CaseTasksTab caseId={caseId} cnjNumber={caseData?.fileNumber} />}
        {activeTab === 'parties' && <CasePartiesTab parties={parties} />}
        {activeTab === 'documents' && <CaseDocumentsTab documents={documents} />}
        {activeTab === 'deadlines' && <CaseDeadlinesTab deadlines={deadlines} />}
        {activeTab === 'history' && <CaseHistoryTab history={history} />}
        {activeTab === 'tags' && <CaseTagsTab tags={tags} />}
      </div>
    </div>
  );
};