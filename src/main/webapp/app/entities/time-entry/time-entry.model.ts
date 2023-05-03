import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ITimeEntry {
  id: number;
  date?: dayjs.Dayjs | null;
  minutesWorked?: number | null;
  taskName?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewTimeEntry = Omit<ITimeEntry, 'id'> & { id: null };
