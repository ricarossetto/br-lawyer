import React from 'react';
import { Users, User, Shield, Briefcase, Building } from 'lucide-react';
import { RestfulPartyV1 } from '../../types/cases';
import { Badge } from '../common/Badge';

export const CasePartiesTab: React.FC<{ parties: RestfulPartyV1[] }> = ({ parties }) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
          Polos e Envolvidos no Processo ({parties.length})
        </h3>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {parties.map((party) => {
          const isAuthor = party.involvementType?.toLowerCase().includes('autor') || party.involvementType?.toLowerCase().includes('requerente');
          const isDefendant = party.involvementType?.toLowerCase().includes('réu') || party.involvementType?.toLowerCase().includes('requerido');

          return (
            <div
              key={party.id}
              className="p-4 bg-slate-900 border border-slate-800 rounded-xl hover:border-slate-700 transition-all flex items-start justify-between"
            >
              <div className="flex items-start gap-3">
                <div className="h-8 w-8 rounded-lg bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300 shrink-0 mt-0.5">
                  <User className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-xs font-semibold text-slate-100">{party.contactName || party.contact}</h4>
                  <p className="text-[11px] text-slate-500 font-mono mt-0.5">ID Contato: {party.addressId || '—'}</p>
                  {party.reference && (
                    <p className="text-[11px] text-slate-400 mt-1">Ref: {party.reference}</p>
                  )}
                </div>
              </div>
              <Badge variant={isAuthor ? 'active' : isDefendant ? 'warning' : 'neutral'} size="sm">
                {party.involvementType || 'Interessado'}
              </Badge>
            </div>
          );
        })}
      </div>
    </div>
  );
};