import type { Invite } from '@domain/invite/inviteTypes';

export const DEFAULT_INVITE_BASE_URL = 'https://app.intensity.example/join';
export const DEFAULT_INVITE_HOST = 'app.intensity.example';
export const DEFAULT_INVITE_PATH = '/join';

function resolveInviteBaseUrl(baseUrl = import.meta.env.VITE_INVITE_BASE_URL): string {
  return (baseUrl || DEFAULT_INVITE_BASE_URL).replace(/\/$/, '');
}

export function resolveInviteHost(baseUrl = import.meta.env.VITE_INVITE_BASE_URL): string {
  const resolved = resolveInviteBaseUrl(baseUrl);
  try {
    return new URL(resolved.includes('://') ? resolved : `https://${resolved}`).host;
  } catch {
    return DEFAULT_INVITE_HOST;
  }
}

export function parseInviteDeepLinkUrl(
  rawUrl: string,
  options?: { host?: string; path?: string },
): string | null {
  const expectedHost = options?.host ?? resolveInviteHost();
  const expectedPath = options?.path ?? DEFAULT_INVITE_PATH;

  try {
    const parsed = rawUrl.includes('://')
      ? new URL(rawUrl)
      : new URL(rawUrl, `https://${expectedHost}`);

    if (parsed.pathname !== expectedPath || parsed.host !== expectedHost) {
      return null;
    }

    const linkToken = parsed.searchParams.get('t')?.trim();
    if (linkToken) {
      return `${expectedPath}?t=${encodeURIComponent(linkToken)}`;
    }

    const code = parsed.searchParams.get('code')?.trim();
    if (code) {
      return `${expectedPath}?code=${encodeURIComponent(code.toUpperCase())}`;
    }

    return null;
  } catch {
    return null;
  }
}

export function buildInviteLink(linkToken: string, baseUrl = import.meta.env.VITE_INVITE_BASE_URL): string {
  return `${resolveInviteBaseUrl(baseUrl)}?t=${linkToken}`;
}

export function buildInviteShareMessage(
  invite: Pick<Invite, 'code' | 'linkToken'>,
  t: (key: string, params?: Record<string, string>) => string,
): string {
  const link = buildInviteLink(invite.linkToken);
  return t('invite.share.message', { link, code: invite.code });
}

export function formatInviteExpiry(isoDate: string, locale: string): string {
  return new Intl.DateTimeFormat(locale, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(isoDate));
}
