import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { BarberosComponent } from './features/admin/barberos/barberos.component';
import { BarberoFormComponent } from './features/admin/barberos/barbero-form/barbero-form.component';
import { ServiciosComponent } from './features/admin/servicios/servicios.component';
import { ServicioFormComponent } from './features/admin/servicios/servicio-form/servicio-form.component';
import { ReservasPageComponent } from './features/reservas/reservas-page.component';
import { RegisterComponent } from './auth/register/register.component';
import { AgendaBarberoComponent } from './features/barbero/agenda/agenda-barbero.component';
import { AuthGuard } from './guards/auth.guard';
import { LandingComponent } from './components/landing/landing.component';
import { NgModule } from '@angular/core';
export const routes: Routes = [
    { path: 'register', component: RegisterComponent },
    { path: 'admin/barberos', component: BarberosComponent },
    { path: 'admin/barberos/nuevo', component: BarberoFormComponent },
    { path: 'admin/barberos/:id/editar', component: BarberoFormComponent },
    { path: 'reservas', component: ReservasPageComponent },
    { path: '', redirectTo: 'admin/login', pathMatch: 'full' },
    { path: 'admin/servicios', component: ServiciosComponent },
    { path: 'admin/servicios/nuevo', component: ServicioFormComponent },
    { path: 'admin/servicios/:id/editar', component: ServicioFormComponent },
    { path: 'barbero/agenda', component: AgendaBarberoComponent },
        { 
    path: '', 
    component: LandingComponent, // <--- Aquí va el componente nuevo
    canActivate: [AuthGuard]     // <--- Esto fuerza el login inmediato
  },

];
