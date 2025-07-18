import { Routes } from '@angular/router';
import { CoursesGridComponent } from './components/courses-grid/courses-grid.component';

export const routes: Routes = [
  { path: 'courses-grid', component: CoursesGridComponent },
  { path: '', redirectTo: '', pathMatch: 'full' }
]; 