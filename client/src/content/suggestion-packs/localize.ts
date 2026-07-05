import { translate, type Locale } from '../../i18n/index';
import { getSuggestionText } from './text';
import type { ExperienceSuggestion, ExperienceSuggestionSeed } from './types';

function localizedField(
  locale: Locale,
  seed: ExperienceSuggestionSeed,
  field: 'description' | 'reflection',
): string {
  const authored = getSuggestionText(seed.id, locale);
  if (authored?.[field]) {
    return authored[field];
  }

  const placeholderKey = `suggestions.placeholder.${field}`;
  const typeTitle = translate(locale, `boxTypes.${seed.boxType}.title`);
  return translate(locale, placeholderKey, {
    type: typeTitle,
    level: seed.intensity,
    idea: seed.ideaIndex,
  });
}

export function localizeSuggestion(
  locale: Locale,
  seed: ExperienceSuggestionSeed,
): ExperienceSuggestion {
  return {
    id: seed.id,
    boxType: seed.boxType,
    intensity: seed.intensity,
    description: localizedField(locale, seed, 'description'),
    reflection: localizedField(locale, seed, 'reflection'),
    parameters: seed.parameters,
  };
}
