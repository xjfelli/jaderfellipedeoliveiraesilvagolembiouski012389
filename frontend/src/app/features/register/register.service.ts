import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { RegisterData } from './model';
import { AuthResponse } from '../../core/auth/models/auth.model';

/**
 * Service responsável pelas operações de registro de usuários
 */
@Injectable()
export class RegisterService {
  private http = inject(HttpClient);
  private apiUrl = '/api/v1';

  /**
   * Registra um novo usuário no sistema
   * @param data Dados do usuário a ser registrado
   * @returns Observable com a resposta de autenticação
   */
  register(data: RegisterData): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/usuarios`, data).pipe(
      catchError((error) => {
        throw error;
      }),
    );
  }

  /**
   * Verifica se um username já está em uso
   * @param username Nome de usuário a ser verificado
   * @returns Observable com boolean indicando disponibilidade
   */
  checkUsernameAvailability(username: string): Observable<{ available: boolean }> {
    return this.http.get<{ available: boolean }>(`${this.apiUrl}/auth/check-username/${username}`);
  }

  /**
   * Verifica se um email já está em uso
   * @param email Email a ser verificado
   * @returns Observable com boolean indicando disponibilidade
   */
  checkEmailAvailability(email: string): Observable<{ available: boolean }> {
    return this.http.get<{ available: boolean }>(`${this.apiUrl}/auth/check-email/${email}`);
  }
}
