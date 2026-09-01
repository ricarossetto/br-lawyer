export interface RestfulCaseOverviewV8 {
  id: string;
  externalId?: string;
  fileNumber: string;
  name: string;
  reason?: string;
  subjectField?: string;
  lawyer?: string;
  assistant?: string;
  claimNumber?: string;
  claimValue?: number | string;
  archived: boolean;
  dateChanged?: number;
  tags?: string[];
}

export interface RestfulCasePageV8 {
  total: number;
  offset: number;
  limit: number;
  items: RestfulCaseOverviewV8[];
}

export interface RestfulCaseV1 {
  id: string;
  externalId?: string;
  fileNumber: string;
  name: string;
  reason?: string;
  subjectField?: string;
  lawyer?: string;
  assistant?: string;
  claimNumber?: string;
  claimValue?: string;
  notice?: string;
  custom1?: string;
  custom2?: string;
  custom3?: string;
  archived: number; // 0 = false, 1 = true
}

export interface RestfulPartyV1 {
  id: string;
  caseId: string;
  addressId?: string;
  contact?: string;
  contactName?: string;
  involvementType?: string;
  reference?: string;
  custom1?: string;
  custom2?: string;
  custom3?: string;
}

export interface RestfulTagV1 {
  id?: string;
  name: string;
  tagValue?: string;
  dateSet?: number;
}

export interface RestfulDocumentV1 {
  id: string;
  name?: string;
  fileName?: string;
  version?: number;
  dateChanged?: number;
  creationDate?: string | number;
  deleted?: boolean;
  folderId?: string;
  caseId?: string;
  externalId?: string;
  tags?: RestfulTagV1[];
  size?: number;
}

export interface RestfulDocumentContentV1 {
  id: string;
  fileName: string;
  version?: number;
  caseId: string;
  folderId?: string;
  base64content: string;
}

export interface DocumentPreviewPdfResponse {
  fileName: string;
  kind: 'pdf' | 'text';
  base64content?: string;
  text?: string;
}

export interface RestfulDueDateV1 {
  id: string;
  dueDate: number;
  reason: string;
  assignee?: string;
  done: boolean;
  type: string; // RESPITE, FOLLOWUP, EVENT
}

export interface RestfulCaseHistoryV8 {
  id: string;
  changeDate: number;
  principal?: string;
  changeDescription?: string;
  changeType?: string;
  userName?: string;
  description?: string;
}