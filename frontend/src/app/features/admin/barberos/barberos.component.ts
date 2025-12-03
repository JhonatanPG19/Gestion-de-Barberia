import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BarberosService, Barbero } from '../../../core/services/barberos.service';
import { CommonModule } from '@angular/common';
import { SweetAlertService } from '../../../core/services/sweet-alert.service';

@Component({
  selector: 'app-barberos',
  templateUrl: 'barberos.component.html',
  standalone: true,
  imports: [RouterLink, CommonModule],
})
export class BarberosComponent implements OnInit {
  barberos: Barbero[] = [];

  constructor(
    private barberiaService: BarberosService,
    private sweetAlertService: SweetAlertService
  ) { }

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
    this.barberiaService.getBarbero(id).subscribe(barbero => {
      this.sweetAlertService.confirmarCambioEstado('inactivar', barbero.nombre).then(confirmado => {
        if (confirmado) {
          const barberoInactivo = { ...barbero, estado: 'inactivo' };
          this.barberiaService.updateBarbero(id, barberoInactivo).subscribe({
            next: () => {
              this.sweetAlertService.mostrarExito('inactivado', barbero.nombre);
              this.cargarBarberos();
            },
            error: (error) => {
              console.error('Error al inactivar barbero:', error);
              this.sweetAlertService.mostrarError('No se pudo inactivar el barbero. Por favor, intente nuevamente.');
            }
          });
        }
      });
    });
  }

  // src/app/features/admin/barberos/barberos.component.ts
  cambiarEstado(id: number, nuevoEstado: string) {
    this.barberiaService.getBarbero(id).subscribe(barbero => {
      const accion = nuevoEstado === 'activo' ? 'activar' : 'inactivar';
      
      this.sweetAlertService.confirmarCambioEstado(accion, barbero.nombre).then(confirmado => {
        if (confirmado) {
          const barberoActualizado = { ...barbero, estado: nuevoEstado };
          this.barberiaService.updateBarbero(id, barberoActualizado).subscribe({
            next: () => {
              const accionPasado = nuevoEstado === 'activo' ? 'activado' : 'inactivado';
              this.sweetAlertService.mostrarExito(accionPasado, barbero.nombre);
              this.cargarBarberos();
            },
            error: (error) => {
              console.error(`Error al ${accion} barbero:`, error);
              this.sweetAlertService.mostrarError(`No se pudo ${accion} el barbero. Por favor, intente nuevamente.`);
            }
          });
        }
      });
    });
  }

}