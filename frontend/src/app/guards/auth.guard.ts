import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private notificationService: NotificationService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // Pequeño delay para asegurar que localStorage se haya actualizado después del login
    const token = localStorage.getItem('access_token');
    const rol = localStorage.getItem('rol');
    
    console.log('AuthGuard - Token:', token);
    console.log('AuthGuard - Rol:', rol);
    console.log('AuthGuard - Ruta:', state.url);
    
    // 1. Validar si está logueado
    if (!token || !rol) {
      console.log('No hay sesión activa, redirigiendo a login');
      // Solo redirigir si no estamos ya en login
      if (state.url !== '/login') {
        this.router.navigate(['/login']);
      }
      return false;
    }

    // 2. Validar rol según la ruta
    const requiredRoles = route.data['roles'] as string[] | undefined;
    
    if (requiredRoles && requiredRoles.length > 0) {
      const hasRole = requiredRoles.some(r => r.toUpperCase() === rol.toUpperCase());
      
      if (!hasRole) {
        console.log(`Acceso denegado. Se requiere rol: ${requiredRoles.join(' o ')}, pero tienes: ${rol}`);
        this.notificationService.warning('No tienes permisos para acceder a esta sección', 'Acceso Denegado');
        // Redirigir según el rol que tiene
        if (rol.toUpperCase() === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (rol.toUpperCase() === 'BARBERO') {
          this.router.navigate(['/barbero/agenda']);
        } else {
          this.router.navigate(['/reservas']);
        }
        return false;
      }
    }

    console.log('Acceso permitido');
    return true;
  }
}