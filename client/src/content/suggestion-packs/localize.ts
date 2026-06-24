import { translate, type Locale } from '../../i18n/index';
import type { ExperienceSuggestion, ExperienceSuggestionSeed } from './types';

function localizedField(
  locale: Locale,
  seed: ExperienceSuggestionSeed,
  field: 'description' | 'reflection',
): string {
  const contentKey = `suggestions.content.${seed.id}.${field}`;
  const content = translate(locale, contentKey);
  if (content !== contentKey) {
    return content;
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
