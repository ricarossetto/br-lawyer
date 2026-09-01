import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { LoginPage } from './components/auth/LoginPage';
import { Shell } from './components/layout/Shell';
import { NavItemKey } from './components/layout/Sidebar';
import { DailyCommandCenter } from './components/dashboard/DailyCommandCenter';
import { CasesList } from './components/cases/CasesList';
import { CaseDetailView } from './components/cases/CaseDetailView';
import { Calendar, Clock, FileText, Settings, ShieldCheck } from 'lucide-react';
import { Badge } from './components/common/Badge';

const MainAppContent: React.FC = () => {
  const { isAuthenticated, isLoading, session } = useAuth();
  const [currentView, setCurrentView] = useState<NavItemKey>('dashboard');
  const [selectedCaseId, setSelectedCaseId] = useState<string | null>(null);

  if (isLoading) {
    return (
      <div className="h-screen w-screen bg-slate-950 flex flex-col items-center justify-center text-slate-400 text-xs">
        <svg className="animate-spin h-8 w-8 text-indigo-500 mb-3" viewBox="0 0 24 24" fill="none">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
        </svg>
        <span>Inicializando sessão BR-LAWYER...</span>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <LoginPage />;
  }

  const handleSelectCase = (caseId: string) => {
    setSelectedCaseId(caseId);
    setCurrentView('cases');
  };

  const handleNavigate = (view: NavItemKey) => {
    if (view !== 'cases') {
      setSelectedCaseId(null);
    }
    setCurrentView(view);
  };

  const viewTitles: Record<NavItemKey, { title: string; subtitle: string }> = {
    dashboard: {
      title: 'Central Diária de Comando (Cockpit)',
      subtitle: 'Visão executiva operacional — Prazos fatais e processos prioritários',
    },
    cases: {
      title: selectedCaseId ? 'Detalhe do Processo' : 'Gestão de Processos',
      subtitle: selectedCaseId
        ? 'Autos digitais, histórico, documentos e prazos'
        : 'Listagem de processos com paginação server-side WildFly v8',
    },
    calendar: {
      title: 'Prazos & Agenda Processual',
      subtitle: 'Controle de prazos fatais, audiências e perícias judiciais',
    },
    documents: {
      title: 'Repositório de Documentos & Modelos',
      subtitle: 'Pesquisa em texto integral via Apache Lucene',
    },
    settings: {
      title: 'Configurações & Painel do Sistema',
      subtitle: 'Conexão WildFly Elytron, RBAC e Perfil do Usuário',
    },
  };

  return (
    <Shell
      currentView={currentView}
      onNavigate={handleNavigate}
      title={viewTitles[currentView].title}
      subtitle={viewTitles[currentView].subtitle}
      onSelectCaseFromSearch={handleSelectCase}
    >
      {currentView === 'dashboard' && (
        <DailyCommandCenter
          onSelectCase={handleSelectCase}
          onNavigateToCases={() => handleNavigate('cases')}
          onNavigateToCalendar={() => handleNavigate('calendar')}
        />
      )}

      {currentView === 'cases' && (
        selectedCaseId ? (
          <CaseDetailView
            caseId={selectedCaseId}
            onBack={() => setSelectedCaseId(null)}
          />
        ) : (
          <CasesList onSelectCase={handleSelectCase} />
        )
      )}

      {currentView === 'calendar' && (
        <div className="space-y-4">
          <div className="p-8 bg-slate-900 border border-slate-800 rounded-xl text-center">
            <Calendar className="h-10 w-10 text-indigo-400 mx-auto mb-3" />
            <h3 className="text-sm font-semibold text-slate-100">Agenda & Prazos Processuais</h3>
            <p className="text-xs text-slate-400 mt-1 max-w-md mx-auto">
              Eventos integrados via endpoint <span className="font-mono text-indigo-300">/v8/calendar/events</span> com suporte a prazos de 15 dias úteis (CPC), audiências e sessões de julgamento.
            </p>
          </div>
        </div>
      )}

      {currentView === 'documents' && (
        <div className="space-y-4">
          <div className="p-8 bg-slate-900 border border-slate-800 rounded-xl text-center">
            <FileText className="h-10 w-10 text-indigo-400 mx-auto mb-3" />
            <h3 className="text-sm font-semibold text-slate-100">Repositório Geral de Documentos</h3>
            <p className="text-xs text-slate-400 mt-1 max-w-md mx-auto">
              Utilize o atalho <kbd className="px-1.5 py-0.5 font-mono text-[10px] bg-slate-800 rounded border border-slate-700">Ctrl+K</kbd> para pesquisar em texto integral através do motor Apache Lucene v8.
            </p>
          </div>
        </div>
      )}

      {currentView === 'settings' && (
        <div className="space-y-4">
          <div className="p-6 bg-slate-900 border border-slate-800 rounded-xl">
            <h3 className="text-sm font-semibold text-slate-100 mb-4">Informações do Operador & Sessão</h3>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between py-2 border-b border-slate-800">
                <span className="text-slate-400">Usuário Principal:</span>
                <span className="font-mono font-medium text-slate-200">{session?.principal}</span>
              </div>
              <div className="flex justify-between py-2 border-b border-slate-800">
                <span className="text-slate-400">Permissões (Roles):</span>
                <div className="flex gap-1 flex-wrap justify-end">
                  {session?.roles.map((r, idx) => (
                    <Badge key={idx} variant="mono" size="sm">{r}</Badge>
                  ))}
                </div>
              </div>
              <div className="flex justify-between py-2 border-b border-slate-800">
                <span className="text-slate-400">Autenticação:</span>
                <span className="text-emerald-400 font-medium">JWT RS256 + HttpOnly Refresh Cookie</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-400">Servidor WildFly:</span>
                <span className="font-mono text-slate-300">http://localhost:8000/j-lawyer-io/rest</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </Shell>
  );
};

export const App: React.FC = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <MainAppContent />
      </AuthProvider>
    </ThemeProvider>
  );
};