
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should login and return token', () => {
    service.login('a@b.com', 'x').subscribe(res => {
      expect(res.token).toBe('t');
    });

    const req = http.expectOne(r => r.url.includes('/api/auth/login'));
    expect(req.request.method).toBe('POST');
    req.flush({ token: 't' });
  });
});
