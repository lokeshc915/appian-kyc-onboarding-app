import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { AuthInterceptor } from './core/auth.interceptor';
import { LoginComponent } from './pages/login.component';
import { OAuth2RedirectComponent } from './pages/oauth2-redirect.component';
import { DashboardComponent } from './pages/dashboard.component';
import { CaseStep1Component } from './pages/case-step1.component';
import { CaseDocumentsComponent } from './pages/case-documents.component';
import { CaseAuditComponent } from './pages/case-audit.component';
import { AdminDashboardComponent } from './pages/admin-dashboard.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    OAuth2RedirectComponent,
    DashboardComponent,
    CaseStep1Component,
    CaseDocumentsComponent,
    CaseAuditComponent,
    AdminDashboardComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
