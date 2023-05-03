import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITimeEntry } from '../time-entry.model';
import { TimeEntryService } from '../service/time-entry.service';

@Injectable({ providedIn: 'root' })
export class TimeEntryRoutingResolveService implements Resolve<ITimeEntry | null> {
  constructor(protected service: TimeEntryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITimeEntry | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((timeEntry: HttpResponse<ITimeEntry>) => {
          if (timeEntry.body) {
            return of(timeEntry.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
