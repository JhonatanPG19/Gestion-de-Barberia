// src/app/auth/auth-callback/auth-callback.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `
    <div class="callback-container">
      <p>Procesando autenticación...</p>
    </div>
  `
})
export class AuthCallbackComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const code = params['code'];
      const state = params['state'];
      const error = params['error'];

      if (error) {
        // Manejar errores de Keycloak (access_denied, etc.)
        console.error('Error in callback', error);
        this.router.navigate(['/admin/login']);
        return;
      }

      if (!code || !state) {
        this.router.navigate(['/admin/login']);
        return;
      }

      this.authService.exchangeCodeForTokens(code, state).subscribe({
        next: () => {
          // Redirige donde quieras después de login
          this.router.navigate(['/']);
        },
        error: err => {
          console.error('Error exchanging code for tokens', err);
          this.router.navigate(['/admin/login']);
        }
      });
    });
  }
}
