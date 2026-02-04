
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RegisterComponent } from './register.component';
import { RegisterFacade } from './register.facade';
import { signal } from '@angular/core';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let facade: RegisterFacade;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    facade = fixture.debugElement.injector.get(RegisterFacade);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have register form with all fields', () => {
    expect(component.registerForm).toBeDefined();
    expect(component.registerForm.get('username')).toBeDefined();
    expect(component.registerForm.get('email')).toBeDefined();
    expect(component.registerForm.get('password')).toBeDefined();
    expect(component.registerForm.get('confirmPassword')).toBeDefined();
    expect(component.registerForm.get('fullname')).toBeDefined();
  });

  it('should call facade.register on submit', () => {
    const registerSpy = vi.spyOn(facade, 'register');
    
    component.onSubmit();
    
    expect(registerSpy).toHaveBeenCalled();
  });

  it('should toggle password visibility', () => {
    const initialState = facade.showPassword();
    
    component.togglePasswordVisibility();
    
    expect(facade.showPassword()).toBe(!initialState);
  });

  it('should toggle confirm password visibility', () => {
    const initialState = facade.showConfirmPassword();
    
    component.toggleConfirmPasswordVisibility();
    
    expect(facade.showConfirmPassword()).toBe(!initialState);
  });

  it('should get loading state from facade', () => {
    facade.isLoading.set(true);
    
    expect(component.isLoading()).toBe(true);
  });

  it('should get error message from facade', () => {
    facade.errorMessage.set('Error message');
    
    expect(component.errorMessage()).toBe('Error message');
  });

  it('should get success message from facade', () => {
    facade.successMessage.set('Success message');
    
    expect(component.successMessage()).toBe('Success message');
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
