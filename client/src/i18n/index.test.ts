import { describe, expect, it } from 'vitest';
import { detectDeviceLocale, normalizeLocale, translate } from './index';

describe('i18n', () => {
  it('normalizes locale codes', () => {
    expect(normalizeLocale('pt')).toBe('pt-BR');
    expect(normalizeLocale('it-IT')).toBe('it');
    expect(normalizeLocale('en-US')).toBe('en');
  });

  it('translates nested keys with interpolation', () => {
    expect(translate('en', 'onboarding.stepIndicator', { current: 2, total: 4 })).toBe(
      'Step 2 of 4',
    );
    expect(translate('pt-BR', 'app.name')).toBe('Intensity');
  });

  it('falls back to English for missing keys', () => {
    expect(translate('it', 'app.name')).toBe('Intensity');
  });

  it('detects device locale safely in tests', () => {
    expect(['en', 'pt-BR', 'it']).toContain(detectDeviceLocale());
  });
});
