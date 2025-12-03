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
    console.log('Landing: Checking authentication...');
    const auth = await this.keycloak.isLoggedIn();
    console.log('Landing: Is logged in?', auth);
    
    if (!auth) {
      console.log('Landing: Not authenticated, redirecting to Keycloak login...');
      this.keycloak.login({
        redirectUri: window.location.origin + '/landing'
      });
      return;
    }

    const roles = this.keycloak.getUserRoles(true);
    console.log('Landing: User roles (raw):', roles);
    const rolesUpper = roles.map((role) => role.toUpperCase());
    console.log('Landing: User roles (normalized):', rolesUpper);

    if (rolesUpper.includes('ADMIN')) {
      console.log('Landing: Navigating to admin panel...');
      await this.router.navigate(['/admin/barberos']);
      return;
    }

    if (rolesUpper.includes('BARBERO')) {
      console.log('Landing: Navigating to barbero agenda...');
      await this.router.navigate(['/barbero/agenda']);
      return;
    }

    console.log('Landing: Navigating to reservas (default for CLIENTE)...');
    await this.router.navigate(['/reservas']);
  }
}