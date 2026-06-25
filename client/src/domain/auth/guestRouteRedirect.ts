import type { SessionState } from '@domain/session/SessionPort';
import { resolvePostAuthDestination } from '@domain/invite/pendingInvite';

export function resolveGuestRouteRedirect(
  session: SessionState | null,
): string | null {
  if (!session) {
    return null;
  }

  if (session.accessMode === 'EXPERIENCES') {
    // Allow /auth while an individual session is active (locked login UI + mode switch).
    return null;
  }

  if (session.accessMode === 'EXPERIENCE_BOX') {
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
