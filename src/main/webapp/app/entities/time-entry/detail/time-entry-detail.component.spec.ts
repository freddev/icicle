import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TimeEntryDetailComponent } from './time-entry-detail.component';

describe('TimeEntry Management Detail Component', () => {
  let comp: TimeEntryDetailComponent;
  let fixture: ComponentFixture<TimeEntryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TimeEntryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ timeEntry: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TimeEntryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TimeEntryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load timeEntry on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.timeEntry).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
