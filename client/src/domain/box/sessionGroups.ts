import type { GroupAccent } from '@domain/box/boxTypes';
import type { SessionState } from '@domain/session/SessionPort';

export function resolveSessionGroupIds(session: Pick<SessionState, 'groupId' | 'groupIds'>): string[] {
  if (session.groupIds && session.groupIds.length > 0) {
    return session.groupIds;
  }

  if (session.groupId) {
    return [session.groupId];
  }

  return [];
}

export interface GroupHeadingEntry {
  name: string;
  accent: GroupAccent;
}
