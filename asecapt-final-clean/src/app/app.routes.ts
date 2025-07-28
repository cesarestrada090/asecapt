import { Routes } from '@angular/router';
import { CoursesGridComponent } from './components/courses-grid/courses-grid.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { AboutComponent } from './components/about/about.component';
import { CertificateValidationComponent } from './components/certificate-validation/certificate-validation.component';
import { ComplaintsBookComponent } from './components/complaints-book/complaints-book.component';
import { VirtualClassroomComponent } from './components/virtual-classroom/virtual-classroom.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'courses-grid', component: CoursesGridComponent },
  { path: 'about', component: AboutComponent },
  { path: 'certificate-validation', component: CertificateValidationComponent },
  { path: 'complaints-book', component: ComplaintsBookComponent },
  { path: 'virtual-classroom', component: VirtualClassroomComponent },
  { path: '**', redirectTo: '' }
];
