import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { ServiciosService, Servicio } from '../../../core/services/servicios.service';

@Component({
  selector: 'app-servicios',
  templateUrl: './servicios.component.html',
  standalone: true,
  imports: [RouterLink, CommonModule]
})
export class ServiciosComponent implements OnInit {
  servicios: Servicio[] = [];

  constructor(private service: ServiciosService) {}

  ngOnInit() {
    this.service.getServicios().subscribe(data => {
      this.servicios = data;
    });
  }
}