export interface ClaimItem {
  claimId: number;
  claimNumber: string;
  claimantId?: string;
  claimantName?: string;
  claimantFirstName?: string;
  claimantLastName?: string;
  ssn?: string;
  status?: string;
  statusFlag?: 'O' | 'R' | 'C' | string;
  incidentDate?: string; // ISO date
  adjOffice?: string;
  type?: string;
  brokerName?: string;
  insured?: string;
  injuryType?: string;
}

export interface PaginationInfo {
  currentPage: number; // 1-based
  pageSize: number;
  totalItems: number;
  totalPages: number;
}

export interface ClaimsSearchResponse {
  items: ClaimItem[];
  pagination: PaginationInfo;
  // echo of filters/sort can be included by backend; keep it open-ended
  [key: string]: any;
}

export type SortDirection = 'asc' | 'desc';

export interface SearchCriteria {
  [key: string]: any;
  q?: string;
  claimNumber?: string;
  claimantFirstName?: string;
  claimantLastName?: string;
  ssn?: string;
  status?: string[]; // e.g., ["Open","Reopen","Closed"]
  incidentDateFrom?: string; // YYYY-MM-DD
  incidentDateTo?: string;   // YYYY-MM-DD
  employeeNumber?: string;
  affiliateClaimNumber?: string;
  examiner?: string;
  employer?: string;
  program?: string;
  claimantType?: string;
  org1?: string;
  org2?: string;
  jurisdictionClaimNumber?: string;
  brokerName?: string;
  emailId?: string;
  phoneNumber?: string;
  dob?: string;              // YYYY-MM-DD
  adjustingOffice?: string;
}
