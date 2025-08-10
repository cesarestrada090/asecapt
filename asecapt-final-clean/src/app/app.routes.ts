import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { AboutComponent } from './components/about/about.component';
import { CoursesGridComponent } from './components/courses-grid/courses-grid.component';
import { CertificateValidationComponent } from './components/certificate-validation/certificate-validation.component';
import { CertificateVerificationComponent } from './components/certificate-verification/certificate-verification.component';
import { ComplaintsBookComponent } from './components/complaints-book/complaints-book.component';
import { VirtualClassroomComponent } from './components/virtual-classroom/virtual-classroom.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StudentDashboardComponent } from './components/student-dashboard/student-dashboard.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'about', component: AboutComponent },
  { path: 'courses-grid', component: CoursesGridComponent },
  { path: 'verify', component: CertificateValidationComponent }, // Manual verification
  { path: 'verify/:token', component: CertificateValidationComponent }, // QR verification with token
  { path: 'public/certificate/:certificateCode', component: CertificateVerificationComponent }, // Public certificate verification
  { path: 'complaints-book', component: ComplaintsBookComponent },
  { path: 'virtual-classroom', component: VirtualClassroomComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] }, // Protected dashboard with auth guard
  { path: 'student-dashboard', component: StudentDashboardComponent, canActivate: [AuthGuard] }, // Protected student dashboard
  { path: '**', redirectTo: '' }
];
