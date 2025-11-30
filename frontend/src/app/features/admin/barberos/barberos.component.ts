import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BarberosService, Barbero } from '../../../core/services/barberos.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-barberos',
  templateUrl: 'barberos.component.html',
  standalone: true,
  imports: [RouterLink, CommonModule],
})
export class BarberosComponent implements OnInit {
  barberos: Barbero[] = [];

  constructor(private barberiaService: BarberosService) {}

  ngOnInit() {
    this.cargarBarberos();
  }

  cargarBarberos() {
    this.barberiaService.getBarberos().subscribe(
      data => this.barberos = data,
      error => console.error('Error al cargar barberos:', error)
    );
  }
}