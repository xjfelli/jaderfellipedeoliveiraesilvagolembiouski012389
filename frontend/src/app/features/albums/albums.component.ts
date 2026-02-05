import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/services/auth.service';
import { DeleteConfirmationModalComponent } from './delete-confirmation-modal/delete-confirmation-modal.component';
import { AlbumsFacade } from './albums.facade';
import type { Album } from './model/album.model';

@Component({
  selector: 'app-albums',
  standalone: true,
  imports: [CommonModule, FormsModule, DeleteConfirmationModalComponent],
  providers: [AlbumsFacade],
  templateUrl: './albums.component.html',
  styleUrl: './albums.component.css'
})
export class AlbumsComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  currentUser$ = this.authService.currentUser$;
  public albums;
  Math = Math;

  constructor(public facade: AlbumsFacade) {
    this.albums = this.facade.albums;
  }

  ngOnInit(): void {
    this.facade.loadAlbums();
  }

  logout(): void {
    this.authService.logout();
  }

  navigateToArtists(): void {
    this.router.navigate(['/artists']);
  }

  get isLoading(): boolean {
    return this.facade.isLoading();
  }

  viewAlbum(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/albums', id]);
    }
  }

  toggleVisibilityAlbumModal(): void {
    this.router.navigate(['/albums/new']);
  }

  editAlbum(id: number | undefined): void {
    this.facade.editAlbum(id);
  }

  deleteAlbum(id: number | undefined): void {
    this.facade.openDeleteModal(id);
  }

  get isOpenDeleteModal(): boolean {
    return this.facade.isOpenDeleteModal();
  }

  closeDeleteModal(): void {
    this.facade.closeDeleteModal();
  }

  confirmDelete(): void {
    this.facade.confirmDeleteAlbum();
  }

  get albumToDeleteTitle(): string {
    const albumId = this.facade.albumToDelete();
    if (!albumId) return '';
    const album = this.albums().find(a => a.id === albumId);
    return album?.title || '';
  }

  viewArtist(artistId: number | undefined): void {
    this.facade.viewArtist(artistId);
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
      for (let i = 0; i < total; i++) {
        pages.push(i);
      }
    } else {
      pages.push(0);

      if (current > 2) {
        pages.push(-1);
      }

      const start = Math.max(1, current - 1);
      const end = Math.min(total - 2, current + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (current < total - 3) {
        pages.push(-1);
      }

      pages.push(total - 1);
    }

    return pages;
  }
}
