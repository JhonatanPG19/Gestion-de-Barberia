import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Reserva, ReservaEstado } from '../models/reserva.model';

type EstadoFiltro = ReservaEstado | 'todas';

export interface ResumenReservas {
  total: number;
  pendientes: number;
  confirmadas: number;
  canceladas: number;
}

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reserva-list.component.html',
  styleUrls: ['./reserva-list.component.css']
})
export class ReservaListComponent {
  @Input({ required: true }) reservas: Reserva[] | null = [];
  @Input({ required: true }) resumen!: ResumenReservas;
  @Input() estadoActual: EstadoFiltro = 'todas';
  @Input() loading = false;

  @Output() estadoChange = new EventEmitter<EstadoFiltro>();
  @Output() actualizarEstado = new EventEmitter<{ id: string; estado: ReservaEstado }>();

  readonly filtros: { label: string; value: EstadoFiltro }[] = [
    { label: 'Todas', value: 'todas' },
    { label: 'Pendientes', value: 'PENDIENTE' },
    { label: 'Confirmadas', value: 'CONFIRMADA' },
    { label: 'Canceladas', value: 'CANCELADA' }
  ];

  cambiarFiltro(valor: EstadoFiltro): void {
    this.estadoChange.emit(valor);
  }

  cambiarEstado(id: string, estado: ReservaEstado): void {
    this.actualizarEstado.emit({ id, estado });
  }

  onEstadoSelect(id: string, event: Event): void {
    const target = event.target as HTMLSelectElement | null;
    const value = target?.value as ReservaEstado | undefined;
    if (!value) {
      return;
    }
    this.cambiarEstado(id, value);
  }

  badgeClass(estado: ReservaEstado): string {
    const map: Record<ReservaEstado, string> = {
      PENDIENTE: 'badge pending',
      CONFIRMADA: 'badge confirmed',
      CANCELADA: 'badge cancelled'
    };
    return map[estado];
  }

  trackById(_: number, reserva: Reserva): string {
    return reserva.id;
  }
}
