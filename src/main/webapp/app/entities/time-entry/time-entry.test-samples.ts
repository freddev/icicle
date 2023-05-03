import dayjs from 'dayjs/esm';

import { ITimeEntry, NewTimeEntry } from './time-entry.model';

export const sampleWithRequiredData: ITimeEntry = {
  id: 3305,
  date: dayjs('2023-05-02'),
  minutesWorked: 82504,
  taskName: 'Intranet azure',
};

export const sampleWithPartialData: ITimeEntry = {
  id: 39956,
  date: dayjs('2023-05-02'),
  minutesWorked: 2586,
  taskName: 'Uganda Rial',
};

export const sampleWithFullData: ITimeEntry = {
  id: 30102,
  date: dayjs('2023-05-03'),
  minutesWorked: 88052,
  taskName: 'platforms',
};

export const sampleWithNewData: NewTimeEntry = {
  date: dayjs('2023-05-02'),
  minutesWorked: 43727,
  taskName: 'Kansas',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
