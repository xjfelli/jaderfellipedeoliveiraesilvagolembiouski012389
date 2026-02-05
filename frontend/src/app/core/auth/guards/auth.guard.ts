import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard para proteger rotas que exigem autenticação
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // No servidor, permite acesso (o cliente vai verificar depois)
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const token = localStorage.getItem('auth_token');

  if (token) {
    return true;
  }

  // Redireciona para o login, salvando a URL original
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url },
  });

  return false;
};

/**
 * Guard para redirecionar usuários autenticados da página de login
 */
export const loginGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // No servidor, permite acesso à página de login
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const token = localStorage.getItem('auth_token');

  if (!token) {
    return true;
  }

  // Redireciona para a página principal se já estiver autenticado
  router.navigate(['/artists']);
  return false;
};
