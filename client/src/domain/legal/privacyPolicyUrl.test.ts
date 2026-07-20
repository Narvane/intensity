import { afterEach, describe, expect, it, vi } from 'vitest';
import { resolvePrivacyPolicyUrl } from './privacyPolicyUrl';

describe('resolvePrivacyPolicyUrl', () => {
  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it('builds the privacy URL from the invite host', () => {
    vi.stubEnv('VITE_INVITE_BASE_URL', 'https://app.narvane.com.br/join');
    expect(resolvePrivacyPolicyUrl()).toBe('https://app.narvane.com.br/privacy');
  });

  it('includes the locale query param when provided', () => {
    vi.stubEnv('VITE_INVITE_BASE_URL', 'https://demo-intensity.narvane.com.br/join');
    expect(resolvePrivacyPolicyUrl('pt-BR')).toBe(
      'https://demo-intensity.narvane.com.br/privacy?lang=pt-BR',
    );
  });
});
