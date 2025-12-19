import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../core/api.service';

@Component({
  template: `
  <div class="card">
    <div class="row" style="align-items:center;">
      <div class="col">
        <h3 style="margin:0">Audit Timeline (Case {{caseId}})</h3>
        <p class="muted" style="margin:4px 0 0">Every status change, upload/delete, and SLA breach is recorded.</p>
      </div>
      <button (click)="refresh()">Refresh</button>
    </div>
  </div>

  <div class="card" *ngIf="events.length === 0">
    <p class="muted">No events yet.</p>
  </div>

  <div class="timeline" *ngIf="events.length">
    <div class="timelineItem" *ngFor="let e of events">
      <div class="dot"></div>
      <div class="content">
        <div class="row" style="align-items:flex-end; gap:12px;">
          <b>{{e.action}}</b>
          <span class="muted">{{fmt(e.eventAt)}}</span>
          <span class="muted" *ngIf="e.actor">by {{e.actor}}</span>
        </div>
        <div class="muted" *ngIf="e.message">{{e.message}}</div>
        <div class="pill" *ngIf="e.fromStatus || e.toStatus">
          {{e.fromStatus || '—'}} → {{e.toStatus || '—'}}
        </div>
        <pre class="meta" *ngIf="e.metadataJson">{{pretty(e.metadataJson)}}</pre>
      </div>
    </div>
  </div>
  `
})
export class CaseAuditComponent {
  caseId = Number(this.route.snapshot.paramMap.get('id'));
  events: any[] = [];

  constructor(private route: ActivatedRoute, private api: ApiService) {
    this.refresh();
  }

  refresh(){
    this.api.audit(this.caseId).subscribe({
      next: r => this.events = r ?? [],
      error: () => this.events = []
    });
  }

  fmt(v: string): string {
    if (!v) return '';
    const d = new Date(v);
    return d.toLocaleString();
  }

  pretty(json: string): string {
    try { return JSON.stringify(JSON.parse(json), null, 2); } catch { return json; }
  }
}
