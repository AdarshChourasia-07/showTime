export type SeatCategory = 'REGULAR' | 'PREMIUM' | 'RECLINER';

export interface City {
  id: number;
  code: string;
  name: string;
}

export interface ShowSummary {
  id: number;
  startsAt: string;
  endsAt: string;
  contentId: number;
  title: string;
  language: string;
  venueId: number;
  venueName: string;
  screenName: string;
  cityCode: string;
  priceFrom: number;
}

export interface Content {
  id: number;
  type: 'MOVIE' | 'CONCERT' | 'EVENT';
  title: string;
  language: string;
  genre: string | null;
  durationMinutes: number;
  certificate: string | null;
  description: string | null;
  posterUrl: string | null;
}

export interface Venue {
  id: number;
  name: string;
  address: string;
  cityCode: string;
  cityName: string;
}

export interface ShowDetail {
  id: number;
  status: 'SCHEDULED' | 'CANCELLED';
  startsAt: string;
  endsAt: string;
  content: Content;
  venue: Venue;
  screenId: number;
  screenName: string;
  totalSeats: number;
  prices: Partial<Record<SeatCategory, number>>;
}

export interface Seat {
  id: number;
  row: string;
  number: number;
  label: string;
  category: SeatCategory;
}

export interface SeatLayout {
  screenId: number;
  screenName: string;
  totalSeats: number;
  seats: Seat[];
}
