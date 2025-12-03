import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ServiciosService, Servicio } from '../../../../core/services/servicios.service';
import { BarberosService, Barbero } from '../../../../core/services/barberos.service';
import { NotificationService } from '../../../../services/notification.service';

@Component({
  selector: 'app-servicio-form',
  templateUrl: './servicio-form.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class ServicioFormComponent implements OnInit {
  servicioForm: FormGroup;
  editMode = false;
  servicioId?: number;
  barberos: Barbero[] = []; // Lista de barberos para el selector

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    public router: Router,
    private serviciosService: ServiciosService,
    private barberiaService: BarberosService,
    private notificationService: NotificationService
  ) {
    this.servicioForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      descripcion: ['', Validators.maxLength(500)],
      duracionMinutos: [45, [Validators.required, Validators.min(45), Validators.max(300)]],
      precio: [0, [Validators.required, Validators.min(0.01), Validators.max(1000000)]],
      barberosIds: [[]] // Array de IDs de barberos seleccionados
    });
  }

  ngOnInit() {
    // Cargar lista de barberos (siempre, para el selector)
    this.barberiaService.getBarberos().subscribe({
      next: (data) => {
        this.barberos = data;
      },
      error: (err) => {
        this.notificationService.handleError(err, 'Error al cargar barberos');
      }
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editMode = true;
      this.servicioId = +id;
      this.cargarServicio(this.servicioId);
    }
  }

  cargarServicio(id: number) {
    this.serviciosService.getServicio(id).subscribe({
      next: (servicio) => {
        this.servicioForm.patchValue(servicio);
      },
      error: (err) => {
        this.notificationService.handleError(err, 'Error al cargar el servicio');
      }
    });
  }

  onSubmit() {
    if (this.servicioForm.valid) {
      const servicioData = this.servicioForm.value;

      if (this.editMode && this.servicioId) {
        this.serviciosService.updateServicio(this.servicioId, servicioData).subscribe({
          next: () => {
            this.notificationService.success('Servicio actualizado exitosamente').then(() => {
              this.router.navigate(['/admin/servicios']);
            });
          },
          error: (err) => {
            this.notificationService.handleError(err, 'Error al actualizar el servicio');
          }
        });
      } else {
        this.serviciosService.createServicio(servicioData).subscribe({
          next: () => {
            this.notificationService.success('Servicio creado exitosamente').then(() => {
              this.router.navigate(['/admin/servicios']);
            });
          },
          error: (err) => {
            this.notificationService.handleError(err, 'Error al crear el servicio');
          }
        });
      }
    } else {
      this.notificationService.warning('Por favor, complete todos los campos obligatorios', 'Formulario incompleto');
    }
  }

  toggleBarbero(barberoId: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const current = this.servicioForm.get('barberosIds')?.value || [];
    const updated = input.checked
      ? [...current, barberoId]
      : current.filter((id: number) => id !== barberoId);
    this.servicioForm.patchValue({ barberosIds: updated });
  }


}