import { describe, expect, it } from 'vitest';
import { isTokenExpired } from './jwtToken';

function tokenWithExpiry(expSeconds: number): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(JSON.stringify({ exp: expSeconds }));
  return `${header}.${payload}.signature`;
}

describe('isTokenExpired', () => {
  it('returns false for tokens without exp', () => {
    const header = btoa(JSON.stringify({ alg: 'HS256' }));
    const payload = btoa(JSON.stringify({ sub: 'user' }));
    expect(isTokenExpired(`${header}.${payload}.signature`)).toBe(false);
  });

  it('returns true when exp is in the past', () => {
    const expired = tokenWithExpiry(Math.floor(Date.now() / 1000) - 60);
    expect(isTokenExpired(expired)).toBe(true);
  });

  it('returns false when exp is in the future', () => {
    const valid = tokenWithExpiry(Math.floor(Date.now() / 1000) + 3600);
    expect(isTokenExpired(valid)).toBe(false);
  });
});
