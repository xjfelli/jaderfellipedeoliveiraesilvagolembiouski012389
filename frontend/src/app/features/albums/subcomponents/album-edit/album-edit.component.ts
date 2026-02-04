import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AlbumsService } from '../../albums.service';
import { ArtistsService } from '../../../artists/artists.service';
import type { Album } from '../../model/album.model';
import type { Artist } from '../../../artists/model/artist.model';

@Component({
  selector: 'app-album-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './album-edit.component.html',
  styleUrl: './album-edit.component.css'
})
export class AlbumEditComponent implements OnInit {
  private fb = inject(FormBuilder);
  private albumsService = inject(AlbumsService);
  private artistsService = inject(ArtistsService);
  private cdr = inject(ChangeDetectorRef);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  albumForm: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  saving = false;
  loading = false;
  error: string | null = null;
  isEditMode = false;
  artists: Artist[] = [];
  loadingArtists = false;
  albumId: number | null = null;
  album: Album | null = null;

  constructor() {
    this.albumForm = this.fb.group({
      artistId: ['', Validators.required],
      title: ['', [Validators.required, Validators.minLength(1)]],
      releaseYear: [null, [Validators.min(1900), Validators.max(new Date().getFullYear() + 1)]],
      recordLabel: [''],
      trackCount: [null, [Validators.min(1)]],
      description: [''],
      file: [null]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (id && id !== 'new') {
      this.isEditMode = true;
      this.albumId = parseInt(id, 10);
      this.loadAlbum();
    }

    this.loadArtists();
  }

  loadAlbum(): void {
    if (!this.albumId) return;

    this.loading = true;
    this.albumsService.getAlbumById(this.albumId).subscribe({
      next: (album) => {
        this.album = album;
        this.albumForm.patchValue({
          title: album.title,
          releaseYear: album.releaseYear,
          recordLabel: album.recordLabel,
          trackCount: album.trackCount,
          description: album.description,
          artistId: album.artists && album.artists.length > 0 ? album.artists[0].id : ''
        });

        if (album.coverUrl) {
          this.imagePreview = album.coverUrl;
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar álbum:', err);
        this.error = 'Não foi possível carregar o álbum.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadArtists(): void {
    this.loadingArtists = true;
    this.cdr.detectChanges();

    this.artistsService.findAll()
      .pipe(
        finalize(() => {
          this.loadingArtists = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (artists) => {
          this.artists = artists;
        },
        error: (err) => {
          console.error('Erro ao carregar artistas:', err);
          this.error = 'Não foi possível carregar a lista de artistas.';
        }
      });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      if (!file.type.startsWith('image/')) {
        this.error = 'Por favor, selecione um arquivo de imagem válido.';
        this.cdr.detectChanges();
        return;
      }

      this.selectedFile = file;
      this.error = null;

      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
        this.cdr.detectChanges();
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.albumForm.invalid) {
      this.albumForm.markAllAsTouched();
      return;
    }

    if (!this.isEditMode && !this.selectedFile) {
      this.error = 'A capa do álbum é obrigatória.';
      this.cdr.detectChanges();
      return;
    }

    const formValues = this.albumForm.value;
    const selectedArtistId = formValues.artistId;

    if (!selectedArtistId) {
      this.error = 'Selecione um artista.';
      this.cdr.detectChanges();
      return;
    }

    this.saving = true;
    this.error = null;
    this.cdr.detectChanges();

    const albumData = {
      title: formValues.title,
      releaseYear: formValues.releaseYear || undefined,
      recordLabel: formValues.recordLabel || undefined,
      trackCount: formValues.trackCount || undefined,
      description: formValues.description || undefined
    };

    const request = this.isEditMode && this.albumId
      ? this.albumsService.updateAlbum(this.albumId, albumData)
      : this.albumsService.create(albumData, selectedArtistId, this.selectedFile!);

    request
      .pipe(
        finalize(() => {
          this.saving = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.router.navigate(['/albums']);
        },
        error: (err) => {
          console.error('Erro ao salvar álbum:', err);
          this.error = err.error?.message || 'Não foi possível salvar o álbum. Tente novamente.';
        }
      });
  }

  cancel(): void {
    this.router.navigate(['/albums']);
  }

  getFieldError(fieldName: string): string | null {
    const control = this.albumForm.get(fieldName);
    if (!control || !control.touched) {
      return null;
    }

    if (control.hasError('required')) {
      return 'Este campo é obrigatório';
    }

    if (control.hasError('min')) {
      const min = control.errors?.['min'].min;
      return `O valor mínimo é ${min}`;
    }

    if (control.hasError('max')) {
      const max = control.errors?.['max'].max;
      return `O valor máximo é ${max}`;
    }

    if (control.hasError('minlength')) {
      return 'Este campo deve ter pelo menos 1 caractere';
    }

    return null;
  }
}
