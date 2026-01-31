import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/artists',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent),
    canActivate: [loginGuard],
  },
  {
    path: 'artists',
    loadComponent: () =>
      import('./pages/artists/artists.component').then((m) => m.ArtistsComponent),
    canActivate: [authGuard],
  },
  {
    path: 'albums',
    loadComponent: () => import('./pages/albums/albums.component').then((m) => m.AlbumsComponent),
    canActivate: [authGuard],
  },
  {
    path: '**',
    redirectTo: '/artists',
  },
];
