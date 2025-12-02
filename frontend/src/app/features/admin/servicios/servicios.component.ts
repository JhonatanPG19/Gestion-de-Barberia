import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ServiciosService, Servicio } from '../../../core/services/servicios.service';

@Component({
  selector: 'app-servicios',
  templateUrl: './servicios.component.html',
  standalone: true,
  imports: [RouterLink, CommonModule]
})
export class ServiciosComponent implements OnInit {
  servicios: Servicio[] = [];


  constructor(private serviciosService: ServiciosService) { }

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
        alert('No se pudieron cargar los servicios.');
      }
    });
  }

  // ✅ Método para eliminar
  eliminarServicio(id: number) {
    if (confirm('¿Eliminar PERMANENTEMENTE este servicio?')) {
      this.serviciosService.eliminarServicio(id).subscribe({
        next: () => {
          this.cargarServicios();
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          alert('Error al eliminar el servicio.');
        }
      });
    }
  }
}