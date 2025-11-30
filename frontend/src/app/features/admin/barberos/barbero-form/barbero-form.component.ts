// src/app/features/admin/barberos/barbero-form/barbero-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BarberosService, Barbero } from '../../../../core/services/barberos.service';
import { CommonModule } from '@angular/common';

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

  // Días de la semana para el select múltiple
  diasSemana = [
    'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo'
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    public router: Router,
    private barberiaService: BarberosService
  ) {
    this.barberoForm = this.fb.group({
      nombre: ['', Validators.required],
      telefono: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      estado: ['activo', Validators.required],
      horarioInicioLaboral: ['08:00', Validators.required],
      horarioFinLaboral: ['18:00', Validators.required],
      horaInicioDescanso: [''],
      horaFinDescanso: [''],
      diasLaborables: [['Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado']]
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editMode = true;
      this.barberId = +id;
      this.cargarBarbero(this.barberId);
    }
  }

  cargarBarbero(id: number) {
    this.barberiaService.getBarbero(id).subscribe(barbero => {
      // Convertir diasLaborables string a array
      const diasArray = barbero.diasLaborables.split(',');
      this.barberoForm.patchValue({
        ...barbero,
        diasLaborables: diasArray
      });
    });
  }

  onSubmit() {
    if (this.barberoForm.valid) {
      const formValue = this.barberoForm.value;
      const barberData: Barbero = {
        ...formValue,
        // Convertir array de días a string
        diasLaborables: formValue.diasLaborables.join(',')
      };

      if (this.editMode && this.barberId) {
        this.barberiaService.updateBarbero(this.barberId, barberData).subscribe(() => {
          this.router.navigate(['/admin/barberos']);
        });
      } else {
        this.barberiaService.createBarbero(barberData).subscribe(() => {
          this.router.navigate(['/admin/barberos']);
        });
      }
    }
  }
}