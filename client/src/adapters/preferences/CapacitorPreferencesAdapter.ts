import { Preferences } from '@capacitor/preferences';
import {
  DEFAULT_LOCALE,
  detectDeviceLocale,
  normalizeLocale,
  type Locale,
} from '../../i18n';
import {
  PREFERENCE_KEYS,
  type PreferencesPort,
  type UserPreferences,
} from '@domain/preferences/PreferencesPort';

export class CapacitorPreferencesAdapter implements PreferencesPort {
  async load(): Promise<UserPreferences> {
    const [{ value: languageValue }, { value: onboardingValue }] = await Promise.all([
      Preferences.get({ key: PREFERENCE_KEYS.language }),
      Preferences.get({ key: PREFERENCE_KEYS.onboardingCompleted }),
    ]);

    const language = languageValue
      ? normalizeLocale(languageValue)
      : detectDeviceLocale();

    if (!languageValue) {
      await Preferences.set({ key: PREFERENCE_KEYS.language, value: language });
    }

    return {
      language,
      onboardingCompleted: onboardingValue === 'true',
    };
  }

  async saveLanguage(language: Locale): Promise<void> {
    await Preferences.set({ key: PREFERENCE_KEYS.language, value: language });
  }

  async completeOnboarding(): Promise<void> {
    await Preferences.set({ key: PREFERENCE_KEYS.onboardingCompleted, value: 'true' });
  }
}

export class InMemoryPreferencesAdapter implements PreferencesPort {
  private store = new Map<string, string>();
  private shouldFail = false;

  constructor(initial?: Partial<UserPreferences>) {
    if (initial?.language) {
      this.store.set(PREFERENCE_KEYS.language, initial.language);
    }
    if (initial?.onboardingCompleted) {
      this.store.set(PREFERENCE_KEYS.onboardingCompleted, 'true');
    }
  }

  failOnLoad(value = true): void {
    this.shouldFail = value;
  }

  async load(): Promise<UserPreferences> {
    if (this.shouldFail) {
      throw new Error('preferences_unavailable');
    }
    const languageValue = this.store.get(PREFERENCE_KEYS.language);
    const language = languageValue
      ? normalizeLocale(languageValue)
      : detectDeviceLocale();

    if (!languageValue) {
      this.store.set(PREFERENCE_KEYS.language, language);
    }

    return {
      language,
      onboardingCompleted: this.store.get(PREFERENCE_KEYS.onboardingCompleted) === 'true',
    };
  }

  async saveLanguage(language: Locale): Promise<void> {
    this.store.set(PREFERENCE_KEYS.language, language);
  }

  async completeOnboarding(): Promise<void> {
    this.store.set(PREFERENCE_KEYS.onboardingCompleted, 'true');
  }
}

export function createDefaultPreferences(): PreferencesPort {
  return new CapacitorPreferencesAdapter();
}

export { DEFAULT_LOCALE };
