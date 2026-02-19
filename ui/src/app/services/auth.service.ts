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

    constructor() {}

    get currentUser(): UserResponse | null {
        return this.currentUserSubject.value;
    }

    get isLoggedIn(): boolean {
        return this.currentUserSubject.value !== null;
    }

    login(username: string, password: string): void {
        console.log('Login clicked', { username, password });

        var keycloak = new Keycloak({
            url: 'http://localhost:18080',
            realm: 'skal',
            clientId: 'ps'
        });
// Additional configuration for Keycloak endpoints
        keycloak.tokenUri = 'http://localhost:18080/realms/skal/protocol/openid-connect/token';
        keycloak.userInfoUri = 'http://localhost:18080/realms/skal/protocol/openid-connect/userinfo';
        keycloak.jwkSetUri = 'http://localhost:18080/realms/skal/protocol/openid-connect/certs';
        keycloak.authorizationUri = 'http://localhost:18080/realms/skal/protocol/openid-connect/auth';
        keycloak.issuerUri = 'http://localhost:18080/realms/skal';
        keycloak.userNameAttribute = 'preferred_username';
        keycloak.onAuthLogout = () => {
            console.log('Logout esemény történt');
        };

        keycloak.init({
            onLoad: 'login-required',
            scope: 'openid profile email roles',
            redirectUri: window.location,
        }).then(function (authenticated:boolean) {
            if (authenticated) {
                console.log('JWT:', keycloak.token);
                localStorage.setItem('jwt', keycloak.token);
            } else {
                console.log('not authenticated');
            }
        }).catch(function () {
            console.log('failed to initialize');
        });
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
