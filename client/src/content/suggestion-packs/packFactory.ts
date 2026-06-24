import type { BoxType } from '@domain/box/boxTypes';
import type { ExperienceParameters } from '@domain/experience/experienceTypes';
import type { ExperienceSuggestionSeed, SuggestionIdeaIndex, SuggestionIntensity } from './types';

const IDEA_OFFSETS: Record<SuggestionIdeaIndex, ExperienceParameters> = {
  1: { effort: 0, openness: 0, novelty: 0 },
  2: { effort: 1, openness: -1, novelty: 0 },
  3: { effort: -1, openness: 1, novelty: 1 },
};

function clampLevel(value: number): number {
  return Math.min(5, Math.max(1, value));
}

function deriveParameters(
  intensity: SuggestionIntensity,
  ideaIndex: SuggestionIdeaIndex,
): ExperienceParameters {
  const offset = IDEA_OFFSETS[ideaIndex];
  return {
    effort: clampLevel(intensity + offset.effort),
    openness: clampLevel(intensity + offset.openness),
    novelty: clampLevel(intensity + offset.novelty),
  };
}

function buildSuggestionId(
  boxType: BoxType,
  intensity: SuggestionIntensity,
  ideaIndex: SuggestionIdeaIndex,
): string {
  return `${boxType}-${intensity}-${String(ideaIndex).padStart(2, '0')}`;
}

/** Builds the 15 canonical seeds for a single box type. */
export function buildPackForType(boxType: BoxType): ExperienceSuggestionSeed[] {
  const seeds: ExperienceSuggestionSeed[] = [];

  for (let intensity = 1; intensity <= 5; intensity += 1) {
    for (let ideaIndex = 1; ideaIndex <= 3; ideaIndex += 1) {
      const typedIntensity = intensity as SuggestionIntensity;
      const typedIdeaIndex = ideaIndex as SuggestionIdeaIndex;
      seeds.push({
        id: buildSuggestionId(boxType, typedIntensity, typedIdeaIndex),
        boxType,
        intensity: typedIntensity,
        ideaIndex: typedIdeaIndex,
        parameters: deriveParameters(typedIntensity, typedIdeaIndex),
      });
    }
  }

  return seeds;
}
