import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ServiciosService, Servicio } from '../../../../core/services/servicios.service';
import { BarberosService, Barbero } from '../../../../core/services/barberos.service';

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
    private barberiaService: BarberosService
  ) {
    this.servicioForm = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: [''],
      duracionMinutos: [45, [Validators.required, Validators.min(45)]],
      precio: [0, [Validators.required, Validators.min(1)]],
      barberosIds: [[]] // Array de IDs de barberos seleccionados
    });
  }

  ngOnInit() {
    // Cargar lista de barberos (siempre, para el selector)
    this.barberiaService.getBarberos().subscribe(data => {
      this.barberos = data;
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editMode = true;
      this.servicioId = +id;
      this.cargarServicio(this.servicioId);
    }
  }

  cargarServicio(id: number) {
    this.serviciosService.getServicio(id).subscribe(servicio => {
      this.servicioForm.patchValue(servicio);
    });
  }

  onSubmit() {
    if (this.servicioForm.valid) {
      const servicioData: Servicio = this.servicioForm.value;

      if (this.editMode && this.servicioId) {
        this.serviciosService.updateServicio(this.servicioId, servicioData).subscribe(() => {
          this.router.navigate(['/admin/servicios']);
        });
      } else {
        this.serviciosService.createServicio(servicioData).subscribe(() => {
          this.router.navigate(['/admin/servicios']);
        });
      }
    }
  }
}