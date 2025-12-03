import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class SweetAlertService {

  constructor() { }

  /**
   * Muestra una alerta de confirmación para activar/inactivar barbero
   * @param accion 'activar' o 'inactivar'
   * @param nombreBarbero Nombre del barbero
   * @returns Promise<boolean> true si confirmó, false si canceló
   */
  async confirmarCambioEstado(accion: 'activar' | 'inactivar', nombreBarbero: string): Promise<boolean> {
    const esActivar = accion === 'activar';
    
    const result = await Swal.fire({
      title: `¿${esActivar ? 'Activar' : 'Inactivar'} barbero?`,
      html: `¿Estás seguro de que deseas <strong>${accion}</strong> a <strong>${nombreBarbero}</strong>?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: esActivar ? '#28a745' : '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: esActivar ? '<i class="fas fa-check"></i> Sí, activar' : '<i class="fas fa-ban"></i> Sí, inactivar',
      cancelButtonText: '<i class="fas fa-times"></i> Cancelar',
      reverseButtons: true,
      focusCancel: true,
      customClass: {
        confirmButton: 'btn btn-lg px-4',
        cancelButton: 'btn btn-lg px-4'
      },
      buttonsStyling: true
    });

    return result.isConfirmed;
  }

  /**
   * Muestra una alerta de éxito
   * @param accion 'activado' o 'inactivado'
   * @param nombreBarbero Nombre del barbero
   */
  mostrarExito(accion: 'activado' | 'inactivado', nombreBarbero: string): void {
    const esActivado = accion === 'activado';
    
    Swal.fire({
      title: '¡Éxito!',
      html: `El barbero <strong>${nombreBarbero}</strong> ha sido ${accion} correctamente.`,
      icon: 'success',
      confirmButtonColor: esActivado ? '#28a745' : '#dc3545',
      confirmButtonText: '<i class="fas fa-check"></i> Entendido',
      timer: 3000,
      timerProgressBar: true,
      customClass: {
        confirmButton: 'btn btn-lg px-4'
      }
    });
  }

  /**
   * Muestra una alerta de error
   * @param mensaje Mensaje de error
   */
  mostrarError(mensaje: string = 'Ha ocurrido un error al procesar la solicitud'): void {
    Swal.fire({
      title: 'Error',
      text: mensaje,
      icon: 'error',
      confirmButtonColor: '#dc3545',
      confirmButtonText: '<i class="fas fa-times"></i> Cerrar',
      customClass: {
        confirmButton: 'btn btn-lg px-4'
      }
    });
  }

  /**
   * Muestra una alerta de advertencia
   * @param mensaje Mensaje de advertencia
   */
  mostrarAdvertencia(mensaje: string): void {
    Swal.fire({
      title: 'Atención',
      text: mensaje,
      icon: 'warning',
      confirmButtonColor: '#ffc107',
      confirmButtonText: '<i class="fas fa-exclamation-triangle"></i> Entendido',
      customClass: {
        confirmButton: 'btn btn-lg px-4'
      }
    });
  }

  /**
   * Muestra una alerta de información
   * @param titulo Título de la alerta
   * @param mensaje Mensaje informativo
   */
  mostrarInfo(titulo: string, mensaje: string): void {
    Swal.fire({
      title: titulo,
      text: mensaje,
      icon: 'info',
      confirmButtonColor: '#17a2b8',
      confirmButtonText: '<i class="fas fa-info-circle"></i> Entendido',
      customClass: {
        confirmButton: 'btn btn-lg px-4'
      }
    });
  }

  /**
   * Muestra un toast (notificación temporal)
   * @param tipo Tipo de toast: 'success', 'error', 'warning', 'info'
   * @param mensaje Mensaje a mostrar
   */
  mostrarToast(tipo: 'success' | 'error' | 'warning' | 'info', mensaje: string): void {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer);
        toast.addEventListener('mouseleave', Swal.resumeTimer);
      }
    });

    Toast.fire({
      icon: tipo,
      title: mensaje
    });
  }
}
