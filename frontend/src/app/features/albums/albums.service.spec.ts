import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AlbumsService, CreateAlbumDTO } from './albums.service';
import { Album, PagedResponse } from './model/album.model';

describe('AlbumsService', () => {
  let service: AlbumsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AlbumsService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(AlbumsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    if (httpMock) { httpMock.verify(); }
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('create', () => {
    it('should throw error if no file is provided', () => {
      const albumData: CreateAlbumDTO = {
        title: 'Test Album',
        releaseYear: 2024,
      };

      expect(() => {
        service.create(albumData, '1').subscribe();
      }).toThrow('A capa do álbum é obrigatória');
    });

    it('should create album and associate with artist', () => {
      const albumData: CreateAlbumDTO = {
        title: 'Test Album',
        releaseYear: 2024,
      };
      const file = new File([''], 'cover.jpg', { type: 'image/jpeg' });

      const mockAlbum: Album = {
        id: 1,
        title: 'Test Album',
        releaseYear: 2024,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      service.create(albumData, '1', file).subscribe((album) => {
        expect(album).toEqual(mockAlbum);
      });

      // First request: create album
      const createReq = httpMock.expectOne('/api/v1/albums');
      expect(createReq.request.method).toBe('POST');
      createReq.flush(mockAlbum);

      // Second request: associate with artist
      const associateReq = httpMock.expectOne('/api/v1/artists/1/albums/1');
      expect(associateReq.request.method).toBe('POST');
      associateReq.flush(mockAlbum);
    });
  });

  describe('findAll', () => {
    it('should return all albums with artist count', () => {
      const mockAlbums: Album[] = [
        {
          id: 1,
          title: 'Album 1',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          artists: [{ id: '1', name: 'Artist 1' } as any],
        },
        {
          id: 2,
          title: 'Album 2',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          artists: [],
        },
      ];

      service.findAll().subscribe((albums) => {
        expect(albums.length).toBe(2);
        expect(albums[0].artistCount).toBe(1);
        expect(albums[1].artistCount).toBe(0);
      });

      const req = httpMock.expectOne((request) =>
        request.url === '/api/v1/albums' && request.params.has('includeArtists')
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockAlbums);
    });
  });

  describe('findAllPaginated', () => {
    it('should return paginated albums', () => {
      const mockResponse: PagedResponse<Album> = {
        content: [
          {
            id: 1,
            title: 'Album 1',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            artists: [],
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
        expect(response.content[0].artistCount).toBe(0);
      });

      const req = httpMock.expectOne((request) =>
        request.url === '/api/v1/albums/paginated'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('page')).toBe('0');
      expect(req.request.params.get('size')).toBe('10');
      req.flush(mockResponse);
    });
  });

  describe('getAlbumById', () => {
    it('should return a single album with artists included', () => {
      const mockAlbum: Album = {
        id: 1,
        title: 'Test Album',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        artists: [{ id: 1, name: 'Test Artist' }],
      };

      service.getAlbumById(1).subscribe((album) => {
        expect(album).toEqual(mockAlbum);
      });

      const req = httpMock.expectOne((request) =>
        request.url === '/api/v1/albums/1' && request.params.has('includeArtists')
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('includeArtists')).toBe('true');
      req.flush(mockAlbum);
    });
  });

  describe('getAlbumsByArtistId', () => {
    it('should return albums for a specific artist', () => {
      const mockAlbums: Album[] = [
        {
          id: 1,
          title: 'Album 1',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        },
      ];

      service.getAlbumsByArtistId('1').subscribe((albums) => {
        expect(albums).toEqual(mockAlbums);
      });

      const req = httpMock.expectOne('/api/v1/albums/artist/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockAlbums);
    });
  });

  describe('updateAlbum', () => {
    it('should update an album without file', () => {
      const albumData: CreateAlbumDTO = {
        title: 'Updated Album',
        releaseYear: 2024,
      };

      const mockAlbum: Album = {
        id: 1,
        title: 'Updated Album',
        releaseYear: 2024,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      service.updateAlbum(1, albumData).subscribe((album) => {
        expect(album).toEqual(mockAlbum);
      });

      const req = httpMock.expectOne('/api/v1/albums/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(albumData);
      req.flush(mockAlbum);
    });

    it('should update an album with file', () => {
      const albumData: CreateAlbumDTO = {
        title: 'Updated Album',
        releaseYear: 2024,
      };
      const file = new File([''], 'new-cover.jpg', { type: 'image/jpeg' });

      const mockAlbum: Album = {
        id: 1,
        title: 'Updated Album',
        releaseYear: 2024,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      service.updateAlbum(1, albumData, file).subscribe((album) => {
        expect(album).toEqual(mockAlbum);
      });

      const req = httpMock.expectOne('/api/v1/albums/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body instanceof FormData).toBe(true);
      req.flush(mockAlbum);
    });
  });

  describe('deleteAlbum', () => {
    it('should delete an album', () => {
      service.deleteAlbum(1).subscribe(() => {
      });

      const req = httpMock.expectOne('/api/v1/albums/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
