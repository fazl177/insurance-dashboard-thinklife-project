import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

export interface ColumnFilter {
  columnKey: string;
  filterType: 'text' | 'select' | 'date';
  options?: Array<{ value: string; label: string }>;
  value?: string;
}

@Component({
  selector: 'app-column-filter',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule
  ],
  template: `
    <button mat-icon-button [matMenuTriggerFor]="filterMenu" [class.has-filter]="hasActiveFilter()">
      <mat-icon>filter_list</mat-icon>
    </button>
    
    <mat-menu #filterMenu="matMenu" class="column-filter-menu">
      <div class="filter-content" (click)="$event.stopPropagation()">
        <div class="filter-header">
          <span>Filter {{columnLabel}}</span>
          <button mat-icon-button (click)="clearFilter()" [disabled]="!hasActiveFilter()">
            <mat-icon>clear</mat-icon>
          </button>
        </div>
        
        <div class="filter-controls">
          <!-- Text Filter -->
          <mat-form-field *ngIf="filterType === 'text'" appearance="outline">
            <mat-label>Filter by {{columnLabel}}</mat-label>
            <input matInput [formControl]="filterControl" placeholder="Enter filter text">
          </mat-form-field>
          
          <!-- Select Filter -->
          <mat-form-field *ngIf="filterType === 'select'" appearance="outline">
            <mat-label>Select {{columnLabel}}</mat-label>
            <mat-select [formControl]="filterControl" multiple>
              <mat-option *ngFor="let option of options" [value]="option.value">
                {{option.label}}
              </mat-option>
            </mat-select>
          </mat-form-field>
          
          <!-- Date Filter -->
          <div *ngIf="filterType === 'date'" class="date-filter">
            <mat-form-field appearance="outline">
              <mat-label>From Date</mat-label>
              <input matInput [formControl]="fromDateControl" type="date">
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>To Date</mat-label>
              <input matInput [formControl]="toDateControl" type="date">
            </mat-form-field>
          </div>
        </div>
      </div>
    </mat-menu>
  `,
  styles: [`
    .has-filter {
      color: #2563eb !important;
    }
    
    .filter-content {
      padding: 16px;
      min-width: 250px;
    }
    
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      font-weight: 500;
    }
    
    .filter-controls {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    
    .date-filter {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    
    ::ng-deep .column-filter-menu .mat-menu-panel {
      max-width: none !important;
    }
  `]
})
export class ColumnFilterComponent implements OnInit {
  @Input() columnKey!: string;
  @Input() columnLabel!: string;
  @Input() filterType: 'text' | 'select' | 'date' = 'text';
  @Input() options: Array<{ value: string; label: string }> = [];
  @Input() currentValue: any = null;
  
  @Output() filterChange = new EventEmitter<{ column: string; value: any; type: string }>();
  
  filterControl = new FormControl();
  fromDateControl = new FormControl();
  toDateControl = new FormControl();
  
  ngOnInit() {
    // Set initial values
    if (this.currentValue !== null) {
      if (this.filterType === 'date' && typeof this.currentValue === 'object') {
        this.fromDateControl.setValue(this.currentValue.from || '');
        this.toDateControl.setValue(this.currentValue.to || '');
      } else {
        this.filterControl.setValue(this.currentValue);
      }
    }
    
    // Subscribe to filter changes
    if (this.filterType === 'text') {
      this.filterControl.valueChanges
        .pipe(debounceTime(300), distinctUntilChanged())
        .subscribe(value => this.emitFilterChange(value));
    } else if (this.filterType === 'select') {
      this.filterControl.valueChanges
        .subscribe(value => this.emitFilterChange(value));
    } else if (this.filterType === 'date') {
      this.fromDateControl.valueChanges
        .pipe(debounceTime(300), distinctUntilChanged())
        .subscribe(() => this.emitDateFilterChange());
      
      this.toDateControl.valueChanges
        .pipe(debounceTime(300), distinctUntilChanged())
        .subscribe(() => this.emitDateFilterChange());
    }
  }
  
  hasActiveFilter(): boolean {
    if (this.filterType === 'date') {
      return !!(this.fromDateControl.value || this.toDateControl.value);
    }
    const value = this.filterControl.value;
    return value !== null && value !== undefined && value !== '' && 
           !(Array.isArray(value) && value.length === 0);
  }
  
  clearFilter() {
    if (this.filterType === 'date') {
      this.fromDateControl.setValue('');
      this.toDateControl.setValue('');
    } else {
      this.filterControl.setValue(this.filterType === 'select' ? [] : '');
    }
    this.emitFilterChange(null);
  }
  
  private emitFilterChange(value: any) {
    this.filterChange.emit({
      column: this.columnKey,
      value: value,
      type: this.filterType
    });
  }
  
  private emitDateFilterChange() {
    const from = this.fromDateControl.value;
    const to = this.toDateControl.value;
    
    if (from || to) {
      this.emitFilterChange({ from, to });
    } else {
      this.emitFilterChange(null);
    }
  }
}