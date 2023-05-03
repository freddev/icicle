import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'time-entry',
        data: { pageTitle: 'icicleApp.timeEntry.home.title' },
        loadChildren: () => import('./time-entry/time-entry.module').then(m => m.TimeEntryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
