import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ArtistsService } from '../../artists.service';
import type { Artist, Album } from '../../model/artist.model';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { AlbumsService } from '../../../albums/albums.service';
import { AlbumFormComponent } from '../../../albums/album-form/album-form.component';

@Component({
  selector: 'app-artist-detail',
  standalone: true,
  imports: [CommonModule, AlbumFormComponent],
  templateUrl: './artist-detail.component.html',
  styleUrl: './artist-detail.component.css'
})
export class ArtistDetailComponent implements OnInit {
  private artistsService = inject(ArtistsService);
  private albumsService = inject(AlbumsService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  artist: Artist | null = null;
  loading = false;
  error: string | null = null;
  showAlbumModal = false;
  selectedAlbum?: Album;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadArtist(id);
    }
  }

  loadArtist(id: string): void {
    this.loading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.artistsService.getArtistById(id)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (artist) => {
          this.artist = artist;
        },
        error: (err) => {
          this.error = `Erro ao carregar artista: ${err.message || 'Não foi possível carregar os dados do artista.'}`;
        }
      });
  }

  editArtist(): void {
    if (this.artist) {
      this.router.navigate(['/artists', this.artist.id, 'edit']);
    }
  }

  deleteArtist(): void {
    if (!this.artist || !this.artist.id) return;

    if (confirm(`Tem certeza que deseja deletar o artista "${this.artist.name}"?`)) {
      this.artistsService.deleteArtist(this.artist.id).subscribe({
        next: () => {
          this.router.navigate(['/artists']);
        },
        error: (err) => {
          alert('Não foi possível deletar o artista. Tente novamente.');
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/artists']);
  }

  logout(): void {
    this.authService.logout();
  }

  openAddAlbumModal(): void {
    this.selectedAlbum = undefined;
    this.showAlbumModal = true;
    // Forçar detecção de mudanças
    this.cdr.detectChanges();
  }

  openEditAlbumModal(album: Album): void {
    this.selectedAlbum = album;
    this.showAlbumModal = true;
  }

  closeAlbumModal(): void {
    this.showAlbumModal = false;
    this.selectedAlbum = undefined;
  }

  onAlbumSaved(album: Album): void {
    // Recarrega os dados do artista para atualizar a lista de álbuns
    if (this.artist?.id) {
      this.loadArtist(this.artist.id);
    }
  }

  deleteAlbum(album: Album): void {
    if (!album.id) return;

    if (confirm(`Tem certeza que deseja deletar o álbum "${album.title}"?`)) {
      this.albumsService.deleteAlbum(album.id).subscribe({
        next: () => {
          // Recarrega os dados do artista
          if (this.artist?.id) {
            this.loadArtist(this.artist.id);
          }
        },
        error: (err) => {
          alert('Não foi possível deletar o álbum. Tente novamente.');
        }
      });
    }
  }
}
