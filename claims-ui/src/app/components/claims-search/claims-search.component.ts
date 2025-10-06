import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ClaimService } from '../../services/claim.service';
import { Claim, ClaimFilter } from '../../models/claim.model';

@Component({
  selector: 'app-claims-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './claims-search.component.html',
  styleUrls: ['./claims-search.component.scss']
})
export class ClaimsSearchComponent {
  form: FormGroup;

  // Signals-based state
  claims = signal<Claim[]>([]);
  totalResults = signal<number>(0);
  page = signal<number>(1);
  pageSize = 20;
  sortBy = signal<string>('claimNumber');
  sortDir = signal<'asc' | 'desc'>('asc');

  constructor(
    private fb: FormBuilder,
    private claimService: ClaimService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      quickSearch: [''],
      claimantName: [''],
      ssn: ['', Validators.pattern(/^[0-9]{3}-[0-9]{2}-[0-9]{4}$/)],
      claimNumber: [''],
      status: [[]], // multi-select like structure, simple array for now
      incidentDateFrom: [''],
      incidentDateTo: [''],
      lossState: [''],
      program: [''],
      insuranceType: [''],
      examiner: [''],
      policyNumber: [''],
      organizations: this.fb.array<FormControl<string | null>>([new FormControl<string | null>('')])
    });

    // Initialize from URL
    this.route.queryParams.subscribe((params) => {
      // Patch only known fields
      const knownKeys = Object.keys(this.form.controls);
      const toPatch: any = {};
      for (const k of knownKeys) {
        if (params[k] !== undefined) toPatch[k] = params[k];
      }
      this.form.patchValue(toPatch, { emitEvent: false });
      this.fetchClaims();
    });
  }

  get organizations(): FormArray<FormControl<string | null>> {
    return this.form.get('organizations') as FormArray<FormControl<string | null>>;
  }

  addOrganization() {
    if (this.organizations.length < 4) this.organizations.push(new FormControl<string | null>(''));
  }

  removeOrganization(index: number) {
    if (this.organizations.length > 1) this.organizations.removeAt(index);
  }

  onSearch() {
    const filters = this.form.value;
    this.router.navigate([], { queryParams: filters, queryParamsHandling: 'merge' });
    this.page.set(1);
    this.fetchClaims();
  }

  onReset() {
    this.form.reset({
      quickSearch: '',
      claimantName: '',
      ssn: '',
      claimNumber: '',
      status: [],
      incidentDateFrom: '',
      incidentDateTo: '',
      lossState: '',
      program: '',
      insuranceType: '',
      examiner: '',
      policyNumber: '',
      organizations: ['']
    });
    this.router.navigate([], { queryParams: {} });
    this.claims.set([]);
    this.totalResults.set(0);
    this.page.set(1);
  }

  private fetchClaims() {
    const filters: ClaimFilter = {
      ...(this.form.value as any),
      page: this.page(),
      pageSize: this.pageSize,
      sortBy: this.sortBy(),
      sortDir: this.sortDir()
    };
    this.claimService.searchClaims(filters).subscribe((res: { claims: Claim[]; total: number }) => {
      this.claims.set(res.claims);
      this.totalResults.set(res.total);
    });
  }

  sort(column: string) {
    if (this.sortBy() === column) {
      this.sortDir.set(this.sortDir() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortBy.set(column);
      this.sortDir.set('asc');
    }
    this.fetchClaims();
  }

  nextPage() {
    this.page.set(this.page() + 1);
    this.fetchClaims();
  }
  prevPage() {
    if (this.page() > 1) {
      this.page.set(this.page() - 1);
      this.fetchClaims();
    }
  }

  exportCSV() {
    const data = this.claims();
    if (!data?.length) return;
    const headers = Object.keys(data[0]).join(',');
    const csvRows = data.map((row) => Object.values(row).map((v) => `"${String(v ?? '')}"`).join(','));
    const csv = [headers, ...csvRows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'claims.csv';
    a.click();
    URL.revokeObjectURL(url);
  }

  exportJSON() {
    const data = this.claims();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'claims.json';
    a.click();
    URL.revokeObjectURL(url);
  }

  // trackBy for table rows
  trackByClaimNumber = (_: number, item: Claim) => item.claimNumber;
}
