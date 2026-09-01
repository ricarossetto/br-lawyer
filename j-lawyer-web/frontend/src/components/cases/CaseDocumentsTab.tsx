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
        <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
          Pasta Digital de Documentos & Peças ({documents.length})
        </h3>
        <Button variant="primary" size="xs" leftIcon={<Upload className="h-3.5 w-3.5" />}>
          Anexar Documento
        </Button>
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-xs">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-800 text-[11px] font-semibold text-slate-400 uppercase tracking-wider bg-slate-950/60">
              <th className="py-2.5 px-4">Nome do Documento</th>
              <th className="py-2.5 px-4">Versão</th>
              <th className="py-2.5 px-4">Etiquetas / Tags</th>
              <th className="py-2.5 px-4">Última Modificação</th>
              <th className="py-2.5 px-4 text-right">Ações</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60 text-xs">
            {documents.length === 0 ? (
              <tr>
                <td colSpan={5} className="py-8 text-center text-slate-500">
                  Nenhum documento anexado a este processo.
                </td>
              </tr>
            ) : (
              documents.map((doc) => (
                <tr key={doc.id} className="hover:bg-slate-800/50 transition-colors group">
                  <td className="py-2.5 px-4 font-medium text-slate-200 flex items-center gap-2">
                    <FileText className="h-4 w-4 text-indigo-400 shrink-0" />
                    <span className="truncate max-w-md">{doc.fileName}</span>
                  </td>
                  <td className="py-2.5 px-4 font-mono text-slate-400">v{doc.version || 1}</td>
                  <td className="py-2.5 px-4">
                    {doc.tags && doc.tags.length > 0 ? (
                      <div className="flex items-center gap-1 flex-wrap">
                        {doc.tags.map((t, idx) => (
                          <Badge key={idx} variant="mono" size="sm">{t.name}</Badge>
                        ))}
                      </div>
                    ) : (
                      <span className="text-slate-600">—</span>
                    )}
                  </td>
                  <td className="py-2.5 px-4 font-mono text-slate-400">{formatDate(doc.dateChanged)}</td>
                  <td className="py-2.5 px-4 text-right">
                    <div className="flex items-center justify-end gap-1.5">
                      <Button
                        variant="ghost"
                        size="xs"
                        leftIcon={<Eye className="h-3 w-3" />}
                        onClick={() => setSelectedDocForPreview({ id: doc.id, name: doc.fileName })}
                      >
                        Visualizar
                      </Button>
                    </div>
                  </td>
                </tr>
              ))
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