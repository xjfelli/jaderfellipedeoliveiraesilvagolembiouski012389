import { Injectable, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService, LoginCredentials } from '../../core/auth/services/auth.service';

/**
 * Facade para a tela de login
 * Encapsula toda a lógica de negócio e coordena as interações
 * entre o componente, formulário e serviço de autenticação
 */
@Injectable()
export class LoginFacade {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  // Estado do facade
  loginForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  showPassword = signal(false);
  showCreateArtistsModal = signal(false);

  constructor() {
    this.loginForm = this.createLoginForm();
  }

  /**
   * Cria e configura o formulário de login
   */
  private createLoginForm(): FormGroup {
    return this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  login(): void {
    if (!this.isFormValid()) {
      this.markFormAsTouched();
      return;
    }

    this.startLogin();

    const credentials = this.getFormCredentials();

    this.authService.login(credentials).subscribe({
      next: () => this.handleLoginSuccess(),
      error: (error) => this.handleLoginError(error),
      complete: () => this.finishLogin(),
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update((value) => !value);
  }

  /**
   * Verifica se um campo tem um erro específico
   */
  hasFieldError(fieldName: string, errorType: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field?.hasError(errorType) && (field.dirty || field.touched));
  }

  /**
   * Obtém a mensagem de erro de um campo
   */
  getFieldErrorMessage(fieldName: string): string {
    const field = this.loginForm.get(fieldName);

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
    return this.loginForm.valid;
  }

  /**
   * Marca todos os campos do formulário como tocados
   */
  private markFormAsTouched(): void {
    Object.keys(this.loginForm.controls).forEach((key) => {
      this.loginForm.get(key)?.markAsTouched();
    });
  }

  /**
   * Obtém as credenciais do formulário
   */
  private getFormCredentials(): LoginCredentials {
    return this.loginForm.value as LoginCredentials;
  }

  /**
   * Inicia o processo de login
   */
  private startLogin(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
  }

  /**
   * Finaliza o processo de login
   */
  private finishLogin(): void {
    this.isLoading.set(false);
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

    console.error('Erro no login:', error);
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
