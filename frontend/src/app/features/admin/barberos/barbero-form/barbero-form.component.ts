// src/app/features/admin/barberos/barbero-form/barbero-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BarberosService, Barbero } from '../../../../core/services/barberos.service';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from '../../../../services/notification.service';

interface Usuario {
  id: number;
  username: string;
  nombre: string;
  apellido: string;
  correo: string;
  rol: string;
}

@Component({
  selector: 'app-barbero-form',
  templateUrl: './barbero-form.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class BarberoFormComponent implements OnInit {
  barberoForm: FormGroup;
  editMode = false;
  barberId?: number;
  usuarios: Usuario[] = [];

  // Días de la semana para el select múltiple
  diasSemana = [
    'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo'
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    public router: Router,
    private barberiaService: BarberosService,
    private http: HttpClient,
    private notificationService: NotificationService
  ) {
    this.barberoForm = this.fb.group({
      userId: ['', Validators.required],
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      email: ['', [Validators.required, Validators.email]],
      estado: ['activo', Validators.required],
      horarioInicioLaboral: ['08:00', Validators.required],
      horarioFinLaboral: ['18:00', Validators.required],
      horaInicioDescanso: [''],
      horaFinDescanso: [''],
      diasLaborables: [[]]
    });
  }

  ngOnInit() {
    this.cargarUsuariosBarberos();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editMode = true;
      this.barberId = +id;
      this.cargarBarbero(this.barberId);
    }
  }

  cargarUsuariosBarberos() {
    this.http.get<Usuario[]>('http://localhost:8081/api/v1/usuarios').subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios.filter(u => u.rol.toUpperCase() === 'BARBERO');
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);
      }
    });
  }

  cargarBarbero(id: number) {
    this.barberiaService.getBarbero(id).subscribe({
      next: (barbero) => {
        // Convertir string de la BD a array para el formulario
        const diasArray = barbero.diasLaborables.split(',').filter(d => d.trim());
        this.barberoForm.patchValue({
          ...barbero,
          diasLaborables: diasArray
        });
      },
      error: (err) => {
        console.error('Error al cargar barbero:', err);
        this.notificationService.handleError(err, 'No se pudo cargar el barbero');
      }
    });
  }

  toggleDia(dia: string, checked: boolean) {
    const current = this.barberoForm.get('diasLaborables')?.value || [];
    const newDias = checked
      ? [...current, dia]
      : current.filter((d: string) => d !== dia);
    this.barberoForm.patchValue({ diasLaborables: newDias });
  }

  // src/app/features/admin/barberos/barbero-form/barbero-form.component.ts
  onSubmit() {
    if (this.barberoForm.valid) {
      const formValue = { ...this.barberoForm.value };
      formValue.diasLaborables = formValue.diasLaborables.join(',');

      const save$ = this.editMode && this.barberId
        ? this.barberiaService.updateBarbero(this.barberId, formValue)
        : this.barberiaService.createBarbero(formValue);

      save$.subscribe({
        next: () => {
          const mensaje = this.editMode ? 'Barbero actualizado exitosamente' : 'Barbero creado exitosamente';
          this.notificationService.success(mensaje).then(() => {
            this.router.navigate(['/admin/barberos']);
          });
        },
        error: (err) => {
          this.notificationService.handleError(err, 'Error al guardar el barbero');
        }
      });
    } else {
      this.notificationService.warning('Por favor, complete todos los campos obligatorios.', 'Formulario incompleto');
    }
  }

  eliminarBarbero() {
    this.notificationService.confirm(
      '¿Está seguro de eliminar PERMANENTEMENTE este barbero? ¡Esta acción no se puede deshacer!',
      'Confirmar eliminación'
    ).then((result) => {
      if (result.isConfirmed) {
        this.barberiaService.eliminarFisico(this.barberId!).subscribe({
          next: () => {
            this.notificationService.success('Barbero eliminado exitosamente').then(() => {
              this.router.navigate(['/admin/barberos']);
            });
          },
          error: (err) => {
            this.notificationService.handleError(err, 'No se puede eliminar el barbero');
          }
        });
      }
    });
  }

}