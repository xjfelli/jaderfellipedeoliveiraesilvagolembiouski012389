import { HttpInterceptorFn, HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs';
import { ApiResponse } from '../utils/api-response.model';

/**
 * Interceptor para processar respostas da API
 * Automaticamente extrai o campo 'data' das respostas no formato global { success: boolean, data: T }
 */
export const apiResponseInterceptor: HttpInterceptorFn = (req, next) => {
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
  );
};
