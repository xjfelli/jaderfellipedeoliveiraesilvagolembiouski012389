import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { first, map, of, tap, timeout } from 'rxjs';
import type { Artist } from './model/artist.model';
import { environment } from '../../../environments/environment';

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root',
})
export class ArtistsService {
  private readonly API = '/api/v1/artists';
  constructor(private http: HttpClient) {}

  create(formData: FormData) {
    return this.http.post<Artist>(`${environment.apiUrl}${this.API}`, formData).pipe(
      first(),
    );
  }

  findAll() {
    const params = new HttpParams().set('includeAlbums', 'true');
    return this.http.get<Artist[]>(`${environment.apiUrl}${this.API}`, { params }).pipe(
      map(artists => artists.map(artist => ({
        ...artist,
        albumCount: artist.albums?.length || 0
      })))
    );
  }

  findAllPaginated(page: number = 0, size: number = 10, sortBy: string = 'name', sortDirection: 'asc' | 'desc' = 'asc') {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('includeAlbums', 'true');

    return this.http.get<PagedResponse<Artist>>(`${environment.apiUrl}${this.API}/paginated`, { params }).pipe(
      map(response => ({
        ...response,
        content: response.content.map(artist => ({
          ...artist,
          albumCount: artist.albums?.length || 0
        }))
      }))
    );
  }

  getArtistById(id: string) {
    return this.http.get<Artist>(`${environment.apiUrl}${this.API}/${id}`).pipe(
      timeout(10000) // 10 segundos de timeout
    );
  }

  updateArtist(id: string, formData: FormData) {
    return this.http.put<Artist>(`${environment.apiUrl}${this.API}/${id}`, formData);
  }

  deleteArtist(id: string) {
    return this.http.delete<void>(`${environment.apiUrl}${this.API}/${id}`);
  }
}
