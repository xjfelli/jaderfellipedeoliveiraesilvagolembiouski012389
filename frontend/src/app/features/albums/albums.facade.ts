import { Injectable, inject, signal, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AlbumsService } from './albums.service';
import { WebSocketService } from '../../core/websocket.service';
import type { Album } from './model/album.model';

@Injectable()
export class AlbumsFacade {
  private router = inject(Router);
  private albumsService = inject(AlbumsService);
  private webSocketService = inject(WebSocketService);
  private platformId = inject(PLATFORM_ID);

  albums = signal<Album[]>([]);
  loading = signal(false);
  isOpenDeleteModal = signal(false);
  albumToDelete = signal<number | undefined>(undefined);

  // Controles de busca e ordenação
  searchTerm = signal('');
  sortOrder = signal<'asc' | 'desc'>('desc');

  // Controles de paginação
  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  totalElements = signal(0);

  constructor() {
    // Subscrever às notificações de novos álbuns apenas no browser
    if (isPlatformBrowser(this.platformId)) {
      this.webSocketService.getAlbumNotifications().subscribe(notification => {
        if (notification) {
          console.log(`📢 Álbum ${notification.action}: ${notification.title}`);
          // Recarregar a lista de álbuns quando receber notificação
          this.loadAlbums();
        }
      });
    }
  }

  isLoading(): boolean {
    return this.loading();
  }

  setSearchTerm(term: string): void {
    this.searchTerm.set(term);
    this.currentPage.set(0);
    this.loadAlbums();
  }

  toggleSortOrder(): void {
    this.sortOrder.update(order => order === 'asc' ? 'desc' : 'asc');
    this.currentPage.set(0);
    this.loadAlbums();
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(page => page + 1);
      this.loadAlbums();
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.loadAlbums();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadAlbums();
    }
  }

  changePageSize(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(0);
    this.loadAlbums();
  }

  loadAlbums(): void {
    this.loading.set(true);

    const search = this.searchTerm().toLowerCase();
    if (search) {
      // Busca com filtro local
      this.albumsService.findAll().subscribe({
        next: (albums) => {
          let filteredAlbums = albums.filter(album =>
            album.title.toLowerCase().includes(search) ||
            album.artists?.some(artist => artist.name.toLowerCase().includes(search))
          );

          // Aplicar ordenação
          filteredAlbums.sort((a, b) => {
            const comparison = a.title.localeCompare(b.title);
            return this.sortOrder() === 'asc' ? comparison : -comparison;
          });

          this.albums.set(filteredAlbums);
          this.totalElements.set(filteredAlbums.length);
          this.totalPages.set(1);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Erro ao carregar álbuns:', error);
          this.loading.set(false);
        },
      });
    } else {
      // Paginação backend - ordena por ID (mais recentes primeiro quando desc)
      this.albumsService.findAllPaginated(
        this.currentPage(),
        this.pageSize(),
        'id',
        this.sortOrder()
      ).subscribe({
        next: (response) => {
          this.albums.set(response.content);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Erro ao carregar álbuns:', error);
          this.loading.set(false);
        },
      });
    }
  }

  viewArtist(artistId: number | undefined): void {
    if (artistId) {
      this.router.navigate(['/artists', artistId]);
    }
  }

  editAlbum(albumId: number | undefined): void {
    if (albumId) {
      this.router.navigate(['/albums', albumId, 'edit']);
    }
  }

  openDeleteModal(albumId: number | undefined): void {
    if (albumId) {
      this.albumToDelete.set(albumId);
      this.isOpenDeleteModal.set(true);
    }
  }

  closeDeleteModal(): void {
    this.isOpenDeleteModal.set(false);
    this.albumToDelete.set(undefined);
  }

  confirmDeleteAlbum(): void {
    const albumId = this.albumToDelete();
    if (!albumId) return;

    this.loading.set(true);
    this.albumsService.deleteAlbum(albumId).subscribe({
      next: () => {
        this.closeDeleteModal();
        this.loadAlbums();
      },
      error: (error) => {
        console.error('Erro ao excluir álbum:', error);
        alert('Erro ao excluir álbum. Tente novamente.');
        this.loading.set(false);
      },
    });
  }
}
