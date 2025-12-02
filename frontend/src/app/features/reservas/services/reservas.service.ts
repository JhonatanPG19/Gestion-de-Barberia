import { Injectable, computed, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap, catchError, of } from 'rxjs';
import { Reserva, ReservaCreation, ReservaEstado } from '../models/reserva.model';

// Interfaz para la respuesta del backend
export interface ReservaResponse {
  id: number;
  clienteId: number;
  clienteNombre: string;
  barberoId: number;
  barberoNombre: string;
  servicioId: number;
  servicioNombre: string;
  fechaHora: string; // ISO DateTime
  fechaHoraFin: string;
  estado: ReservaEstado;
  tiempoEstimado: number;
  observaciones?: string;
  fechaCreacion: string;
  esWalkIn: boolean;
}

// Interfaz para crear reserva
export interface CrearReservaRequest {
  clienteId: number;
  barberoId: number;
  servicioId: number;
  fechaHora: string; // ISO DateTime
  observaciones?: string;
  esWalkIn?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ReservasService {
  private readonly apiUrl = 'http://localhost:8084/api/v1/reservas';
  
  private readonly _reservas = signal<Reserva[]>([]);
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

  constructor(private http: HttpClient) {
    // Cargar reservas al inicializar
    this.cargarReservas();
  }

  // ============ Métodos de UI ============
  
  setFiltro(estado: ReservaEstado | 'todas'): void {
    this._estadoFiltro.set(estado);
  }

  // ============ Métodos HTTP ============

  /**
   * Obtener todas las reservas de un día específico
   */
  obtenerReservasDelDia(fecha: string): Observable<ReservaResponse[]> {
    return this.http.get<ReservaResponse[]>(`${this.apiUrl}/dia/${fecha}`);
  }

  /**
   * Obtener reserva por ID
   */
  obtenerReservaPorId(id: number): Observable<ReservaResponse> {
    return this.http.get<ReservaResponse>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtener reservas de un cliente
   */
  obtenerReservasPorCliente(clienteId: number): Observable<ReservaResponse[]> {
    return this.http.get<ReservaResponse[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  /**
   * Obtener reservas de un barbero en una fecha
   */
  obtenerReservasPorBarbero(barberoId: number, fecha: string): Observable<ReservaResponse[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<ReservaResponse[]>(`${this.apiUrl}/barbero/${barberoId}`, { params });
  }

  /**
   * Obtener horarios disponibles
   */
  obtenerHorariosDisponibles(barberoId: number, servicioId: number, fecha: string): Observable<string[]> {
    const params = new HttpParams()
      .set('barberoId', barberoId.toString())
      .set('servicioId', servicioId.toString())
      .set('fecha', fecha);
    return this.http.get<string[]>(`${this.apiUrl}/disponibilidad`, { params });
  }

  /**
   * Crear una nueva reserva
   */
  crearReserva(request: CrearReservaRequest): Observable<ReservaResponse> {
    this._loading.set(true);
    return this.http.post<ReservaResponse>(this.apiUrl, request).pipe(
      tap((response) => {
        const nuevaReserva = this.convertirAReserva(response);
        this._reservas.update((reservas) => [nuevaReserva, ...reservas]);
        this._loading.set(false);
      }),
      catchError((error) => {
        this._error.set(error?.message ?? 'Error al crear reserva');
        this._loading.set(false);
        throw error;
      })
    );
  }

  /**
   * Confirmar una reserva
   */
  confirmarReserva(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/confirmar`, {}).pipe(
      tap(() => {
        this.actualizarEstadoLocal(id.toString(), 'CONFIRMADA');
      })
    );
  }

  /**
   * Iniciar servicio
   */
  iniciarServicio(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/iniciar`, {}).pipe(
      tap(() => {
        this.actualizarEstadoLocal(id.toString(), 'EN_PROCESO');
      })
    );
  }

  /**
   * Completar servicio
   */
  completarServicio(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/completar`, {}).pipe(
      tap(() => {
        this.actualizarEstadoLocal(id.toString(), 'COMPLETADA');
      })
    );
  }

  /**
   * Marcar como no asistido
   */
  marcarNoAsistio(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/no-asistio`, {}).pipe(
      tap(() => {
        this.actualizarEstadoLocal(id.toString(), 'NO_ASISTIO');
      })
    );
  }

  /**
   * Cancelar reserva
   */
  cancelarReserva(id: number, motivo?: string): Observable<void> {
    const params = motivo ? new HttpParams().set('motivo', motivo) : undefined;
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { params }).pipe(
      tap(() => {
        this.actualizarEstadoLocal(id.toString(), 'CANCELADA');
      })
    );
  }

  /**
   * Reprogramar reserva
   */
  reprogramarReserva(id: number, nuevaFechaHora: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/reprogramar`, { nuevaFechaHora });
  }

  // ============ Métodos auxiliares ============

  /**
   * Cargar todas las reservas (por ejemplo, del día actual)
   */
  private cargarReservas(): void {
    this._loading.set(true);
    const hoy = new Date().toISOString().split('T')[0]; // Formato: YYYY-MM-DD
    
    this.obtenerReservasDelDia(hoy).pipe(
      tap((reservas) => {
        const reservasConvertidas = reservas.map(r => this.convertirAReserva(r));
        this._reservas.set(reservasConvertidas);
        this._loading.set(false);
      }),
      catchError((error) => {
        this._error.set(error?.message ?? 'Error al cargar reservas');
        this._loading.set(false);
        return of([]);
      })
    ).subscribe();
  }

  /**
   * Actualizar el estado de una reserva localmente
   */
  private actualizarEstadoLocal(id: string, estado: ReservaEstado): void {
    this._reservas.update((reservas) =>
      reservas.map((reserva) => (reserva.id === id ? { ...reserva, estado } : reserva))
    );
  }

  /**
   * Convertir ReservaResponse del backend a modelo Reserva del frontend
   */
  private convertirAReserva(response: ReservaResponse): Reserva {
    const fechaHora = new Date(response.fechaHora);
    return {
      id: response.id.toString(),
      cliente: response.clienteNombre,
      servicio: response.servicioNombre,
      telefono: '', // No viene en la respuesta, podrías obtenerlo del cliente si es necesario
      fecha: fechaHora.toISOString().split('T')[0],
      hora: fechaHora.toTimeString().substring(0, 5),
      estado: response.estado,
      notas: response.observaciones
    };
  }
}
