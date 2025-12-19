import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login.component';
import { DashboardComponent } from './pages/dashboard.component';
import { OAuth2RedirectComponent } from './pages/oauth2-redirect.component';
import { CaseStep1Component } from './pages/case-step1.component';
import { CaseDocumentsComponent } from './pages/case-documents.component';
import { CaseAuditComponent } from './pages/case-audit.component';
import { AdminDashboardComponent } from './pages/admin-dashboard.component';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'oauth2/redirect', component: OAuth2RedirectComponent },
  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'cases/:id/step1', component: CaseStep1Component, canActivate: [authGuard] },
  { path: 'cases/:id/documents', component: CaseDocumentsComponent, canActivate: [authGuard] },
  { path: 'cases/:id/audit', component: CaseAuditComponent, canActivate: [authGuard] },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [authGuard] }
];
