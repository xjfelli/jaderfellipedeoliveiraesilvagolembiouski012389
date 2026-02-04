export interface ArtistSimplified {
  id: number;
  name: string;
  musicalGenre?: string;
  photoUrl?: string;
}

export interface Album {
  id?: number;
  title: string;
  releaseYear?: number;
  recordLabel?: string;
  trackCount?: number;
  coverUrl?: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
  artists?: ArtistSimplified[];
  artistCount?: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
