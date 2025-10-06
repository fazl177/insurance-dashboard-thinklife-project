import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideNgxMask } from 'ngx-mask';

import { ClaimsRoutingModule } from './claims-routing.module';
import { ClaimsComponent } from './claims.component';
import { ClaimsService } from './claims.service';

@NgModule({
  imports: [
    CommonModule,
    ClaimsRoutingModule,
    ClaimsComponent
  ],
  providers: [
    ClaimsService,
    provideNgxMask()
  ]
})
export class ClaimsModule { }