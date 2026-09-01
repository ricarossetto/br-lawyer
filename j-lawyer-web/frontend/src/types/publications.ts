export type PublicationStatus = 'NEW' | 'ANALYZING' | 'LINKED' | 'TREATED' | 'DISPENSED' | 'ARCHIVED';
export type PublicationReadStatus = 'UNREAD' | 'READ';
export type PublicationTreatmentStatus = 'UNTREATED' | 'TREATED' | 'NO_ACTION_REQUIRED';

export interface PublicationOverview {
  id: string;
  externalId?: string;
  source?: string;
  sourceType?: string;
  courtCode?: string;
  processId?: string;
  caseFileNumber?: string;
  caseName?: string;
  cnjNumber?: string;
  cnjNumberClean?: string;
  publicationDate?: number;
  availabilityDate?: number;
  publicationType?: string;
  recipient?: string;
  lawyerName?: string;
  lawyerOab?: string;
  status: PublicationStatus;
  readStatus: PublicationReadStatus;
  treatmentStatus: PublicationTreatmentStatus;
  assignedUser?: string;
  linkProvenance?: string;
  linkConfidence?: number;
  suggestedDueDate?: number;
  suggestedDeadlineDays?: number;
  snippet?: string;
  createdAt?: number;
  treatedAt?: number;
}

export interface PublicationEvent {
  id: string;
  publicationId: string;
  eventType: string;
  actorPrincipal: string;
  eventDescription: string;
  payloadJson?: string;
  createdAt: number;
}

export interface PublicationDetail extends PublicationOverview {
  content?: string;
  rawContent?: string;
  suggestionSource?: string;
  suggestionConfidence?: number;
  fingerprint?: string;
  provenance?: string;
  updatedAt?: number;
  readAt?: number;
  treatedBy?: string;
  archivedAt?: number;
  archivedBy?: string;
  notes?: string;
  events: PublicationEvent[];
  linkedTasks: import('./tasks').TaskOverview[];
}

export interface PublicationPage {
  total: number;
  page: number;
  pageSize: number;
  items: PublicationOverview[];
}

export interface PublicationFilter {
  status?: string;
  readStatus?: string;
  treatmentStatus?: string;
  courtCode?: string;
  processId?: string;
  cnjNumber?: string;
  assignedUser?: string;
  lawyerOab?: string;
  searchText?: string;
  fromDate?: string;
  toDate?: string;
  page?: number;
  pageSize?: number;
  limit?: number;
}

export interface PublicationLinkRequest {
  processId: string;
  caseFileNumber?: string;
  caseName?: string;
  user?: string;
  confidence?: number;
  notes?: string;
}

export interface PublicationTreatRequest {
  action?: 'MARK_TREATED' | 'CREATE_TASK' | 'ARCHIVE';
  notes?: string;
  user?: string;
  createTask?: boolean;
  taskTitle?: string;
  taskDescription?: string;
  taskAssignedUser?: string;
  taskPriority?: string;
  taskCategory?: string;
  taskDueDate?: number;
  taskDueTime?: string;
  syncCalendar?: boolean;
}
