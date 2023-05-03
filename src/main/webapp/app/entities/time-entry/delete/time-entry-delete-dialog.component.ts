import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITimeEntry } from '../time-entry.model';
import { TimeEntryService } from '../service/time-entry.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './time-entry-delete-dialog.component.html',
})
export class TimeEntryDeleteDialogComponent {
  timeEntry?: ITimeEntry;

  constructor(protected timeEntryService: TimeEntryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.timeEntryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
