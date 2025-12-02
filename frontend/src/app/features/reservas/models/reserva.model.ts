export type ReservaEstado = 'PENDIENTE' | 'CONFIRMADA' | 'EN_PROCESO' | 'COMPLETADA' | 'CANCELADA' | 'NO_ASISTIO';

export interface Reserva {
  id: string;
  cliente: string;
  servicio: string;
  telefono: string;
  fecha: string; // ISO date string YYYY-MM-DD
  hora: string; // HH:mm 24h format
  estado: ReservaEstado;
  notas?: string;
}

export type ReservaCreation = Omit<Reserva, 'id' | 'estado'> & { estado?: ReservaEstado };
