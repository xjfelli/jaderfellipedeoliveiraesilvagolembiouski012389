import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ArtistsService, PagedResponse } from './artists.service';
import { Artist } from './model/artist.model';

describe('ArtistsService', () => {
  let service: ArtistsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ArtistsService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(ArtistsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    if (httpMock) {
      if (httpMock) { httpMock.verify(); }
    }
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('create', () => {
    it('should create a new artist', () => {
      const formData = new FormData();
      formData.append('name', 'Test Artist');

      const mockArtist: Artist = {
        id: '1',
        name: 'Test Artist',
        biography: '',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      service.create(formData).subscribe((artist) => {
        expect(artist).toEqual(mockArtist);
      });

      const req = httpMock.expectOne('/api/v1/artists');
      expect(req.request.method).toBe('POST');
      req.flush(mockArtist);
    });
  });

  describe('findAll', () => {
    it('should return all artists with album count', () => {
      const mockArtists: Artist[] = [
        {
          id: '1',
          name: 'Artist 1',
          biography: '',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          albums: [{ id: 1, title: 'Album 1' } as any],
        },
        {
          id: '2',
          name: 'Artist 2',
          biography: '',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          albums: [],
        },
      ];

      service.findAll().subscribe((artists) => {
        expect(artists.length).toBe(2);
        expect(artists[0].albumCount).toBe(1);
        expect(artists[1].albumCount).toBe(0);
      });

      const req = httpMock.expectOne((request) => 
        request.url === '/api/v1/artists' && request.params.has('includeAlbums')
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockArtists);
    });
  });

  describe('findAllPaginated', () => {
    it('should return paginated artists', () => {
      const mockResponse: PagedResponse<Artist> = {
        content: [
          {
            id: '1',
            name: 'Artist 1',
            biography: '',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            albums: [],
          },
        ],
        totalElements: 1,
        totalPages: 1,
        size: 10,
        number: 0,
      };

      service.findAllPaginated(0, 10).subscribe((response) => {
        expect(response.content.length).toBe(1);
        expect(response.totalElements).toBe(1);
        expect(response.content[0].albumCount).toBe(0);
      });

      const req = httpMock.expectOne((request) =>
        request.url === '/api/v1/artists/paginated'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('page')).toBe('0');
      expect(req.request.params.get('size')).toBe('10');
      req.flush(mockResponse);
    });
  });

  describe('getArtistById', () => {
    it('should return a single artist', () => {
      const mockArtist: Artist = {
        id: '1',
        name: 'Test Artist',
        biography: '',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      service.getArtistById('1').subscribe((artist) => {
        expect(artist).toEqual(mockArtist);
      });

      const req = httpMock.expectOne('/api/v1/artists/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockArtist);
    });
  });

  describe('updateArtist', () => {
    it('should update an artist', () => {
      const formData = new FormData();
      formData.append('name', 'Updated Artist');

      const mockArtist: Artist = {
        id: '1',
        name: 'Updated Artist',
        biography: '',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      service.updateArtist('1', formData).subscribe((artist) => {
        expect(artist).toEqual(mockArtist);
      });

      const req = httpMock.expectOne('/api/v1/artists/1');
      expect(req.request.method).toBe('PUT');
      req.flush(mockArtist);
    });
  });

  describe('deleteArtist', () => {
    it('should delete an artist', () => {
      service.deleteArtist('1').subscribe();

      const req = httpMock.expectOne('/api/v1/artists/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
