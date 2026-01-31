import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { LoginFacade } from './login.facade';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  providers: [LoginFacade],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  constructor(public facade: LoginFacade) {}

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
