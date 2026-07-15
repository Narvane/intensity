import { Dices, Dumbbell, Sparkles, type LucideIcon } from 'lucide-react';
import type { ExperienceParameters } from '@domain/experience/experienceTypes';

export type ParameterKey = keyof ExperienceParameters;

export interface ParameterVisual {
  Icon: LucideIcon;
  cssVar: '--param-effort' | '--param-unpredictability' | '--param-novelty';
}

const PARAMETER_VISUALS: Record<ParameterKey, ParameterVisual> = {
  effort: { Icon: Dumbbell, cssVar: '--param-effort' },
  unpredictability: { Icon: Dices, cssVar: '--param-unpredictability' },
  novelty: { Icon: Sparkles, cssVar: '--param-novelty' },
};

export const PARAMETER_KEYS: ParameterKey[] = ['effort', 'unpredictability', 'novelty'];

export function getParameterVisual(key: ParameterKey): ParameterVisual {
  return PARAMETER_VISUALS[key];
}
