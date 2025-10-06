import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClaimItem, MyQueueResponse, PaginatedResponse, OptionItem } from '../models/claim';

export interface ClaimsFilter {
  q?: string;
  status?: string[];
  dolStart?: string;
  dolEnd?: string;
  reportedStart?: string;
  reportedEnd?: string;
  minAmount?: number;
  maxAmount?: number;
  orgIds?: string[];
  adjusterIds?: string[];
  tags?: string[];

  claimantName?: string;
  ssn?: string;
  claimNumber?: string;
  lossState?: string;
  program?: string;
  insuranceType?: string[];
  examiner?: string;
  policyNumber?: string;
  organization1?: string;
  organization2?: string;
  organization3?: string;
  organization4?: string;

  claimantNameOp?: string;
  claimNumberOp?: string;
  ssnOp?: string;
  incidentDateOp?: string;
  policyNumberOp?: string;
  examinerOp?: string;
  programOp?: string;

  page?: number;
  pageSize?: number;
  sort?: string;
}

function buildParams(filter: Record<string, unknown>): HttpParams {
  let params = new HttpParams();
  Object.entries(filter).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') return;
    if (Array.isArray(value)) {
      if (value.length === 0) return;
      params = params.set(key, value.join(','));
    } else {
      params = params.set(key, String(value));
    }
  });
  return params;
}

@Injectable({ providedIn: 'root' })
export class ClaimsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api';

  searchClaims(filter: ClaimsFilter): Observable<PaginatedResponse<ClaimItem>> {
    const params = buildParams(filter as Record<string, unknown>);
    return this.http.get<PaginatedResponse<ClaimItem>>(`${this.baseUrl}/claims`, { params });
  }

  fetchMyqueue(filter: ClaimsFilter): Observable<MyQueueResponse> {
    const params = buildParams(filter as Record<string, unknown>);
    return this.http.get<MyQueueResponse>(`${this.baseUrl}/fetchMyqueue`, { params });
  }

  exportClaims(filter: ClaimsFilter, format: 'csv' | 'xlsx' | 'json' = 'csv'): Observable<Blob> {
    const params = buildParams({ ...(filter as Record<string, unknown>), format });
    return this.http.get(`${this.baseUrl}/claims/export`, { params, responseType: 'blob' });
  }

  // Dropdowns
  insuranceTypes(): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/lookups/insurance-types`);
  }
  statuses(): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/lookups/statuses`);
  }
  claimantTypes(): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/lookups/claimant-types`);
  }
  organizations(): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/lookups/organizations`);
  }

  // Typeaheads
  employers(q: string): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/typeahead/employers`, { params: new HttpParams().set('q', q) });
  }
  programs(q: string): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/typeahead/programs`, { params: new HttpParams().set('q', q) });
  }
  policies(q: string): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/typeahead/policies`, { params: new HttpParams().set('q', q) });
  }
  underwriters(q: string): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/typeahead/underwriters`, { params: new HttpParams().set('q', q) });
  }
  brokers(q: string): Observable<OptionItem[]> {
    return this.http.get<OptionItem[]>(`${this.baseUrl}/typeahead/brokers`, { params: new HttpParams().set('q', q) });
  }
}


