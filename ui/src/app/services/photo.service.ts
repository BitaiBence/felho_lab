import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Photo, PhotoListItem, PhotoListResponse } from '../models';

export type SortBy = 'name' | 'date';
export type SortOrder = 'asc' | 'desc';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  listPhotos(sortBy: SortBy = 'date', order: SortOrder = 'desc'): Observable<PhotoListResponse> {
    const params = new HttpParams()
      .set('sortBy', sortBy)
      .set('order', order);
    
    return this.http.get<PhotoListResponse>(`${this.baseUrl}/photos`, { params });
  }

  getPhoto(photoId: number): Observable<Photo> {
    return this.http.get<Photo>(`${this.baseUrl}/photos/${photoId}`);
  }

  getImageUrl(photoId: number): string {
    return `${this.baseUrl}/photos/${photoId}/image`;
  }

  uploadPhoto(file: File, name: string): Observable<Photo> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('name', name);
    
    return this.http.post<Photo>(`${this.baseUrl}/photos`, formData);
  }

  deletePhoto(photoId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/photos/${photoId}`);
  }
}
