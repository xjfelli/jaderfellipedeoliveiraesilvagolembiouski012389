
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ArtistDetailComponent } from './artist-detail.component';
import { ArtistsService } from '../../artists.service';
import { AlbumsService } from '../../../albums/albums.service';
import { of, throwError } from 'rxjs';
import { Artist } from '../../model/artist.model';

describe('ArtistDetailComponent', () => {
  let component: ArtistDetailComponent;
  let fixture: ComponentFixture<ArtistDetailComponent>;
  let artistsService: ArtistsService;
  let albumsService: AlbumsService;

  const mockArtist: Artist = {
    id: '1',
    name: 'Test Artist',
    biography: 'Test Bio',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    albums: [],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistDetailComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => '1',
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArtistDetailComponent);
    component = fixture.componentInstance;
    artistsService = TestBed.inject(ArtistsService);
    albumsService = TestBed.inject(AlbumsService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load artist on init', () => {
    const loadArtistSpy = vi.spyOn(component, 'loadArtist');
    
    component.ngOnInit();
    
    expect(loadArtistSpy).toHaveBeenCalledWith('1');
  });

  it('should load artist successfully', async () => {
    vi.spyOn(artistsService, 'getArtistById').mockReturnValue(of(mockArtist));
    
    component.loadArtist('1');
    
    // Wait for async operations
    await new Promise(resolve => setTimeout(resolve, 150));
    
    expect(component.artist).toEqual(mockArtist);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
  });

  it('should handle error when loading artist', async () => {
    vi.spyOn(artistsService, 'getArtistById').mockReturnValue(
      throwError(() => new Error('Failed to load'))
    );
    
    component.loadArtist('1');
    
    // Wait for async operations
    await new Promise(resolve => setTimeout(resolve, 150));
    
    expect(component.loading).toBe(false);
    expect(component.error).toBeTruthy();
  });

  it('should initialize with null artist', () => {
    expect(component.artist).toBeNull();
  });

  it('should initialize with loading false', () => {
    expect(component.loading).toBe(false);
  });

  it('should initialize with null error', () => {
    expect(component.error).toBeNull();
  });
});
