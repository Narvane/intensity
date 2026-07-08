import type { SessionState } from '@domain/session/SessionPort';
import { resolvePostAuthDestination } from '@domain/invite/pendingInvite';

export function resolveGuestRouteRedirect(
  experienceBoxSession: SessionState | null,
): string | null {
  if (!experienceBoxSession) {
    return null;
  }

  if (experienceBoxSession.accessMode === 'EXPERIENCE_BOX') {
    return '/box-home';
  }

  return null;
}

/** Default post-auth path when continuing an existing Experiences session from /auth. */
export function resolveExperiencesSessionContinuePath(
  options?: { returnTo?: string | null; pendingReturnPath?: string | null },
): string {
  return resolvePostAuthDestination(options?.returnTo, options?.pendingReturnPath);
}
