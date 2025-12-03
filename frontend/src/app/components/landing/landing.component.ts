import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="height: 100vh; display: flex; justify-content: center; align-items: center;">
      <h2>🔄 Redirigiendo a tu panel...</h2>
    </div>
  `
})
export class LandingComponent implements OnInit {

  constructor(
    private keycloak: KeycloakService,
    private router: Router
  ) {}

  async ngOnInit() {
    // Si estamos aquí, el AuthGuard ya garantizó que el usuario está logueado.
    
    // 1. Obtenemos los roles
    const roles = this.keycloak.getUserRoles();
    
    // 2. Decidimos a dónde mandarlo
    if (roles.includes('ADMIN')) {
      this.router.navigate(['/admin']);
    } else if (roles.includes('BARBERO')) {
      this.router.navigate(['/barbero/agenda']); // Ajusta a tu ruta real
    } else {
      // Es un cliente
      // Si aún no tienes componente de reservas, mándalo a una ruta temporal o crea una
      this.router.navigate(['/register']); // O '/reservas' cuando exista
    }
  }
}