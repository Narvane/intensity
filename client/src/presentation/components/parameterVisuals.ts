import { Compass, Dumbbell, MessageCircleHeart, type LucideIcon } from 'lucide-react';
import type { ExperienceParameters } from '@domain/experience/experienceTypes';

export type ParameterKey = keyof ExperienceParameters;

export interface ParameterVisual {
  Icon: LucideIcon;
  cssVar: '--param-effort' | '--param-openness' | '--param-novelty';
}

const PARAMETER_VISUALS: Record<ParameterKey, ParameterVisual> = {
  effort: { Icon: Dumbbell, cssVar: '--param-effort' },
  openness: { Icon: MessageCircleHeart, cssVar: '--param-openness' },
  novelty: { Icon: Compass, cssVar: '--param-novelty' },
};

export const PARAMETER_KEYS: ParameterKey[] = ['effort', 'openness', 'novelty'];

export function getParameterVisual(key: ParameterKey): ParameterVisual {
  return PARAMETER_VISUALS[key];
}
