import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITimeEntry, NewTimeEntry } from '../time-entry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimeEntry for edit and NewTimeEntryFormGroupInput for create.
 */
type TimeEntryFormGroupInput = ITimeEntry | PartialWithRequiredKeyOf<NewTimeEntry>;

type TimeEntryFormDefaults = Pick<NewTimeEntry, 'id'>;

type TimeEntryFormGroupContent = {
  id: FormControl<ITimeEntry['id'] | NewTimeEntry['id']>;
  date: FormControl<ITimeEntry['date']>;
  minutesWorked: FormControl<ITimeEntry['minutesWorked']>;
  taskName: FormControl<ITimeEntry['taskName']>;
  user: FormControl<ITimeEntry['user']>;
};

export type TimeEntryFormGroup = FormGroup<TimeEntryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimeEntryFormService {
  createTimeEntryFormGroup(timeEntry: TimeEntryFormGroupInput = { id: null }): TimeEntryFormGroup {
    const timeEntryRawValue = {
      ...this.getFormDefaults(),
      ...timeEntry,
    };
    return new FormGroup<TimeEntryFormGroupContent>({
      id: new FormControl(
        { value: timeEntryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(timeEntryRawValue.date, {
        validators: [Validators.required],
      }),
      minutesWorked: new FormControl(timeEntryRawValue.minutesWorked, {
        validators: [Validators.required],
      }),
      taskName: new FormControl(timeEntryRawValue.taskName, {
        validators: [Validators.required],
      }),
      user: new FormControl(timeEntryRawValue.user),
    });
  }

  getTimeEntry(form: TimeEntryFormGroup): ITimeEntry | NewTimeEntry {
    return form.getRawValue() as ITimeEntry | NewTimeEntry;
  }

  resetForm(form: TimeEntryFormGroup, timeEntry: TimeEntryFormGroupInput): void {
    const timeEntryRawValue = { ...this.getFormDefaults(), ...timeEntry };
    form.reset(
      {
        ...timeEntryRawValue,
        id: { value: timeEntryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TimeEntryFormDefaults {
    return {
      id: null,
    };
  }
}
