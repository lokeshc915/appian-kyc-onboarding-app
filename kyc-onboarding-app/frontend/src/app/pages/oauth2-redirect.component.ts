import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  template: \`
    <div class="card">
      <h3>OAuth2 Redirect</h3>
      <p class="muted">Saving token…</p>
    </div>
  \`
})
export class OAuth2RedirectComponent {
  constructor(route: ActivatedRoute, auth: AuthService) {
    const token = route.snapshot.queryParamMap.get('token');
    if (token) auth.setToken(token);
    window.location.href = '/';
  }
}
