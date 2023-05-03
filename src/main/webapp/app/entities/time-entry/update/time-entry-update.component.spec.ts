import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TimeEntryFormService } from './time-entry-form.service';
import { TimeEntryService } from '../service/time-entry.service';
import { ITimeEntry } from '../time-entry.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TimeEntryUpdateComponent } from './time-entry-update.component';

describe('TimeEntry Management Update Component', () => {
  let comp: TimeEntryUpdateComponent;
  let fixture: ComponentFixture<TimeEntryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let timeEntryFormService: TimeEntryFormService;
  let timeEntryService: TimeEntryService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TimeEntryUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TimeEntryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TimeEntryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    timeEntryFormService = TestBed.inject(TimeEntryFormService);
    timeEntryService = TestBed.inject(TimeEntryService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const timeEntry: ITimeEntry = { id: 456 };
      const user: IUser = { id: 35257 };
      timeEntry.user = user;

      const userCollection: IUser[] = [{ id: 88264 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timeEntry });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const timeEntry: ITimeEntry = { id: 456 };
      const user: IUser = { id: 93546 };
      timeEntry.user = user;

      activatedRoute.data = of({ timeEntry });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.timeEntry).toEqual(timeEntry);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeEntry>>();
      const timeEntry = { id: 123 };
      jest.spyOn(timeEntryFormService, 'getTimeEntry').mockReturnValue(timeEntry);
      jest.spyOn(timeEntryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: timeEntry }));
      saveSubject.complete();

      // THEN
      expect(timeEntryFormService.getTimeEntry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(timeEntryService.update).toHaveBeenCalledWith(expect.objectContaining(timeEntry));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeEntry>>();
      const timeEntry = { id: 123 };
      jest.spyOn(timeEntryFormService, 'getTimeEntry').mockReturnValue({ id: null });
      jest.spyOn(timeEntryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeEntry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: timeEntry }));
      saveSubject.complete();

      // THEN
      expect(timeEntryFormService.getTimeEntry).toHaveBeenCalled();
      expect(timeEntryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeEntry>>();
      const timeEntry = { id: 123 };
      jest.spyOn(timeEntryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(timeEntryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
