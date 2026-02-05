import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RegisterFacade } from './register.facade';
import { RegisterService } from './register.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  providers: [RegisterFacade, RegisterService],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  constructor(public facade: RegisterFacade) {}

  get registerForm(): any {
    return this.facade.registerForm;
  }

  isLoading(): boolean {
    return this.facade.isLoading();
  }

  errorMessage(): string | null {
    return this.facade.errorMessage();
  }

  successMessage(): string | null {
    return this.facade.successMessage();
  }

  showPassword(): boolean {
    return this.facade.showPassword();
  }

  showConfirmPassword(): boolean {
    return this.facade.showConfirmPassword();
  }

  onSubmit(): void {
    this.facade.register();
  }

  togglePasswordVisibility(): void {
    this.facade.togglePasswordVisibility();
  }

  toggleConfirmPasswordVisibility(): void {
    this.facade.toggleConfirmPasswordVisibility();
  }

  hasError(fieldName: string, errorType: string): boolean {
    return this.facade.hasFieldError(fieldName, errorType);
  }

  getErrorMessage(fieldName: string): string {
    return this.facade.getFieldErrorMessage(fieldName);
  }
}
