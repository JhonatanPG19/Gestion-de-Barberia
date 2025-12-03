import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot
} from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override readonly router: Router,
    protected readonly keycloak: KeycloakService
  ) {
    super(router, keycloak);
  }

  // Esta función decide: ¿Pasa o no pasa?
  public async isAccessAllowed(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    
    // 1. Si el usuario NO está logueado...
    if (!this.authenticated) {
      // ... lo mandamos a la página de Login de Keycloak
      await this.keycloak.login({
        redirectUri: window.location.origin + state.url
      });
      return false;
    }

    // 2. Si está logueado, verificamos Roles (si la ruta los pide)
    const requiredRoles = route.data['roles'];

    // Si la ruta no exige roles específicos, ¡Pase adelante!
    if (!Array.isArray(requiredRoles) || requiredRoles.length === 0) {
      return true;
    }

    // Si exige roles, miramos si el usuario tiene alguno de ellos
    const hasRole = requiredRoles.some((role) => this.roles.includes(role));

    if (!hasRole) {
        // Si entra aquí, es un usuario logueado pero sin permiso (ej: Cliente queriendo ver Admin)
        alert('No tienes permisos para ver esta sección');
        return false;
    }

    return true;
  }
}