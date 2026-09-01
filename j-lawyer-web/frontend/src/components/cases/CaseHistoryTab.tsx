import React from 'react';
import { History, User } from 'lucide-react';
import { RestfulCaseHistoryV8 } from '../../types/cases';
import { formatDateTime } from '../../utils/formatters';

export const CaseHistoryTab: React.FC<{ history: RestfulCaseHistoryV8[] }> = ({ history }) => {
  return (
    <div className="space-y-4">
      <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
        Trilha de Auditoria & Linha do Tempo ({history.length} Eventos)
      </h3>

      <div className="relative pl-6 border-l border-slate-800 space-y-6">
        {history.length === 0 ? (
          <p className="text-xs text-slate-500 italic">Nenhum evento registrado no histórico.</p>
        ) : (
          history.map((h) => (
            <div key={h.id} className="relative group">
              {/* Dot */}
              <div className="absolute -left-[31px] top-1 h-2.5 w-2.5 rounded-full bg-indigo-500 ring-4 ring-slate-950" />
              
              <div className="bg-slate-900 border border-slate-800 rounded-lg p-3.5 shadow-xs">
                <div className="flex items-center justify-between text-xs mb-1">
                  <span className="font-semibold text-slate-200">{h.changeType || 'Auditoria / Histórico'}</span>
                  <span className="font-mono text-[11px] text-slate-500">{formatDateTime(h.changeDate)}</span>
                </div>
                <p className="text-xs text-slate-300">{h.changeDescription || h.description || 'Registro de alteração no processo.'}</p>
                <div className="flex items-center gap-1 text-[11px] text-slate-500 mt-2 font-mono">
                  <User className="h-3 w-3" />
                  <span>Operador: {h.principal || h.userName || 'admin'}</span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};