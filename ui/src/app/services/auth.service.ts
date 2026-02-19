import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
  currentUser$: Observable<UserResponse | null> = this.currentUserSubject.asObservable();

  constructor() {}

  get currentUser(): UserResponse | null {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  login(username: string, password: string): void {
    // TODO: Implement login API call
    console.log('Login clicked', { username, password });
  }

  logout(): void {
    // TODO: Implement logout API call
    console.log('Logout clicked');
    this.currentUserSubject.next(null);
  }

  register(username: string, password: string): void {
    // TODO: Implement register API call
    console.log('Register clicked', { username, password });
  }
}
