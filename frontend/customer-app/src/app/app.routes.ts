import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/show-list/show-list').then((m) => m.ShowList) },
  {
    path: 'shows/:id',
    loadComponent: () => import('./pages/show-detail/show-detail').then((m) => m.ShowDetailPage),
  },
  { path: '**', redirectTo: '' },
];
