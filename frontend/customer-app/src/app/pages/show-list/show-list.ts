import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { citiesResource, showsResource } from '../../core/catalog.api';
import { ShowSummary } from '../../core/catalog.models';

interface ContentGroup {
  contentId: number;
  title: string;
  language: string;
  venues: { venueName: string; shows: ShowSummary[] }[];
}

/** Local date as YYYY-MM-DD (not toISOString, which converts to UTC and can shift the day). */
function isoDay(d: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

@Component({
  selector: 'app-show-list',
  imports: [
    DatePipe, DecimalPipe, RouterLink,
    MatButtonModule, MatCardModule, MatChipsModule, MatFormFieldModule, MatProgressBarModule, MatSelectModule,
  ],
  templateUrl: './show-list.html',
  styleUrl: './show-list.scss',
})
export class ShowList {
  protected readonly city = signal(environment.defaultCity);
  protected readonly days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date();
    d.setDate(d.getDate() + i);
    return { iso: isoDay(d), date: d };
  });
  protected readonly date = signal(this.days[0].iso);

  protected readonly cities = citiesResource();
  protected readonly shows = showsResource(this.city, this.date);

  /** Shows grouped as: movie -> venue -> showtimes, the way ticketing apps list them. */
  protected readonly groups = computed<ContentGroup[]>(() => {
    const byContent = new Map<number, ContentGroup>();
    for (const s of this.shows.value()) {
      let group = byContent.get(s.contentId);
      if (!group) {
        group = { contentId: s.contentId, title: s.title, language: s.language, venues: [] };
        byContent.set(s.contentId, group);
      }
      let venue = group.venues.find((v) => v.venueName === s.venueName);
      if (!venue) {
        venue = { venueName: s.venueName, shows: [] };
        group.venues.push(venue);
      }
      venue.shows.push(s);
    }
    return [...byContent.values()];
  });
}
