import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Barbero {
  id?: number;
  nombre: string;
  telefono: string;
  email: string;
  estado: string; // 'activo' | 'inactivo'
  horarioInicioLaboral: string;  // '08:00'
  horarioFinLaboral: string;     // '18:00'
  horaInicioDescanso?: string;
  horaFinDescanso?: string;
  diasLaborables: string; // 'Lunes,Martes,...'
}

@Injectable({ providedIn: 'root' })
export class BarberosService {
  private apiUrl = 'http://localhost:8082/api/barbero';

  constructor(private http: HttpClient) {}

  getBarberos(): Observable<Barbero[]> {
    return this.http.get<Barbero[]>(this.apiUrl);
  }

  getBarbero(id: number): Observable<Barbero> {
    return this.http.get<Barbero>(`${this.apiUrl}/${id}`);
  }

  createBarbero(barbero: Barbero): Observable<Barbero> {
    return this.http.post<Barbero>(this.apiUrl, barbero);
  }

  updateBarbero(id: number, barbero: Barbero): Observable<Barbero> {
    return this.http.put<Barbero>(`${this.apiUrl}/${id}`, barbero);
  }
}