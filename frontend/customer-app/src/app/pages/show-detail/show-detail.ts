import { DatePipe, DecimalPipe, LowerCasePipe } from '@angular/common';
import { Component, computed, input, numberAttribute, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RouterLink } from '@angular/router';
import { layoutResource, showResource } from '../../core/catalog.api';
import { Seat, SeatCategory } from '../../core/catalog.models';

interface SeatRow {
  label: string;
  category: SeatCategory;
  seats: Seat[];
}

const MAX_SEATS = 10;

const CATEGORY_NAMES: Record<SeatCategory, string> = {
  REGULAR: 'Regular',
  PREMIUM: 'Premium',
  RECLINER: 'Recliner',
};

@Component({
  selector: 'app-show-detail',
  imports: [DatePipe, DecimalPipe, LowerCasePipe, RouterLink, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: './show-detail.html',
  styleUrl: './show-detail.scss',
})
export class ShowDetailPage {
  /** Bound from the route /shows/:id (withComponentInputBinding). */
  readonly id = input.required({ transform: numberAttribute });

  protected readonly show = showResource(this.id);
  protected readonly layout = layoutResource(computed(() => this.show.value()?.screenId));
  protected readonly selected = signal<ReadonlySet<number>>(new Set());
  protected readonly maxSeats = MAX_SEATS;

  /** Price legend for the seat categories this show actually has. */
  protected readonly legend = computed(() => {
    const prices = this.show.value()?.prices ?? {};
    return (Object.keys(CATEGORY_NAMES) as SeatCategory[])
      .filter((category) => prices[category] !== undefined)
      .map((category) => ({ category, name: CATEGORY_NAMES[category], price: prices[category]! }));
  });

  /** Seats grouped by row, back rows (premium/recliner) last, like the real hall. */
  protected readonly rows = computed<SeatRow[]>(() => {
    const byRow = new Map<string, SeatRow>();
    for (const seat of this.layout.value()?.seats ?? []) {
      let row = byRow.get(seat.row);
      if (!row) {
        row = { label: seat.row, category: seat.category, seats: [] };
        byRow.set(seat.row, row);
      }
      row.seats.push(seat);
    }
    return [...byRow.values()];
  });

  protected readonly selectedSeats = computed(() =>
    (this.layout.value()?.seats ?? []).filter((s) => this.selected().has(s.id)),
  );

  protected readonly total = computed(() => {
    const prices = this.show.value()?.prices ?? {};
    return this.selectedSeats().reduce((sum, s) => sum + (prices[s.category] ?? 0), 0);
  });

  protected toggle(seat: Seat): void {
    this.selected.update((current) => {
      const next = new Set(current);
      if (next.has(seat.id)) {
        next.delete(seat.id);
      } else if (next.size < MAX_SEATS) {
        next.add(seat.id);
      }
      return next;
    });
  }

  protected seatLabels(): string {
    return this.selectedSeats().map((s) => s.label).join(', ');
  }
}
