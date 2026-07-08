import type { SessionState } from './SessionPort';

export interface StoredSessions {
  experiences: SessionState | null;
  experienceBox: SessionState | null;
}

export function isValidSession(session: SessionState | null | undefined): session is SessionState {
  return Boolean(session?.token && session.accessMode);
}
