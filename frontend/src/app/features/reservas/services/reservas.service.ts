import { Injectable, computed, signal } from '@angular/core';
import { Reserva, ReservaCreation, ReservaEstado } from '../models/reserva.model';

const MOCK_RESERVAS: Reserva[] = [
  {
    id: '1',
    cliente: 'Juan Pérez',
    servicio: 'Corte fade + barba',
    telefono: '3124567890',
    fecha: '2025-12-01',
    hora: '09:00',
    estado: 'CONFIRMADA',
    notas: 'Prefiere navaja tradicional'
  },
  {
    id: '2',
    cliente: 'Ana Gómez',
    servicio: 'Color + peinado',
    telefono: '3009876543',
    fecha: '2025-12-01',
    hora: '11:30',
    estado: 'PENDIENTE'
  },
  {
    id: '3',
    cliente: 'Luis Herrera',
    servicio: 'Afeitado clásico',
    telefono: '3012223344',
    fecha: '2025-12-02',
    hora: '15:00',
    estado: 'CANCELADA',
    notas: 'Solicitó reagendar'
  }
];

@Injectable({ providedIn: 'root' })
export class ReservasService {
  private readonly _reservas = signal<Reserva[]>(MOCK_RESERVAS);
  private readonly _estadoFiltro = signal<ReservaEstado | 'todas'>('todas');
  private readonly _loading = signal(false);
  private readonly _error = signal<string | null>(null);

  readonly filtroActual = this._estadoFiltro.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  readonly reservas = computed(() => {
    const estado = this._estadoFiltro();
    const data = this._reservas();
    return estado === 'todas' ? data : data.filter((reserva) => reserva.estado === estado);
  });

  readonly resumen = computed(() => {
    const data = this._reservas();
    const count = (estado: ReservaEstado) => data.filter((reserva) => reserva.estado === estado).length;
    return {
      total: data.length,
      pendientes: count('PENDIENTE'),
      confirmadas: count('CONFIRMADA'),
      canceladas: count('CANCELADA')
    };
  });

  setFiltro(estado: ReservaEstado | 'todas'): void {
    this._estadoFiltro.set(estado);
  }

  crearReserva(reserva: ReservaCreation): void {
    const nueva: Reserva = {
      ...reserva,
      id: crypto.randomUUID(),
      estado: reserva.estado ?? 'PENDIENTE'
    };
    this._reservas.update((reservas) => [nueva, ...reservas]);
  }

  actualizarEstado(id: string, estado: ReservaEstado): void {
    this._reservas.update((reservas) =>
      reservas.map((reserva) => (reserva.id === id ? { ...reserva, estado } : reserva))
    );
  }

  // Método listo para conectarse a un backend cuando esté disponible.
  sincronizarConBackend(promise: Promise<Reserva[]>): void {
    this._loading.set(true);
    promise
      .then((data) => this._reservas.set(data))
      .catch((err) => this._error.set(err?.message ?? 'Error desconocido cargando reservas'))
      .finally(() => this._loading.set(false));
  }
}
