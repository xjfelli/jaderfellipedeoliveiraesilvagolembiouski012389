import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RegisterService } from './register.service';
import { RegisterData } from './model';
import { AuthResponse } from '../../core/auth/models/auth.model';

describe('RegisterService', () => {
  let service: RegisterService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RegisterService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(RegisterService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    if (httpMock) { httpMock.verify(); }
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {
    it('should register a new user', () => {
      const registerData: RegisterData = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'password123',
        fullname: 'Test User',
      };

      const mockResponse: AuthResponse = {
        accessToken: 'mock-token',
        refreshToken: 'mock-refresh-token',
        expiresIn: '3600',
        username: 'testuser',
        email: 'test@example.com',
        tokenType: 'Bearer',
      };

      service.register(registerData).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(response.username).toBe('testuser');
      });

      const req = httpMock.expectOne('/api/v1/usuarios');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerData);
      req.flush(mockResponse);
    });

    it('should handle registration errors', () => {
      const registerData: RegisterData = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'password123',
        fullname: 'Test User',
      };

      service.register(registerData).subscribe({
        next: () => {
          throw new Error('should have failed');
        },
        error: (err) => {
          expect(err.status).toBe(400);
        },
      });

      const req = httpMock.expectOne('/api/v1/usuarios');
      req.flush({ message: 'Username already exists' }, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('checkUsernameAvailability', () => {
    it('should check if username is available', () => {
      const username = 'testuser';

      service.checkUsernameAvailability(username).subscribe((response) => {
        expect(response.available).toBe(true);
      });

      const req = httpMock.expectOne(`/api/v1/auth/check-username/${username}`);
      expect(req.request.method).toBe('GET');
      req.flush({ available: true });
    });

    it('should check if username is not available', () => {
      const username = 'existinguser';

      service.checkUsernameAvailability(username).subscribe((response) => {
        expect(response.available).toBe(false);
      });

      const req = httpMock.expectOne(`/api/v1/auth/check-username/${username}`);
      req.flush({ available: false });
    });
  });

  describe('checkEmailAvailability', () => {
    it('should check if email is available', () => {
      const email = 'test@example.com';

      service.checkEmailAvailability(email).subscribe((response) => {
        expect(response.available).toBe(true);
      });

      const req = httpMock.expectOne(`/api/v1/auth/check-email/${email}`);
      expect(req.request.method).toBe('GET');
      req.flush({ available: true });
    });

    it('should check if email is not available', () => {
      const email = 'existing@example.com';

      service.checkEmailAvailability(email).subscribe((response) => {
        expect(response.available).toBe(false);
      });

      const req = httpMock.expectOne(`/api/v1/auth/check-email/${email}`);
      req.flush({ available: false });
    });
  });
});
