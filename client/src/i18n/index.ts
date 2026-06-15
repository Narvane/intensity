import en from './locales/en.json';
import ptBr from './locales/pt-BR.json';
import it from './locales/it.json';

export type Locale = 'en' | 'pt-BR' | 'it';

export const SUPPORTED_LOCALES: Locale[] = ['en', 'pt-BR', 'it'];
export const DEFAULT_LOCALE: Locale = 'en';

const catalogs: Record<Locale, Record<string, unknown>> = {
  en,
  'pt-BR': ptBr,
  it,
};

function resolvePath(source: Record<string, unknown>, path: string): unknown {
  return path.split('.').reduce<unknown>((current, segment) => {
    if (current && typeof current === 'object' && segment in current) {
      return (current as Record<string, unknown>)[segment];
    }
    return undefined;
  }, source);
}

export function normalizeLocale(value: string | null | undefined): Locale {
  if (!value) {
    return DEFAULT_LOCALE;
  }

  const lower = value.toLowerCase();
  if (lower.startsWith('pt')) {
    return 'pt-BR';
  }
  if (lower.startsWith('it')) {
    return 'it';
  }
  return 'en';
}

export function detectDeviceLocale(): Locale {
  if (typeof navigator === 'undefined') {
    return DEFAULT_LOCALE;
  }

  const candidates = navigator.languages?.length
    ? navigator.languages
    : [navigator.language];

  for (const candidate of candidates) {
    const normalized = normalizeLocale(candidate);
    if (SUPPORTED_LOCALES.includes(normalized)) {
      return normalized;
    }
  }

  return DEFAULT_LOCALE;
}

export function translate(
  locale: Locale,
  key: string,
  params?: Record<string, string | number>,
): string {
  const catalog = catalogs[locale] ?? catalogs[DEFAULT_LOCALE];
  const fallback = catalogs[DEFAULT_LOCALE];
  const raw = resolvePath(catalog, key) ?? resolvePath(fallback, key);

  if (typeof raw !== 'string') {
    return key;
  }

  if (!params) {
    return raw;
  }

  return raw.replace(/\{\{(\w+)\}\}/g, (_, token: string) =>
    params[token] !== undefined ? String(params[token]) : `{{${token}}}`,
  );
}

export type TranslateFn = (key: string, params?: Record<string, string | number>) => string;

export function createTranslator(locale: Locale): TranslateFn {
  return (key, params) => translate(locale, key, params);
}
