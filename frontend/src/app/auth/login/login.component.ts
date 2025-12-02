// src/app/auth/login/login.component.ts
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  template: `<p>Redirigiendo a la pantalla de inicio de sesión...</p>`
})
export class LoginComponent implements OnInit {

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.startLoginFlow();
  }
}
