import React from 'react';
import { Clock, CheckCircle2, AlertCircle, Plus } from 'lucide-react';
import { RestfulDueDateV1 } from '../../types/cases';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { formatDate } from '../../utils/formatters';

export const CaseDeadlinesTab: React.FC<{ deadlines: RestfulDueDateV1[] }> = ({ deadlines }) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-[10px] font-bold uppercase font-mono tracking-wider text-[#737373]">
          Prazos e Compromissos Processuais ({deadlines.length})
        </h3>
        <Button variant="primary" size="xs" leftIcon={<Plus className="h-3.5 w-3.5" />}>
          Novo Prazo
        </Button>
      </div>

      <div className="space-y-2">
        {deadlines.length === 0 ? (
          <div className="p-8 text-center bg-[#0A0A0A] border border-[#262626] rounded-none text-[#737373] font-mono text-xs">
            Nenhum prazo cadastrado para este processo.
          </div>
        ) : (
          deadlines.map((d) => (
            <div
              key={d.id}
              className="p-4 bg-[#0A0A0A] border border-[#262626] rounded-none flex items-start justify-between hover:border-[#737373] transition-colors"
            >
              <div className="flex items-start gap-3">
                <div className="p-2 rounded-none bg-[#141414] text-[#FF3D00] border border-[#262626] shrink-0 mt-0.5">
                  <Clock className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-[#FAFAFA]">{d.reason}</h4>
                  <p className="text-[11px] text-[#737373] font-mono mt-0.5">
                    Data Fatal: <span className="text-[#FF3D00] font-bold">{formatDate(d.dueDate)}</span>
                  </p>
                  {d.assignee && (
                    <p className="text-[10px] text-[#525252] font-mono mt-1">Responsável: {d.assignee}</p>
                  )}
                </div>
              </div>
              <Badge variant={d.done ? 'green' : 'yellow'} size="sm">
                {d.done ? 'Cumprido' : 'Pendente'}
              </Badge>
            </div>
          ))
        )}
      </div>
    </div>
  );
};