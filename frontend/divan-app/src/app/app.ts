import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ButtonModule],
  template: `
    <h1>Teste PrimeNG</h1>
    <p-button label="Funciona!" severity="success"></p-button>
  `
})
export class App {}