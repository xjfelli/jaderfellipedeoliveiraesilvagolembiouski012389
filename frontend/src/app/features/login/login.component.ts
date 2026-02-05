import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LoginFacade } from './login.facade';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  providers: [LoginFacade],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  constructor(public facade: LoginFacade) {}

  get loginForm(): any {
    return this.facade.loginForm;
  }

  isLoading(): boolean {
    return this.facade.isLoading();
  }

  errorMessage(): string | null {
    return this.facade.errorMessage();
  }

  showPassword(): boolean {
    return this.facade.showPassword();
  }

  toggleShowCreateArtistsModal(): boolean {
    return this.facade.showCreateArtistsModal();
  }

  onSubmit(): void {
    this.facade.login();
  }

  togglePasswordVisibility(): void {
    this.facade.togglePasswordVisibility();
  }

  hasError(fieldName: string, errorType: string): boolean {
    return this.facade.hasFieldError(fieldName, errorType);
  }

  getErrorMessage(fieldName: string): string {
    return this.facade.getFieldErrorMessage(fieldName);
  }
}
