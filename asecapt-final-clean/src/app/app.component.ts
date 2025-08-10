import { Component, HostListener } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'asecapt-final-clean';
  isMobileMenuOpen = false;
  isDashboardRoute = false;

  constructor(private router: Router) {
    // Listen to route changes to determine if we're on a dashboard route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      // Only hide header/footer for actual dashboard routes, not virtual-classroom
      this.isDashboardRoute = event.url === '/dashboard' || event.url === '/student-dashboard';
    });
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
    console.log('Menu toggled:', this.isMobileMenuOpen); // Debug
  }

  closeMobileMenu() {
    this.isMobileMenuOpen = false;
    console.log('Menu closed'); // Debug
  }

  // Cerrar menú al hacer click en cualquier parte de la página
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;

    // Si el menú está abierto y el click NO es en el botón del menú ni en el menú mismo
    if (this.isMobileMenuOpen &&
        !target.closest('.navbar-toggler') &&
        !target.closest('#nav')) {
      this.isMobileMenuOpen = false;
    }
  }
}
