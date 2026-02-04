
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { LoginComponent } from './login.component';
import { LoginFacade } from './login.facade';
import { signal } from '@angular/core';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let facade: LoginFacade;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    facade = fixture.debugElement.injector.get(LoginFacade);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have login form', () => {
    expect(component.loginForm).toBeDefined();
    expect(component.loginForm.get('username')).toBeDefined();
    expect(component.loginForm.get('password')).toBeDefined();
  });

  it('should call facade.login on submit', () => {
    const loginSpy = vi.spyOn(facade, 'login');
    
    component.onSubmit();
    
    expect(loginSpy).toHaveBeenCalled();
  });

  it('should toggle password visibility', () => {
    const initialState = facade.showPassword();
    
    component.togglePasswordVisibility();
    
    expect(facade.showPassword()).toBe(!initialState);
  });

  it('should get loading state from facade', () => {
    facade.isLoading.set(true);
    
    expect(component.isLoading()).toBe(true);
  });

  it('should get error message from facade', () => {
    facade.errorMessage.set('Error message');
    
    expect(component.errorMessage()).toBe('Error message');
  });

  it('should check field errors through facade', () => {
    const hasErrorSpy = vi.spyOn(facade, 'hasFieldError').mockReturnValue(true);
    
    const result = component.hasError('username', 'required');
    
    expect(hasErrorSpy).toHaveBeenCalledWith('username', 'required');
    expect(result).toBe(true);
  });

  it('should get field error message through facade', () => {
    const getErrorSpy = vi.spyOn(facade, 'getFieldErrorMessage').mockReturnValue('Field is required.');
    
    const message = component.getErrorMessage('username');
    
    expect(getErrorSpy).toHaveBeenCalledWith('username');
    expect(message).toBe('Field is required.');
  });
});
