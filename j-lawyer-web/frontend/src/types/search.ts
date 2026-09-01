export interface RestfulSearchHitV8 {
  id: string;
  fileName?: string;
  archiveFileId?: string;
  archiveFileName?: string;
  archiveFileNumber?: string;
  snippet?: string;
  score?: number;
  title?: string;
  summary?: string;
  entityType?: 'case' | 'contact' | 'document';
  caseId?: string;
  date?: number;
}