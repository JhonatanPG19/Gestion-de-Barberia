import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  /**
   * Muestra una notificación de éxito
   */
  success(message: string, title: string = '¡Éxito!'): Promise<any> {
    return Swal.fire({
      icon: 'success',
      title: title,
      text: message,
      confirmButtonText: 'Aceptar',
      confirmButtonColor: '#9b87f5',
      timer: 3000,
      timerProgressBar: true,
      showClass: {
        popup: 'animate__animated animate__fadeInDown'
      },
      hideClass: {
        popup: 'animate__animated animate__fadeOutUp'
      }
    });
  }

  /**
   * Muestra una notificación de error
   */
  error(message: string, title: string = 'Error'): Promise<any> {
    return Swal.fire({
      icon: 'error',
      title: title,
      text: message,
      confirmButtonText: 'Aceptar',
      confirmButtonColor: '#9b87f5',
      showClass: {
        popup: 'animate__animated animate__shakeX'
      }
    });
  }

  /**
   * Muestra una notificación de advertencia
   */
  warning(message: string, title: string = 'Advertencia'): Promise<any> {
    return Swal.fire({
      icon: 'warning',
      title: title,
      text: message,
      confirmButtonText: 'Aceptar',
      confirmButtonColor: '#9b87f5',
      showClass: {
        popup: 'animate__animated animate__headShake'
      }
    });
  }

  /**
   * Muestra una notificación informativa
   */
  info(message: string, title: string = 'Información'): Promise<any> {
    return Swal.fire({
      icon: 'info',
      title: title,
      text: message,
      confirmButtonText: 'Aceptar',
      confirmButtonColor: '#9b87f5'
    });
  }

  /**
   * Muestra un diálogo de confirmación
   */
  confirm(message: string, title: string = '¿Estás seguro?'): Promise<any> {
    return Swal.fire({
      icon: 'question',
      title: title,
      text: message,
      showCancelButton: true,
      confirmButtonText: 'Sí, continuar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#9b87f5',
      cancelButtonColor: '#6c757d',
      reverseButtons: true
    });
  }

  /**
   * Muestra una notificación tipo toast (pequeña y temporal)
   */
  toast(message: string, icon: 'success' | 'error' | 'warning' | 'info' = 'success'): void {
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
      icon: icon,
      title: message
    });
  }

  /**
   * Maneja errores HTTP y muestra mensajes apropiados
   */
  handleError(error: any, defaultMessage: string = 'Ha ocurrido un error inesperado'): void {
    let message = defaultMessage;
    let title = 'Error';

    if (error.status === 400) {
      title = 'Error de Validación';
      message = error.error?.message || error.error || 'Los datos enviados no son válidos';
    } else if (error.status === 404) {
      title = 'No Encontrado';
      message = error.error?.message || 'El recurso solicitado no fue encontrado';
    } else if (error.status === 500) {
      title = 'Error del Servidor';
      message = 'Ha ocurrido un error en el servidor. Por favor, intenta más tarde.';
    } else if (error.status === 0) {
      title = 'Error de Conexión';
      message = 'No se puede conectar con el servidor. Verifica tu conexión a internet.';
    } else if (error.error?.message) {
      message = error.error.message;
    } else if (typeof error.error === 'string') {
      message = error.error;
    }

    this.error(message, title);
  }

  /**
   * Muestra un loader mientras se ejecuta una operación
   */
  loading(message: string = 'Cargando...', title: string = 'Por favor espera'): void {
    Swal.fire({
      title: title,
      text: message,
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });
  }

  /**
   * Cierra cualquier modal abierto
   */
  close(): void {
    Swal.close();
  }
}
