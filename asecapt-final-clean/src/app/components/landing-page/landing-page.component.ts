import { Component, AfterViewInit, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProgramService, Program } from '../../services/program.service';

declare var $: any;

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent implements AfterViewInit, OnDestroy, OnInit {
  courses: Program[] = [];
  loading: boolean = false;
  error: string | null = null;

  constructor(private programService: ProgramService) {}

  ngOnInit() {
    this.loadCourses();
  }

  loadCourses() {
    this.loading = true;
    this.error = null;

    this.programService.getActivePrograms().subscribe({
      next: (programs) => {
        // Limitar a los primeros 6 cursos para el landing page
        this.courses = programs.slice(0, 6);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.error = 'Error cargando cursos.';
        this.loading = false;
      }
    });
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=400&h=220&q=80';
  }

  ngAfterViewInit() {
    // Initialize Owl Carousel for banner after view is initialized
    setTimeout(() => {
      if (typeof $ !== 'undefined' && $.fn.owlCarousel) {
        // Destroy any existing carousel first to avoid conflicts
        $('.slider-fade1').trigger('destroy.owl.carousel');
        $('.slider-fade1').removeClass('owl-carousel owl-theme');
        $('.slider-fade1').addClass('owl-carousel owl-theme');

        // Initialize the carousel
        $('.slider-fade1').owlCarousel({
          items: 1,
          loop: true,
          autoplay: true,
          autoplayTimeout: 5000,
          autoplayHoverPause: true,
          nav: false,
          dots: false,
          animateOut: 'fadeOut',
          animateIn: 'fadeIn',
          smartSpeed: 1000,
          mouseDrag: false,
          touchDrag: false
        });
      }
    }, 300);
  }

  ngOnDestroy() {
    // Clean up carousel when component is destroyed
    if (typeof $ !== 'undefined' && $.fn.owlCarousel) {
      $('.slider-fade1').trigger('destroy.owl.carousel');
    }
  }
}
