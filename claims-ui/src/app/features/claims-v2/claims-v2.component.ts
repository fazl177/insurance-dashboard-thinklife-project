import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ClaimsService } from '../../core/services/claims.service';
import { ClaimItem, ClaimsSearchResponse, SearchCriteria } from '../../core/models/claim.model';

@Component({
  selector: 'app-claims-v2',
  standalone: true,
  templateUrl: './claims-v2.component.html',
  styleUrls: ['./claims-v2.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatMenuModule,
    MatPaginatorModule,
    MatProgressSpinnerModule
  ]
})
export class ClaimsV2Component implements OnInit {
  form!: FormGroup;

  // state signals
  loading = signal<boolean>(false);
  items = signal<ClaimItem[]>([]);
  totalItems = signal<number>(0);
  currentPage = signal<number>(1); // 1-based
  pageSize = signal<number>(20);
  sortField = signal<string | null>(null);
  sortDir = signal<'asc' | 'desc' | null>(null);

  constructor(
    private fb: FormBuilder,
    private claimsService: ClaimsService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      claimantFirstName: [''],
      claimantLastName: [''],
      claimNumber: [''],
      incidentDateFrom: [''],
      incidentDateTo: [''],
      ssn: ['', [Validators.pattern(/^[0-9]{3}-[0-9]{2}-[0-9]{4}$/)]],
      status: [[]],
      employeeNumber: [''],
      affiliateClaimNumber: [''],
      examiner: [''],
      employer: [''],
      program: [''],
      claimantType: [''],
      org1: [''],
      org2: [''],
      jurisdictionClaimNumber: [''],
      brokerName: [''],
      emailId: [''],
      phoneNumber: [''],
      dob: [''],
      adjustingOffice: [''],
      quickSearch: ['']
    });

    // initial fetch
    this.search();
  }

  private criteriaFromForm(): SearchCriteria {
    const v = this.form.value;
    const {
      claimantFirstName, claimantLastName, claimNumber, incidentDateFrom, incidentDateTo,
      ssn, status, employeeNumber, affiliateClaimNumber, examiner, employer, program,
      claimantType, org1, org2, jurisdictionClaimNumber, brokerName, emailId, phoneNumber,
      dob, adjustingOffice
    } = v;

    return {
      claimantFirstName, claimantLastName, claimNumber, incidentDateFrom, incidentDateTo,
      ssn, status, employeeNumber, affiliateClaimNumber, examiner, employer, program,
      claimantType, org1, org2, jurisdictionClaimNumber, brokerName, emailId, phoneNumber,
      dob, adjustingOffice
    };
  }

  search(): void {
    this.loading.set(true);
    const criteria = this.criteriaFromForm();
    const sort = this.sortField() && this.sortDir()
      ? { field: this.sortField() as string, direction: this.sortDir() as 'asc' | 'desc' }
      : undefined;

    this.claimsService
      .searchClaims(criteria, this.currentPage(), this.pageSize(), sort)
      .subscribe({
        next: (res: ClaimsSearchResponse) => {
          this.items.set(res.items || []);
          this.totalItems.set(res.pagination?.totalItems ?? 0);
          this.loading.set(false);
        },
        error: () => {
          this.items.set([]);
          this.totalItems.set(0);
          this.loading.set(false);
        }
      });
  }

  reset(): void {
    this.form.reset({ status: [] });
    this.currentPage.set(1);
    this.sortField.set(null);
    this.sortDir.set(null);
    this.search();
  }

  toggleSort(column: string): void {
    if (this.sortField() !== column) {
      this.sortField.set(column);
      this.sortDir.set('asc');
    } else if (this.sortDir() === 'asc') {
      this.sortDir.set('desc');
    } else {
      this.sortField.set(null);
      this.sortDir.set(null);
    }
    this.search();
  }

  pageChange(nextPage: number): void {
    if (nextPage < 1) return;
    this.currentPage.set(nextPage);
    this.search();
  }

  export(format: 'csv' | 'xlsx' | 'json'): void {
    const criteria = this.criteriaFromForm();
    const sort = this.sortField() && this.sortDir()
      ? { field: this.sortField() as string, direction: this.sortDir() as 'asc' | 'desc' }
      : undefined;

    this.loading.set(true);
    this.claimsService.exportClaims(format, criteria, sort).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        const ts = new Date().toISOString().replace(/[-:T.Z]/g, '').slice(0, 15);
        a.href = url;
        a.download = `claims_export_${ts}.${format}`;
        a.click();
        URL.revokeObjectURL(url);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  // trackBy for rows
  trackByClaimId = (_: number, item: ClaimItem) => item.claimId;

  // pagination helpers to keep template simple
  totalPages(): number {
    const total = this.totalItems();
    const size = this.pageSize();
    return total > 0 ? Math.ceil(total / size) : 1;
  }

  showingFrom(): number {
    return (this.currentPage() - 1) * this.pageSize() + (this.totalItems() > 0 ? 1 : 0);
  }

  showingTo(): number {
    const to = this.currentPage() * this.pageSize();
    return Math.min(to, this.totalItems());
  }

  onPageInput(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    const val = Number(input.value) || 1;
    this.pageChange(val);
  }

  getMinPageSize(): number {
    const to = this.currentPage() * this.pageSize();
    return Math.min(to, this.totalItems());
  }
}
