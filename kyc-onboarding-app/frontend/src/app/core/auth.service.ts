import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

const API = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    return this.http.post<{accessToken: string}>(`${API}/api/auth/login`, { username, password })
      .pipe(tap(r => localStorage.setItem('accessToken', r.accessToken)));
  }

  register(username: string, password: string, email?: string) {
    return this.http.post(`${API}/api/auth/register`, { username, password, email });
  }

  setToken(token: string) { localStorage.setItem('accessToken', token); }
  token(): string | null { return localStorage.getItem('accessToken'); }
  isLoggedIn(): boolean { return !!this.token(); }
  logout(){ localStorage.removeItem('accessToken'); window.location.href = '/login'; }

  private decodePayload(): any | null {
    const t = this.token();
    if (!t) return null;
    const parts = t.split('.');
    if (parts.length !== 3) return null;
    try {
      const json = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  username(): string {
    return this.decodePayload()?.username ?? '';
  }

  roles(): string[] {
    return this.decodePayload()?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  isApprover(): boolean { return this.hasRole('ROLE_APPROVER'); }
  isAdmin(): boolean { return this.hasRole('ROLE_ADMIN'); }

  oauth2GoogleUrl(): string { return `${API}/oauth2/authorization/google`; }
}
