import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { ReservaFormComponent } from './components/reserva-form.component';
import { ReservaListComponent } from './components/reserva-list.component';
import { ReservasService } from './services/reservas.service';
import { ReservaCreation, ReservaEstado } from './models/reserva.model';

@Component({
  selector: 'app-reservas-page',
  standalone: true,
  imports: [CommonModule, ReservaFormComponent, ReservaListComponent],
  templateUrl: './reservas-page.component.html',
  styleUrls: ['./reservas-page.component.css']
})
export class ReservasPageComponent {
  private readonly reservasService = inject(ReservasService);

  readonly reservas = this.reservasService.reservas;
  readonly resumen = this.reservasService.resumen;
  readonly estadoActual = this.reservasService.filtroActual;
  readonly loading = this.reservasService.loading;
  readonly error = this.reservasService.error;

  readonly heroSubtitle = computed(() => {
    const total = this.reservasService.resumen().total;
    return total > 0 ? `Gestiona ${total} reservas activas en un solo lugar.` : 'Aún no hay reservas registradas.';
  });

  onCrear(reserva: ReservaCreation): void {
    this.reservasService.crearReserva(reserva);
  }

  onFiltroChange(estado: ReservaEstado | 'todas'): void {
    this.reservasService.setFiltro(estado);
  }

  onActualizarEstado(payload: { id: string; estado: ReservaEstado }): void {
    this.reservasService.actualizarEstado(payload.id, payload.estado);
  }
}
