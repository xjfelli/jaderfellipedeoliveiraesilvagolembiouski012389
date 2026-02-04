import type { Album } from '../../albums/model/album.model';

export type { Album };

export interface Artist {
  id?: string;
  name: string;
  musicalGenre?: string;
  biography?: string;
  countryOfOrigin?: string;
  photoUrl?: string;
  file?: File | string | null;
  albums?: Album[];
  albumCount?: number;
  createdAt?: string;
  updatedAt?: string;
}
