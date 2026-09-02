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
        <h3 className="text-[10px] font-bold uppercase font-mono tracking-wider text-[#737373]">
          Etiquetas & Classificações do Processo ({tags.length})
        </h3>
        <Button variant="primary" size="xs" leftIcon={<Plus className="h-3.5 w-3.5" />}>
          Adicionar Etiqueta
        </Button>
      </div>

      <div className="p-5 bg-[#0F0F0F] border border-[#262626] rounded-none">
        {tags.length === 0 ? (
          <p className="text-xs text-[#737373] font-mono italic text-center py-4">Nenhuma etiqueta atribuída a este processo.</p>
        ) : (
          <div className="flex items-center gap-2 flex-wrap">
            {tags.map((tag) => (
              <div key={tag.id || tag.name} className="flex items-center gap-1.5 px-3 py-1.5 bg-[#0A0A0A] border border-[#262626] rounded-none">
                <Tag className="h-3.5 w-3.5 text-[#FF3D00]" />
                <span className="text-xs font-bold text-[#FAFAFA]">{tag.name}</span>
                {tag.dateSet && (
                  <span className="text-[10px] text-[#737373] font-mono ml-1">({formatDate(tag.dateSet)})</span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};