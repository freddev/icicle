import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TimeEntryComponent } from '../list/time-entry.component';
import { TimeEntryDetailComponent } from '../detail/time-entry-detail.component';
import { TimeEntryUpdateComponent } from '../update/time-entry-update.component';
import { TimeEntryRoutingResolveService } from './time-entry-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const timeEntryRoute: Routes = [
  {
    path: '',
    component: TimeEntryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TimeEntryDetailComponent,
    resolve: {
      timeEntry: TimeEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TimeEntryUpdateComponent,
    resolve: {
      timeEntry: TimeEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TimeEntryUpdateComponent,
    resolve: {
      timeEntry: TimeEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(timeEntryRoute)],
  exports: [RouterModule],
})
export class TimeEntryRoutingModule {}
