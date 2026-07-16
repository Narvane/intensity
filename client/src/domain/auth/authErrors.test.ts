import { describe, expect, it } from 'vitest';
import { ApiError } from '@adapters/api/ApiClient';
import {
  isValidAuthPasswordLength,
  resolveAuthError,
} from '@domain/auth/authErrors';

describe('resolveAuthError', () => {
  const t = (key: string) => key;

  it('maps invalid credentials', () => {
    expect(
      resolveAuthError(new ApiError(401, 'INVALID_CREDENTIALS', 'Invalid email or password.'), t),
    ).toBe('auth.errors.invalidCredentials');
  });

  it('maps bean-validation password size on joint login', () => {
    expect(
      resolveAuthError(
        new ApiError(
          422,
          'VALIDATION_ERROR',
          'credentials[0].password: size must be between 8 and 128',
        ),
        t,
      ),
    ).toBe('auth.errors.passwordLength');
  });

  it('maps group membership conflict', () => {
    expect(
      resolveAuthError(new ApiError(409, 'GROUP_MEMBERSHIP_CONFLICT', 'conflict'), t),
    ).toBe('auth.errors.groupMembershipConflict');
  });

  it('maps allowlist rejection', () => {
    expect(
      resolveAuthError(new ApiError(403, 'EMAIL_NOT_ALLOWLISTED', 'not allowed'), t),
    ).toBe('auth.errors.emailNotAllowlisted');
  });

  it('maps network failures', () => {
    expect(resolveAuthError(new Error('Failed to fetch'), t)).toBe('auth.errors.network');
  });

  it('maps NETWORK_ERROR api failures', () => {
    expect(resolveAuthError(new ApiError(0, 'NETWORK_ERROR', 'Failed to fetch'), t)).toBe(
      'auth.errors.network',
    );
  });

  it('hides technical fallback messages', () => {
    expect(resolveAuthError(new ApiError(500, 'UNKNOWN', 'NullPointerException'), t)).toBe(
      'common.error',
    );
  });
});

describe('isValidAuthPasswordLength', () => {
  it('accepts passwords in the allowed range', () => {
    expect(isValidAuthPasswordLength('12345678')).toBe(true);
    expect(isValidAuthPasswordLength('a'.repeat(128))).toBe(true);
  });

  it('rejects passwords outside the allowed range', () => {
    expect(isValidAuthPasswordLength('short')).toBe(false);
    expect(isValidAuthPasswordLength('a'.repeat(129))).toBe(false);
  });
});
