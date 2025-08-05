import { Routes } from '@angular/router';
import { CoursesGridComponent } from './components/courses-grid/courses-grid.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { AboutComponent } from './components/about/about.component';
import { CertificateValidationComponent } from './components/certificate-validation/certificate-validation.component';
import { CertificateVerificationComponent } from './components/certificate-verification/certificate-verification.component';
import { ComplaintsBookComponent } from './components/complaints-book/complaints-book.component';
import { VirtualClassroomComponent } from './components/virtual-classroom/virtual-classroom.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
// Legacy - keeping AdminPanelComponent for compatibility
import { AdminPanelComponent } from './components/admin-panel/admin-panel.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'courses-grid', component: CoursesGridComponent },
  { path: 'about', component: AboutComponent },
  { path: 'certificate-validation', component: CertificateValidationComponent },
  { path: 'verify', component: CertificateVerificationComponent }, // Manual verification
  { path: 'verify/:token', component: CertificateVerificationComponent }, // QR verification with token
  { path: 'complaints-book', component: ComplaintsBookComponent },
  { path: 'virtual-classroom', component: VirtualClassroomComponent },
  { path: 'dashboard', component: DashboardComponent }, // New modular dashboard
  { path: 'admin-panel', component: AdminPanelComponent }, // Legacy admin panel
  { path: '**', redirectTo: '' }
];
