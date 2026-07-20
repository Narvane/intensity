import { afterEach, describe, expect, it, vi } from 'vitest';
import { resolveDeleteAccountUrl } from './deleteAccountUrl';

describe('resolveDeleteAccountUrl', () => {
  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it('builds the delete-account URL from the invite host', () => {
    vi.stubEnv('VITE_INVITE_BASE_URL', 'https://app.narvane.com.br/join');
    expect(resolveDeleteAccountUrl()).toBe('https://app.narvane.com.br/delete-account');
  });

  it('includes the locale query param when provided', () => {
    vi.stubEnv('VITE_INVITE_BASE_URL', 'https://demo-intensity.narvane.com.br/join');
    expect(resolveDeleteAccountUrl('it')).toBe(
      'https://demo-intensity.narvane.com.br/delete-account?lang=it',
    );
  });
});
