import { Component } from '@angular/core';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  template: `
  <div class="card">
    <div class="row" style="align-items:center;">
      <div class="col">
        <h3 style="margin:0">Cases</h3>
        <p class="muted" style="margin:4px 0 0">Step 1: Account Details → Step 2: Documents → Submit → Review</p>
      </div>
      <div style="display:flex; gap:8px; align-items:center;">
        <select *ngIf="auth.isApprover() || auth.isAdmin()" [(ngModel)]="scope" (change)="refresh()">
          <option value="mine">My cases</option>
          <option value="all">All cases</option>
        </select>
        <button (click)="create()">New Onboarding Case</button>
        <button (click)="refresh()">Refresh</button>
      </div>
    </div>
  </div>

  <div class="card" *ngIf="msg"><p class="muted">{{msg}}</p></div>

  <div class="card">
    <table class="table">
      <tr>
        <th>ID</th>
        <th>Status</th>
        <th>Due</th>
        <th>SLA</th>
        <th style="width: 420px;">Actions</th>
      </tr>
      <tr *ngFor="let c of cases">
        <td><b>#{{c.id}}</b></td>
        <td><span class="pill">{{c.status}}</span></td>
        <td><span class="muted">{{c.dueAt ? fmt(c.dueAt) : '—'}}</span></td>
        <td>
          <span class="pill" [class.pillDanger]="c.slaState==='BREACHED'">{{c.slaState}}</span>
        </td>
        <td>
          <div class="row" style="gap:8px; flex-wrap:wrap;">
            <a class="btn" [href]="'/cases/' + c.id + '/step1'">Step 1</a>
            <a class="btn" [href]="'/cases/' + c.id + '/documents'">Step 2</a>
            <a class="btn" [href]="'/cases/' + c.id + '/audit'">Audit</a>

            <button *ngIf="canSubmit(c)" (click)="submit(c.id)">Submit</button>
            <button *ngIf="canStartReview(c)" (click)="startReview(c.id)">Start Review</button>
            <button *ngIf="canApprove(c)" (click)="approve(c.id)">Approve</button>
            <button *ngIf="canReject(c)" (click)="reject(c.id)">Reject</button>
          </div>
        </td>
      </tr>
      <tr *ngIf="cases.length === 0">
        <td colspan="5" class="muted">No cases yet. Click “New Onboarding Case”.</td>
      </tr>
    </table>
  </div>

  <div class="card" *ngIf="auth.isAdmin()">
    <h3>Admin</h3>
    <a class="btn" href="/admin">Open Admin Dashboard</a>
  </div>
  `
})
export class DashboardComponent {
  cases: any[] = [];
  msg = '';
  scope: 'mine' | 'all' = 'mine';

  constructor(private api: ApiService, public auth: AuthService) {
    this.refresh();
  }

  refresh(){
    this.msg = '';
    this.api.listCases(this.scope).subscribe({
      next: r => this.cases = r ?? [],
      error: () => { this.cases = []; this.msg = 'Failed to load cases (check login/roles).'; }
    });
  }

  create(){
    this.msg = 'Creating…';
    this.api.createCase().subscribe({
      next: r => { this.msg = `Created case #${r.caseId}`; this.refresh(); },
      error: () => this.msg = 'Create failed'
    });
  }

  submit(id: number){
    this.msg = 'Submitting…';
    this.api.submitCase(id).subscribe({
      next: () => { this.msg = 'Submitted'; this.refresh(); },
      error: (e) => this.msg = e?.error?.error ?? 'Submit failed (missing required docs?)'
    });
  }

  startReview(id: number){
    this.msg = 'Starting review…';
    this.api.startReview(id).subscribe({
      next: () => { this.msg = 'In review'; this.refresh(); },
      error: (e) => this.msg = e?.error?.error ?? 'Start review failed'
    });
  }

  approve(id: number){
    this.msg = 'Approving…';
    this.api.approve(id).subscribe({
      next: () => { this.msg = 'Approved'; this.refresh(); },
      error: (e) => this.msg = e?.error?.error ?? 'Approve failed'
    });
  }

  reject(id: number){
    const reason = prompt('Rejection reason?') || '';
    if (!reason.trim()) { this.msg = 'Rejection requires a reason.'; return; }
    this.msg = 'Rejecting…';
    this.api.reject(id, reason).subscribe({
      next: () => { this.msg = 'Rejected'; this.refresh(); },
      error: (e) => this.msg = e?.error?.error ?? 'Reject failed'
    });
  }

  fmt(v: string): string {
    if (!v) return '';
    return new Date(v).toLocaleString();
  }

  canSubmit(c: any): boolean { return c.status === 'DRAFT_STEP2'; }
  canStartReview(c: any): boolean { return (this.auth.isApprover() || this.auth.isAdmin()) && c.status === 'SUBMITTED'; }
  canApprove(c: any): boolean { return (this.auth.isApprover() || this.auth.isAdmin()) && (c.status === 'SUBMITTED' || c.status === 'IN_REVIEW'); }
  canReject(c: any): boolean { return this.canApprove(c); }
}
