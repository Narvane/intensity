import type { BoxType } from '@domain/box/boxTypes';
import type { ExperienceParameters } from '@domain/experience/experienceTypes';

export type SuggestionIntensity = 1 | 2 | 3 | 4 | 5;
export type SuggestionIdeaIndex = 1 | 2 | 3;

/** Canonical seed — text is resolved at query time via i18n. */
export interface ExperienceSuggestionSeed {
  id: string;
  boxType: BoxType;
  intensity: SuggestionIntensity;
  ideaIndex: SuggestionIdeaIndex;
  parameters: ExperienceParameters;
}

/** Localized suggestion ready for UI and persistence. */
export interface ExperienceSuggestion {
  id: string;
  boxType: BoxType;
  intensity: SuggestionIntensity;
  description: string;
  reflection: string;
  parameters: ExperienceParameters;
}
