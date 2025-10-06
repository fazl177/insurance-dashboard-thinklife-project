import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TypeaheadOption {
  id: string | number;
  name: string;
  code?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TypeaheadService {
  private readonly baseUrl = `${environment.apiUrl}/typeahead`;

  constructor(private http: HttpClient) {}

  search(entity: 'examiner' | 'employer' | 'program', searchTerm: string): Observable<TypeaheadOption[]> {
    const params = new HttpParams().set('q', searchTerm);
    return this.http.get<TypeaheadOption[]>(`${this.baseUrl}/${entity}`, { params });
  }
}
