// src/app/auth/login/login.component.ts
import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.component.html'

})
export class LoginComponent {

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.startLoginFlow();
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}
