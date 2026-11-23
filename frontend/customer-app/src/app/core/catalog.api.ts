import { httpResource } from '@angular/common/http';
import { Signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { City, SeatLayout, ShowDetail, ShowSummary } from './catalog.models';

const base = `${environment.apiUrl}/catalog`;

/**
 * Signal-based data loading: each resource re-fetches automatically
 * when the signals it reads (city, date, id) change.
 */
export function citiesResource() {
  return httpResource<City[]>(() => `${base}/cities`, { defaultValue: [] });
}

export function showsResource(city: Signal<string>, date: Signal<string>) {
  return httpResource<ShowSummary[]>(
    () => ({ url: `${base}/shows`, params: { city: city(), date: date() } }),
    { defaultValue: [] },
  );
}

export function showResource(id: Signal<number>) {
  return httpResource<ShowDetail>(() => `${base}/shows/${id()}`);
}

export function layoutResource(screenId: Signal<number | undefined>) {
  return httpResource<SeatLayout>(() => {
    const id = screenId();
    return id ? `${base}/screens/${id}/layout` : undefined;
  });
}
