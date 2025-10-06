import { Routes } from '@angular/router';

export const CLAIMS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/claims-search/claims-search.component').then(m => m.ClaimsSearchComponent),
    pathMatch: 'full'
  },
  {
    path: ':claimNumber',
    loadComponent: () => import('./components/claim-details/claim-details.component').then(m => m.ClaimDetailsComponent)
  }
];
