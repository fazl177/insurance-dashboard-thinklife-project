import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Claim, ClaimFilter } from '../models/claim.model';

@Injectable({ providedIn: 'root' })
export class ClaimService {
  private readonly mockClaims: Claim[] = [
    { claimNumber: '1001', claimantId: 'C123', suffix: 'Jr', claimantName: 'John Doe', incidentDate: '2023-01-15', status: 'Open', insured: 'Acme Corp', examiner: 'Eve', ssn: '123-45-6789', lossState: 'CA', program: 'Auto', insuranceType: 'Liability', policyNumber: 'P-001', organizations: ['Org1'] },
    { claimNumber: '1002', claimantId: 'C124', suffix: 'Sr', claimantName: 'Jane Smith', incidentDate: '2023-02-10', status: 'Closed', insured: 'Beta Inc', examiner: 'Mallory', ssn: '987-65-4321', lossState: 'NY', program: 'Home', insuranceType: 'Property', policyNumber: 'P-002', organizations: ['Org2','Org3'] },
    { claimNumber: '1003', claimantId: 'C125', claimantName: 'Alex Johnson', incidentDate: '2023-03-05', status: 'Pending', insured: 'Gamma LLC', examiner: 'Trent', ssn: '222-33-4444', lossState: 'TX', program: 'Health', insuranceType: 'HMO', policyNumber: 'P-003', organizations: [] },
  ];

  constructor(private http: HttpClient) {}

  searchClaims(filter: ClaimFilter): Observable<{ claims: Claim[]; total: number }> {
    if (environment.useMockData) {
      let data = [...this.mockClaims];
      const q = (s?: string) => (s ?? '').toString().toLowerCase();

      if (filter.quickSearch) {
        const needle = q(filter.quickSearch);
        data = data.filter(c =>
          q(c.claimNumber).includes(needle) ||
          q(c.claimantName).includes(needle) ||
          q(c.ssn).includes(needle)
        );
      }
      if (filter.claimantName) data = data.filter(c => q(c.claimantName).includes(q(filter.claimantName)));
      if (filter.ssn) data = data.filter(c => q(c.ssn).includes(q(filter.ssn)));
      if (filter.claimNumber) data = data.filter(c => q(c.claimNumber).includes(q(filter.claimNumber)));
      if (filter.status && filter.status.length) data = data.filter(c => filter.status!.includes(c.status));
      if (filter.incidentDateFrom) data = data.filter(c => new Date(c.incidentDate) >= new Date(filter.incidentDateFrom!));
      if (filter.incidentDateTo) data = data.filter(c => new Date(c.incidentDate) <= new Date(filter.incidentDateTo!));
      if (filter.lossState) data = data.filter(c => q(c.lossState).includes(q(filter.lossState)));
      if (filter.program) data = data.filter(c => q(c.program).includes(q(filter.program)));
      if (filter.insuranceType) data = data.filter(c => q(c.insuranceType).includes(q(filter.insuranceType)));
      if (filter.examiner) data = data.filter(c => q(c.examiner).includes(q(filter.examiner)));
      if (filter.policyNumber) data = data.filter(c => q(c.policyNumber).includes(q(filter.policyNumber)));

      if (filter.sortBy) {
        const dir = filter.sortDir === 'desc' ? -1 : 1;
        data.sort((a: any, b: any) => {
          const av = (a[filter.sortBy!] ?? '').toString();
          const bv = (b[filter.sortBy!] ?? '').toString();
          return av.localeCompare(bv) * dir;
        });
      }

      const total = data.length;
      const start = Math.max(0, (filter.page - 1) * filter.pageSize);
      const paged = data.slice(start, start + filter.pageSize);
      return of({ claims: paged, total }).pipe(delay(300));
    }

    // Real HTTP branch
    let params = new HttpParams();
    Object.entries(filter as any).forEach(([k, v]) => {
      if (v == null) return;
      if (Array.isArray(v)) {
        if (v.length) params = params.set(k, v.join(','));
      } else if (v !== '') {
        params = params.set(k, String(v));
      }
    });
    return this.http.get<{ claims: Claim[]; total: number }>(`${environment.apiBaseUrl}/claims`, { params });
  }
}
