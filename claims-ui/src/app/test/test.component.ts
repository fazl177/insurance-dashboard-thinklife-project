import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 20px;">
      <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 16px;">Test Component</h1>
      <p>This is a simple test component to verify basic Angular functionality.</p>
      <button style="background-color: #3b82f6; color: white; padding: 8px 16px; border: none; border-radius: 4px; margin-top: 16px; cursor: pointer;" (click)="onClick()">
        Test Button
      </button>
      <p *ngIf="clicked" style="margin-top: 16px; color: #059669;">Button was clicked!</p>
    </div>
  `,
  styles: []
})
export class TestComponent {
  clicked = false;

  onClick() {
    this.clicked = true;
    console.log('Test button clicked!');
  }
}