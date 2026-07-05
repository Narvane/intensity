export const INTENSITY_MIN = 1;
export const INTENSITY_MAX = 5;

export const INTENSITY_COLORS: Record<number, string> = {
  1: '#2DBD9A',
  2: '#5BC8B0',
  3: '#FFC94D',
  4: '#FF9A4D',
  5: '#FF6B3D',
};

export const PARAMETER_KEYS = ['effort', 'unpredictability', 'novelty'] as const;

export type ParameterKey = (typeof PARAMETER_KEYS)[number];

export const PARAMETER_COLORS: Record<ParameterKey, string> = {
  effort: '#2DBD9A',
  unpredictability: '#7B5CF6',
  novelty: '#FFC94D',
};

export function isValidIntensity(value: number): boolean {
  return Number.isInteger(value) && value >= INTENSITY_MIN && value <= INTENSITY_MAX;
}

export function isValidParameterValue(value: number): boolean {
  return isValidIntensity(value);
}

export function clampIntensity(value: number): number {
  return Math.min(INTENSITY_MAX, Math.max(INTENSITY_MIN, Math.round(value)));
}

export interface ExperienceParametersInput {
  effort: number;
  unpredictability: number;
  novelty: number;
}

export function validateExperienceParameters(
  parameters: ExperienceParametersInput,
): string | null {
  for (const key of PARAMETER_KEYS) {
    if (!isValidParameterValue(parameters[key])) {
      return key;
    }
  }

  return null;
}
