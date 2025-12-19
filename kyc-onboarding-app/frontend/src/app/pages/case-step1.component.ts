import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { ApiService } from '../core/api.service';

@Component({
  template: \`
  <div class="card">
    <h3>Step 1: Account Details (Case {{caseId}})</h3>
    <form [formGroup]="form" (ngSubmit)="save()">
      <div class="row">
        <div class="col">
          <label>First Name</label>
          <input formControlName="firstName">
        </div>
        <div class="col">
          <label>Last Name</label>
          <input formControlName="lastName">
        </div>
      </div>

      <label>Account Type</label>
      <select formControlName="accountType">
        <option value="Savings">Savings</option>
        <option value="Checking">Checking</option>
        <option value="Business">Business</option>
      </select>

      <label>Phone</label>
      <input formControlName="phone">

      <label>Address</label>
      <input formControlName="addressLine1">
      <div class="row">
        <div class="col"><input placeholder="City" formControlName="city"></div>
        <div class="col"><input placeholder="State" formControlName="state"></div>
        <div class="col"><input placeholder="Zip" formControlName="zip"></div>
      </div>

      <button type="submit">Save & Next</button>
    </form>
    <p *ngIf="msg" class="muted">{{msg}}</p>
  </div>\`
})
export class CaseStep1Component {
  caseId = Number(this.route.snapshot.paramMap.get('id'));
  msg = '';
  case: any = null;
  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    accountType: ['Savings', Validators.required],
    phone: [''],
    addressLine1: [''],
    city: [''],
    state: [''],
    zip: ['']
  });

  constructor(private route: ActivatedRoute, private fb: FormBuilder, private api: ApiService) {
    this.api.getCase(this.caseId).subscribe({
      next: c => {
        this.case = c;
        if (c?.accountDetails) {
          this.form.patchValue(c.accountDetails);
        }
      },
      error: () => { /* ignore */ }
    });
  }

  save(){
    this.msg = '';
    this.api.saveStep1(this.caseId, this.form.value).subscribe({
      next: () => { this.msg = 'Saved. Redirecting to Step 2…'; window.location.href = `/cases/${this.caseId}/documents`; },
      error: () => this.msg = 'Save failed'
    });
  }
}
