import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
    canActivate: [loginGuard],
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/register/register.component').then((m) => m.RegisterComponent),
    canActivate: [loginGuard],
  },
  {
    path: 'artists',
    loadComponent: () =>
      import('./features/artists/artists.component').then((m) => m.ArtistsComponent),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: 'artists/new',
    loadComponent: () =>
      import('./features/artists/subcomponents/artist-edit/artist-edit.component').then(
        (m) => m.ArtistEditComponent,
      ),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: 'artists/:id',
    loadComponent: () =>
      import('./features/artists/subcomponents/artist-detail/artist-detail.component').then(
        (m) => m.ArtistDetailComponent,
      ),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: 'artists/:id/edit',
    loadComponent: () =>
      import('./features/artists/subcomponents/artist-edit/artist-edit.component').then(
        (m) => m.ArtistEditComponent,
      ),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: 'albums',
    loadComponent: () =>
      import('./features/albums/albums.component').then((m) => m.AlbumsComponent),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: 'albums/new',
    loadComponent: () =>
      import('./features/albums/subcomponents/album-edit/album-edit.component').then(
        (m) => m.AlbumEditComponent,
      ),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: 'albums/:id/edit',
    loadComponent: () =>
      import('./features/albums/subcomponents/album-edit/album-edit.component').then(
        (m) => m.AlbumEditComponent,
      ),
    canActivate: [authGuard],
    data: { prerender: false }
  },
  {
    path: '**',
    redirectTo: '/login',
  },
];
