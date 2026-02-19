import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserResponse } from '../models';

// Declare Keycloak as a global (loaded via angular.json scripts)
declare const Keycloak: any;

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private keycloak: any;

    constructor() {
        this.keycloak = new Keycloak({
            url: 'http://localhost:8091',
            realm: 'skal',
            clientId: 'ui'
        });

        // Additional configuration for Keycloak endpoints
        this.keycloak.tokenUri = 'http://localhost:8091/realms/skal/protocol/openid-connect/token';
        this.keycloak.userInfoUri = 'http://localhost:8091/realms/skal/protocol/openid-connect/userinfo';
        this.keycloak.jwkSetUri = 'http://localhost:8091/realms/skal/protocol/openid-connect/certs';
        this.keycloak.authorizationUri = 'http://localhost:8091/realms/skal/protocol/openid-connect/auth';
        this.keycloak.issuerUri = 'http://localhost:8091/realms/skal';
        this.keycloak.userNameAttribute = 'preferred_username';

        this.keycloak.onAuthLogout = () => {
            console.log('Logout esemény történt');
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
            onLoad: 'login-required',
            scope: 'openid profile email roles',
            redirectUri: 'http://localhost:8090/ui/',
        }).then((authenticated: boolean) => {
            if (authenticated) {
                console.log('Authenticated!');
                console.log('JWT:', this.keycloak.token);
                localStorage.setItem('jwt', this.keycloak.token);
            } else {
                console.log('Not authenticated');
            }
            return authenticated;
        }).catch((err: any) => {
            console.error('Keycloak init failed', err);
            return false;
        });
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
        this.keycloak.logout({
            redirectUri: 'http://localhost:8090/ui/',
        });
    }

    register(): void {
        this.keycloak.register({
            redirectUri: 'http://localhost:8090/ui/',
        });
    }

    getToken(): string | null {
        return this.keycloak.token ?? localStorage.getItem('jwt');
    }
}
