export interface AdjusterSummary {
  id: string;
  name: string;
}

export interface ClaimItem {
  id?: string; // grid api uses claim id as string
  claimId?: string;
  claimNumber?: string;
  claimantId?: number | string;
  claimant?: string;
  claimantName?: string;
  claimantSuffix?: string;
  ssn?: string;
  status?: string;
  statusFlag?: string; // 'R ' for reopen
  incidentDate?: string;
  incidentDateStr?: string;
  reportedDate?: string;
  amount?: number;
  organization?: string;
  adjustingOfficeDesc?: string;
  adjOffice?: string;
  adjuster?: AdjusterSummary | string;
  examiner?: string;
  insured?: string;
  insuranceType?: string | number;
  insuranceTypeDesc?: string;
  programDesc?: string;
  policyNumber?: string;
  updatedAt?: string;
  addDate?: string;
  addDateStr?: string;
  stateCode?: string;
  type?: string;
  brokerName?: string;
}

export interface PaginatedResponse<T> {
  items: T[];
  page: number;
  pageSize: number;
  total: number;
}

export interface MyQueueResponse {
  iTotalRecords: number;
  iTotalDisplayRecords: number;
  data: any[];
}

export interface OptionItem {
  id?: string | number;
  name?: string;
  code?: string | number;
  desc?: string;
}


