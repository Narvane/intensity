import type { BoxType } from '@domain/box/boxTypes';
import { translate, type Locale } from '../../i18n/index';
import { localizeSuggestion } from './localize';
import {
  ALL_SUGGESTION_SEEDS,
  getSuggestionSeedById,
  SUGGESTION_PACKS,
} from './packs';
import type { ExperienceSuggestion, SuggestionIntensity } from './types';

const SUGGESTIONS_PER_TYPE = 15;
const SUGGESTIONS_TOTAL = ALL_SUGGESTION_SEEDS.length;

export type { ExperienceSuggestion, ExperienceSuggestionSeed, SuggestionIntensity } from './types';

export function listSuggestions(
  locale: Locale,
  boxType: BoxType,
  intensity?: SuggestionIntensity,
): ExperienceSuggestion[] {
  const seeds = SUGGESTION_PACKS[boxType];
  const filtered = intensity
    ? seeds.filter((seed) => seed.intensity === intensity)
    : seeds;

  return filtered.map((seed) => localizeSuggestion(locale, seed));
}

export function getSuggestionById(
  locale: Locale,
  id: string,
): ExperienceSuggestion | undefined {
  const seed = getSuggestionSeedById(id);
  return seed ? localizeSuggestion(locale, seed) : undefined;
}

export function pickRandomSuggestion(
  locale: Locale,
  boxType: BoxType,
  intensity: SuggestionIntensity,
  excludeId?: string,
): ExperienceSuggestion {
  const pool = listSuggestions(locale, boxType, intensity).filter(
    (item) => item.id !== excludeId,
  );

  if (pool.length === 0) {
    return listSuggestions(locale, boxType, intensity)[0];
  }

  const index = Math.floor(Math.random() * pool.length);
  return pool[index];
}

export function countSuggestions(boxType?: BoxType): number {
  if (boxType) {
    return SUGGESTIONS_PER_TYPE;
  }
  return SUGGESTIONS_TOTAL;
}

/** @deprecated Use listSuggestions — kept for transitional callers. */
export function getSuggestions(
  locale: Locale,
  boxType: BoxType,
  intensity: SuggestionIntensity,
): string[] {
  return listSuggestions(locale, boxType, intensity).map((item) => item.description);
}

/** @deprecated Use countSuggestions */
export function countSuggestionPack(): number {
  return countSuggestions();
}

export function suggestionDescriptionSummary(description: string, maxLength = 72): string {
  if (description.length <= maxLength) {
    return description;
  }
  return `${description.slice(0, maxLength - 1).trimEnd()}…`;
}

export function formatSuggestionIntensityLabel(locale: Locale, intensity: number): string {
  return translate(locale, 'suggestions.intensityLabel', { level: intensity });
}
