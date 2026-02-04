
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ArtistsComponent } from './artists.component';
import { ArtistsFacade } from './artists.facade';
import { ArtistsCreateFacade } from './subcomponents/artists-create/artists-create.facade';
import { AuthService } from '../../core/auth/services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('ArtistsComponent', () => {
  let component: ArtistsComponent;
  let fixture: ComponentFixture<ArtistsComponent>;
  let facade: ArtistsFacade;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistsComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArtistsComponent);
    component = fixture.componentInstance;
    facade = fixture.debugElement.injector.get(ArtistsFacade);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    
    vi.spyOn(facade, 'loadArtists');
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load artists on init', () => {
    expect(facade.loadArtists).toHaveBeenCalled();
  });

  it('should call authService.logout on logout', () => {
    const logoutSpy = vi.spyOn(authService, 'logout');
    
    component.logout();
    
    expect(logoutSpy).toHaveBeenCalled();
  });

  it('should navigate to albums', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    
    component.navigateToAlbums();
    
    expect(navigateSpy).toHaveBeenCalledWith(['/albums']);
  });

  it('should toggle create artists modal', () => {
    const toggleSpy = vi.spyOn(facade, 'toggleVisibilityCreateArtistsModal');
    
    component.toggleVisibilityCreateArtistsModal();
    
    expect(toggleSpy).toHaveBeenCalled();
  });

  it('should get modal visibility state from facade', () => {
    const isOpenSpy = vi.spyOn(facade, 'isOpenCreateArtistsModal').mockReturnValue(true);
    
    expect(component.isOpenCreateArtistsModal).toBe(true);
    expect(isOpenSpy).toHaveBeenCalled();
  });

  it('should have current user observable', () => {
    expect(component.currentUser$).toBeDefined();
  });

  it('should have artists from facade', () => {
    expect(component.artists).toBeDefined();
  });
});
