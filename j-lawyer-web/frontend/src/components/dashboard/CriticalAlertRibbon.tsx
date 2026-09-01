import React from 'react';
import { AlertTriangle, Clock, ArrowRight, ShieldAlert } from 'lucide-react';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';

interface AlertItem {
  id: string;
  npu: string;
  title: string;
  deadline: string;
  court: string;
  isFatalToday: boolean;
}

interface CriticalAlertRibbonProps {
  onSelectCase: (caseId: string) => void;
}

export const CriticalAlertRibbon: React.FC<CriticalAlertRibbonProps> = ({ onSelectCase }) => {
  // Sample critical deadlines for immediate action
  const criticalItems: AlertItem[] = [
    {
      id: 'case-urgent-1',
      npu: '5001234-56.2026.8.13.0024',
      title: 'Manifestação sobre Contestação / Réplica (15 dias úteis)',
      deadline: 'HOJE às 23:59',
      court: 'TJMG — 3ª Vara Cível de Belo Horizonte',
      isFatalToday: true,
    },
    {
      id: 'case-urgent-2',
      npu: '0019876-12.2026.5.03.0001',
      title: 'Recurso Ordinário Trabalhista — Prazo Fatal D-1',
      deadline: 'Amanhã (D-1)',
      court: 'TRT3 — 1ª Vara do Trabalho de BH',
      isFatalToday: false,
    },
  ];

  return (
    <div className="bg-red-950/30 border border-red-900/50 rounded-xl p-4 mb-6 shadow-sm">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <div className="h-6 w-6 rounded-md bg-red-600/20 text-red-400 flex items-center justify-center">
            <ShieldAlert className="h-4 w-4" />
          </div>
          <h2 className="text-xs font-semibold text-red-300 uppercase tracking-wider">
            Faixa de Alerta Crítico — Prazos Fatais Iminentes
          </h2>
        </div>
        <Badge variant="urgent" size="sm">
          {criticalItems.length} Providências Críticas
        </Badge>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {criticalItems.map((item) => (
          <div
            key={item.id}
            className="p-3 bg-slate-900/90 border border-slate-800 rounded-lg flex items-start justify-between hover:border-red-500/40 transition-all cursor-pointer group"
            onClick={() => onSelectCase(item.id)}
          >
            <div className="flex items-start gap-2.5 overflow-hidden">
              <Clock className="h-4 w-4 text-red-400 shrink-0 mt-0.5" />
              <div className="truncate">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-xs font-medium text-slate-200 group-hover:text-white">
                    {item.npu}
                  </span>
                  <Badge variant={item.isFatalToday ? 'urgent' : 'warning'} size="sm">
                    {item.deadline}
                  </Badge>
                </div>
                <div className="text-xs text-slate-300 font-medium truncate mt-1">{item.title}</div>
                <div className="text-[11px] text-slate-500 truncate mt-0.5">{item.court}</div>
              </div>
            </div>
            <ArrowRight className="h-4 w-4 text-slate-600 group-hover:text-red-400 transition-colors shrink-0 ml-2 mt-2" />
          </div>
        ))}
      </div>
    </div>
  );
};