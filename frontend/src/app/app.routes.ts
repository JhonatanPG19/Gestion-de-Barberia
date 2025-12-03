import { Routes } from '@angular/router';
import { BarberosComponent } from './features/admin/barberos/barberos.component';
import { BarberoFormComponent } from './features/admin/barberos/barbero-form/barbero-form.component';
import { ServiciosComponent } from './features/admin/servicios/servicios.component';
import { ServicioFormComponent } from './features/admin/servicios/servicio-form/servicio-form.component';
import { ReservasPageComponent } from './features/reservas/reservas-page.component';
import { RegisterComponent } from './auth/register/register.component';
import { AgendaBarberoComponent } from './features/barbero/agenda/agenda-barbero.component';
import { LoginComponent } from './auth/login/login.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard.component';
import { AdminReservaFormComponent } from './features/admin/reservas/admin-reserva-form.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'admin',
    component: AdminDashboardComponent
  },
  {
    path: 'admin/reservas/crear',
    component: AdminReservaFormComponent
  },
  {
    path: 'admin/barberos',
    component: BarberosComponent
  },
  {
    path: 'admin/barberos/nuevo',
    component: BarberoFormComponent
  },
  {
    path: 'admin/barberos/:id/editar',
    component: BarberoFormComponent
  },
  {
    path: 'admin/servicios',
    component: ServiciosComponent
  },
  {
    path: 'admin/servicios/nuevo',
    component: ServicioFormComponent
  },
  {
    path: 'admin/servicios/:id/editar',
    component: ServicioFormComponent
  },
  {
    path: 'barbero/agenda',
    component: AgendaBarberoComponent
  },
  {
    path: 'reservas',
    component: ReservasPageComponent
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
