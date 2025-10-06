import { Component, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, RouterOutlet, CommonModule],
  template: `
    <div class="app-shell">
      <!-- Top Navigation Bar -->
      <nav class="topbar">
        <div class="nav-left">
          <div class="app-logo">
            <img src="/favicon.ico" alt="Claims System" class="logo-icon">
            <span class="app-title">Claims Search</span>
          </div>
        </div>

        <div class="nav-center">
          <ul class="nav-tabs">
            <li class="nav-tab" [class.active]="activeTab() === 'search'">
              <a (click)="setActiveTab('search')">Search</a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'batch-approval'">
              <a (click)="setActiveTab('batch-approval')">Batch Approval</a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'document-search'">
              <a (click)="setActiveTab('document-search')">Document Search</a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'new-mail'">
              <a (click)="setActiveTab('new-mail')" class="notification-badge">
                New Mail
                <span class="badge" *ngIf="mailCount > 0">{{ mailCount }}</span>
              </a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'parking-lot'">
              <a (click)="setActiveTab('parking-lot')">Parking Lot</a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'rules-config'">
              <a (click)="setActiveTab('rules-config')">Rules Configuration</a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'link-documents'">
              <a (click)="setActiveTab('link-documents')">Link Documents</a>
            </li>
            <li class="nav-tab" [class.active]="activeTab() === 'dms-admin'">
              <a (click)="setActiveTab('dms-admin')">DMS Admin</a>
            </li>
          </ul>
        </div>

        <div class="nav-right">
          <div class="user-menu">
            <span class="user-name">User</span>
            <button class="logout-btn">Logout</button>
          </div>
        </div>
      </nav>

      <!-- Main Content -->
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-shell {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .topbar {
      background: var(--bg-primary, #FFFFFF);
      border-bottom: 1px solid var(--border-color, #DEE2E6);
      padding: 0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 60px;
      position: sticky;
      top: 0;
      z-index: 100;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .nav-left {
      display: flex;
      align-items: center;
      padding: 0 20px;
    }

    .app-logo {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .logo-icon {
      width: 32px;
      height: 32px;
    }

    .app-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary, #212529);
    }

    .nav-center {
      flex: 1;
      display: flex;
      justify-content: center;
    }

    .nav-tabs {
      display: flex;
      list-style: none;
      margin: 0;
      padding: 0;
      gap: 0;
    }

    .nav-tab {
      position: relative;
    }

    .nav-tab a {
      display: block;
      padding: 20px 24px;
      text-decoration: none;
      color: var(--text-secondary, #6C757D);
      font-size: 14px;
      font-weight: 500;
      border-bottom: 3px solid transparent;
      transition: all 0.2s;
      white-space: nowrap;
    }

    .nav-tab a:hover {
      color: var(--primary-color, #4A90E2);
      background: var(--bg-hover, #E9ECEF);
    }

    .nav-tab.active a {
      color: var(--primary-color, #4A90E2);
      border-bottom-color: var(--primary-color, #4A90E2);
      background: var(--bg-selected, #E3F2FD);
    }

    .notification-badge {
      position: relative;
    }

    .badge {
      position: absolute;
      top: 12px;
      right: 8px;
      background: var(--danger-color, #DC3545);
      color: white;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 10px;
      min-width: 16px;
      text-align: center;
    }

    .nav-right {
      display: flex;
      align-items: center;
      padding: 0 20px;
    }

    .user-menu {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .user-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-primary, #212529);
    }

    .logout-btn {
      background: var(--secondary-color, #6C757D);
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 4px;
      font-size: 14px;
      cursor: pointer;
      transition: background 0.2s;
    }

    .logout-btn:hover {
      background: #5A6268;
    }

    .main-content {
      flex: 1;
      background: var(--bg-secondary, #F8F9FA);
      min-height: calc(100vh - 60px);
    }
  `]
})
export class AppComponent {
  title = 'claims-ui';
  activeTab = signal<string>('search');
  mailCount = 18; // Example notification count

  setActiveTab(tab: string): void {
    this.activeTab.set(tab);
  }
}
