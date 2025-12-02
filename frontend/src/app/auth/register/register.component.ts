import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';

interface RegistroForm {
  username: FormControl<string>;
  nombre: FormControl<string>;
  apellido: FormControl<string>;
  telefono: FormControl<string>;
  correo: FormControl<string>;
  password: FormControl<string>;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  registerForm: FormGroup<RegistroForm>;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private router: Router
  ) {
    this.registerForm = this.fb.group<RegistroForm>({
      username: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
      nombre:   this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
      apellido: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
      telefono: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
      correo:   this.fb.control('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
      password: this.fb.control('', { nonNullable: true, validators: [Validators.required, Validators.minLength(6)] })
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const dto = {
      username: this.f.username.value,
      nombre:   this.f.nombre.value,
      apellido: this.f.apellido.value,
      telefono: this.f.telefono.value,
      correo:   this.f.correo.value,
      password: this.f.password.value,
      rol: 'cliente'
    };

    this.usuarioService.registrarCliente(dto).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Registro exitoso. Ahora puedes iniciar sesión.';
        setTimeout(() => this.router.navigate(['/admin/login']), 1500);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error || 'Error al registrar usuario';
        console.error(err);
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/admin/login']);
  }
}
