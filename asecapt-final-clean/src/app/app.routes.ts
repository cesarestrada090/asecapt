import { Routes } from '@angular/router';
import { CoursesGridComponent } from './components/courses-grid/courses-grid.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { AboutComponent } from './components/about/about.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'courses-grid', component: CoursesGridComponent },
  { path: 'about', component: AboutComponent },
  { path: '**', redirectTo: '' }
]; 