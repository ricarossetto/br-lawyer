import React from 'react';
import { Tag, Plus } from 'lucide-react';
import { RestfulTagV1 } from '../../types/cases';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { formatDate } from '../../utils/formatters';

export const CaseTagsTab: React.FC<{ tags: RestfulTagV1[] }> = ({ tags }) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
          Etiquetas & Classificações do Processo ({tags.length})
        </h3>
        <Button variant="primary" size="xs" leftIcon={<Plus className="h-3.5 w-3.5" />}>
          Adicionar Etiqueta
        </Button>
      </div>

      <div className="p-5 bg-slate-900 border border-slate-800 rounded-xl">
        {tags.length === 0 ? (
          <p className="text-xs text-slate-500 italic text-center py-4">Nenhuma etiqueta atribuída a este processo.</p>
        ) : (
          <div className="flex items-center gap-2 flex-wrap">
            {tags.map((tag) => (
              <div key={tag.id || tag.name} className="flex items-center gap-1.5 p-2 bg-slate-950 border border-slate-800 rounded-lg">
                <Tag className="h-3.5 w-3.5 text-indigo-400" />
                <span className="text-xs font-medium text-slate-200">{tag.name}</span>
                {tag.dateSet && (
                  <span className="text-[10px] text-slate-500 font-mono ml-1">({formatDate(tag.dateSet)})</span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};