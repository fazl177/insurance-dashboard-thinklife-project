import { Component, inject, Output, EventEmitter, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { LookupService, LookupOption } from '../../../../core/services/lookup.service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TypeaheadComponent } from '../../../../shared/components/typeahead/typeahead.component';
import { QuickSearchComponent } from '../../../../shared/components/quick-search/quick-search.component';
// Import other necessary modules from Angular Material, etc.

@Component({
  selector: 'app-claims-search-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TypeaheadComponent, QuickSearchComponent],
  templateUrl: './claims-search-form.component.html',
  styleUrls: ['./claims-search-form.component.scss']
})
export class ClaimsSearchFormComponent implements OnInit {
  @Output() search = new EventEmitter<any>();
  @Output() reset = new EventEmitter<void>();
  private fb = inject(FormBuilder);
  private lookupService = inject(LookupService);

  statusOptions$!: Observable<LookupOption[]>;
  claimantTypeOptions$!: Observable<LookupOption[]>;
  organizationOptions$!: Observable<LookupOption[]>;
  brokerOptions$!: Observable<LookupOption[]>;
  adjustingOfficeOptions$!: Observable<LookupOption[]>;
  searchForm: FormGroup;

  constructor() {
    this.searchForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadDropdowns();
  }

  private loadDropdowns(): void {
    this.statusOptions$ = this.lookupService.getLookups('status');
    this.claimantTypeOptions$ = this.lookupService.getLookups('claimantTypes');
    this.organizationOptions$ = this.lookupService.getLookups('organizations');
    this.brokerOptions$ = this.lookupService.getLookups('brokers');
    this.adjustingOfficeOptions$ = this.lookupService.getLookups('adjustingOffices');
  }

  private createForm(): FormGroup {
    return this.fb.group({
      claimantFirstName: [''],
      claimantLastName: [''],
      claimNumber: [''],
      incidentDateFrom: [null],
      incidentDateTo: [null],
      ssn: [''],
      status: [[]],
      employeeNumber: [''],
      affiliateClaimNumber: [''],
      examiner: [''],
      employer: [''],
      program: [''],
      claimantType: [''],
      organization1: [''],
      organization2: [''],
      jurisdictionClaimNumber: [''],
      brokerName: [''],
      emailId: [''],
      phoneNumber: [''],
      dob: [null],
      adjustingOffice: ['']
    });
  }

  onSearch(): void {
    if (this.searchForm.valid) {
      this.search.emit(this.searchForm.value);
    }
  }

  onReset(): void {
    this.searchForm.reset();
    this.reset.emit();
  }

  onQuickSearchSelected(criteria: any): void {
    this.searchForm.patchValue(criteria);
    this.onSearch();
  }
}
