import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { forkJoin } from 'rxjs';

import { PhotoService, SortBy, SortOrder } from '../../services/photo.service';
import { PhotoListItem } from '../../models';
import { UploadDialogComponent } from '../upload-dialog/upload-dialog.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-photo-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatCheckboxModule,
    MatButtonModule,
    MatSelectModule,
    MatFormFieldModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './photo-list.component.html',
  styleUrl: './photo-list.component.scss'
})
export class PhotoListComponent implements OnInit {
  photos: PhotoListItem[] = [];
  displayedColumns: string[] = ['select', 'name', 'uploadDate'];
  selection = new SelectionModel<PhotoListItem>(true, []);
  
  sortBy: SortBy = 'date';
  sortOrder: SortOrder = 'desc';
  
  isLoading = false;

  constructor(
    private photoService: PhotoService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadPhotos();
  }

  loadPhotos(): void {
    this.isLoading = true;
    this.photoService.listPhotos(this.sortBy, this.sortOrder).subscribe({
      next: (response) => {
        this.photos = response.photos;
        this.selection.clear();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading photos', error);
        this.snackBar.open('Error loading photos', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  onSortChange(): void {
    this.loadPhotos();
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.photos.length;
    return numSelected === numRows;
  }

  toggleAllRows(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.photos);
  }

  onRowClick(photo: PhotoListItem): void {
    this.router.navigate(['/photos', photo.id]);
  }

  openUploadDialog(): void {
    const dialogRef = this.dialog.open(UploadDialogComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadPhotos();
      }
    });
  }

  deleteSelected(): void {
    const selected = this.selection.selected;
    if (selected.length === 0) {
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '300px',
      data: {
        title: 'Confirm Delete',
        message: `Are you sure you want to delete ${selected.length} photo(s)?`
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.performDelete(selected);
      }
    });
  }

  private performDelete(photos: PhotoListItem[]): void {
    this.isLoading = true;
    const deleteRequests = photos.map(photo => this.photoService.deletePhoto(photo.id));
    
    forkJoin(deleteRequests).subscribe({
      next: () => {
        this.snackBar.open(`Deleted ${photos.length} photo(s)`, 'Close', { duration: 3000 });
        this.loadPhotos();
      },
      error: (error) => {
        console.error('Error deleting photos', error);
        this.snackBar.open('Error deleting some photos', 'Close', { duration: 3000 });
        this.loadPhotos();
      }
    });
  }
}
