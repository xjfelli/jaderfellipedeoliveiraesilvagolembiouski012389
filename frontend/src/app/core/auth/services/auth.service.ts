import { Injectable, inject, signal, PLATFORM_ID, afterNextRender } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, BehaviorSubject, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthResponse, User } from '../models/auth.model';

export interface LoginCredentials {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  private isBrowser: boolean;

  private apiUrl = '/api/v1';

  private tokenKey = 'auth_token';
  private refreshTokenKey = 'refresh_token';
  private tokenExpirationKey = 'token_expiration';

  private initialAuthState = false;

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  private tokenRefreshTimer: any;

  isAuthenticated = signal(this.initialAuthState);

  /** Indica se a hidratação do cliente foi concluída */
  isHydrated = signal(false);

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);

    // Verifica se há token válido ao iniciar (apenas no browser)
    if (this.isBrowser) {
      const token = this.getToken();
      if (token) {
        this.isAuthenticated.set(true);
        this.scheduleTokenRefresh();

        // Carrega informações do usuário do token
        const user = this.decodeToken(token);
        this.currentUserSubject.next(user);
      }
      // Marca como hidratado imediatamente no browser
      this.isHydrated.set(true);
    }

    // Callback após a hidratação do Angular
    afterNextRender(() => {
      this.isHydrated.set(true);
    });
  }

  /**
   * Realiza o login do usuário
   */
  login(credentials: LoginCredentials): Observable<AuthResponse> {
    console.log('Iniciando login com credenciais:', credentials);
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap((response) => this.handleAuthResponse(response)),
      catchError((error) => {
        console.error('Erro no login:', error);
        return throwError(() => error);
      }),
    );
  }

  /**
   * Realiza o logout do usuário
   */
  logout(): void {
    this.clearTokens();
    this.isAuthenticated.set(false);
    this.currentUserSubject.next(null);

    if (this.tokenRefreshTimer) {
      clearTimeout(this.tokenRefreshTimer);
    }

    this.router.navigate(['/login']);
  }

  /**
   * Renova o token usando o refresh token
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();

    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('Refresh token não encontrado'));
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/refresh`, { refreshToken }).pipe(
      tap((response) => this.handleAuthResponse(response)),
      catchError((error) => {
        console.error('Erro ao renovar token:', error);
        this.logout();
        return throwError(() => error);
      }),
    );
  }

  /**
   * Renova o token silenciosamente (sem fazer logout automático em caso de erro)
   * Usado pelo interceptor para controle manual do fluxo
   */
  refreshTokenSilent(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();

    if (!refreshToken) {
      return throwError(() => new Error('Refresh token não encontrado'));
    }

    return this.http
      .post<AuthResponse>(`${this.apiUrl}/auth/refresh`, { refreshToken })
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  /**
   * Obtém o token de acesso
   */
  getToken(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Obtém o refresh token
   */
  getRefreshToken(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem(this.refreshTokenKey);
  }

  /**
   * Verifica se o token ainda é válido
   */
  hasValidToken(): boolean {
    if (!this.isBrowser) return false;

    const token = this.getToken();
    const expiration = localStorage.getItem(this.tokenExpirationKey);

    if (!token || !expiration) {
      return false;
    }

    const expirationDate = new Date(expiration);
    const now = new Date();

    return expirationDate > now;
  }

  /**
   * Verifica sincronamente se está autenticado (para uso nos guards)
   * Verifica diretamente o localStorage sem depender do signal
   */
  isAuthenticatedSync(): boolean {
    if (!this.isBrowser) return false;
    return !!this.getToken();
  }

  /**
   * Processa a resposta de autenticação
   */
  public handleAuthResponse(response: AuthResponse): void {
    if (!this.isBrowser) return;

    // Armazena o token
    localStorage.setItem(this.tokenKey, response.accessToken);

    // Armazena o refresh token se disponível
    if (response.refreshToken) {
      localStorage.setItem(this.refreshTokenKey, response.refreshToken);
    }

    // Calcula e armazena a data de expiração
    const expirationDate = this.calculateExpirationDate(parseInt(response.expiresIn));
    localStorage.setItem(this.tokenExpirationKey, expirationDate.toISOString());

    // Atualiza o estado de autenticação
    this.isAuthenticated.set(true);

    // Decodifica o token para obter informações do usuário
    const user = this.decodeToken(response.accessToken);
    this.currentUserSubject.next(user);

    // Agenda a renovação do token
    this.scheduleTokenRefresh();
  }

  /**
   * Calcula a data de expiração do token
   */
  private calculateExpirationDate(expiresIn?: number): Date {
    // Se não informado, assume 1 hora (3600 segundos)
    const seconds = expiresIn || 3600;
    const now = new Date();
    return new Date(now.getTime() + seconds * 1000);
  }

  /**
   * Decodifica o token JWT para extrair informações do usuário
   */
  private decodeToken(token: string): User | null {
    if (!this.isBrowser) return null;

    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));

      return {
        id: parseInt(decoded.sub || decoded.id, 10).toString(),
        username: decoded.username || decoded.name,
        email: decoded.email,
        fullname: decoded.fullname || decoded.name,
      };
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  /**
   * Agenda a renovação automática do token
   */
  private scheduleTokenRefresh(): void {
    if (!this.isBrowser) return;

    if (this.tokenRefreshTimer) {
      clearTimeout(this.tokenRefreshTimer);
    }

    const expiration = localStorage.getItem(this.tokenExpirationKey);
    if (!expiration) return;

    const expirationDate = new Date(expiration);
    const now = new Date();

    // Renova 5 minutos antes de expirar
    const refreshTime = expirationDate.getTime() - now.getTime() - 5 * 60 * 1000;

    if (refreshTime > 0) {
      this.tokenRefreshTimer = setTimeout(() => {
        this.refreshToken().subscribe();
      }, refreshTime);
    }
  }

  /**
   * Limpa todos os tokens do localStorage
   */
  private clearTokens(): void {
    if (!this.isBrowser) return;

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.tokenExpirationKey);
  }
}
