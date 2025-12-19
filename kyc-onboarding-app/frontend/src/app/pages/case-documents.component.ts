import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../core/api.service';

type UploadRow = { type: string; file: File | null; uploading: boolean; error?: string };

@Component({
  template: `
  <div class="card">
    <div class="row" style="align-items:center;">
      <div class="col">
        <h3 style="margin:0">Step 2: Onboarding Case Documents (Case {{caseId}})</h3>
        <p class="muted" style="margin:4px 0 0">Drag & drop or choose file. Click “Add Row” to upload multiple documents.</p>
      </div>
      <div style="display:flex; gap:8px; align-items:center;">
        <button (click)="refresh()">Refresh</button>
        <button (click)="submit()" [disabled]="case?.status !== 'DRAFT_STEP2'">Submit</button>
      </div>
    </div>

    <div class="row" style="margin-top:10px; gap:8px; flex-wrap:wrap;">
      <span class="pill">Status: {{case?.status || '—'}}</span>
      <span class="pill" *ngIf="case?.dueAt">Due: {{fmt(case?.dueAt)}}</span>
      <span class="pill" [class.pillDanger]="case?.slaState==='BREACHED'" *ngIf="case?.slaState">SLA: {{case?.slaState}}</span>
    </div>
    <p *ngIf="msg" class="muted">{{msg}}</p>
  </div>

  <div class="card">
    <div class="row" style="align-items:center;">
      <div class="col">
        <h3 style="margin:0">Upload Rows</h3>
        <p class="muted" style="margin:4px 0 0">Required: Identity Document, Financial Disclosure Form, Letter of Recommendation</p>
      </div>
      <button (click)="addRow()">Add Row</button>
    </div>

    <div class="uploadRow" *ngFor="let r of rows; let i = index" (drop)="onDrop($event, i)" (dragover)="onDragOver($event)">
      <div class="row" style="gap:10px; align-items:flex-end; flex-wrap:wrap;">
        <div class="col" style="min-width:240px;">
          <label>Document Type</label>
          <select [(ngModel)]="r.type">
            <option value="IDENTITY_DOCUMENT">Identity Document</option>
            <option value="FINANCIAL_DISCLOSURE_FORM">Financial Disclosure Form</option>
            <option value="LETTER_OF_RECOMMENDATION">Letter of Recommendation</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
        <div class="col" style="min-width:280px;">
          <label>File</label>
          <input type="file" (change)="onFile($event, i)">
          <div class="muted" *ngIf="r.file">{{r.file.name}} ({{r.file.size}} bytes)</div>
          <div class="muted" *ngIf="!r.file">Drop a file here</div>
        </div>
        <div style="display:flex; gap:8px; align-items:center;">
          <button (click)="upload(i)" [disabled]="!r.file || r.uploading">Upload</button>
          <button class="btn" (click)="removeRow(i)">Remove</button>
        </div>
      </div>
      <div class="muted" *ngIf="r.error" style="margin-top:6px;">{{r.error}}</div>
    </div>
  </div>

  <div class="card">
    <div class="row" style="align-items:center;">
      <h3 style="margin:0" class="col">Uploaded Documents</h3>
      <a class="btn" [href]="'/cases/' + caseId + '/audit'">View Audit Timeline</a>
    </div>
    <table class="table" style="margin-top:8px;">
      <tr style="text-align:left;">
        <th>ID</th><th>Type</th><th>Name</th><th>Uploaded</th><th>Download</th>
      </tr>
      <tr *ngFor="let d of docs">
        <td>{{d.id}}</td>
        <td>{{d.type}}</td>
        <td>{{d.originalFileName}}</td>
        <td class="muted">{{d.uploadedAt ? fmt(d.uploadedAt) : '—'}}</td>
        <td><a [href]="api.downloadUrl(d.id)" target="_blank">Download</a></td>
      </tr>
    </table>
  </div>
  `
})
export class CaseDocumentsComponent {
  caseId = Number(this.route.snapshot.paramMap.get('id'));
  msg = '';
  case: any = null;
  docs: any[] = [];
  rows: UploadRow[] = [{ type: 'IDENTITY_DOCUMENT', file: null, uploading: false }];

  constructor(private route: ActivatedRoute, public api: ApiService) {
    this.refresh();
  }

  refresh(){
    this.msg = '';
    this.api.getCase(this.caseId).subscribe({ next: c => this.case = c, error: () => this.case = null });
    this.api.listDocs(this.caseId).subscribe({ next: r => this.docs = r ?? [], error: () => this.docs = [] });
  }

  addRow(){
    this.rows.push({ type: 'OTHER', file: null, uploading: false });
  }

  removeRow(i: number){
    this.rows.splice(i, 1);
    if (this.rows.length === 0) this.addRow();
  }

  onFile(e: any, i: number){
    const f = e.target.files?.[0] ?? null;
    this.rows[i].file = f;
    this.rows[i].error = undefined;
  }

  onDragOver(e: DragEvent){
    e.preventDefault();
  }

  onDrop(e: DragEvent, i: number){
    e.preventDefault();
    const f = e.dataTransfer?.files?.[0] ?? null;
    this.rows[i].file = f;
    this.rows[i].error = undefined;
  }

  upload(i: number){
    const r = this.rows[i];
    if (!r.file) return;
    r.uploading = true;
    r.error = undefined;
    this.api.uploadDoc(this.caseId, r.type, r.file).subscribe({
      next: () => {
        r.uploading = false;
        r.file = null;
        this.msg = 'Uploaded';
        this.refresh();
      },
      error: (e) => {
        r.uploading = false;
        r.error = e?.error?.error ?? 'Upload failed';
      }
    });
  }

  submit(){
    this.msg = 'Submitting…';
    this.api.submitCase(this.caseId).subscribe({
      next: () => { this.msg = 'Submitted for review'; this.refresh(); },
      error: (e) => this.msg = e?.error?.error ?? 'Submit failed'
    });
  }

  fmt(v: string): string {
    if (!v) return '';
    return new Date(v).toLocaleString();
  }
}
