import {
  useCallback,
  useEffect,
  useMemo,
  useState,
  type PropsWithChildren,
} from 'react';
import { createDefaultPreferences } from '@adapters/preferences/CapacitorPreferencesAdapter';
import { CompleteOnboardingUseCase } from '@domain/bootstrap/CompleteOnboardingUseCase';
import { LoadBootstrapUseCase } from '@domain/bootstrap/LoadBootstrapUseCase';
import type { PreferencesPort } from '@domain/preferences/PreferencesPort';
import {
  createTranslator,
  type Locale,
  type TranslateFn,
} from './index';
import { I18nContext } from './I18nContext';

interface I18nProviderProps extends PropsWithChildren {
  preferences?: PreferencesPort;
}

export function I18nProvider({ children, preferences }: I18nProviderProps) {
  const preferencesPort = useMemo(
    () => preferences ?? createDefaultPreferences(),
    [preferences],
  );
  const [locale, setLocaleState] = useState<Locale>('en');
  const [ready, setReady] = useState(false);

  useEffect(() => {
    let active = true;

    preferencesPort
      .load()
      .then((prefs) => {
        if (active) {
          setLocaleState(prefs.language);
          setReady(true);
        }
      })
      .catch(() => {
        if (active) {
          setReady(true);
        }
      });

    return () => {
      active = false;
    };
  }, [preferencesPort]);

  const setLocale = useCallback(
    async (next: Locale) => {
      await preferencesPort.saveLanguage(next);
      setLocaleState(next);
    },
    [preferencesPort],
  );

  const t: TranslateFn = useCallback(
    (key, params) => createTranslator(locale)(key, params),
    [locale],
  );

  const value = useMemo(
    () => ({ locale, setLocale, t }),
    [locale, setLocale, t],
  );

  if (!ready) {
    return null;
  }

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function usePreferencesPort(): PreferencesPort {
  return useMemo(() => createDefaultPreferences(), []);
}

export { CompleteOnboardingUseCase, LoadBootstrapUseCase };
