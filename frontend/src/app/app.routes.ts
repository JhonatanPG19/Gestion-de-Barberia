import { Routes } from '@angular/router';

export const routes: Routes = [
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'reservas'
	},
	{
		path: 'reservas',
		loadChildren: () => import('./features/reservas/reservas.routes').then((m) => m.RESERVAS_ROUTES)
	},
	{
		path: '**',
		redirectTo: 'reservas'
	}
];
