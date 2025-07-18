import { Component, AfterViewInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';

declare var $: any;

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent implements AfterViewInit, OnDestroy {

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
