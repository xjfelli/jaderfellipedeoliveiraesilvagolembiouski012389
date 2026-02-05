import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/services/auth.service';
import { DeleteConfirmationModalComponent } from '../albums/delete-confirmation-modal/delete-confirmation-modal.component';
import { ArtistsFacade } from './artists.facade';
import { ArtistsCreateComponent } from './subcomponents/artists-create/artists-create.component';
import { ArtistsCreateFacade } from './subcomponents/artists-create/artists-create.facade';

@Component({
  selector: 'app-artists',
  standalone: true,
  imports: [CommonModule, FormsModule, ArtistsCreateComponent, DeleteConfirmationModalComponent],
  providers: [ArtistsFacade, ArtistsCreateFacade],
  templateUrl: './artists.component.html',
  styleUrl: './artists.component.css',
})
export class ArtistsComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  currentUser$ = this.authService.currentUser$;
  public artists;
  Math = Math;

  constructor(
    public facade: ArtistsFacade,
    public facadeCreate: ArtistsCreateFacade,
  ) {
    this.artists = this.facade.artists;
  }

  ngOnInit(): void {
    this.facade.loadArtists();
  }

  logout(): void {
    this.authService.logout();
  }

  navigateToAlbums(): void {
    this.router.navigate(['/albums']);
  }

  get isOpenCreateArtistsModal(): boolean {
    return this.facade.isOpenCreateArtistsModal();
  }

  toggleVisibilityCreateArtistsModal(): void {
    this.facade.toggleVisibilityCreateArtistsModal();
  }

  closeCreateArtistsModal(): void {
    this.facade.closeCreateArtistsModal();
    // Recarrega a lista de artistas após criar/editar
    this.facade.loadArtists();
  }

  get isLoading(): boolean {
    return this.facade.isLoading();
  }

  viewArtist(id: string | undefined): void {
    if (id) {
      this.router.navigate(['/artists', id]);
    }
  }

  editArtist(id: string | undefined): void {
    if (id) {
      this.router.navigate(['/artists', id, 'edit']);
    }
  }

  deleteArtist(id: string | undefined): void {
    this.facade.openDeleteModal(id);
  }

  get isOpenDeleteModal(): boolean {
    return this.facade.isOpenDeleteModal();
  }

  closeDeleteModal(): void {
    this.facade.closeDeleteModal();
  }

  confirmDelete(): void {
    this.facade.confirmDeleteArtist();
  }

  get artistToDeleteName(): string {
    const artistId = this.facade.artistToDelete();
    if (!artistId) return '';
    const artist = this.artists().find(a => a.id === artistId);
    return artist?.name || '';
  }

  onSearch(searchTerm: string): void {
    this.facade.setSearchTerm(searchTerm);
  }

  clearSearch(): void {
    this.facade.setSearchTerm('');
  }

  toggleSortOrder(): void {
    this.facade.toggleSortOrder();
  }

  get sortOrder(): 'asc' | 'desc' {
    return this.facade.sortOrder();
  }

  get searchTerm(): string {
    return this.facade.searchTerm();
  }

  set searchTerm(value: string) {
    this.facade.searchTerm.set(value);
  }

  // Métodos de paginação
  get currentPage(): number {
    return this.facade.currentPage();
  }

  get totalPages(): number {
    return this.facade.totalPages();
  }

  get totalElements(): number {
    return this.facade.totalElements();
  }

  get pageSize(): number {
    return this.facade.pageSize();
  }

  nextPage(): void {
    this.facade.nextPage();
  }

  previousPage(): void {
    this.facade.previousPage();
  }

  goToPage(page: number): void {
    this.facade.goToPage(page);
  }

  changePageSize(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = parseInt(target.value, 10);
    this.facade.changePageSize(size);
  }

  get pages(): number[] {
    const total = this.totalPages;
    const current = this.currentPage;
    const pages: number[] = [];

    if (total <= 7) {
      // Mostrar todas as páginas se houver 7 ou menos
      for (let i = 0; i < total; i++) {
        pages.push(i);
      }
    } else {
      // Sempre mostrar primeira página
      pages.push(0);

      if (current > 2) {
        pages.push(-1); // Separador (...)
      }

      // Mostrar páginas ao redor da atual
      const start = Math.max(1, current - 1);
      const end = Math.min(total - 2, current + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (current < total - 3) {
        pages.push(-1); // Separador (...)
      }

      // Sempre mostrar última página
      pages.push(total - 1);
    }

    return pages;
  }
}
