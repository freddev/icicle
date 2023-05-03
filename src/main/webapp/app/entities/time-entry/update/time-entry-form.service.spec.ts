import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../time-entry.test-samples';

import { TimeEntryFormService } from './time-entry-form.service';

describe('TimeEntry Form Service', () => {
  let service: TimeEntryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimeEntryFormService);
  });

  describe('Service methods', () => {
    describe('createTimeEntryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTimeEntryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            minutesWorked: expect.any(Object),
            taskName: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing ITimeEntry should create a new form with FormGroup', () => {
        const formGroup = service.createTimeEntryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            minutesWorked: expect.any(Object),
            taskName: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getTimeEntry', () => {
      it('should return NewTimeEntry for default TimeEntry initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTimeEntryFormGroup(sampleWithNewData);

        const timeEntry = service.getTimeEntry(formGroup) as any;

        expect(timeEntry).toMatchObject(sampleWithNewData);
      });

      it('should return NewTimeEntry for empty TimeEntry initial value', () => {
        const formGroup = service.createTimeEntryFormGroup();

        const timeEntry = service.getTimeEntry(formGroup) as any;

        expect(timeEntry).toMatchObject({});
      });

      it('should return ITimeEntry', () => {
        const formGroup = service.createTimeEntryFormGroup(sampleWithRequiredData);

        const timeEntry = service.getTimeEntry(formGroup) as any;

        expect(timeEntry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITimeEntry should not enable id FormControl', () => {
        const formGroup = service.createTimeEntryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTimeEntry should disable id FormControl', () => {
        const formGroup = service.createTimeEntryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
