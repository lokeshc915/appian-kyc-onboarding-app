import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../core/auth.service';

@Component({
  template: \`
  <div class="card">
    <h3>Login (JWT)</h3>
    <form [formGroup]="form" (ngSubmit)="submit()">
      <label>Username</label>
      <input formControlName="username" />
      <label>Password</label>
      <input type="password" formControlName="password" />
      <div class="row">
        <button type="submit">Login</button>
        <button type="button" (click)="register()">Register demo user</button>
      </div>
    </form>
    <p *ngIf="err" class="error">{{err}}</p>
  </div>

  <div class="card">
    <h3>Login (OAuth2)</h3>
    <p class="muted">Uses backend OAuth2 client. On success, backend redirects back with a JWT token.</p>
    <button (click)="oauth2()">Continue with Google</button>
  </div>\`
})
export class LoginComponent {
  err = '';
  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  constructor(private fb: FormBuilder, private auth: AuthService) {}

  submit() {
    this.err = '';
    const { username, password } = this.form.value;
    this.auth.login(username!, password!).subscribe({
      next: () => window.location.href = '/',
      error: () => this.err = 'Login failed'
    });
  }

  register() {
    const { username, password } = this.form.value;
    if (!username || !password) { this.err = 'Enter username/password first'; return; }
    this.auth.register(username, password).subscribe({
      next: () => this.err = 'Registered. Now click Login.',
      error: () => this.err = 'Register failed (maybe user exists)'
    });
  }

  oauth2() { window.location.href = this.auth.oauth2GoogleUrl(); }
}
