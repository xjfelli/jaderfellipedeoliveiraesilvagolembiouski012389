import { Injectable, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/services/auth.service';
import { RegisterService } from './register.service';
import { RegisterData } from './model';

/**
 * Facade para a tela de registro
 * Encapsula toda a lógica de negócio e coordena as interações
 * entre o componente, formulário e serviços de registro e autenticação
 */
@Injectable()
export class RegisterFacade {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private registerService = inject(RegisterService);
  private router = inject(Router);

  // Estado do facade
  registerForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  constructor() {
    this.registerForm = this.createRegisterForm();
  }

  /**
   * Cria e configura o formulário de registro
   */
  private createRegisterForm(): FormGroup {
    return this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        fullname: ['', [Validators.required, Validators.minLength(3)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]],
      },
      {
        validators: this.passwordMatchValidator,
      },
    );
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ ...confirmPassword.errors, passwordMismatch: true });
      return { passwordMismatch: true };
    }

    if (confirmPassword.hasError('passwordMismatch')) {
      const errors = { ...confirmPassword.errors };
      delete errors['passwordMismatch'];
      confirmPassword.setErrors(Object.keys(errors).length > 0 ? errors : null);
    }

    return null;
  }

  register(): void {
    if (!this.isFormValid()) {
      this.markFormAsTouched();
      return;
    }

    this.startRegister();

    const registerData = this.getFormData();

    this.registerService.register(registerData).subscribe({
      next: (response) => this.handleRegisterSuccess(response),
      error: (error) => this.handleRegisterError(error),
      complete: () => this.finishRegister(),
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update((value) => !value);
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword.update((value) => !value);
  }

  hasFieldError(fieldName: string, errorType: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field?.hasError(errorType) && (field.dirty || field.touched));
  }

  getFieldErrorMessage(fieldName: string): string {
    const field = this.registerForm.get(fieldName);

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

    if (field.hasError('email')) {
      return 'Email inválido';
    }

    if (field.hasError('passwordMismatch')) {
      return 'As senhas não coincidem';
    }

    return '';
  }

  /**
   * Verifica se o formulário é válido
   */
  private isFormValid(): boolean {
    return this.registerForm.valid;
  }

  private markFormAsTouched(): void {
    Object.keys(this.registerForm.controls).forEach((key) => {
      this.registerForm.get(key)?.markAsTouched();
    });
  }

  private getFormData(): RegisterData {
    const formValue = this.registerForm.value;
    return {
      username: formValue.username,
      email: formValue.email,
      fullname: formValue.fullname,
      password: formValue.password,
    };
  }

  private startRegister(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }

  private finishRegister(): void {
    this.isLoading.set(false);
  }

  private handleRegisterSuccess(response: any): void {
    this.successMessage.set('Usuário registrado com sucesso! Redirecionando...');

    this.authService.handleAuthResponse(response);

    setTimeout(() => {
      this.router.navigate(['/artists']);
    }, 2000);
  }

  private handleRegisterError(error: any): void {
    this.isLoading.set(false);

    const errorMessage = this.parseErrorMessage(error);
    this.errorMessage.set(errorMessage);

    console.error('Erro no registro:', error);
  }

  private parseErrorMessage(error: any): string {
    if (error.status === 400) {
      return error.error?.message || 'Dados inválidos. Verifique os campos e tente novamente.';
    }

    if (error.status === 409) {
      return 'Este usuário ou email já está cadastrado.';
    }

    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor';
    }

    return error.error?.message || 'Erro ao registrar usuário. Tente novamente.';
  }
}
