import type { Locale } from '../../i18n/index';
import { resolveInviteHost } from '../invite/invitePresentation';

export const DEFAULT_DELETE_ACCOUNT_PATH = '/delete-account';

export function resolveDeleteAccountUrl(locale?: Locale): string {
  const host = resolveInviteHost();
  const url = new URL(DEFAULT_DELETE_ACCOUNT_PATH, `https://${host}`);
  if (locale) {
    url.searchParams.set('lang', locale);
  }
  return url.toString();
}
