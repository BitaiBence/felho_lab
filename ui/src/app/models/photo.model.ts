export interface PhotoListItem {
  id: number;
  name: string;
  uploadDate: string;
}

export interface Photo {
  id: number;
  name: string;
  uploadDate: string;
  imageUrl: string;
  uploadedBy: number;
  mimeType: string;
}

export interface PhotoListResponse {
  photos: PhotoListItem[];
  total: number;
}
