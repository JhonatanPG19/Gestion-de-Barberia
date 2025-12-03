// src/app/features/admin/barberos/barbero-form/barbero-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BarberosService, Barbero } from '../../../../core/services/barberos.service';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

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
    private http: HttpClient
  ) {
    this.barberoForm = this.fb.group({
      userId: ['', Validators.required],
      nombre: ['', Validators.required],
      telefono: ['', Validators.required],
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
        alert('No se pudo cargar el barbero. Verifica la conexión con el backend.');
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
          this.router.navigate(['/admin/barberos']);
        },
        error: (err) => {
          let mensaje = 'Error al guardar el barbero.';
          //Detectar error de email duplicado
          if (err.status === 400 || err.status === 500) {
            mensaje = 'Ya existe un barbero con ese correo electrónico.';
          }
          console.error('Error:', err);
          alert(mensaje);
        }
      });
    } else {
      alert('Por favor, complete todos los campos obligatorios.');
    }
  }

  eliminarBarbero() {
    if (confirm('¿Está seguro de eliminar PERMANENTEMENTE este barbero?\n¡Esta acción no se puede deshacer!')) {
      this.barberiaService.eliminarFisico(this.barberId!).subscribe({
        next: () => {
          this.router.navigate(['/admin/barberos']);
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          alert('No se puede eliminar: el barbero tiene reservas asociadas.');
        }
      });
    }
  }

}