// src/app/core/services/reservas.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EstadoBarberoResponse {
  barberoId: number;
  estado: 'disponible' | 'ocupado';
  reservaActual: {
    id: number;
    cliente: string;
    servicio: string;
    horaInicio: string;
    horaFin: string;
  } | null;
}

@Injectable({ providedIn: 'root' })
export class ReservasService {

  private apiUrl = 'http://localhost:8084/api/v1/reservas';

  constructor(private http: HttpClient) {}

  getEstadoBarbero(barberoId: number): Observable<EstadoBarberoResponse> {
    return this.http.get<EstadoBarberoResponse>(`${this.apiUrl}/barbero/${barberoId}/estado`);
  }
}