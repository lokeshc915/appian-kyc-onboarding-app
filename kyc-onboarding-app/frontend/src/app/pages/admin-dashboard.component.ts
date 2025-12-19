import { Component } from '@angular/core';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  template: `
  <div class="card">
    <div class="row" style="align-items:center;">
      <div class="col">
        <h3 style="margin:0">Admin Dashboard</h3>
        <p class="muted" style="margin:4px 0 0">High-level counts and reports (production-safe summary).</p>
      </div>
      <button (click)="refresh()">Refresh</button>
    </div>
  </div>

  <div class="card" *ngIf="!auth.isAdmin()">
    <p>You need <b>ROLE_ADMIN</b> to access this page.</p>
  </div>

  <div class="card" *ngIf="auth.isAdmin() && stats">
    <div class="row">
      <div class="col">
        <div class="kpi">
          <div class="kpiLabel">Total Cases</div>
          <div class="kpiValue">{{stats.totalCases}}</div>
        </div>
      </div>
      <div class="col">
        <div class="kpi">
          <div class="kpiLabel">SLA Breached</div>
          <div class="kpiValue">{{stats.slaBreachedCases}}</div>
        </div>
      </div>
    </div>
  </div>

  <div class="grid" *ngIf="auth.isAdmin() && stats">
    <div class="card">
      <h3>Cases by Status</h3>
      <table class="table">
        <tr><th>Status</th><th>Count</th></tr>
        <tr *ngFor="let k of statusKeys()">
          <td>{{k}}</td>
          <td>{{stats.casesByStatus[k]}}</td>
        </tr>
      </table>
    </div>
    <div class="card">
      <h3>Documents by Type</h3>
      <table class="table">
        <tr><th>Type</th><th>Count</th></tr>
        <tr *ngFor="let k of docKeys()">
          <td>{{k}}</td>
          <td>{{stats.documentsByType[k]}}</td>
        </tr>
      </table>
    </div>
  </div>
  `
})
export class AdminDashboardComponent {
  stats: any = null;
  constructor(public api: ApiService, public auth: AuthService) {
    if (this.auth.isAdmin()) this.refresh();
  }

  refresh(){
    this.api.adminStats().subscribe({
      next: s => this.stats = s,
      error: () => this.stats = null
    });
  }

  statusKeys(): string[] { return this.stats ? Object.keys(this.stats.casesByStatus) : []; }
  docKeys(): string[] { return this.stats ? Object.keys(this.stats.documentsByType) : []; }
}
