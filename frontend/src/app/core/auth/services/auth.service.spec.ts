import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService, LoginCredentials } from './auth.service';
import { AuthResponse } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    if (httpMock) { httpMock.verify(); }
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should authenticate user and store tokens', () => {
      const credentials: LoginCredentials = {
        username: 'testuser',
        password: 'password123',
      };

      const mockResponse: AuthResponse = {
        accessToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsImZ1bGxuYW1lIjoiVGVzdCBVc2VyIn0.test',
        refreshToken: 'refresh-token',
        expiresIn: '3600',
        username: 'testuser',
        email: 'test@example.com',
        tokenType: 'Bearer',
      };

      service.login(credentials).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(service.isAuthenticated()).toBe(true);
        expect(service.getToken()).toBe(mockResponse.accessToken);
      });

      const req = httpMock.expectOne('/api/v1/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(mockResponse);
    });

    it('should handle login errors', () => {
      const credentials: LoginCredentials = {
        username: 'testuser',
        password: 'wrongpassword',
      };

      service.login(credentials).subscribe({
        next: () => {
          throw new Error('should have failed');
        },
        error: (err) => {
          expect(err.status).toBe(401);
        },
      });

      const req = httpMock.expectOne('/api/v1/auth/login');
      req.flush({ message: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('logout', () => {
    it('should clear tokens and navigate to login', () => {
      // Set up authenticated state
      localStorage.setItem('auth_token', 'test-token');
      localStorage.setItem('refresh_token', 'test-refresh-token');
      service['isAuthenticated'].set(true);

      service.logout();

      expect(service.getToken()).toBeNull();
      expect(service.getRefreshToken()).toBeNull();
      expect(service.isAuthenticated()).toBe(false);
    });
  });

  describe('getToken', () => {
    it('should return stored token', () => {
      const token = 'test-token';
      localStorage.setItem('auth_token', token);

      expect(service.getToken()).toBe(token);
    });

    it('should return null if no token exists', () => {
      expect(service.getToken()).toBeNull();
    });
  });

  describe('hasValidToken', () => {
    it('should return true for valid token', () => {
      const futureDate = new Date(Date.now() + 3600 * 1000);
      localStorage.setItem('auth_token', 'test-token');
      localStorage.setItem('token_expiration', futureDate.toISOString());

      expect(service.hasValidToken()).toBe(true);
    });

    it('should return false for expired token', () => {
      const pastDate = new Date(Date.now() - 3600 * 1000);
      localStorage.setItem('auth_token', 'test-token');
      localStorage.setItem('token_expiration', pastDate.toISOString());

      expect(service.hasValidToken()).toBe(false);
    });

    it('should return false if no token exists', () => {
      expect(service.hasValidToken()).toBe(false);
    });
  });

  describe('isAuthenticatedSync', () => {
    it('should return true when token exists', () => {
      localStorage.setItem('auth_token', 'test-token');

      expect(service.isAuthenticatedSync()).toBe(true);
    });

    it('should return false when no token exists', () => {
      expect(service.isAuthenticatedSync()).toBe(false);
    });
  });

  describe('refreshToken', () => {
    it('should refresh token successfully', () => {
      localStorage.setItem('refresh_token', 'refresh-token');

      const mockResponse: AuthResponse = {
        accessToken: 'new-access-token',
        email: 'test@example.com',
        tokenType: 'Bearer',
        refreshToken: 'new-refresh-token',
        expiresIn: '3600',
        username: 'testuser',
      };

      service.refreshToken().subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(service.getToken()).toBe('new-access-token');
      });

      const req = httpMock.expectOne('/api/v1/auth/refresh');
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
    });

    it('should handle refresh token errors', () => {
      localStorage.setItem('refresh_token', 'invalid-refresh-token');

      service.refreshToken().subscribe({
        next: () => {
          throw new Error('should have failed');
        },
        error: () => {
          expect(service.isAuthenticated()).toBe(false);
        },
      });

      const req = httpMock.expectOne('/api/v1/auth/refresh');
      req.flush({ message: 'Invalid refresh token' }, { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('handleAuthResponse', () => {
    it('should process auth response and update state', () => {
      const mockResponse: AuthResponse = {
        accessToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSJ9.test',
        refreshToken: 'refresh-token',
        expiresIn: '3600',
        username: 'testuser',
        email: 'test@example.com',
        tokenType: 'Bearer',
      };

      service.handleAuthResponse(mockResponse);

      expect(service.getToken()).toBe(mockResponse.accessToken);
      expect(service.getRefreshToken()).toBe(mockResponse.refreshToken);
      expect(service.isAuthenticated()).toBe(true);
    });
  });
});
