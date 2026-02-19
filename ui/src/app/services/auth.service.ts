import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserResponse } from '../models';

// Declare Keycloak as a global (loaded via angular.json scripts)
declare const Keycloak: any;

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
    currentUser$: Observable<UserResponse | null> = this.currentUserSubject.asObservable();

    private keycloak: any;

    constructor() {
        this.keycloak = new Keycloak({
            url: 'http://localhost:18080',
            realm: 'skal',
            clientId: 'ps'
        });

        this.keycloak.onAuthLogout = () => {
            console.log('Logout esemény történt');
            this.currentUserSubject.next(null);
            localStorage.removeItem('jwt');
        };
    }

    /**
     * Called once at app startup via APP_INITIALIZER.
     * On first visit (no auth code): check-sso detects no session, user is not logged in.
     * After redirect back from Keycloak: check-sso processes the auth response and retrieves the token.
     */
    init(): Promise<boolean> {
        return this.keycloak.init({
            onLoad: 'check-sso',
            scope: 'openid profile email roles',
            redirectUri: window.location.origin,
        }).then((authenticated: boolean) => {
            if (authenticated) {
                console.log('Authenticated!');
                console.log('JWT:', this.keycloak.token);
                localStorage.setItem('jwt', this.keycloak.token);

                const parsed = this.keycloak.tokenParsed;
                this.currentUserSubject.next({
                    id: 0,
                    username: parsed?.preferred_username ?? '',
                    createdAt: '',
                });
            } else {
                console.log('Not authenticated');
            }
            return authenticated;
        }).catch((err: any) => {
            console.error('Keycloak init failed', err);
            return false;
        });
    }

    get currentUser(): UserResponse | null {
        return this.currentUserSubject.value;
    }

    get isLoggedIn(): boolean {
        return this.currentUserSubject.value !== null;
    }

    /** Triggers redirect to Keycloak login page. */
    login(): void {
        this.keycloak.login({
            redirectUri: window.location.origin,
            scope: 'openid profile email roles',
        });
    }

    logout(): void {
        localStorage.removeItem('jwt');
        this.currentUserSubject.next(null);
        this.keycloak.logout({
            redirectUri: window.location.origin,
        });
    }

    register(): void {
        this.keycloak.register({
            redirectUri: window.location.origin,
        });
    }

    getToken(): string | null {
        return this.keycloak.token ?? localStorage.getItem('jwt');
    }
}
