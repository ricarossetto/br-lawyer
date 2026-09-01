export interface RestfulSearchHitV8 {
  id: string;
  title: string;
  summary?: string;
  entityType: 'case' | 'contact' | 'document';
  caseId?: string;
  score?: number;
  date?: number;
}