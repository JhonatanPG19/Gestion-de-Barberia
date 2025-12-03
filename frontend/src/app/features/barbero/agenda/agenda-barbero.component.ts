import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from '../../../services/notification.service';

interface Reserva {
  id: number;
  clienteNombre: string;
  servicioNombre: string;
  fechaHora: string;
  fechaHoraFin: string;
  estado: string;
  tiempoEstimado: number;
  observaciones?: string;
}

@Component({
  selector: 'app-agenda-barbero',
  templateUrl: 'agenda-barbero.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AgendaBarberoComponent implements OnInit {
  reservas = signal<Reserva[]>([]);
  reservasFiltradas = signal<Reserva[]>([]);
  loading = signal(false);
  errorMessage = signal<string>('');
  
  // Obtener barberoId del localStorage (userId del usuario logueado)
  private barberoId: number = 0;

  constructor(
    private http: HttpClient,
    public router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.getBarberoId();
  }

  getBarberoId(): void {
    const userId = localStorage.getItem('userId');
    console.log('UserId del barbero:', userId);
    if (!userId) {
      console.error('No hay userId en localStorage');
      this.errorMessage.set('No se encontró el usuario. Por favor inicia sesión nuevamente.');
      return;
    }

    // Obtener el barberoId asociado al userId
    console.log('Buscando barbero con userId:', userId);
    this.http.get<any>(`http://localhost:8082/api/barbero/by-user/${userId}`).subscribe({
      next: (barbero) => {
        console.log('Barbero encontrado:', barbero);
        this.barberoId = barbero.id;
        console.log('BarberoId asignado:', this.barberoId);
        this.cargarTodasLasReservas(); // Cargar TODAS las reservas primero
      },
      error: (error) => {
        console.error('Error al obtener barbero:', error);
        console.error('Detalles del error:', error.error);
        this.errorMessage.set('No se encontró el barbero asociado a este usuario.');
      }
    });
  }

  cargarTodasLasReservas() {
    if (this.barberoId === 0) {
      console.warn('BarberoId es 0, no se pueden cargar reservas');
      return;
    }
    
    console.log('Cargando TODAS las reservas para barberoId:', this.barberoId);
    this.loading.set(true);
    this.errorMessage.set('');
    
    // Obtener todas las reservas del barbero (sin filtro de fecha)
    this.http.get<Reserva[]>(`http://localhost:8084/api/v1/reservas/barbero/${this.barberoId}/todas`).subscribe({
      next: (data) => {
        console.log('Todas las reservas cargadas:', data);
        this.reservas.set(data);
        this.reservasFiltradas.set(data);
        this.loading.set(false);
        if (data.length === 0) {
          this.errorMessage.set('No tienes reservas registradas aún.');
        }
      },
      error: (error) => {
        console.error('Error al cargar todas las reservas:', error);
        // Si el endpoint no existe, intentar con la fecha de hoy
        console.log('Intentando cargar reservas de hoy...');
        this.cargarReservas();
      }
    });
  }

  cargarReservas() {
    if (this.barberoId === 0) {
      console.warn('BarberoId es 0, no se pueden cargar reservas');
      return;
    }
    
    console.log('Cargando todas las reservas para barberoId:', this.barberoId);
    this.loading.set(true);
    this.errorMessage.set('');
    this.http.get<Reserva[]>(
      `http://localhost:8084/api/v1/reservas/barbero/${this.barberoId}/todas`
    ).subscribe({
      next: (data) => {
        console.log('Reservas cargadas:', data);
        this.reservas.set(data);
        this.reservasFiltradas.set(data);
        this.loading.set(false);
        if (data.length === 0) {
          this.errorMessage.set('No tienes reservas registradas aún.');
        }
      },
      error: (error) => {
        console.error('Error al cargar reservas:', error);
        console.error('Detalles del error:', error.error);
        this.errorMessage.set('Error al cargar las reservas. Intenta nuevamente.');
        this.loading.set(false);
      }
    });
  }

  filtrarPorEstado(estado: string) {
    if (estado === 'todas') {
      this.reservasFiltradas.set(this.reservas());
    } else {
      this.reservasFiltradas.set(
        this.reservas().filter(r => r.estado === estado)
      );
    }
  }

  getEstadoClass(estado: string): string {
    const clases: Record<string, string> = {
      'PENDIENTE': 'warning',
      'CONFIRMADA': 'info',
      'EN_PROCESO': 'primary',
      'COMPLETADA': 'success',
      'CANCELADA': 'danger',
      'NO_ASISTIO': 'secondary'
    };
    return clases[estado] || 'secondary';
  }

  getEstadoTexto(estado: string): string {
    const textos: Record<string, string> = {
      'PENDIENTE': 'Pendiente',
      'CONFIRMADA': 'Confirmada',
      'EN_PROCESO': 'En Proceso',
      'COMPLETADA': 'Completada',
      'CANCELADA': 'Cancelada',
      'NO_ASISTIO': 'No Asistió'
    };
    return textos[estado] || estado;
  }

  formatearHora(fechaHora: string): string {
    return new Date(fechaHora).toLocaleTimeString('es-CO', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  cambiarEstado(id: number, event: Event) {
    const target = event.target as HTMLSelectElement;
    const nuevoEstado = target.value;
    
    console.log(`Cambiando estado de reserva ${id} a ${nuevoEstado}`);
    
    // Mapear el estado al endpoint correspondiente
    let observable;
    switch (nuevoEstado) {
      case 'CONFIRMADA':
        observable = this.http.put(`http://localhost:8084/api/v1/reservas/${id}/confirmar`, {});
        break;
      case 'EN_PROCESO':
        observable = this.http.put(`http://localhost:8084/api/v1/reservas/${id}/iniciar`, {});
        break;
      case 'COMPLETADA':
        observable = this.http.put(`http://localhost:8084/api/v1/reservas/${id}/completar`, {});
        break;
      case 'NO_ASISTIO':
        observable = this.http.put(`http://localhost:8084/api/v1/reservas/${id}/no-asistio`, {});
        break;
      case 'CANCELADA':
        observable = this.http.delete(`http://localhost:8084/api/v1/reservas/${id}`);
        break;
      default:
        console.warn('Estado no soportado:', nuevoEstado);
        return;
    }

    observable.subscribe({
      next: () => {
        console.log('Estado actualizado correctamente');
        this.notificationService.toast('Estado actualizado exitosamente', 'success');
        this.cargarTodasLasReservas();
      },
      error: (err) => {
        console.error('Error al cambiar estado:', err);
        this.notificationService.handleError(err, 'Error al cambiar el estado de la reserva');
        // Recargar para revertir el cambio en el UI
        this.cargarTodasLasReservas();
      }
    });
  }

  iniciarServicio(id: number) {
    this.http.put(`http://localhost:8084/api/v1/reservas/${id}/iniciar`, {}).subscribe({
      next: () => {
        this.notificationService.toast('Servicio iniciado', 'success');
        this.cargarTodasLasReservas();
      },
      error: (err) => {
        console.error('Error al iniciar servicio:', err);
        this.notificationService.handleError(err, 'Error al iniciar el servicio');
      }
    });
  }

  completarServicio(id: number) {
    this.http.put(`http://localhost:8084/api/v1/reservas/${id}/completar`, {}).subscribe({
      next: () => {
        this.notificationService.toast('Servicio completado', 'success');
        this.cargarTodasLasReservas();
      },
      error: (err) => {
        console.error('Error al completar servicio:', err);
        this.notificationService.handleError(err, 'Error al completar el servicio');
      }
    });
  }
}