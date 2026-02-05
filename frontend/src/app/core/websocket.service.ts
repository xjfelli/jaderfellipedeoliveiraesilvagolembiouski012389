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
    if (isPlatformBrowser(this.platformId)) {
      this.connect();
    }
  }

  private connect(): void {
    // Importação dinâmica do SockJS apenas no browser
    import('sockjs-client').then((SockJS) => {
      this.client = new Client({
        webSocketFactory: () => new SockJS.default('http://localhost/ws'),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          this.subscribeToAlbums();
        },
        onStompError: (frame: any) => {
          // Erro no WebSocket - pode ser tratado silenciosamente
        }
      });

      this.client.activate();
    });
  }

  private subscribeToAlbums(): void {
    if (!this.client) return;

    this.client.subscribe('/topic/albums', (message: IMessage) => {
      const notification: AlbumNotification = JSON.parse(message.body);
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
