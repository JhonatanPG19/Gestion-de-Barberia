// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export interface TokenResponse {
  access_token: string;
  refresh_token?: string;
  id_token?: string;
  expires_in: number;
  token_type: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  username: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private keycloakUrl = 'http://localhost:8080';
  private realm = 'BarberiaRealm';
  private clientId = 'barberia-frontend-client'; // cliente público en Keycloak
  private redirectUri = 'http://localhost:4200/auth/callback'; // debe estar en Valid Redirect URIs
  private apiUrl = 'http://localhost:8081/api/v1/usuarios'; // ms-usuarios

  private pkceVerifierKey = 'pkce_code_verifier';
  private oauthStateKey = 'oauth_state';
  private accessTokenKey = 'access_token';
  private refreshTokenKey = 'refresh_token';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  // ==== LOGIN PERSONALIZADO (sin Keycloak) ====
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(response => {
        localStorage.setItem(this.accessTokenKey, response.token);
        localStorage.setItem('user_id', response.userId.toString());
        localStorage.setItem('user_role', response.rol);
      })
    );
  }

  // ==== 1. INICIAR LOGIN (desde /admin/login) ====
  startLoginFlow(): void {
    const state = crypto.randomUUID();
    const codeVerifier = this.generateCodeVerifier();
    sessionStorage.setItem(this.pkceVerifierKey, codeVerifier);
    sessionStorage.setItem(this.oauthStateKey, state);

    this.generateCodeChallenge(codeVerifier).then(codeChallenge => {
      const authUrl = new URL(
        `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/auth`
      );
      authUrl.searchParams.set('client_id', this.clientId);
      authUrl.searchParams.set('response_type', 'code');
      authUrl.searchParams.set('redirect_uri', this.redirectUri);
      authUrl.searchParams.set('scope', 'openid profile');
      authUrl.searchParams.set('state', state);
      authUrl.searchParams.set('code_challenge', codeChallenge);
      authUrl.searchParams.set('code_challenge_method', 'S256');

      window.location.href = authUrl.toString();
    });
  }

  // ==== 2. INTERCAMBIAR CODE POR TOKENS (desde /auth/callback) ====
  exchangeCodeForTokens(code: string, returnedState: string): Observable<TokenResponse> {
    const storedState = sessionStorage.getItem(this.oauthStateKey);
    const codeVerifier = sessionStorage.getItem(this.pkceVerifierKey);

    if (!storedState || storedState !== returnedState) {
      throw new Error('Invalid state');
    }
    if (!codeVerifier) {
      throw new Error('Missing PKCE code verifier');
    }

    const tokenUrl =
      `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/token`;

    const body = new HttpParams()
      .set('grant_type', 'authorization_code')
      .set('client_id', this.clientId)
      .set('code', code)
      .set('redirect_uri', this.redirectUri)
      .set('code_verifier', codeVerifier);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.post<TokenResponse>(tokenUrl, body.toString(), { headers }).pipe(
      tap(tokens => {
        this.storeTokens(tokens);
        // ya no necesitamos state/verifier
        sessionStorage.removeItem(this.oauthStateKey);
        sessionStorage.removeItem(this.pkceVerifierKey);
      })
    );
  }

  // ==== 3. GESTIÓN DE TOKENS ====
  private storeTokens(tokens: TokenResponse): void {
    localStorage.setItem(this.accessTokenKey, tokens.access_token);
    if (tokens.refresh_token) {
      localStorage.setItem(this.refreshTokenKey, tokens.refresh_token);
    }
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessTokenKey);
  }

  logout(): void {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    this.router.navigate(['/admin/login']);
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  // ==== 4. UTILIDADES PKCE ====
  private generateCodeVerifier(): string {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return this.base64UrlEncode(array);
  }

  private async generateCodeChallenge(verifier: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return this.base64UrlEncode(new Uint8Array(digest));
  }

  private base64UrlEncode(array: Uint8Array): string {
    let str = '';
    array.forEach(b => str += String.fromCharCode(b));
    return btoa(str)
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
  }
}
