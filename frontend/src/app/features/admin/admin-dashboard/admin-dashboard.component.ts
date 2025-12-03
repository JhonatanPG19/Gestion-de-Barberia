import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="admin-dashboard">
      <div class="hero-section">
        <div class="container">
          <h1 class="hero-title">Panel de Administración</h1>
          <p class="hero-subtitle">Gestiona todos los aspectos de tu barbería</p>
        </div>
      </div>

      <div class="container">
        <div class="dashboard-grid">
          <!-- Card de Barberos -->
          <div class="dashboard-card" (click)="navigateTo('/admin/barberos')">
            <div class="card-icon barberos">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
              </svg>
            </div>
            <h3 class="card-title">Gestión de Barberos</h3>
            <p class="card-description">Administra el personal de tu barbería</p>
          </div>

          <!-- Card de Servicios -->
          <div class="dashboard-card" (click)="navigateTo('/admin/servicios')">
            <div class="card-icon servicios">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="9" y1="9" x2="15" y2="9"></line>
                <line x1="9" y1="15" x2="15" y2="15"></line>
              </svg>
            </div>
            <h3 class="card-title">Gestión de Servicios</h3>
            <p class="card-description">Configura los servicios ofrecidos</p>
          </div>

          <!-- Card de Crear Reserva -->
          <div class="dashboard-card" (click)="navigateTo('/admin/reservas/crear')">
            <div class="card-icon reservas">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
                <line x1="12" y1="14" x2="12" y2="18"></line>
                <line x1="10" y1="16" x2="14" y2="16"></line>
              </svg>
            </div>
            <h3 class="card-title">Crear Reserva</h3>
            <p class="card-description">Registra una nueva reserva para un cliente</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .admin-dashboard {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .hero-section {
      padding: 3rem 0;
      color: white;
      text-align: center;
    }

    .hero-title {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: 0.5rem;
    }

    .hero-subtitle {
      font-size: 1.2rem;
      opacity: 0.9;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 1.5rem;
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 2rem;
      padding: 2rem 0 4rem;
    }

    .dashboard-card {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .dashboard-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
    }

    .card-icon {
      width: 80px;
      height: 80px;
      margin: 0 auto 1.5rem;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
    }

    .card-icon.barberos {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .card-icon.servicios {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }

    .card-icon.reservas {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }

    .card-title {
      font-size: 1.5rem;
      font-weight: 600;
      color: #2d3748;
      margin-bottom: 0.5rem;
    }

    .card-description {
      color: #718096;
      font-size: 1rem;
    }

    @media (max-width: 768px) {
      .hero-title {
        font-size: 2rem;
      }

      .dashboard-grid {
        grid-template-columns: 1fr;
        gap: 1.5rem;
      }
    }
  `]
})
export class AdminDashboardComponent {
  constructor(private router: Router) {}

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
