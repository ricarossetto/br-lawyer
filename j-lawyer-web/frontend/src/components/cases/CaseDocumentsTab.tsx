import React, { useState } from 'react';
import { FileText, Eye, Download, Upload, Plus } from 'lucide-react';
import { RestfulDocumentV1 } from '../../types/cases';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { formatDate } from '../../utils/formatters';
import { DocumentPreviewModal } from './DocumentPreviewModal';

export const CaseDocumentsTab: React.FC<{ documents: RestfulDocumentV1[] }> = ({ documents }) => {
  const [selectedDocForPreview, setSelectedDocForPreview] = useState<{ id: string; name: string } | null>(null);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-[10px] font-bold uppercase font-mono tracking-wider text-[#737373]">
          Pasta Digital de Documentos & Peças ({documents.length})
        </h3>
        <Button variant="primary" size="xs" leftIcon={<Upload className="h-3.5 w-3.5" />}>
          Anexar Documento
        </Button>
      </div>

      <div className="bg-[#0F0F0F] border border-[#262626] rounded-none overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-[#262626] text-[10px] font-bold text-[#737373] uppercase tracking-wider bg-[#0A0A0A] font-mono">
              <th className="py-2.5 px-4">Nome do Documento</th>
              <th className="py-2.5 px-4">Versão</th>
              <th className="py-2.5 px-4">Etiquetas / Tags</th>
              <th className="py-2.5 px-4">Última Modificação</th>
              <th className="py-2.5 px-4 text-right">Ações</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#262626] text-xs">
            {documents.length === 0 ? (
              <tr>
                <td colSpan={5} className="py-8 text-center text-[#737373] font-mono text-xs">
                  Nenhum documento anexado a este processo.
                </td>
              </tr>
            ) : (
              documents.map((doc) => {
                const displayName = doc.name || doc.fileName || doc.id;
                return (
                  <tr key={doc.id} className="hover:bg-[#141414] transition-colors group">
                    <td className="py-3 px-4 font-bold text-[#FAFAFA] flex items-center gap-2 group-hover:text-[#FF3D00]">
                      <FileText className="h-4 w-4 text-[#737373] shrink-0" />
                      <span className="truncate max-w-md">{displayName}</span>
                    </td>
                    <td className="py-3 px-4 font-mono text-[#737373]">v{doc.version || 1}</td>
                    <td className="py-3 px-4">
                      {doc.tags && doc.tags.length > 0 ? (
                        <div className="flex items-center gap-1 flex-wrap">
                          {doc.tags.map((t, idx) => (
                            <Badge key={idx} variant="mono" size="sm">{t.name}</Badge>
                          ))}
                        </div>
                      ) : (
                        <span className="text-[#525252]">—</span>
                      )}
                    </td>
                    <td className="py-3 px-4 font-mono text-[#737373] text-[11px]">{formatDate(doc.dateChanged || doc.creationDate)}</td>
                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <Button
                          variant="ghost"
                          size="xs"
                          leftIcon={<Eye className="h-3 w-3" />}
                          onClick={() => setSelectedDocForPreview({ id: doc.id, name: displayName })}
                        >
                          Visualizar
                        </Button>
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      <DocumentPreviewModal
        documentId={selectedDocForPreview?.id || null}
        documentName={selectedDocForPreview?.name || ''}
        onClose={() => setSelectedDocForPreview(null)}
      />
    </div>
  );
};