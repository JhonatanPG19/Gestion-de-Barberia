import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservasService, CrearReservaRequest } from '../../reservas/services/reservas.service';
import { HttpClient } from '@angular/common/http';

interface Barbero {
  id: number;
  nombre: string;
  apellido: string;
  especialidad: string;
  telefono: string;
  correo: string;
  imagen: string;
}

interface Servicio {
  id: number;
  nombre: string;
  descripcion: string;
  duracion: number;
  precio: number;
}

@Component({
  selector: 'app-admin-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="admin-reserva-container">
      <div class="header">
        <button class="btn-back" (click)="goBack()">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          Volver
        </button>
        <h1>Crear Reserva (Admin)</h1>
      </div>

      <div class="form-container">
        <form [formGroup]="reservaForm" (ngSubmit)="onSubmit()">
          <!-- ID del Cliente -->
          <div class="form-group">
            <label for="clienteId">ID del Cliente *</label>
            <input
              type="number"
              id="clienteId"
              formControlName="clienteId"
              class="form-control"
              placeholder="Ingrese el ID del cliente"
            />
            <div class="error-message" *ngIf="reservaForm.get('clienteId')?.touched && reservaForm.get('clienteId')?.invalid">
              El ID del cliente es requerido
            </div>
          </div>

          <!-- Seleccionar Barbero -->
          <div class="form-group">
            <label for="barberoId">Barbero *</label>
            <select
              id="barberoId"
              formControlName="barberoId"
              class="form-control"
            >
              <option value="">Seleccione un barbero</option>
              <option *ngFor="let barbero of barberos()" [value]="barbero.id">
                {{ barbero.nombre }} {{ barbero.apellido }} - {{ barbero.especialidad }}
              </option>
            </select>
            <div class="error-message" *ngIf="reservaForm.get('barberoId')?.touched && reservaForm.get('barberoId')?.invalid">
              Debe seleccionar un barbero
            </div>
          </div>

          <!-- Seleccionar Servicio -->
          <div class="form-group">
            <label for="servicioId">Servicio *</label>
            <select
              id="servicioId"
              formControlName="servicioId"
              class="form-control"
            >
              <option value="">Seleccione un servicio</option>
              <option *ngFor="let servicio of servicios()" [value]="servicio.id">
                {{ servicio.nombre }} - {{ servicio.precio }} COP ({{ servicio.duracion }} min)
              </option>
            </select>
            <div class="error-message" *ngIf="reservaForm.get('servicioId')?.touched && reservaForm.get('servicioId')?.invalid">
              Debe seleccionar un servicio
            </div>
          </div>

          <!-- Fecha -->
          <div class="form-group">
            <label for="fecha">Fecha *</label>
            <input
              type="date"
              id="fecha"
              formControlName="fecha"
              class="form-control"
              [min]="minDate"
            />
            <div class="error-message" *ngIf="reservaForm.get('fecha')?.touched && reservaForm.get('fecha')?.invalid">
              La fecha es requerida
            </div>
          </div>

          <!-- Hora -->
          <div class="form-group">
            <label for="hora">Hora *</label>
            <input
              type="time"
              id="hora"
              formControlName="hora"
              class="form-control"
            />
            <div class="error-message" *ngIf="reservaForm.get('hora')?.touched && reservaForm.get('hora')?.invalid">
              La hora es requerida
            </div>
          </div>

          <!-- Observaciones -->
          <div class="form-group">
            <label for="observaciones">Observaciones</label>
            <textarea
              id="observaciones"
              formControlName="observaciones"
              class="form-control"
              rows="3"
              placeholder="Notas adicionales (opcional)"
            ></textarea>
          </div>

          <!-- Botones -->
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="goBack()" [disabled]="loading()">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary" [disabled]="reservaForm.invalid || loading()">
              <span *ngIf="!loading()">Crear Reserva</span>
              <span *ngIf="loading()">Creando...</span>
            </button>
          </div>

          <div class="success-message" *ngIf="successMessage()">
            {{ successMessage() }}
          </div>

          <div class="error-message" *ngIf="errorMessage()">
            {{ errorMessage() }}
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .admin-reserva-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 2rem;
      min-height: 100vh;
      background: #f7fafc;
    }

    .header {
      margin-bottom: 2rem;
    }

    .btn-back {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      color: #4a5568;
      cursor: pointer;
      margin-bottom: 1rem;
      transition: all 0.2s;
    }

    .btn-back:hover {
      background: #f7fafc;
      border-color: #cbd5e0;
    }

    h1 {
      font-size: 2rem;
      font-weight: 700;
      color: #2d3748;
      margin: 0;
    }

    .form-container {
      background: white;
      padding: 2rem;
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .form-group {
      margin-bottom: 1.5rem;
    }

    label {
      display: block;
      font-weight: 600;
      color: #2d3748;
      margin-bottom: 0.5rem;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-size: 1rem;
      transition: all 0.2s;
    }

    .form-control:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .form-control:disabled {
      background: #f7fafc;
      cursor: not-allowed;
    }

    textarea.form-control {
      resize: vertical;
      font-family: inherit;
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      margin-top: 2rem;
    }

    .btn {
      flex: 1;
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s;
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }

    .btn-secondary {
      background: white;
      color: #4a5568;
      border: 1px solid #e2e8f0;
    }

    .btn-secondary:hover:not(:disabled) {
      background: #f7fafc;
    }

    .error-message {
      color: #e53e3e;
      font-size: 0.875rem;
      margin-top: 0.5rem;
    }

    .success-message {
      padding: 1rem;
      background: #c6f6d5;
      color: #2f855a;
      border-radius: 8px;
      margin-top: 1rem;
    }

    @media (max-width: 768px) {
      .admin-reserva-container {
        padding: 1rem;
      }

      .form-container {
        padding: 1.5rem;
      }

      .form-actions {
        flex-direction: column;
      }
    }
  `]
})
export class AdminReservaFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private reservasService = inject(ReservasService);
  private http = inject(HttpClient);

  barberos = signal<Barbero[]>([]);
  servicios = signal<Servicio[]>([]);
  loading = signal(false);
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  minDate = new Date().toISOString().split('T')[0];

  reservaForm: FormGroup = this.fb.group({
    clienteId: ['', [Validators.required, Validators.min(1)]],
    barberoId: ['', Validators.required],
    servicioId: ['', Validators.required],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    observaciones: ['']
  });

  ngOnInit(): void {
    this.cargarBarberos();
    this.cargarServicios();
  }

  cargarBarberos(): void {
    this.http.get<Barbero[]>('http://localhost:8082/api/barbero/todos').subscribe({
      next: (data) => this.barberos.set(data),
      error: (err) => console.error('Error al cargar barberos:', err)
    });
  }

  cargarServicios(): void {
    this.http.get<Servicio[]>('http://localhost:8083/api/servicios/todos').subscribe({
      next: (data) => this.servicios.set(data),
      error: (err) => console.error('Error al cargar servicios:', err)
    });
  }

  onSubmit(): void {
    if (this.reservaForm.invalid) {
      this.reservaForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    const formValue = this.reservaForm.value;
    const fechaHora = `${formValue.fecha}T${formValue.hora}:00`;

    const request: CrearReservaRequest = {
      clienteId: Number(formValue.clienteId),
      barberoId: Number(formValue.barberoId),
      servicioId: Number(formValue.servicioId),
      fechaHora: fechaHora,
      observaciones: formValue.observaciones || undefined,
      esWalkIn: false
    };

    this.reservasService.crearReserva(request).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set('¡Reserva creada exitosamente!');
        setTimeout(() => {
          this.router.navigate(['/admin']);
        }, 2000);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Error al crear la reserva. Verifica que el ID del cliente exista.');
        console.error('Error al crear reserva:', err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin']);
  }
}
