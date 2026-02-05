
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AlbumsComponent } from './albums.component';
import { AlbumsFacade } from './albums.facade';
import { AuthService } from '../../core/auth/services/auth.service';
import { Router } from '@angular/router';

describe('AlbumsComponent', () => {
  let component: AlbumsComponent;
  let fixture: ComponentFixture<AlbumsComponent>;
  let facade: AlbumsFacade;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumsComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumsComponent);
    component = fixture.componentInstance;
    facade = fixture.debugElement.injector.get(AlbumsFacade);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    
    vi.spyOn(facade, 'loadAlbums');
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load albums on init', () => {
    expect(facade.loadAlbums).toHaveBeenCalled();
  });

  it('should call authService.logout on logout', () => {
    const logoutSpy = vi.spyOn(authService, 'logout');
    
    component.logout();
    
    expect(logoutSpy).toHaveBeenCalled();
  });

  it('should navigate to artists', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    
    component.navigateToArtists();
    
    expect(navigateSpy).toHaveBeenCalledWith(['/artists']);
  });

  it('should have current user observable', () => {
    expect(component.currentUser$).toBeDefined();
  });

  it('should have albums from facade', () => {
    expect(component.albums).toBeDefined();
  });
});
