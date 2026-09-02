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
              <div className="absolute -left-[31px] top-1 h-2.5 w-2.5 rounded-full bg-[#F7931A] ring-4 ring-[#030304] shadow-[0_0_8px_rgba(247,147,26,0.6)]" />
              
              <div className="bg-[#0F1115] border border-white/10 rounded-2xl p-4 shadow-[0_0_20px_-8px_rgba(247,147,26,0.1)]">
                <div className="flex items-center justify-between text-xs mb-1">
                  <span className="font-semibold text-slate-100 font-heading">{h.changeType || 'Auditoria / Histórico'}</span>
                  <span className="font-mono text-[11px] text-slate-400">{formatDateTime(h.changeDate)}</span>
                </div>
                <p className="text-xs text-slate-300 font-sans">{h.changeDescription || h.description || 'Registro de alteração no processo.'}</p>
                <div className="flex items-center gap-1 text-[11px] text-slate-500 mt-2 font-mono">
                  <User className="h-3 w-3 text-[#FFD600]" />
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