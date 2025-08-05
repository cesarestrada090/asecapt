import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { AboutComponent } from './components/about/about.component';
import { CoursesGridComponent } from './components/courses-grid/courses-grid.component';
import { CertificateValidationComponent } from './components/certificate-validation/certificate-validation.component';
import { ComplaintsBookComponent } from './components/complaints-book/complaints-book.component';
import { VirtualClassroomComponent } from './components/virtual-classroom/virtual-classroom.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
// Legacy - keeping AdminPanelComponent for compatibility
import { AdminPanelComponent } from './components/admin-panel/admin-panel.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'about', component: AboutComponent },
  { path: 'courses-grid', component: CoursesGridComponent },
  { path: 'verify', component: CertificateValidationComponent }, // Manual verification
  { path: 'verify/:token', component: CertificateValidationComponent }, // QR verification with token
  { path: 'complaints-book', component: ComplaintsBookComponent },
  { path: 'virtual-classroom', component: VirtualClassroomComponent },
  { path: 'dashboard', component: DashboardComponent }, // Modular dashboard with internal navigation
  { path: 'admin-panel', component: AdminPanelComponent }, // Legacy admin panel
  { path: '**', redirectTo: '' }
];
