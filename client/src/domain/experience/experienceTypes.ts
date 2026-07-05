import type { ExperienceType } from './experienceType';

export interface ExperienceParameters {
  effort: number;
  unpredictability: number;
  novelty: number;
}

export interface Experience {
  id: string;
  boxId: string;
  authorId: string;
  authorDisplayName?: string;
  description?: string;
  reflection?: string;
  intensity: number;
  parameters: ExperienceParameters;
  type: ExperienceType;
  seal: string;
  summaryOnly: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface ExperienceInput {
  description: string;
  reflection: string;
  intensity: number;
  parameters: ExperienceParameters;
  type: ExperienceType;
}

export const DEFAULT_PARAMETERS: ExperienceParameters = {
  effort: 3,
  unpredictability: 3,
  novelty: 3,
};

export function suggestIntensity(parameters: ExperienceParameters): number {
  const average =
    (parameters.effort + parameters.unpredictability + parameters.novelty) / 3;
  return Math.min(5, Math.max(1, Math.round(average)));
}
