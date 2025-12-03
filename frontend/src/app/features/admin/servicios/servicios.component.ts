import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ServiciosService, Servicio } from '../../../core/services/servicios.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-servicios',
  templateUrl: './servicios.component.html',
  standalone: true,
  imports: [RouterLink, CommonModule]
})
export class ServiciosComponent implements OnInit {
  servicios: Servicio[] = [];


  constructor(
    private serviciosService: ServiciosService,
    private notificationService: NotificationService
  ) { }

  ngOnInit() {
    this.cargarServicios();
  }


  cargarServicios() {
    this.serviciosService.getServicios().subscribe({
      next: (data) => {
        this.servicios = data;
      },
      error: (err) => {
        console.error('Error al cargar servicios:', err);
        this.notificationService.handleError(err, 'No se pudieron cargar los servicios');
      }
    });
  }

  // ✅ Método para eliminar
  eliminarServicio(id: number) {
    this.notificationService.confirm(
      '¿Está seguro de eliminar PERMANENTEMENTE este servicio?',
      'Confirmar eliminación'
    ).then((result) => {
      if (result.isConfirmed) {
        this.serviciosService.eliminarServicio(id).subscribe({
          next: () => {
            this.notificationService.toast('Servicio eliminado exitosamente', 'success');
            this.cargarServicios();
          },
          error: (err) => {
            this.notificationService.handleError(err, 'Error al eliminar el servicio');
          }
        });
      }
    });
  }
}