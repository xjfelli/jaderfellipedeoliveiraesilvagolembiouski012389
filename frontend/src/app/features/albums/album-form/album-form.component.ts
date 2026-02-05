import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AlbumsService } from '../albums.service';
import { ArtistsService } from '../../artists/artists.service';
import type { Album } from '../model/album.model';
import type { Artist } from '../../artists/model/artist.model';

@Component({
  selector: 'app-album-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './album-form.component.html',
  styleUrl: './album-form.component.css'
})
export class AlbumFormComponent implements OnInit {
  @Input() artistId!: string | undefined;
  @Input() album?: Album;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<Album>();

  private fb = inject(FormBuilder);
  private albumsService = inject(AlbumsService);
  private artistsService = inject(ArtistsService);
  private cdr = inject(ChangeDetectorRef);

  albumForm: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  saving = false;
  error: string | null = null;
  isEditMode = false;
  artists: Artist[] = [];
  loadingArtists = false;

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
    this.isEditMode = !!this.album;

    // Carrega a lista de artistas
    this.loadArtists();

    // Se artistId foi passado como input, define no formulário
    if (this.artistId) {
      this.albumForm.patchValue({
        artistId: this.artistId
      });
    }

    if (this.album) {
      this.albumForm.patchValue({
        title: this.album.title,
        releaseYear: this.album.releaseYear,
        recordLabel: this.album.recordLabel,
        trackCount: this.album.trackCount,
        description: this.album.description
      });

      if (this.album.coverUrl) {
        this.imagePreview = this.album.coverUrl;
      }
    }
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

      // Preview da imagem
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

    // Validação: arquivo obrigatório para criação
    if (!this.isEditMode && !this.selectedFile) {
      this.error = 'A capa do álbum é obrigatória.';
      this.cdr.detectChanges();
      return;
    }

    const formValues = this.albumForm.value;
    const selectedArtistId = formValues.artistId;

    // Validação: artistId obrigatório
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

    const request = this.isEditMode && this.album?.id
      ? this.albumsService.updateAlbum(this.album.id, albumData)
      : this.albumsService.create(albumData, selectedArtistId, this.selectedFile!);

    request
      .pipe(
        finalize(() => {
          this.saving = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (album) => {
          this.saved.emit(album);
          this.close.emit();
        },
        error: (err) => {
          this.error = err.error?.message || 'Não foi possível salvar o álbum. Tente novamente.';
        }
      });
  }

  closeModal(): void {
    this.close.emit();
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
