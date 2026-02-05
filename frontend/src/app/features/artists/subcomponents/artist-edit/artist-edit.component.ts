import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ArtistsService } from '../../artists.service';
import type { Artist } from '../../model/artist.model';

@Component({
  selector: 'app-artist-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './artist-edit.component.html',
  styleUrl: './artist-edit.component.css'
})
export class ArtistEditComponent implements OnInit {
  private artistsService = inject(ArtistsService);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  artistForm: FormGroup;
  artistId: string | null = null;
  loading = false;
  saving = false;
  error: string | null = null;
  isEditMode = false;
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  constructor() {
    this.artistForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      musicalGenre: ['', [Validators.required]],
      biography: ['', [Validators.required]],
      countryOfOrigin: ['', [Validators.required]],
      file: [null]
    });
  }

  ngOnInit(): void {
    this.artistId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.artistId;

    if (this.isEditMode && this.artistId) {
      this.loadArtist(this.artistId);
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
          this.artistForm.patchValue({
            name: artist.name,
            musicalGenre: artist.musicalGenre || '',
            biography: artist.biography || '',
            countryOfOrigin: artist.countryOfOrigin || ''
          });
          if (artist.photoUrl) {
            this.imagePreview = artist.photoUrl;
          }
        },
        error: (err) => {
          this.error = `Erro: ${err.message || 'Não foi possível carregar os dados do artista.'}`;
        }
      });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      if (!file.type.startsWith('image/')) {
        this.error = 'Por favor, selecione um arquivo de imagem válido.';
        return;
      }

      this.selectedFile = file;
      this.artistForm.patchValue({ file });
      this.artistForm.get('file')?.markAsTouched();

      // Preview da imagem
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.artistForm.invalid) {
      this.artistForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;

    const artistData = this.artistForm.value;

    // Converter para FormData
    const formData = new FormData();

    // O backend espera os dados do artista dentro de "artist" como JSON string
    const artistJson = {
      name: artistData.name,
      musicalGenre: artistData.musicalGenre,
      biography: artistData.biography,
      countryOfOrigin: artistData.countryOfOrigin
    };

    const artistBlob = new Blob([JSON.stringify(artistJson)], { type: 'application/json' });
    formData.append('artist', artistBlob);

    if (this.selectedFile) {
      formData.append('file', this.selectedFile, this.selectedFile.name);
    }

    const request = this.isEditMode && this.artistId
      ? this.artistsService.updateArtist(this.artistId, formData)
      : this.artistsService.create(formData);

    request.subscribe({
      next: (artist) => {
        this.router.navigate(['/artists', artist.id]);
      },
      error: (err) => {
        this.error = err.error?.message || 'Não foi possível salvar o artista. Tente novamente.';
        this.saving = false;
      }
    });
  }

  cancel(): void {
    this.goBack();
  }

  goBack(): void {
    this.router.navigate(['/artists']);
  }

  get nameControl() {
    return this.artistForm.get('name');
  }

  get nameError(): string | null {
    const control = this.nameControl;
    if (control?.hasError('required') && control.touched) {
      return 'O nome é obrigatório';
    }
    if (control?.hasError('minlength') && control.touched) {
      return 'O nome deve ter pelo menos 2 caracteres';
    }
    return null;
  }

  getFieldError(fieldName: string): string | null {
    const control = this.artistForm.get(fieldName);
    if (control?.hasError('required') && control.touched) {
      return 'Este campo é obrigatório';
    }
    return null;
  }
}
