import { Component, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { ColumnFilterModalComponent } from '../column-filter-modal/column-filter-modal.component';
import { ClaimItem } from '../../../../core/models/claim.model';
import { ClaimsStoreService } from '../../services/claims-store.service';

@Component({
  selector: 'app-claims-data-table',
  standalone: true,
  imports: [CommonModule, RouterLink, PaginationComponent, ColumnFilterModalComponent],
  templateUrl: './claims-data-table.component.html',
  styleUrls: ['./claims-data-table.component.scss']
})
export class ClaimsDataTableComponent {
  store = inject(ClaimsStoreService);

  claims = input.required<ClaimItem[]>();
  loading = input(false);
  pagination = input.required<any>();

  sortColumn = this.store.sortColumn;
  sortDirection = this.store.sortDirection;
  selectedRows = signal<Set<number>>(new Set());

  activeFilterColumn = signal<string | null>(null);


  onSort(column: string): void {
    this.store.sort(column);
  }

  getStatusBadgeClass(status: string | undefined): string {
    if (!status) return '';

    const statusLower = status.toLowerCase();
    switch (statusLower) {
      case 'open':
        return 'status-open';
      case 'reopen':
      case 're-open':
        return 'status-reopen';
      case 'closed':
        return 'status-closed';
      default:
        return 'status-default';
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        month: '2-digit',
        day: '2-digit',
        year: 'numeric'
      });
    } catch {
      return dateString;
    }
  }

  formatSSN(ssn: string | undefined): string {
    if (!ssn) return '';
    // Mask SSN: show only last 4 digits
    if (ssn.length >= 4) {
      const lastFour = ssn.slice(-4);
      return `XXX-XX-${lastFour}`;
    }
    return ssn;
  }

  toggleFilter(column: string): void {
    this.activeFilterColumn.set(this.activeFilterColumn() === column ? null : column);
  }

  onApplyFilter(column: string, value: any): void {
    this.store.filterColumn(column, value);
    this.activeFilterColumn.set(null);
  }

  onClearFilter(column: string): void {
    this.store.filterColumn(column, null);
    this.activeFilterColumn.set(null);
  }

  onSelectRow(claimId: number): void {
    const selected = new Set(this.selectedRows());
    if (selected.has(claimId)) {
      selected.delete(claimId);
    } else {
      selected.add(claimId);
    }
    this.selectedRows.set(selected);
  }

  onSelectAll(): void {
    if (this.selectedRows().size === this.claims().length) {
      this.selectedRows.set(new Set());
    } else {
      const allIds = this.claims().map(c => c.claimId);
      this.selectedRows.set(new Set(allIds));
    }
  }
}
