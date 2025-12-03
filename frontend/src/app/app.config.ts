import { ApplicationConfig, APP_INITIALIZER, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

// Función para iniciar Keycloak antes de que arranque la app
function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080', // Tu Keycloak en Docker
        realm: 'BarberiaRealm',
        clientId: 'barberia-frontend'
      },
      initOptions: {
        onLoad: 'check-sso', // Revisa si ya hay sesión sin forzar login
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html'
      },
      // ¡ESTO REEMPLAZA TU INTERCEPTOR MANUAL!
      enableBearerInterceptor: true, 
      bearerPrefix: 'Bearer',
    });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    importProvidersFrom(KeycloakAngularModule),  // Importar módulo de Keycloak
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }
  ]
};
