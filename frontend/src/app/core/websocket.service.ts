import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

export interface AlbumNotification {
  action: 'CREATE' | 'UPDATE' | 'DELETE';
  id: number;
  title: string;
  releaseYear: number;
  coverUrl: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client | null = null;
  private albumNotifications$ = new BehaviorSubject<AlbumNotification | null>(null);

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    console.log('🔌 WebSocketService: Constructor chamado');
    console.log('🔌 Platform ID:', this.platformId);
    console.log('🔌 Is Browser?', isPlatformBrowser(this.platformId));
    
    if (isPlatformBrowser(this.platformId)) {
      console.log('🔌 Iniciando conexão WebSocket...');
      this.connect();
    } else {
      console.log('🔌 Não é browser, WebSocket não será iniciado');
    }
  }

  private connect(): void {
    console.log('🔌 Método connect() chamado');
    // Importação dinâmica do SockJS apenas no browser
    import('sockjs-client').then((SockJS) => {
      console.log('🔌 SockJS importado com sucesso');
      this.client = new Client({
        webSocketFactory: () => new SockJS.default('http://localhost/ws'),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: (str) => {
          console.log('STOMP: ' + str);
        },
        onConnect: () => {
          console.log('✅ WebSocket conectado!');
          this.subscribeToAlbums();
        },
        onStompError: (frame) => {
          console.error('❌ Erro STOMP: ' + frame.headers['message']);
          console.error('Detalhes: ' + frame.body);
        }
      });

      console.log('🔌 Ativando cliente STOMP...');
      this.client.activate();
    }).catch((err) => {
      console.error('❌ Erro ao importar SockJS:', err);
    });
  }

  private subscribeToAlbums(): void {
    if (!this.client) return;

    this.client.subscribe('/topic/albums', (message: IMessage) => {
      const notification: AlbumNotification = JSON.parse(message.body);
      console.log(`🎵 Notificação de álbum (${notification.action}):`, notification);
      this.albumNotifications$.next(notification);
    });
  }

  public getAlbumNotifications(): Observable<AlbumNotification | null> {
    return this.albumNotifications$.asObservable();
  }

  public disconnect(): void {
    if (this.client) {
      this.client.deactivate();
    }
  }
}
