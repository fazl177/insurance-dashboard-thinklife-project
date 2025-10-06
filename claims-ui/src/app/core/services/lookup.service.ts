import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { shareReplay, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface LookupOption {
  code: string | number;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class LookupService {
  private readonly baseUrl = `${environment.apiUrl}/lookups`;
  private cache: { [key: string]: Observable<LookupOption[]> } = {};

  constructor(private http: HttpClient) {}

  getLookups(type: 'status' | 'claimantTypes' | 'organizations' | 'brokers' | 'adjustingOffices'): Observable<LookupOption[]> {
    if (!this.cache[type]) {
      this.cache[type] = this.http.get<LookupOption[]>(`${this.baseUrl}/${type}`).pipe(
        shareReplay(1)
      );
    }
    return this.cache[type];
  }
}
