import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

// Controla se o refresh está em andamento
let isRefreshing = false;
// Subject para notificar quando o refresh terminar
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

/**
 * Interceptor para adicionar o token JWT nas requisições HTTP
 * e tratar renovação automática do token quando expirado
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // URLs que não precisam de autenticação
  const excludedUrls = ['/auth/login', '/auth/refresh', '/auth/register'];
  const shouldExclude = excludedUrls.some((url) => req.url.includes(url));

  if (shouldExclude) {
    return next(req);
  }

  // Adiciona o token se disponível
  const token = authService.getToken();
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Se receber erro 401 (não autorizado) ou 403 (proibido/token expirado), tenta renovar o token
      const isAuthError = error.status === 401 || error.status === 403;
      const isNotRefreshRequest = !req.url.includes('/auth/refresh');

      if (isAuthError && isNotRefreshRequest) {
        // Se já está fazendo refresh, aguarda terminar e usa o novo token
        if (isRefreshing) {
          return refreshTokenSubject.pipe(
            filter((token) => token !== null),
            take(1),
            switchMap((token) => {
              const clonedReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${token}`,
                },
              });
              return next(clonedReq);
            }),
          );
        }

        // Inicia o processo de refresh
        isRefreshing = true;
        refreshTokenSubject.next(null);

        return authService.refreshTokenSilent().pipe(
          switchMap((response) => {
            isRefreshing = false;
            const newToken = response.accessToken;
            refreshTokenSubject.next(newToken);

            // Refaz a requisição original com o novo token
            const clonedReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`,
              },
            });
            return next(clonedReq);
          }),
          catchError((refreshError) => {
            isRefreshing = false;
            refreshTokenSubject.next(null);
            // Só faz logout se o refresh realmente falhou
            authService.logout();
            return throwError(() => refreshError);
          }),
        );
      }

      return throwError(() => error);
    }),
  );
};
