import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaimsSearchResponse, SearchCriteria } from '../models/claim.model';

@Injectable({ providedIn: 'root' })
export class ClaimsService {
  private readonly baseUrl = `${environment.apiUrl}/claims`;

  constructor(private http: HttpClient) {}

  searchClaims(
    criteria: SearchCriteria,
    page: number = 1,
    pageSize: number = 20,
    sort?: { field: string; direction: 'asc' | 'desc' }
  ): Observable<ClaimsSearchResponse> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('pageSize', String(pageSize));

    // Append criteria as query params
    Object.entries(criteria).forEach(([key, value]) => {
      if (value == null || value === '') return;
      if (Array.isArray(value)) {
        if (value.length) params = params.set(key, value.join(','));
      } else {
        params = params.set(key, String(value));
      }
    });

    if (sort && sort.field && sort.direction) {
      params = params.set('sort', `${sort.field}:${sort.direction}`);
    }

    return this.http.get<ClaimsSearchResponse>(this.baseUrl, { params });
  }

  exportClaims(
    format: 'csv' | 'xlsx' | 'json',
    criteria: SearchCriteria,
    sort?: { field: string; direction: 'asc' | 'desc' },
    columns?: string[]
  ): Observable<Blob> {
    const body: any = {
      format,
      filters: criteria,
    };
    if (sort) body.sort = `${sort.field}:${sort.direction}`;
    if (columns) body.columns = columns;

    return this.http.post(`${this.baseUrl}/export`, body, { responseType: 'blob' });
  }
}
