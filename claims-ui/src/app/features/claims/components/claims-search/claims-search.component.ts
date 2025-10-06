import { Component, inject } from '@angular/core';
import { ClaimsSearchFormComponent } from '../claims-search-form/claims-search-form.component';
import { ClaimsDataTableComponent } from '../claims-data-table/claims-data-table.component';
import { ClaimsStoreService } from '../../services/claims-store.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-claims-search',
  standalone: true,
  imports: [CommonModule, ClaimsSearchFormComponent, ClaimsDataTableComponent],
  templateUrl: './claims-search.component.html',
  styleUrls: ['./claims-search.component.scss'],
  providers: [ClaimsStoreService] // Provide the store at the component level
})
export class ClaimsSearchComponent {
  private store = inject(ClaimsStoreService);

  claims = this.store.claims;
  loading = this.store.loading;
  pagination = this.store.pagination;

  onSearch(criteria: any): void {
    this.store.search(criteria);
  }

  onReset(): void {
    this.store.reset();
  }
}
