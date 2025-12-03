import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup, FormControl, AbstractControl, ValidationErrors } from '@angular/forms';
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
      username: this.fb.control('', { 
        nonNullable: true, 
        validators: [
          Validators.required, 
          Validators.minLength(8)
        ] 
      }),
      nombre: this.fb.control('', { 
        nonNullable: true, 
        validators: [
          Validators.required, 
          Validators.minLength(3),
          this.soloLetrasValidator()
        ] 
      }),
      apellido: this.fb.control('', { 
        nonNullable: true, 
        validators: [
          Validators.required, 
          Validators.minLength(3),
          this.soloLetrasValidator()
        ] 
      }),
      telefono: this.fb.control('', { 
        nonNullable: true, 
        validators: [
          Validators.required, 
          this.telefonoColombiaValidator()
        ] 
      }),
      correo: this.fb.control('', { 
        nonNullable: true, 
        validators: [
          Validators.required, 
          Validators.email,
          this.emailFormatoValidator()
        ] 
      }),
      password: this.fb.control('', { 
        nonNullable: true, 
        validators: [
          Validators.required, 
          Validators.minLength(8)
        ] 
      })
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  // Validador personalizado: solo letras (incluye tildes y ñ)
  soloLetrasValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(control.value);
      return soloLetras ? null : { soloLetras: true };
    };
  }

  // Validador personalizado: teléfono colombiano (10 dígitos, empieza con 3)
  telefonoColombiaValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const telefonoValido = /^3\d{9}$/.test(control.value);
      return telefonoValido ? null : { telefonoColombia: true };
    };
  }

  // Validador adicional para formato de email más estricto
  emailFormatoValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const emailValido = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(control.value);
      return emailValido ? null : { emailFormato: true };
    };
  }

  // Método para obtener mensaje de error específico
  getErrorMessage(controlName: keyof RegistroForm): string {
    const control = this.f[controlName];
    
    if (!control.touched || !control.errors) {
      return '';
    }

    switch (controlName) {
      case 'username':
        if (control.errors['required']) {
          return 'El nombre de usuario es obligatorio';
        }
        if (control.errors['minlength']) {
          const faltantes = 8 - control.value.length;
          return `Faltan ${faltantes} caracteres (mínimo 8)`;
        }
        break;

      case 'nombre':
        if (control.errors['required']) {
          return 'El nombre es obligatorio';
        }
        if (control.errors['minlength']) {
          const faltantes = 3 - control.value.length;
          return `Faltan ${faltantes} caracteres (mínimo 3)`;
        }
        if (control.errors['soloLetras']) {
          return 'Solo se permiten letras';
        }
        break;

      case 'apellido':
        if (control.errors['required']) {
          return 'El apellido es obligatorio';
        }
        if (control.errors['minlength']) {
          const faltantes = 3 - control.value.length;
          return `Faltan ${faltantes} caracteres (mínimo 3)`;
        }
        if (control.errors['soloLetras']) {
          return 'Solo se permiten letras';
        }
        break;

      case 'telefono':
        if (control.errors['required']) {
          return 'El teléfono es obligatorio';
        }
        if (control.errors['telefonoColombia']) {
          return 'Formato inválido. Debe ser: 3XXXXXXXXX (10 dígitos)';
        }
        break;

      case 'correo':
        if (control.errors['required']) {
          return 'El correo es obligatorio';
        }
        if (control.errors['email'] || control.errors['emailFormato']) {
          return 'Formato de correo inválido (ejemplo@dominio.com)';
        }
        break;

      case 'password':
        if (control.errors['required']) {
          return 'La contraseña es obligatoria';
        }
        if (control.errors['minlength']) {
          const faltantes = 8 - control.value.length;
          return `Faltan ${faltantes} caracteres (mínimo 8)`;
        }
        break;
    }

    return '';
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
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || err.error || 'Error al registrar usuario';
        console.error(err);
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
