import { Routes } from '@angular/router';
import { BarberosComponent } from './features/admin/barberos/barberos.component';
import { BarberoFormComponent } from './features/admin/barberos/barbero-form/barbero-form.component';
import { ServiciosComponent } from './features/admin/servicios/servicios.component';
import { ServicioFormComponent } from './features/admin/servicios/servicio-form/servicio-form.component';

export const routes: Routes = [
    { path: 'admin/barberos', component: BarberosComponent },
    { path: 'admin/barberos/nuevo', component: BarberoFormComponent },
    { path: 'admin/barberos/:id/editar', component: BarberoFormComponent },
    { path: '', redirectTo: 'admin/barberos', pathMatch: 'full' },
    { path: 'admin/servicios', component: ServiciosComponent },
    { path: 'admin/servicios/nuevo', component: ServicioFormComponent },
    { path: 'admin/servicios/:id/editar', component: ServicioFormComponent }
];
