import { Component, EventEmitter, Input, Output, forwardRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NG_VALUE_ACCESSOR, ControlValueAccessor, FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { TypeaheadService, TypeaheadOption } from '../../../core/services/typeahead.service';

@Component({
  selector: 'app-typeahead',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './typeahead.component.html',
  styleUrls: ['./typeahead.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TypeaheadComponent),
      multi: true
    }
  ]
})
export class TypeaheadComponent implements ControlValueAccessor {
  @Input() entity!: 'examiner' | 'employer' | 'program';
  @Output() selectionChange = new EventEmitter<TypeaheadOption>();

  searchTerm$ = new Subject<string>();
  options: TypeaheadOption[] = [];
  loading = false;
  value: any;
  searchTerm = signal('');

  onChange: any = () => {};
  onTouched: any = () => {};

  constructor(private typeaheadService: TypeaheadService) {
    this.searchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (term.length < 2) {
          this.options = [];
          return [];
        }
        this.loading = true;
        return this.typeaheadService.search(this.entity, term);
      })
    ).subscribe(options => {
      this.options = options;
      this.loading = false;
    });
  }

  writeValue(value: any): void {
    this.value = value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  onSearch(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchTerm.set(inputElement.value);
    this.searchTerm$.next(inputElement.value);
  }

  onSelect(option: TypeaheadOption): void {
    this.value = option.name;
    this.onChange(option.name);
    this.selectionChange.emit(option);
    this.options = [];
  }
}
