import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registro',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  registroForm: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  roles = [
    { codigo: 'ESTUDIANTE', nombre: 'Estudiante' },
    { codigo: 'ADMINISTRATIVO', nombre: 'Administrativo' },
    { codigo: 'COORDINADOR', nombre: 'Coordinador' },
    { codigo: 'DOCENTE', nombre: 'Docente' },
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registroForm = this.fb.group(
      {
        nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
        identificacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]],
        correo: ['', [Validators.required, Validators.email, Validators.maxLength(200)]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]],
        confirmPassword: ['', [Validators.required]],
        rol: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    if (confirmPassword?.hasError('passwordMismatch')) {
      confirmPassword.setErrors(null);
    }
    return null;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit(): void {
    if (this.registroForm.invalid) {
      this.registroForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.registroForm.value;

    const payload = {
      nombre: formValue.nombre,
      identificacion: formValue.identificacion,
      correo: formValue.correo,
      password: formValue.password,
      activo: true,
      rol: { codigo: formValue.rol },
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '¡Registro exitoso! Redirigiendo...';
        setTimeout(() => {
          this.router.navigate(['/solicitudes']);
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.errorMessage = 'Ya existe un usuario con esa identificación o correo.';
        } else if (err.status === 400) {
          this.errorMessage = err.error?.message || 'Datos inválidos. Revise los campos e intente nuevamente.';
        } else if (err.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifique que el backend esté activo.';
        } else {
          this.errorMessage = err.error?.message || 'Ocurrió un error inesperado. Intente nuevamente.';
        }
      },
    });
  }

  get f() {
    return this.registroForm.controls;
  }
}
