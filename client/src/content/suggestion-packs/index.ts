import type { BoxType } from '@domain/box/boxTypes';
import { translate, type Locale } from '../../i18n/index';

type Intensity = 1 | 2 | 3 | 4 | 5;

export function getSuggestions(
  locale: Locale,
  boxType: BoxType,
  intensity: Intensity,
): string[] {
  const typeTitle = translate(locale, `boxTypes.${boxType}.title`);
  const start = (intensity - 1) * 3;

  return [0, 1, 2].map((offset) =>
    translate(locale, 'assistant.suggestionItem', {
      type: typeTitle,
      number: start + offset + 1,
    }),
  );
}

export function countSuggestionPack(): number {
  return 11 * 5 * 3;
}
