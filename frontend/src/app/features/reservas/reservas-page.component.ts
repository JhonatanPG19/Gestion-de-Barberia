import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ReservaFormComponent } from './components/reserva-form.component';
import { ReservaListComponent } from './components/reserva-list.component';
import { ReservasService, CrearReservaRequest } from './services/reservas.service';
import { ReservaEstado } from './models/reserva.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-reservas-page',
  standalone: true,
  imports: [CommonModule, ReservaListComponent, ReservaFormComponent],
  templateUrl: './reservas-page.component.html',
  styleUrls: ['./reservas-page.component.css']
})
export class ReservasPageComponent implements OnInit {
  readonly reservasService = inject(ReservasService);
  public router = inject(Router);
  private notificationService = inject(NotificationService);

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

  ngOnInit(): void {
    // Cargar reservas del cliente logueado
    const userId = localStorage.getItem('userId');
    console.log('UserId del localStorage:', userId);
    if (userId) {
      console.log('Llamando a cargarReservasCliente con userId:', userId);
      this.reservasService.cargarReservasCliente(parseInt(userId));
    } else {
      console.error('No se encontró userId en localStorage');
    }
  }

  volverInicio(): void {
    this.router.navigate(['/']);
  }

  onCrear(request: CrearReservaRequest): void {
    this.creandoReserva.set(true);
    this.reservasService.crearReserva(request).subscribe({
      next: () => {
        this.creandoReserva.set(false);
        this.notificationService.success('Reserva creada exitosamente', '¡Reserva confirmada!');
      },
      error: (err) => {
        this.creandoReserva.set(false);
        console.error('Error al crear reserva:', err);
        this.notificationService.handleError(err, 'Error al crear la reserva');
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
        this.notificationService.toast('Estado actualizado exitosamente', 'success');
      },
      error: (err) => {
        this.actualizandoEstado.set(false);
        console.error('Error al actualizar estado:', err);
        this.notificationService.handleError(err, 'Error al actualizar el estado');
      }
    });
  }
}
