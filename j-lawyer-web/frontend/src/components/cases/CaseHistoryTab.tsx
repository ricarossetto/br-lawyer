import React from 'react';
import { History, User } from 'lucide-react';
import { RestfulCaseHistoryV8 } from '../../types/cases';
import { formatDateTime } from '../../utils/formatters';

export const CaseHistoryTab: React.FC<{ history: RestfulCaseHistoryV8[] }> = ({ history }) => {
  return (
    <div className="space-y-4">
      <h3 className="text-[10px] font-bold uppercase font-mono tracking-wider text-[#737373]">
        Trilha de Auditoria & Linha do Tempo ({history.length} Eventos)
      </h3>

      <div className="relative pl-6 border-l border-[#262626] space-y-4">
        {history.length === 0 ? (
          <p className="text-xs text-[#737373] font-mono italic">Nenhum evento registrado no histórico.</p>
        ) : (
          history.map((h) => (
            <div key={h.id} className="relative group">
              {/* Dot */}
              <div className="absolute -left-[30px] top-1.5 h-2 w-2 rounded-none bg-[#FF3D00]" />
              
              <div className="bg-[#0A0A0A] border border-[#262626] rounded-none p-4">
                <div className="flex items-center justify-between text-xs mb-1">
                  <span className="font-bold text-[#FAFAFA] font-heading">{h.changeType || 'Auditoria / Histórico'}</span>
                  <span className="font-mono text-[10px] text-[#737373]">{formatDateTime(h.changeDate)}</span>
                </div>
                <p className="text-xs text-[#FAFAFA] font-sans">{h.changeDescription || h.description || 'Registro de alteração no processo.'}</p>
                <div className="flex items-center gap-1 text-[10px] text-[#525252] mt-2 font-mono">
                  <User className="h-3 w-3 text-[#737373]" />
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