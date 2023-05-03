import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITimeEntry } from '../time-entry.model';

@Component({
  selector: 'jhi-time-entry-detail',
  templateUrl: './time-entry-detail.component.html',
})
export class TimeEntryDetailComponent implements OnInit {
  timeEntry: ITimeEntry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeEntry }) => {
      this.timeEntry = timeEntry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
