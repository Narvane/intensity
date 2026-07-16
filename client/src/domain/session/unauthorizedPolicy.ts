import type { SessionState } from './SessionPort';

export interface UnauthorizedDecision {
  clearExperiences: boolean;
  clearExperienceBox: boolean;
}

/**
 * Decides which stored session (if any) must be dropped after the API
 * rejected `failedToken` with 401 INVALID_TOKEN.
 *
 * Strict rule: a session is only cleared when the rejected token is exactly
 * the token of that session. A 401 for an unknown/stale token — or a request
 * that never carried this session's token — must not log the user out.
 */
export function resolveUnauthorizedDecision(
  experiences: SessionState | null,
  experienceBox: SessionState | null,
  failedToken: string | undefined,
): UnauthorizedDecision {
  if (!failedToken) {
    return { clearExperiences: false, clearExperienceBox: false };
  }

  return {
    clearExperiences: experiences?.token === failedToken,
    clearExperienceBox: experienceBox?.token === failedToken,
  };
}
