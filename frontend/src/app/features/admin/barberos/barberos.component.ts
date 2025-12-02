import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BarberosService, Barbero } from '../../../core/services/barberos.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-barberos',
  templateUrl: 'barberos.component.html',
  standalone: true,
  imports: [RouterLink, CommonModule],
})
export class BarberosComponent implements OnInit {
  barberos: Barbero[] = [];

  constructor(private barberiaService: BarberosService) { }

  ngOnInit() {
    this.cargarBarberos();
  }

  cargarBarberos() {
    this.barberiaService.getBarberos().subscribe(
      data => this.barberos = data,
      error => console.error('Error al cargar barberos:', error)
    );
  }

  inactivarBarbero(id: number) {
    if (confirm('¿Está seguro de inactivar este barbero?')) {
      this.barberiaService.getBarbero(id).subscribe(barbero => {
        // Cambiar estado a "inactivo"
        const barberoInactivo = { ...barbero, estado: 'inactivo' };
        this.barberiaService.updateBarbero(id, barberoInactivo).subscribe(() => {
          this.cargarBarberos(); // Recargar la lista
        });
      });
    }
  }

  // src/app/features/admin/barberos/barberos.component.ts
  cambiarEstado(id: number, nuevoEstado: string) {
    const confirmMsg = nuevoEstado === 'activo'
      ? '¿Activar este barbero?'
      : '¿Inactivar este barbero?';

    if (confirm(confirmMsg)) {
      this.barberiaService.getBarbero(id).subscribe(barbero => {
        const barberoActualizado = { ...barbero, estado: nuevoEstado };
        this.barberiaService.updateBarbero(id, barberoActualizado).subscribe(() => {
          this.cargarBarberos(); // Recargar lista
        });
      });
    }
  }

}