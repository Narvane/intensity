import { Preferences } from '@capacitor/preferences';
import type { SessionPort, SessionState } from '@domain/session/SessionPort';

const SESSION_KEY = 'session';

export class SessionPreferencesAdapter implements SessionPort {
  async load(): Promise<SessionState | null> {
    const { value } = await Preferences.get({ key: SESSION_KEY });
    if (!value) {
      return null;
    }

    try {
      return JSON.parse(value) as SessionState;
    } catch {
      return null;
    }
  }

  async save(session: SessionState): Promise<void> {
    await Preferences.set({ key: SESSION_KEY, value: JSON.stringify(session) });
  }

  async clear(): Promise<void> {
    await Preferences.remove({ key: SESSION_KEY });
  }
}

export class InMemorySessionAdapter implements SessionPort {
  private session: SessionState | null = null;

  async load(): Promise<SessionState | null> {
    return this.session;
  }

  async save(session: SessionState): Promise<void> {
    this.session = session;
  }

  async clear(): Promise<void> {
    this.session = null;
  }
}

export function createDefaultSessionAdapter(): SessionPort {
  return new SessionPreferencesAdapter();
}
