import { Routes } from '@angular/router';
import { BarberosComponent } from './features/admin/barberos/barberos.component';
import { BarberoFormComponent } from './features/admin/barberos/barbero-form/barbero-form.component';
import { ServiciosComponent } from './features/admin/servicios/servicios.component';
import { ServicioFormComponent } from './features/admin/servicios/servicio-form/servicio-form.component';
import { ReservasPageComponent } from './features/reservas/reservas-page.component';

export const routes: Routes = [
    { path: 'admin/barberos', component: BarberosComponent },
    { path: 'admin/barberos/nuevo', component: BarberoFormComponent },
    { path: 'admin/barberos/:id/editar', component: BarberoFormComponent },
    { path: 'reservas', component: ReservasPageComponent },
    { path: '', redirectTo: 'reservas', pathMatch: 'full' },
    { path: 'admin/servicios', component: ServiciosComponent },
    { path: 'admin/servicios/nuevo', component: ServicioFormComponent },
    { path: 'admin/servicios/:id/editar', component: ServicioFormComponent }
];
