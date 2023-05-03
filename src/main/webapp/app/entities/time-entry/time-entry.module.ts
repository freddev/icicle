import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TimeEntryComponent } from './list/time-entry.component';
import { TimeEntryDetailComponent } from './detail/time-entry-detail.component';
import { TimeEntryUpdateComponent } from './update/time-entry-update.component';
import { TimeEntryDeleteDialogComponent } from './delete/time-entry-delete-dialog.component';
import { TimeEntryRoutingModule } from './route/time-entry-routing.module';

@NgModule({
  imports: [SharedModule, TimeEntryRoutingModule],
  declarations: [TimeEntryComponent, TimeEntryDetailComponent, TimeEntryUpdateComponent, TimeEntryDeleteDialogComponent],
})
export class TimeEntryModule {}
