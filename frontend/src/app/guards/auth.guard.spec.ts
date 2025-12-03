import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { AuthGuard } from './auth.guard';

describe('authGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    const guard: AuthGuard = TestBed.inject(AuthGuard);
    expect(guard).toBeTruthy();
  });
});
