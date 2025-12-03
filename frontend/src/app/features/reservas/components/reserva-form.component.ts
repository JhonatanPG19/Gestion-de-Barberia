import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BarberosService, Barbero } from '../../../core/services/barberos.service';
import { ServiciosService, Servicio } from '../../../core/services/servicios.service';
import { CrearReservaRequest } from '../services/reservas.service';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reserva-form.component.html',
  styleUrls: ['./reserva-form.component.css']
})
export class ReservaFormComponent implements OnInit {
  @Output() crear = new EventEmitter<CrearReservaRequest>();

  private readonly barberosService = inject(BarberosService);
  private readonly serviciosService = inject(ServiciosService);
  private readonly fb = inject(FormBuilder);
  private readonly clienteDemoId = 5; // Valor temporal para pruebas mientras no hay sesión

  readonly barberos = signal<Barbero[]>([]);
  readonly servicios = signal<Servicio[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly formulario = this.fb.nonNullable.group({
    clienteId: [this.clienteDemoId, [Validators.required, Validators.min(1)]], // Temporal: ID fijo, debería venir del cliente logueado
    barberoId: [0, [Validators.required, Validators.min(1)]],
    servicioId: [0, [Validators.required, Validators.min(1)]],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    observaciones: ['', Validators.maxLength(500)],
    esWalkIn: [false]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.loading.set(true);
    
    // Cargar barberos
    this.barberosService.getBarberos().subscribe({
      next: (barberos) => {
        this.barberos.set(barberos.filter(b => b.estado === 'activo'));
      },
      error: (err) => {
        this.error.set('Error al cargar barberos: ' + err.message);
        console.error('Error cargando barberos:', err);
      }
    });

    // Cargar servicios
    this.serviciosService.getServicios().subscribe({
      next: (servicios) => {
        this.servicios.set(servicios.filter(s => s.activo));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar servicios: ' + err.message);
        this.loading.set(false);
        console.error('Error cargando servicios:', err);
      }
    });
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const formData = this.formulario.getRawValue();
    
    // Combinar fecha y hora en formato ISO DateTime
    const fechaHora = `${formData.fecha}T${formData.hora}:00`;

    const request: CrearReservaRequest = {
      clienteId: this.clienteDemoId,
      barberoId: formData.barberoId,
      servicioId: formData.servicioId,
      fechaHora: fechaHora,
      observaciones: formData.observaciones || undefined,
      esWalkIn: formData.esWalkIn
    };

    this.crear.emit(request);
    this.formulario.reset({
      clienteId: this.clienteDemoId, // Resetear con el valor fijo
      barberoId: 0,
      servicioId: 0,
      fecha: '',
      hora: '',
      observaciones: '',
      esWalkIn: false
    });
  }

  campoInvalido(campo: keyof typeof this.formulario.controls): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && control.touched;
  }
}
