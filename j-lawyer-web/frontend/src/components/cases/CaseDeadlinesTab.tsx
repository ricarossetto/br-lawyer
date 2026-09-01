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
        <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
          Prazos e Compromissos Processuais ({deadlines.length})
        </h3>
        <Button variant="primary" size="xs" leftIcon={<Plus className="h-3.5 w-3.5" />}>
          Novo Prazo
        </Button>
      </div>

      <div className="space-y-2">
        {deadlines.length === 0 ? (
          <div className="p-8 text-center bg-slate-900 border border-slate-800 rounded-xl text-slate-500 text-xs">
            Nenhum prazo cadastrado para este processo.
          </div>
        ) : (
          deadlines.map((d) => (
            <div
              key={d.id}
              className="p-4 bg-slate-900 border border-slate-800 rounded-xl flex items-start justify-between hover:border-slate-700 transition-all"
            >
              <div className="flex items-start gap-3">
                <div className="p-2 rounded-lg bg-amber-500/10 text-amber-400 border border-amber-500/20 shrink-0 mt-0.5">
                  <Clock className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-xs font-semibold text-slate-100">{d.reason}</h4>
                  <p className="text-[11px] text-slate-400 font-mono mt-0.5">
                    Data Fatal: <span className="text-amber-300 font-semibold">{formatDate(d.dueDate)}</span>
                  </p>
                  {d.assignee && (
                    <p className="text-[11px] text-slate-500 mt-1">Responsável: {d.assignee}</p>
                  )}
                </div>
              </div>
              <Badge variant={d.done ? 'success' : 'warning'} size="sm">
                {d.done ? 'Cumprido' : 'Pendente'}
              </Badge>
            </div>
          ))
        )}
      </div>
    </div>
  );
};