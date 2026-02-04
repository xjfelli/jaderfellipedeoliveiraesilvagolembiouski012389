import { Component, inject } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/auth/services/auth.service';
import { WebSocketService } from './core/websocket.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  authService = inject(AuthService);
  router = inject(Router);
  private webSocketService = inject(WebSocketService); // Inicializa o WebSocket

  currentUser$ = this.authService.currentUser$;
  isLoginPage = false;

  constructor() {
    console.log('🚀 App Component: Constructor chamado');
    console.log('🚀 WebSocketService injetado:', this.webSocketService);
    
    // Inicializa isLoginPage com a rota atual
    this.isLoginPage = this.router.url === '/login';

    // Monitora mudanças de rota
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.isLoginPage = event.url === '/login';
      });
  }

  /**
   * Verifica se deve mostrar a navbar
   */
  get shouldShowNavbar(): boolean {
    return !this.isLoginPage && this.authService.isAuthenticatedSync();
  }

  logout(): void {
    this.authService.logout();
  }

  navigateToArtists(): void {
    this.router.navigate(['/artists']);
  }

  navigateToAlbums(): void {
    this.router.navigate(['/albums']);
  }
}
