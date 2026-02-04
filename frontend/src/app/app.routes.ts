import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/artists',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
    canActivate: [loginGuard],
  },
  {
    path: 'artists',
    loadComponent: () =>
      import('./features/artists/artists.component').then((m) => m.ArtistsComponent),
    canActivate: [authGuard],
  },
  {
    path: 'albums',
    loadComponent: () =>
      import('./features/albums/albums.component').then((m) => m.AlbumsComponent),
    canActivate: [authGuard],
  },
  {
    path: '**',
    redirectTo: '/artists',
  },
];
