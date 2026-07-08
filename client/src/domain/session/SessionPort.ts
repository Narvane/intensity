import type { StoredSessions } from './sessionStorage';

export type AccessMode = 'EXPERIENCES' | 'EXPERIENCE_BOX';

export interface SessionMember {
  participantId: string;
  displayName: string;
}

export interface SessionState {
  token: string;
  accessMode: AccessMode;
  participantId?: string;
  displayName?: string;
  /** Participant e-mail; persisted for auth UI when returning to login. */
  email?: string;
  groupId?: string;
  groupIds?: string[];
  members?: SessionMember[];
  experienceBox?: {
    drawCount: number;
    sessionStartedAt: string;
  };
}

export interface SessionPort {
  load(): Promise<StoredSessions>;
  saveExperiences(session: SessionState): Promise<void>;
  saveExperienceBox(session: SessionState): Promise<void>;
  clearExperiences(): Promise<void>;
  clearExperienceBox(): Promise<void>;
  clearAll(): Promise<void>;
}
