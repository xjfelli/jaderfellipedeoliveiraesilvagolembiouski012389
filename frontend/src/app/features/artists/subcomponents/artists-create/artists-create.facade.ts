import { Injectable, inject, signal, type EventEmitter } from '@angular/core';
import { NonNullableFormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import type { Artist } from '../../model/artist.model';
import { ArtistsService } from '../../artists.service';
import { FormUtilsService } from '../../../../shared/services/form-utils.service';

@Injectable()
export class ArtistsCreateFacade {
  private fb = inject(NonNullableFormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private artistsService = inject(ArtistsService);
  public formUtils = inject(FormUtilsService);

  artistsForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  showCreateArtistsModal = signal(false);

  constructor() {
    this.artistsForm = this.createArtistsForm();
  }

  /**
   * Cria e configura o formulário de login
   */
  private createArtistsForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required]],
      musicalGenre: ['', [Validators.required]],
      biography: ['', [Validators.required]],
      countryOfOrigin: ['', [Validators.required]],
      file: [null, [Validators.required]],
    });
  }

  submit(event: EventEmitter<void>): void {
    if (!this.isFormValid()) {
      this.formUtils.validateAllFormFields(this.artistsForm);
      return;
    }

    const formData = this.buildFormData();
    this.artistsService.create(formData).subscribe({
      next: () => {
        this.artistsForm.reset();
        event.emit();
      },
    });
  }

  private buildFormData(): FormData {
    const formData = new FormData();
    const values = this.artistsForm.value;

    // O backend espera os dados do artista dentro de "artist" como JSON string
    const artistData = {
      name: values.name,
      musicalGenre: values.musicalGenre,
      biography: values.biography,
      countryOfOrigin: values.countryOfOrigin,
    };

    const artistBlob = new Blob([JSON.stringify(artistData)], { type: 'application/json' });
    formData.append('artist', artistBlob);

    if (values.file) {
      formData.append('file', values.file, values.file.name);
    }

    return formData;
  }

  hasFieldError(fieldName: string, errorType: string): boolean {
    const field = this.artistsForm.get(fieldName);
    return !!(field?.hasError(errorType) && (field.dirty || field.touched));
  }

  /**
   * Obtém a mensagem de erro de um campo
   */
  getFieldErrorMessage(fieldName: string): string {
    const field = this.artistsForm.get(fieldName);

    if (!field || !(field.dirty || field.touched)) {
      return '';
    }

    if (field.hasError('required')) {
      return 'Este campo é obrigatório';
    }

    if (field.hasError('minlength')) {
      const minLength = field.errors?.['minlength'].requiredLength;
      return `Mínimo de ${minLength} caracteres`;
    }

    return '';
  }

  /**
   * Verifica se o formulário é válido
   */
  private isFormValid(): boolean {
    return this.artistsForm.valid;
  }

  /**
   * Marca todos os campos do formulário como tocados
   */
  private markFormAsTouched(): void {
    Object.keys(this.artistsForm.controls).forEach((key) => {
      this.artistsForm.get(key)?.markAsTouched();
    });
  }

  /**
   * Inicia o processo de login
   */
  private startCreateArtistsRequest(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
  }

  /**
   * Finaliza o processo de login
   */
  private finishCreateArtistsRequest(): void {
    this.isLoading.set(false);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      // Validação opcional: Verifica se é uma imagem
      if (!file.type.startsWith('image/')) {
        this.errorMessage.set('Por favor, selecione um arquivo de imagem válido.');
        return;
      }
      // Limpa mensagens de erro anteriores
      this.errorMessage.set(null);
      // Define o arquivo no formulário
      this.artistsForm.patchValue({ file });
      // Marca o campo como tocado para validações
      this.artistsForm.get('file')?.markAsTouched();
    }
  }

  /**
   * Processa o sucesso do login
   */
  private handleLoginSuccess(): void {
    const returnUrl = this.getReturnUrl();
    this.router.navigate([returnUrl]);
  }

  /**
   * Processa os erros de login
   */
  private handleLoginError(error: any): void {
    this.isLoading.set(false);

    const errorMessage = this.parseErrorMessage(error);
    this.errorMessage.set(errorMessage);
  }

  /**
   * Obtém a URL de retorno após o login
   */
  private getReturnUrl(): string {
    return this.route.snapshot.queryParams['returnUrl'] || '/artists';
  }

  /**
   * Converte o erro HTTP em mensagem amigável
   */
  private parseErrorMessage(error: any): string {
    if (error.status === 401) {
      return 'Usuário ou senha incorretos';
    }

    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor';
    }

    return 'Erro ao fazer login. Tente novamente.';
  }
}
