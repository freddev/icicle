import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TimeEntryFormService, TimeEntryFormGroup } from './time-entry-form.service';
import { ITimeEntry } from '../time-entry.model';
import { TimeEntryService } from '../service/time-entry.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-time-entry-update',
  templateUrl: './time-entry-update.component.html',
})
export class TimeEntryUpdateComponent implements OnInit {
  isSaving = false;
  timeEntry: ITimeEntry | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: TimeEntryFormGroup = this.timeEntryFormService.createTimeEntryFormGroup();

  constructor(
    protected timeEntryService: TimeEntryService,
    protected timeEntryFormService: TimeEntryFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeEntry }) => {
      this.timeEntry = timeEntry;
      if (timeEntry) {
        this.updateForm(timeEntry);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const timeEntry = this.timeEntryFormService.getTimeEntry(this.editForm);
    if (timeEntry.id !== null) {
      this.subscribeToSaveResponse(this.timeEntryService.update(timeEntry));
    } else {
      this.subscribeToSaveResponse(this.timeEntryService.create(timeEntry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITimeEntry>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(timeEntry: ITimeEntry): void {
    this.timeEntry = timeEntry;
    this.timeEntryFormService.resetForm(this.editForm, timeEntry);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, timeEntry.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.timeEntry?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
