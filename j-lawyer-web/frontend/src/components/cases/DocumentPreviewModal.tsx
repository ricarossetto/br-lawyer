import React, { useEffect, useState } from 'react';
import { FileText, Download, AlertCircle } from 'lucide-react';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { casesService } from '../../api/casesService';
import { DocumentPreviewPdfResponse } from '../../types/cases';

interface DocumentPreviewModalProps {
  documentId: string | null;
  documentName: string;
  onClose: () => void;
}

export const DocumentPreviewModal: React.FC<DocumentPreviewModalProps> = ({
  documentId,
  documentName,
  onClose,
}) => {
  const [preview, setPreview] = useState<DocumentPreviewPdfResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!documentId) return;

    const loadPreview = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const data = await casesService.getDocumentPreviewPdf(documentId);
        setPreview(data);
      } catch (err: any) {
        setError(err?.response?.data?.message || err?.message || 'Falha ao carregar pré-visualização do documento do servidor.');
        setPreview(null);
      } finally {
        setIsLoading(false);
      }
    };

    loadPreview();
  }, [documentId, documentName]);

  const handleDownload = async () => {
    if (!documentId) return;
    try {
      const doc = await casesService.getDocumentContent(documentId);
      const byteCharacters = atob(doc.base64content);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray]);
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = doc.fileName || documentName;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      alert('Erro ao baixar o arquivo do servidor.');
    }
  };

  if (!documentId) return null;

  return (
    <Modal
      isOpen={!!documentId}
      onClose={onClose}
      title={`Visualizador: ${documentName}`}
      maxWidth="4xl"
    >
      <div className="flex flex-col h-[70vh]">
        {/* Top Action Bar */}
        <div className="flex items-center justify-between pb-3 mb-3 border-b border-white/10">
          <div className="flex items-center gap-2 text-xs text-slate-300 font-mono">
            <FileText className="h-4 w-4 text-[#FFD600]" />
            <span>{documentName}</span>
          </div>
          <Button
            variant="secondary"
            size="xs"
            leftIcon={<Download className="h-3.5 w-3.5" />}
            onClick={handleDownload}
          >
            Baixar Original
          </Button>
        </div>

        {/* Content Viewer */}
        <div className="flex-1 overflow-y-auto bg-[#030304] rounded-xl p-4 border border-white/10">
          {isLoading ? (
            <div className="h-full flex flex-col items-center justify-center text-slate-500 text-xs">
              <svg className="animate-spin h-6 w-6 text-[#F7931A] mb-2" viewBox="0 0 24 24" fill="none">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
              </svg>
              <span>Gerando pré-visualização no servidor (StirlingPDF / Tika)...</span>
            </div>
          ) : error ? (
            <div className="h-full flex flex-col items-center justify-center text-red-400 text-xs">
              <AlertCircle className="h-6 w-6 mb-2" />
              <span>{error}</span>
            </div>
          ) : preview?.kind === 'pdf' && preview.base64content ? (
            <iframe
              src={`data:application/pdf;base64,${preview.base64content}`}
              className="w-full h-full rounded border-0"
              title={documentName}
            />
          ) : (
            <pre className="text-xs text-slate-300 font-mono whitespace-pre-wrap leading-relaxed">
              {preview?.text || 'Nenhum conteúdo de texto extraído disponível para este documento.'}
            </pre>
          )}
        </div>
      </div>
    </Modal>
  );
};