import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
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

    private getAuthHeader(): HttpHeaders | null {
        const token = localStorage.getItem('jwt');
        if (!token) {
            console.error('Authentication error: No JWT token found in localStorage.');
            return null;
        }
        return new HttpHeaders().set('Authorization', `Bearer ${token}`);
    }

    listPhotos(sortBy: SortBy = 'date', order: SortOrder = 'desc'): Observable<PhotoListResponse> {
        const headers = this.getAuthHeader();
        if (!headers) {
            return of({ photos: [], total: 0 });
        }

        const params = new HttpParams()
            .set('sortBy', sortBy)
            .set('order', order);

        return this.http.get<PhotoListResponse>(`${this.baseUrl}/photos`, { params, headers });
    }

    getPhoto(photoId: number): Observable<Photo> {
        const headers = this.getAuthHeader();
        if (!headers) {
            throw new Error('Authentication error: No JWT token found.');
        }
        return this.http.get<Photo>(`${this.baseUrl}/photos/${photoId}`, { headers });
    }

    getImageUrl(photoId: number): string {
        const token = localStorage.getItem('jwt');
        return `${this.baseUrl}/photos/${photoId}/image?token=${token}`;
    }

    uploadPhoto(file: File, name: string): Observable<Photo> {
        const headers = this.getAuthHeader();
        if (!headers) {
            throw new Error('Authentication error: No JWT token found.');
        }
        const formData = new FormData();
        formData.append('file', file);
        formData.append('name', name);

        return this.http.post<Photo>(`${this.baseUrl}/photos`, formData, { headers });
    }

    deletePhoto(photoId: number): Observable<void> {
        const headers = this.getAuthHeader();
        if (!headers) {
            throw new Error('Authentication error: No JWT token found.');
        }
        return this.http.delete<void>(`${this.baseUrl}/photos/${photoId}`, { headers });
    }
}
