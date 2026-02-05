
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AlbumEditComponent } from './album-edit.component';
import { of } from 'rxjs';

describe('AlbumEditComponent', () => {
  let component: AlbumEditComponent;
  let fixture: ComponentFixture<AlbumEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumEditComponent],
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

    fixture = TestBed.createComponent(AlbumEditComponent);
    component = fixture.componentInstance;
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load album on init', () => {
    vi.spyOn(component, 'loadAlbum');
    
    component.ngOnInit();
    
    expect(component.loadAlbum).toHaveBeenCalled();
  });

  it('should have album form', () => {
    expect(component.albumForm).toBeDefined();
  });

  it('should have loading property', () => {
    expect(component.loading).toBeDefined();
  });

  it('should have String function exposed for template', () => {
    expect(component.String).toBe(String);
  });

  describe('loadAlbum', () => {
    it('should load album and set artistId as string', () => {
      const mockAlbum = {
        id: 1,
        title: 'Test Album',
        artists: [{ id: 123, name: 'Test Artist' }],
        releaseYear: 2024,
        recordLabel: 'Test Label',
        trackCount: 10,
        description: 'Test Description',
        coverUrl: 'http://test.com/cover.jpg',
      };

      vi.spyOn(component['albumsService'], 'getAlbumById').mockReturnValue(
        of(mockAlbum) as any
      );

      component.loadAlbum();

      expect(component.album).toEqual(mockAlbum);
      expect(component.albumForm.get('artistId')?.value).toBe('123');
      expect(component.imagePreview).toBe('http://test.com/cover.jpg');
    });
  });

  describe('loadArtists', () => {
    it('should load artists list', () => {
      const mockArtists = [
        { id: '1', name: 'Artist 1' },
        { id: '2', name: 'Artist 2' },
      ];

      vi.spyOn(component['artistsService'], 'findAll').mockReturnValue(
        of(mockArtists) as any
      );

      component.loadArtists();

      expect(component.artists).toEqual(mockArtists);
    });
  });

  describe('onSubmit', () => {
    it('should mark form as touched if invalid', () => {
      component.albumForm.setValue({
        artistId: '',
        title: '',
        releaseYear: null,
        recordLabel: '',
        trackCount: null,
        description: '',
        file: null,
      });

      const markAllAsTouchedSpy = vi.spyOn(component.albumForm, 'markAllAsTouched');

      component.onSubmit();

      expect(markAllAsTouchedSpy).toHaveBeenCalled();
    });
  });
});
