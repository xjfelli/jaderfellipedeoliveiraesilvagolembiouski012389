import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Album, PagedResponse } from './model/album.model';
import { environment } from '../../../environments/environment';

export interface CreateAlbumDTO {
  title: string;
  releaseYear?: number;
  recordLabel?: string;
  trackCount?: number;
  description?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AlbumsService {
  private readonly API = '/api/v1/albums';

  constructor(private http: HttpClient) {}

  /**
   * Cria um novo álbum com upload de capa e associa a um artista
   */
  create(albumData: CreateAlbumDTO, artistId: string, file?: File): Observable<Album> {
    if (!file) {
      throw new Error('A capa do álbum é obrigatória');
    }

    const formData = new FormData();

    // O backend espera os dados do álbum como @RequestPart
    const albumBlob = new Blob([JSON.stringify(albumData)], { type: 'application/json' });
    formData.append('album', albumBlob);
    formData.append('file', file, file.name);

    // Primeiro cria o álbum, depois associa ao artista
    return this.http.post<Album>(`${environment.apiUrl}${this.API}`, formData).pipe(
      switchMap((createdAlbum) => {
        if (createdAlbum.id) {
          // Associa o álbum ao artista
          return this.http.post<Album>(
            `${environment.apiUrl}/api/v1/artists/${artistId}/albums/${createdAlbum.id}`,
            {}
          );
        }
        return new Observable<Album>(observer => {
          observer.next(createdAlbum);
          observer.complete();
        });
      })
    );
  }

  /**
   * Lista todos os álbuns com artistas
   */
  findAll(): Observable<Album[]> {
    const params = new HttpParams().set('includeArtists', 'true');
    return this.http.get<Album[]>(`${environment.apiUrl}${this.API}`, { params }).pipe(
      map(albums => albums.map(album => ({
        ...album,
        artistCount: album.artists?.length || 0
      })))
    );
  }

  /**
   * Lista álbuns com paginação
   */
  findAllPaginated(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'title',
    sortDirection: 'asc' | 'desc' = 'asc'
  ): Observable<PagedResponse<Album>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('includeArtists', 'true');

    return this.http.get<PagedResponse<Album>>(`${environment.apiUrl}${this.API}/paginated`, { params }).pipe(
      map(response => ({
        ...response,
        content: response.content.map(album => ({
          ...album,
          artistCount: album.artists?.length || 0
        }))
      }))
    );
  }

  /**
   * Busca um álbum por ID
   */
  getAlbumById(id: number): Observable<Album> {
    const params = new HttpParams().set('includeArtists', 'true');
    return this.http.get<Album>(`${environment.apiUrl}${this.API}/${id}`, { params });
  }

  /**
   * Busca álbuns de um artista específico
   */
  getAlbumsByArtistId(artistId: string): Observable<Album[]> {
    return this.http.get<Album[]>(`${environment.apiUrl}${this.API}/artist/${artistId}`);
  }

  /**
   * Atualiza um álbum existente
   */
  updateAlbum(id: number, albumData: CreateAlbumDTO, file?: File): Observable<Album> {
    if (file) {
      // Se há arquivo, usa FormData
      const formData = new FormData();
      const albumBlob = new Blob([JSON.stringify(albumData)], { type: 'application/json' });
      formData.append('album', albumBlob);
      formData.append('file', file, file.name);
      return this.http.put<Album>(`${environment.apiUrl}${this.API}/${id}`, formData);
    } else {
      // Sem arquivo, envia apenas JSON
      return this.http.put<Album>(`${environment.apiUrl}${this.API}/${id}`, albumData);
    }
  }

  /**
   * Deleta um álbum
   */
  deleteAlbum(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}${this.API}/${id}`);
  }
}
