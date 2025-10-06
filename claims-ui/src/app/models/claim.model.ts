export interface Claim {
  claimNumber: string;
  claimantId: string;
  suffix?: string;
  claimantName: string;
  incidentDate: string | Date;
  status: string;
  insured?: string;
  examiner?: string;
  ssn?: string;
  lossState?: string;
  program?: string;
  insuranceType?: string;
  policyNumber?: string;
  organizations?: string[];
}

export interface ClaimFilter {
  quickSearch?: string;
  claimantName?: string;
  ssn?: string;
  claimNumber?: string;
  status?: string[];
  incidentDateFrom?: string;
  incidentDateTo?: string;
  lossState?: string;
  program?: string;
  insuranceType?: string;
  examiner?: string;
  policyNumber?: string;
  organizations?: string[];
  page: number;
  pageSize: number;
  sortBy?: keyof Claim | string;
  sortDir?: 'asc' | 'desc';
}
