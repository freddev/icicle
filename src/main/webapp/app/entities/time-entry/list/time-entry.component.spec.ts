import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TimeEntryService } from '../service/time-entry.service';

import { TimeEntryComponent } from './time-entry.component';

describe('TimeEntry Management Component', () => {
  let comp: TimeEntryComponent;
  let fixture: ComponentFixture<TimeEntryComponent>;
  let service: TimeEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'time-entry', component: TimeEntryComponent }]), HttpClientTestingModule],
      declarations: [TimeEntryComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(TimeEntryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TimeEntryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TimeEntryService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.timeEntries?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to timeEntryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTimeEntryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTimeEntryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
