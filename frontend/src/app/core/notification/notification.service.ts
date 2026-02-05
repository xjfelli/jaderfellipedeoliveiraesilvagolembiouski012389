import { Injectable, signal } from '@angular/core';

export interface Notification {
  message: string;
  type: 'error' | 'success' | 'warning' | 'info';
  duration?: number;
}

/**
 * Serviço para exibir notificações ao usuário
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notificationSignal = signal<Notification | null>(null);

  notification = this.notificationSignal.asReadonly();

  /**
   * Mostra uma notificação de erro
   */
  error(message: string, duration = 5000): void {
    this.show({ message, type: 'error', duration });
  }

  /**
   * Mostra uma notificação de sucesso
   */
  success(message: string, duration = 3000): void {
    this.show({ message, type: 'success', duration });
  }

  /**
   * Mostra uma notificação de aviso
   */
  warning(message: string, duration = 4000): void {
    this.show({ message, type: 'warning', duration });
  }

  /**
   * Mostra uma notificação de informação
   */
  info(message: string, duration = 3000): void {
    this.show({ message, type: 'info', duration });
  }

  /**
   * Mostra uma notificação
   */
  private show(notification: Notification): void {
    this.notificationSignal.set(notification);

    if (notification.duration && notification.duration > 0) {
      setTimeout(() => {
        this.clear();
      }, notification.duration);
    }
  }

  /**
   * Limpa a notificação atual
   */
  clear(): void {
    this.notificationSignal.set(null);
  }
}
