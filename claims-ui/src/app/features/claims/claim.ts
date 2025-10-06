export interface ClaimItem {
  claimId: string;
  claimNumber: string;
  adjusterOffice: string;
  claimantName: string;
  ssn: string;
  status: string;
  incidentDate: string;
  type: string;
  brokerName: string;
  insured: string;
  // Additional fields for details
  claimantFirstName?: string;
  claimantLastName?: string;
  employeeNumber?: string;
  examiner?: string;
  program?: string;
  organization?: string;
  jurisdictionClaimNumber?: string;
  emailId?: string;
  phoneNumber?: string;
  dob?: string;
}

export interface ClaimsFilter {
  page: number;
  pageSize: number;
  sortField: string;
  sortDirection: 'asc' | 'desc';
  // Search filters
  claimNumber?: string;
  ssn?: string;
  claimType?: string;
  status?: string;
  incidentDate?: Date;
  claimantFirstName?: string;
  claimantLastName?: string;
  adjusterOffice?: string;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}