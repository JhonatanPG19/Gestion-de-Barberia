import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservaCreation } from '../models/reserva.model';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reserva-form.component.html',
  styleUrls: ['./reserva-form.component.css']
})
export class ReservaFormComponent {
  @Output() crear = new EventEmitter<ReservaCreation>();

  readonly serviciosDisponibles = [
    'Corte clásico',
    'Corte fade + barba',
    'Color + peinado',
    'Afeitado clásico',
    'Diseño de cejas'
  ];

  readonly formulario = this.fb.nonNullable.group({
    cliente: ['', [Validators.required, Validators.minLength(3)]],
    telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{7,10}$/)]],
    servicio: ['', Validators.required],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    notas: ['']
  });

  constructor(private readonly fb: FormBuilder) {}

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.crear.emit(this.formulario.getRawValue());
    this.formulario.reset();
  }

  campoInvalido(campo: keyof typeof this.formulario.controls): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && control.touched;
  }
}
