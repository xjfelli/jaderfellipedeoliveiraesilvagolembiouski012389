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
    console.log('[authGuard] SSR - permitindo acesso');
    return true;
  }

  const token = localStorage.getItem('auth_token');
  console.log('[authGuard] Token encontrado:', !!token);

  if (token) {
    console.log('[authGuard] Usuário autenticado, permitindo acesso');
    return true;
  }

  console.log('[authGuard] Usuário não autenticado, redirecionando para login');
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
    console.log('[loginGuard] SSR - permitindo acesso ao login');
    return true;
  }

  const token = localStorage.getItem('auth_token');
  console.log('[loginGuard] Token encontrado:', !!token);

  if (!token) {
    console.log('[loginGuard] Sem token, permitindo acesso ao login');
    return true;
  }

  console.log('[loginGuard] Usuário já autenticado, redirecionando para artists');
  // Redireciona para a página principal se já estiver autenticado
  router.navigate(['/artists']);
  return false;
};
