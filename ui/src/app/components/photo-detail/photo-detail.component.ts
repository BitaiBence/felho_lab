import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { PhotoService } from '../../services/photo.service';
import { Photo } from '../../models';

@Component({
  selector: 'app-photo-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './photo-detail.component.html',
  styleUrl: './photo-detail.component.scss'
})
export class PhotoDetailComponent implements OnInit {
  photo: Photo | null = null;
  imageUrl: string = '';
  isLoading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private photoService: PhotoService
  ) {}

  ngOnInit(): void {
    const photoId = Number(this.route.snapshot.paramMap.get('id'));
    if (photoId) {
      this.loadPhoto(photoId);
    }
  }

  loadPhoto(photoId: number): void {
    this.isLoading = true;
    this.imageUrl = this.photoService.getImageUrl(photoId);
    
    this.photoService.getPhoto(photoId).subscribe({
      next: (photo) => {
        this.photo = photo;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading photo', error);
        this.error = 'Failed to load photo details';
        this.isLoading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
