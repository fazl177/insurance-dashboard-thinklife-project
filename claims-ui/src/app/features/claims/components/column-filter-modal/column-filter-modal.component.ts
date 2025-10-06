import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-column-filter-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './column-filter-modal.component.html',
  styleUrls: ['./column-filter-modal.component.scss']
})
export class ColumnFilterModalComponent {
  @Input() columnType: 'text' | 'date' | 'select' = 'text';
  @Output() applyFilter = new EventEmitter<any>();
  @Output() clearFilter = new EventEmitter<void>();

  filterValue: any;
  filterValueTo: any; // For date ranges

  onApply(): void {
    let valueToEmit = this.filterValue;
    if (this.columnType === 'date') {
      valueToEmit = { from: this.filterValue, to: this.filterValueTo };
    }
    this.applyFilter.emit(valueToEmit);
  }

  onClear(): void {
    this.filterValue = null;
    this.filterValueTo = null;
    this.clearFilter.emit();
  }
}
