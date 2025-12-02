// src/app/services/usuario.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RegistroUsuarioRequest {
  username: string;
  nombre: string;
  apellido: string;
  telefono: string;
  password: string;
  correo: string;
  rol: string;          // tu backend lo espera en RegistroUsuarioDTO
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private baseUrl = 'http://localhost:9090/api/v1/usuarios';

  constructor(private http: HttpClient) {}

  registrarCliente(dto: RegistroUsuarioRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/registro`, dto);
  }
}
