import { HttpInterceptorFn, HttpEvent, HttpEventType, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { map, catchError } from 'rxjs';
import { throwError } from 'rxjs';
import { inject } from '@angular/core';
import { ApiResponse } from '../utils/api-response.model';
import { NotificationService } from '../app/core/notification/notification.service';

/**
 * Interceptor para processar respostas da API
 * Automaticamente extrai o campo 'data' das respostas no formato global { success: boolean, data: T }
 */
export const apiResponseInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    map((event: HttpEvent<any>) => {
      // Verifica se é uma resposta HTTP
      if (event.type === HttpEventType.Response) {
        const response = event as HttpResponse<any>;
        const body = response.body;

        // Verifica se o corpo da resposta tem o formato ApiResponse
        if (body && typeof body === 'object' && 'success' in body && 'data' in body) {
          const apiResponse = body as ApiResponse<any>;

          if (apiResponse.success) {
            // Substitui o corpo da resposta pelo campo 'data'
            const newResponse = response.clone({ body: apiResponse.data });
            return newResponse;
          } else {
            // Se success for false, lança um erro
            throw new Error('API response indicates failure');
          }
        }
      }

      // Retorna o evento original se não for uma resposta ApiResponse
      return event;
    }),
    catchError((error: HttpErrorResponse) => {
      // Tratamento específico para erro de rate limiting
      if (error.error?.message) {
        const message = error.error.message;
        
        // Verifica se é erro de rate limiting
        if (message.toLowerCase().includes('requisições por minuto excedido') || 
            message.toLowerCase().includes('rate limit')) {
          notificationService.warning(
            '⏱️ Você está fazendo muitas requisições. Por favor, aguarde um momento antes de tentar novamente.',
            6000
          );
        } else {
          // Outros erros da API
          notificationService.error(message);
        }
      } else if (error.status === 429) {
        // Status HTTP 429 (Too Many Requests)
        notificationService.warning(
          '⏱️ Limite de requisições excedido. Por favor, aguarde um momento.',
          6000
        );
      } else if (error.status >= 500) {
        // Erros do servidor
        notificationService.error('Erro no servidor. Tente novamente mais tarde.');
      } else if (error.status === 0) {
        // Erro de rede
        notificationService.error('Erro de conexão. Verifique sua internet.');
      }

      return throwError(() => error);
    })
  );
};
