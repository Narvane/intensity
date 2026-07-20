import type { Locale } from '../../i18n/index';
import { resolveInviteHost } from '../invite/invitePresentation';

export const DEFAULT_PRIVACY_POLICY_PATH = '/privacy';

export function resolvePrivacyPolicyUrl(locale?: Locale): string {
  const host = resolveInviteHost();
  const url = new URL(DEFAULT_PRIVACY_POLICY_PATH, `https://${host}`);
  if (locale) {
    url.searchParams.set('lang', locale);
  }
  return url.toString();
}
