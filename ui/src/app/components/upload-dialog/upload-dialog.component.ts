import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { PhotoService } from '../../services/photo.service';

@Component({
  selector: 'app-upload-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './upload-dialog.component.html',
  styleUrl: './upload-dialog.component.scss'
})
export class UploadDialogComponent {
  selectedFile: File | null = null;
  fileName: string = '';
  fileError: string | null = null;
  isUploading = false;

  private readonly ALLOWED_TYPES = ['image/png', 'image/jpeg'];
  private readonly MAX_NAME_LENGTH = 40;

  constructor(
    private dialogRef: MatDialogRef<UploadDialogComponent>,
    private photoService: PhotoService,
    private snackBar: MatSnackBar
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];
    this.fileError = null;

    // Validate file type
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      this.fileError = 'Only PNG and JPEG files are allowed';
      this.selectedFile = null;
      this.fileName = '';
      return;
    }

    this.selectedFile = file;
    
    // Extract filename without extension and enforce 40 char limit
    let name = file.name;
    const lastDotIndex = name.lastIndexOf('.');
    if (lastDotIndex > 0) {
      name = name.substring(0, lastDotIndex);
    }
    
    // Truncate to 40 characters
    if (name.length > this.MAX_NAME_LENGTH) {
      name = name.substring(0, this.MAX_NAME_LENGTH);
    }
    
    this.fileName = name;
  }

  upload(): void {
    if (!this.selectedFile || !this.fileName) {
      return;
    }

    this.isUploading = true;
    this.photoService.uploadPhoto(this.selectedFile, this.fileName).subscribe({
      next: () => {
        this.snackBar.open('Photo uploaded successfully!', 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error uploading photo', error);
        this.snackBar.open('Failed to upload photo', 'Close', { duration: 3000 });
        this.isUploading = false;
      }
    });
  }

  cancel(): void {
    this.dialogRef.close(false);
  }
}
