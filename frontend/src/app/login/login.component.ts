import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  private keycloakUrl = 'http://localhost:8080';
  private realm = 'BarberiaRealm';
  private clientId = 'barberia-frontend-client'; // cliente público para el SPA
  private redirectUri = 'http://localhost:4200/'; // ruta donde tu Angular escucha el callback

  login(): void {
    const state = crypto.randomUUID();
    const codeVerifier = this.generateCodeVerifier();
    const codeChallenge = this.sha256ToBase64Url(codeVerifier);

    // Guarda verifier y state en sessionStorage
    sessionStorage.setItem('pkce_code_verifier', codeVerifier);
    sessionStorage.setItem('oauth_state', state);

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
  }

  goToRegister(): void {
    // Lo normal: usar el propio registro de Keycloak
    const registerUrl = new URL(
      `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/registrations`
    );
    registerUrl.searchParams.set('client_id', this.clientId);
    registerUrl.searchParams.set('response_type', 'code');
    registerUrl.searchParams.set('redirect_uri', this.redirectUri);
    registerUrl.searchParams.set('scope', 'openid profile');

    window.location.href = registerUrl.toString();
  }

  private generateCodeVerifier(): string {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return this.base64UrlEncode(array);
  }

  private sha256ToBase64Url(verifier: string): string {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);
    // crypto.subtle es async, en código real deberías manejar la promesa
    // aquí lo dejo como esquema conceptual
    // Usa una función async/await para esto en tu implementación final
    return ''; // Implementa hash + base64url
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
