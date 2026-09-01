import React from 'react';
import { Clock, ArrowRight, ShieldAlert, CheckCircle2 } from 'lucide-react';
import { Badge } from '../common/Badge';
import { RestfulCalendarEventV8 } from '../../types/calendar';
import { formatDate } from '../../utils/formatters';

interface CriticalAlertRibbonProps {
  events?: RestfulCalendarEventV8[];
  onSelectCase: (caseId: string) => void;
}

export const CriticalAlertRibbon: React.FC<CriticalAlertRibbonProps> = ({ events = [], onSelectCase }) => {
  if (!events || events.length === 0) {
    return (
      <div className="bg-emerald-950/20 border border-emerald-900/40 rounded-xl p-3 mb-6 flex items-center gap-3">
        <CheckCircle2 className="h-5 w-5 text-emerald-400 shrink-0" />
        <span className="text-xs text-emerald-200">
          Nenhum prazo crítico pendente para os próximos dias no servidor.
        </span>
      </div>
    );
  }

  return (
    <div className="bg-red-950/30 border border-red-900/50 rounded-xl p-4 mb-6 shadow-sm">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <div className="h-6 w-6 rounded-md bg-red-600/20 text-red-400 flex items-center justify-center">
            <ShieldAlert className="h-4 w-4" />
          </div>
          <h2 className="text-xs font-semibold text-red-300 uppercase tracking-wider">
            Faixa de Alerta Crítico — Prazos e Compromissos
          </h2>
        </div>
        <Badge variant="urgent" size="sm">
          {events.length} {events.length === 1 ? 'Providência Pendente' : 'Providências Pendentes'}
        </Badge>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {events.slice(0, 4).map((item) => (
          <div
            key={item.id}
            className="p-3 bg-slate-900/90 border border-slate-800 rounded-lg flex items-start justify-between hover:border-red-500/40 transition-all cursor-pointer group"
            onClick={() => item.caseId && onSelectCase(item.caseId)}
          >
            <div className="flex items-start gap-2.5 overflow-hidden">
              <Clock className="h-4 w-4 text-red-400 shrink-0 mt-0.5" />
              <div className="truncate">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-xs font-medium text-slate-200 group-hover:text-white">
                    {item.caseNumber || 'Prazo Geral'}
                  </span>
                  <Badge variant={item.done ? 'neutral' : 'urgent'} size="sm">
                    {formatDate(item.start)}
                  </Badge>
                </div>
                <div className="text-xs text-slate-300 font-medium truncate mt-1">{item.summary}</div>
                {item.caseName && (
                  <div className="text-[11px] text-slate-500 truncate mt-0.5">{item.caseName}</div>
                )}
              </div>
            </div>
            <ArrowRight className="h-4 w-4 text-slate-600 group-hover:text-red-400 transition-colors shrink-0 ml-2 mt-2" />
          </div>
        ))}
      </div>
    </div>
  );
};