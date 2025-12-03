import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Barbero {
  id: number;
  nombre: string;
  apellido: string;
  especialidad: string;
  telefono: string;
  correo: string;
  imagen: string;
}

@Injectable({
  providedIn: 'root'
})
export class BarberoService {
  private apiUrl = 'http://localhost:8082/api/barbero';

  constructor(private http: HttpClient) {}

  getTodosBarberos(): Observable<Barbero[]> {
    return this.http.get<Barbero[]>(`${this.apiUrl}/todos`);
  }

  getBarbero(id: number): Observable<Barbero> {
    return this.http.get<Barbero>(`${this.apiUrl}/${id}`);
  }

  crearBarbero(barbero: Omit<Barbero, 'id'>): Observable<Barbero> {
    return this.http.post<Barbero>(this.apiUrl, barbero);
  }

  actualizarBarbero(id: number, barbero: Partial<Barbero>): Observable<Barbero> {
    return this.http.put<Barbero>(`${this.apiUrl}/${id}`, barbero);
  }

  eliminarBarbero(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
