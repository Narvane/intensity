import type { Locale } from '../../i18n';

export interface UserPreferences {
  language: Locale;
  onboardingCompleted: boolean;
}

export interface PreferencesPort {
  load(): Promise<UserPreferences>;
  saveLanguage(language: Locale): Promise<void>;
  completeOnboarding(): Promise<void>;
}

export const PREFERENCE_KEYS = {
  language: 'language',
  onboardingCompleted: 'onboardingCompleted',
} as const;
