import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'claims',
    loadChildren: () => import('./features/claims/claims.module').then(m => m.ClaimsModule)
  },
  {
    path: '',
    redirectTo: 'claims',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }