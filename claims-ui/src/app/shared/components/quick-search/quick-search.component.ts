import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quick-search',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quick-search.component.html',
  styleUrls: ['./quick-search.component.scss']
})
export class QuickSearchComponent {
  @Output() searchSelected = new EventEmitter<any>();

  quickSearchOptions = [
    { name: 'All Open Claims', criteria: { status: ['Open', 'Re-Open'] } },
    { name: 'My Claims', criteria: { examiner: 'currentUser' } }, // 'currentUser' would be resolved by the store
    { name: 'Recent Updates', criteria: { updatedSince: '24h' } } // '24h' would be resolved by the store
  ];

  onSelect(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedOption = this.quickSearchOptions[selectElement.selectedIndex - 1]; // -1 to account for the disabled option
    if (selectedOption) {
      this.searchSelected.emit(selectedOption.criteria);
    }
  }
}
