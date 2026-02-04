import { Injectable, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ArtistsService } from './artists.service';
import type { Artist } from './model/artist.model';

@Injectable()
export class ArtistsFacade {
  private fb = inject(FormBuilder);
  // private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private artistsService = inject(ArtistsService);

  artists = signal<Artist[]>([]);
  loading = signal(false);
  isOpenCreateArtistsModal = signal(false);
  isOpenDeleteModal = signal(false);
  artistToDelete = signal<string | undefined>(undefined);

  // Controles de busca e ordenação
  searchTerm = signal('');
  sortOrder = signal<'asc' | 'desc'>('asc');

  // Controles de paginação
  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  totalElements = signal(0);

  toggleVisibilityCreateArtistsModal(): void {
    this.isOpenCreateArtistsModal.update((value) => !value);
  }

  closeCreateArtistsModal(): void {
    this.isOpenCreateArtistsModal.set(false);
  }

  isLoading(): boolean {
    return this.loading();
  }

  setSearchTerm(term: string): void {
    this.searchTerm.set(term);
    this.currentPage.set(0); // Reset para primeira página ao buscar
    this.loadArtists();
  }

  toggleSortOrder(): void {
    this.sortOrder.update(order => order === 'asc' ? 'desc' : 'asc');
    this.currentPage.set(0); // Reset para primeira página ao ordenar
    this.loadArtists();
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(page => page + 1);
      this.loadArtists();
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.loadArtists();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadArtists();
    }
  }

  changePageSize(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(0); // Reset para primeira página ao mudar tamanho
    this.loadArtists();
  }

  loadArtists(): void {
    this.loading.set(true);

    // Se tiver busca ativa, usa a API antiga sem paginação
    const search = this.searchTerm().toLowerCase();
    if (search) {
      this.artistsService.findAll().subscribe({
        next: (artists) => {
          let filteredArtists = artists.filter(artist =>
            artist.name.toLowerCase().includes(search)
          );

          // Aplicar ordenação
          filteredArtists.sort((a, b) => {
            const comparison = a.name.localeCompare(b.name);
            return this.sortOrder() === 'asc' ? comparison : -comparison;
          });

          this.artists.set(filteredArtists);
          this.totalElements.set(filteredArtists.length);
          this.totalPages.set(1);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Erro ao carregar artistas:', error);
          this.loading.set(false);
        },
      });
    } else {
      // Usa paginação quando não há busca
      this.artistsService.findAllPaginated(
        this.currentPage(),
        this.pageSize(),
        'name',
        this.sortOrder()
      ).subscribe({
        next: (response) => {
          this.artists.set(response.content);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Erro ao carregar artistas:', error);
          this.loading.set(false);
        },
      });
    }
  }

  openDeleteModal(artistId: string | undefined): void {
    if (artistId) {
      this.artistToDelete.set(artistId);
      this.isOpenDeleteModal.set(true);
    }
  }

  closeDeleteModal(): void {
    this.isOpenDeleteModal.set(false);
    this.artistToDelete.set(undefined);
  }

  confirmDeleteArtist(): void {
    const artistId = this.artistToDelete();
    if (!artistId) return;

    this.loading.set(true);
    this.artistsService.deleteArtist(artistId).subscribe({
      next: () => {
        this.closeDeleteModal();
        this.loadArtists();
      },
      error: (error) => {
        console.error('Erro ao excluir artista:', error);
        alert('Erro ao excluir artista. Tente novamente.');
        this.loading.set(false);
      },
    });
  }
}
