import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Claim, ClaimSearchResponse } from './claim.model';

@Injectable({
  providedIn: 'root'
})
export class ClaimsService {
  private apiUrl = `${environment.apiUrl}/claims`;

  constructor(private http: HttpClient) {}

  searchClaims(filters: any): Observable<ClaimSearchResponse> {
    let params = new HttpParams()
      .set('page', filters.page.toString())
      .set('size', filters.size.toString());

    if (filters.sort) {
      params = params.set('sort', filters.sort);
    }

    if (filters.claimId) {
      params = params.set('claimId', filters.claimId);
    }

    if (filters.ssn) {
      params = params.set('ssn', filters.ssn);
    }

    if (filters.status) {
      params = params.set('status', filters.status);
    }

    if (filters.startDate) {
      params = params.set('startDate', filters.startDate.toISOString());
    }

    if (filters.endDate) {
      params = params.set('endDate', filters.endDate.toISOString());
    }

    return this.http.get<ClaimSearchResponse>(this.apiUrl, { params });
  }

  downloadClaims(claimIds: string[], format: string): Observable<Blob> {
    const params = new HttpParams()
      .set('format', format)
      .set('claimIds', claimIds.join(','));

    return this.http.get(`${this.apiUrl}/download`, {
      params,
      responseType: 'blob'
    });
  }

  getClaim(claimId: string): Observable<Claim> {
    return this.http.get<Claim>(`${this.apiUrl}/${claimId}`);
  }

  updateClaim(claimId: string, claim: Partial<Claim>): Observable<Claim> {
    return this.http.patch<Claim>(`${this.apiUrl}/${claimId}`, claim);
  }
}