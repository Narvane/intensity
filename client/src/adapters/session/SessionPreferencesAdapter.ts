import { Preferences } from '@capacitor/preferences';
import type { SessionPort, SessionState } from '@domain/session/SessionPort';
import type { StoredSessions } from '@domain/session/sessionStorage';
import { isValidSession } from '@domain/session/sessionStorage';

const EXPERIENCES_SESSION_KEY = 'experiencesSession';
const EXPERIENCE_BOX_SESSION_KEY = 'experienceBoxSession';
const LEGACY_SESSION_KEY = 'session';

export class SessionPreferencesAdapter implements SessionPort {
  async load(): Promise<StoredSessions> {
    const [experiencesRaw, experienceBoxRaw, legacyRaw] = await Promise.all([
      Preferences.get({ key: EXPERIENCES_SESSION_KEY }),
      Preferences.get({ key: EXPERIENCE_BOX_SESSION_KEY }),
      Preferences.get({ key: LEGACY_SESSION_KEY }),
    ]);

    let experiences = this.parse(experiencesRaw.value);
    let experienceBox = this.parse(experienceBoxRaw.value);

    if (!experiences && !experienceBox && legacyRaw.value) {
      const legacy = this.parse(legacyRaw.value);
      if (legacy?.accessMode === 'EXPERIENCES') {
        experiences = legacy;
      } else if (legacy?.accessMode === 'EXPERIENCE_BOX') {
        experienceBox = legacy;
      }
      await Preferences.remove({ key: LEGACY_SESSION_KEY });
      await this.persist({ experiences, experienceBox });
    }

    return { experiences, experienceBox };
  }

  async saveExperiences(session: SessionState): Promise<void> {
    const stored = await this.load();
    await this.persist({ ...stored, experiences: session });
  }

  async saveExperienceBox(session: SessionState): Promise<void> {
    const stored = await this.load();
    await this.persist({ ...stored, experienceBox: session });
  }

  async clearExperiences(): Promise<void> {
    const stored = await this.load();
    await this.persist({ ...stored, experiences: null });
  }

  async clearExperienceBox(): Promise<void> {
    const stored = await this.load();
    await this.persist({ ...stored, experienceBox: null });
  }

  async clearAll(): Promise<void> {
    await Promise.all([
      Preferences.remove({ key: EXPERIENCES_SESSION_KEY }),
      Preferences.remove({ key: EXPERIENCE_BOX_SESSION_KEY }),
      Preferences.remove({ key: LEGACY_SESSION_KEY }),
    ]);
  }

  private parse(value: string | null): SessionState | null {
    if (!value) {
      return null;
    }

    try {
      const parsed = JSON.parse(value) as SessionState;
      return isValidSession(parsed) ? parsed : null;
    } catch {
      return null;
    }
  }

  private async persist(stored: StoredSessions): Promise<void> {
    await Promise.all([
      stored.experiences
        ? Preferences.set({
            key: EXPERIENCES_SESSION_KEY,
            value: JSON.stringify(stored.experiences),
          })
        : Preferences.remove({ key: EXPERIENCES_SESSION_KEY }),
      stored.experienceBox
        ? Preferences.set({
            key: EXPERIENCE_BOX_SESSION_KEY,
            value: JSON.stringify(stored.experienceBox),
          })
        : Preferences.remove({ key: EXPERIENCE_BOX_SESSION_KEY }),
    ]);
  }
}

export class InMemorySessionAdapter implements SessionPort {
  private experiences: SessionState | null = null;
  private experienceBox: SessionState | null = null;

  async load(): Promise<StoredSessions> {
    return { experiences: this.experiences, experienceBox: this.experienceBox };
  }

  async saveExperiences(session: SessionState): Promise<void> {
    this.experiences = session;
  }

  async saveExperienceBox(session: SessionState): Promise<void> {
    this.experienceBox = session;
  }

  async clearExperiences(): Promise<void> {
    this.experiences = null;
  }

  async clearExperienceBox(): Promise<void> {
    this.experienceBox = null;
  }

  async clearAll(): Promise<void> {
    this.experiences = null;
    this.experienceBox = null;
  }
}

export function createDefaultSessionAdapter(): SessionPort {
  return new SessionPreferencesAdapter();
}
