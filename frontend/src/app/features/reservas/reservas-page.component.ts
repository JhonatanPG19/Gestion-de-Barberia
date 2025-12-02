import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { ReservaFormComponent } from './components/reserva-form.component';
import { ReservaListComponent } from './components/reserva-list.component';
import { ReservasService, CrearReservaRequest } from './services/reservas.service';
import { ReservaEstado } from './models/reserva.model';

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

  readonly creandoReserva = signal(false);
  readonly actualizandoEstado = signal(false);

  readonly heroSubtitle = computed(() => {
    const total = this.reservasService.resumen().total;
    return total > 0 ? `Gestiona ${total} reservas activas en un solo lugar.` : 'Aún no hay reservas registradas.';
  });

  onCrear(request: CrearReservaRequest): void {
    this.creandoReserva.set(true);
    this.reservasService.crearReserva(request).subscribe({
      next: () => {
        this.creandoReserva.set(false);
        console.log('Reserva creada exitosamente');
      },
      error: (err) => {
        this.creandoReserva.set(false);
        console.error('Error al crear reserva:', err);
        alert('Error al crear reserva: ' + (err.error?.message || err.message));
      }
    });
  }

  onFiltroChange(estado: ReservaEstado | 'todas'): void {
    this.reservasService.setFiltro(estado);
  }

  onActualizarEstado(payload: { id: string; estado: ReservaEstado }): void {
    const id = parseInt(payload.id, 10);
    this.actualizandoEstado.set(true);

    // Mapear el estado a la llamada HTTP correspondiente
    let observable;
    switch (payload.estado) {
      case 'CONFIRMADA':
        observable = this.reservasService.confirmarReserva(id);
        break;
      case 'EN_PROCESO':
        observable = this.reservasService.iniciarServicio(id);
        break;
      case 'COMPLETADA':
        observable = this.reservasService.completarServicio(id);
        break;
      case 'NO_ASISTIO':
        observable = this.reservasService.marcarNoAsistio(id);
        break;
      case 'CANCELADA':
        observable = this.reservasService.cancelarReserva(id);
        break;
      default:
        // Para PENDIENTE u otros estados, no hay endpoint específico
        this.actualizandoEstado.set(false);
        console.log('No hay acción para el estado:', payload.estado);
        return;
    }

    observable.subscribe({
      next: () => {
        this.actualizandoEstado.set(false);
        console.log('Estado actualizado exitosamente');
      },
      error: (err) => {
        this.actualizandoEstado.set(false);
        console.error('Error al actualizar estado:', err);
        alert('Error al actualizar estado: ' + (err.error?.message || err.message));
      }
    });
  }
}
