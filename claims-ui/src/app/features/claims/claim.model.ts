export interface Claim {
  claimId: string;
  ssn: string;
  status: 'open' | 'closed' | 'pending';
  createdDate: string;
  updatedDate: string;
}

export interface ClaimSearchResponse {
  content: Claim[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}