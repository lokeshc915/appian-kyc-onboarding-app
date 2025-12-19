import { Component } from '@angular/core';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  template: \`
  <div class="container">
    <div class="card">
      <div class="row" style="align-items:center;">
        <div class="col" style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
          <b>KYC Onboarding</b>
          <span class="muted">No BPM engine</span>
          <a class="btn" href="/">Cases</a>
          <a class="btn" href="/admin" *ngIf="auth.isAdmin()">Admin</a>
        </div>
        <div *ngIf="auth.isLoggedIn()" style="display:flex; gap:8px; align-items:center; flex-wrap:wrap;">
          <span class="muted">{{auth.username()}}</span>
          <span class="pill" *ngIf="auth.roles().length">{{auth.roles().join(', ')}}</span>
          <button (click)="logout()">Logout</button>
        </div>
      </div>
    </div>
    <router-outlet></router-outlet>
  </div>\`
})
export class AppComponent {
  constructor(public auth: AuthService) {}
  logout(){ this.auth.logout(); }
}
