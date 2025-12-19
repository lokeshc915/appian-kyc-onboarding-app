import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

const API = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  createCase() { return this.http.post<{caseId:number}>(`${API}/api/cases`, {}); }
  getCase(caseId: number){ return this.http.get<any>(`${API}/api/cases/${caseId}`); }
  saveStep1(caseId:number, body:any){ return this.http.put<any>(`${API}/api/cases/${caseId}/step1`, body); }

  listCases(scope: 'mine' | 'all' = 'mine') {
    return this.http.get<any[]>(`${API}/api/cases?scope=${scope}`);
  }

  submitCase(caseId:number){ return this.http.put<any>(`${API}/api/cases/${caseId}/submit`, {}); }
  startReview(caseId:number){ return this.http.put<any>(`${API}/api/cases/${caseId}/review/start`, {}); }
  approve(caseId:number){ return this.http.put<any>(`${API}/api/cases/${caseId}/approve`, {}); }
  reject(caseId:number, reason: string){ return this.http.put<any>(`${API}/api/cases/${caseId}/reject`, { reason }); }

  audit(caseId:number){ return this.http.get<any[]>(`${API}/api/cases/${caseId}/audit`); }

  adminStats(){ return this.http.get<any>(`${API}/api/admin/stats`); }

  listDocs(caseId:number){ return this.http.get<any[]>(`${API}/api/cases/${caseId}/documents`); }
  uploadDoc(caseId:number, type:string, file: File){
    const form = new FormData();
    form.append('type', type);
    form.append('file', file);
    return this.http.post<any>(`${API}/api/cases/${caseId}/documents`, form);
  }
  downloadUrl(docId:number){ return `${API}/api/documents/${docId}/download`; }
}
