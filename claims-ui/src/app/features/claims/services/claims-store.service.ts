import { Injectable, signal, computed } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ClaimsService } from '../../../core/services/claims.service';
import { SearchCriteria, ClaimItem, PaginationInfo } from '../../../core/models/claim.model';

@Injectable({ providedIn: 'root' })
export class ClaimsStoreService {
  // Signals for state
  private _claims = signal<ClaimItem[]>([]);
  private _loading = signal(false);
  private _pagination = signal<PaginationInfo>({
    currentPage: 1,
    pageSize: 20,
    totalItems: 0,
    totalPages: 0
  });
  private _filters = signal<SearchCriteria>({});
  private _sort = signal<{ field: string; direction: 'asc' | 'desc' } | null>(null);

  readonly sortColumn = computed(() => this._sort()?.field ?? null);
  readonly sortDirection = computed(() => this._sort()?.direction ?? null);

  // Public readonly signals
  readonly claims = this._claims.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly pagination = this._pagination.asReadonly();
  readonly filters = this._filters.asReadonly();

  constructor(private claimsService: ClaimsService) {}

  search(criteria: SearchCriteria): void {
    this._loading.set(true);
    this._filters.set(criteria);

    this.claimsService.searchClaims(criteria, this._pagination().currentPage, this._pagination().pageSize, this._sort() || undefined)
      .pipe(
        catchError(error => {
          this._loading.set(false);
          // Error is handled by the interceptor, but we can also set a local error state if needed
          return of({ items: [], pagination: { currentPage: 1, pageSize: 20, totalItems: 0, totalPages: 0 } });
        })
      )
      .subscribe(response => {
        this._claims.set(response.items);
        this._pagination.set(response.pagination);
        this._loading.set(false);
      });
  }

  sort(field: string): void {
    const currentSort = this._sort();
    let direction: 'asc' | 'desc' | null = 'asc';

    if (currentSort?.field === field) {
      if (currentSort.direction === 'asc') {
        direction = 'desc';
      } else {
        direction = null;
      }
    } else {
      direction = 'asc';
    }

    this._sort.set(direction ? { field, direction } : null);
    this.search(this._filters());
  }

  filterColumn(column: string, value: any): void {
    const currentFilters = { ...this._filters() };
    currentFilters[column] = value;
    this.search(currentFilters);
  }

  changePage(page: number): void {
    const updatedPagination = { ...this._pagination(), currentPage: page };
    this._pagination.set(updatedPagination);
    this.search(this._filters());
  }

  reset(): void {
    this._claims.set([]);
    this._filters.set({});
    this._sort.set(null);
    this._pagination.set({ currentPage: 1, pageSize: 20, totalItems: 0, totalPages: 0 });
  }
}
