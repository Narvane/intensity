import { describe, expect, it } from 'vitest';
import {
  buildInviteLink,
  buildInviteShareMessage,
  formatInviteExpiry,
  parseInviteDeepLinkUrl,
} from '@domain/convite/invitePresentation';

describe('invitePresentation', () => {
  it('builds deep link from token', () => {
    expect(buildInviteLink('11111111-2222-3333-4444-555555555555', 'https://app.intensity.example/join'))
      .toBe('https://app.intensity.example/join?t=11111111-2222-3333-4444-555555555555');
  });

  it('parses invite deep link with token', () => {
    expect(
      parseInviteDeepLinkUrl(
        'https://app.intensity.example/join?t=11111111-2222-3333-4444-555555555555',
        { host: 'app.intensity.example' },
      ),
    ).toBe('/join?t=11111111-2222-3333-4444-555555555555');
  });

  it('parses invite deep link with code', () => {
    expect(
      parseInviteDeepLinkUrl('https://app.intensity.example/join?code=ab23cd', {
        host: 'app.intensity.example',
      }),
    ).toBe('/join?code=AB23CD');
  });

  it('rejects unrelated urls', () => {
    expect(parseInviteDeepLinkUrl('https://app.intensity.example/auth')).toBeNull();
    expect(parseInviteDeepLinkUrl('https://other.example/join?t=abc')).toBeNull();
    expect(parseInviteDeepLinkUrl('https://app.intensity.example/join')).toBeNull();
  });

  it('builds share message with link and code', () => {
    const message = buildInviteShareMessage(
      { code: 'AB23CD', linkToken: '11111111-2222-3333-4444-555555555555' },
      (key, params) => `${key}:${params?.link}:${params?.code}`,
    );

    expect(message).toContain('AB23CD');
    expect(message).toContain('11111111-2222-3333-4444-555555555555');
  });

  it('formats expiry for locale', () => {
    expect(formatInviteExpiry('2026-06-22T12:00:00.000Z', 'en')).toContain('2026');
  });
});
