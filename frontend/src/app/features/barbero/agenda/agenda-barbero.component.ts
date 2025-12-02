import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReservasService, EstadoBarberoResponse } from '../../../core/services/reservas.service';

@Component({
  selector: 'app-agenda-barbero',
  templateUrl: 'agenda-barbero.component.html',
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class AgendaBarberoComponent implements OnInit {
  estadoBarbero: EstadoBarberoResponse | null = null;
  fechaHoy = new Date().toLocaleDateString('es-CO', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });

  // Supongamos que el barbero logueado tiene ID = 1 (en producción, lo obtendrías del token JWT)
  private barberoId = 1;

  constructor(private reservasService: ReservasService) {}

  ngOnInit() {
    this.cargarEstadoBarbero();
  }

  cargarEstadoBarbero() {
    this.reservasService.getEstadoBarbero(this.barberoId).subscribe({
      next: (data) => {
        this.estadoBarbero = data;
      },
      error: (error) => {
        console.error('Error al cargar estado del barbero:', error);
        // Opcional: mostrar mensaje de error en UI
      }
    });
  }
}