export const EXPERIENCE_TYPE_KEYS = [
  'none',
  'explore',
  'randomness',
  'exposure',
  'constraints',
  'overcoming',
  'creativity',
  'contrast',
  'connection',
  'contemplation',
  'narrative',
] as const;

export type ExperienceType = (typeof EXPERIENCE_TYPE_KEYS)[number];

export const DEFAULT_EXPERIENCE_TYPE: ExperienceType = 'none';

/** Emoji shown alongside the type — kept out of i18n so it stays identical across locales. */
export const EXPERIENCE_TYPE_EMOJI: Record<ExperienceType, string> = {
  none: '',
  explore: '🌍',
  randomness: '🎲',
  exposure: '🎭',
  constraints: '🚧',
  overcoming: '⚡',
  creativity: '🎨',
  contrast: '🔀',
  connection: '🤝',
  contemplation: '🧘',
  narrative: '📖',
};

/** Selectable types in the wizard, in display order (none is the implicit default). */
export const SELECTABLE_EXPERIENCE_TYPES: ExperienceType[] = EXPERIENCE_TYPE_KEYS.filter(
  (key): key is ExperienceType => key !== 'none',
);

export function isExperienceType(value: unknown): value is ExperienceType {
  return (
    typeof value === 'string' &&
    (EXPERIENCE_TYPE_KEYS as readonly string[]).includes(value)
  );
}

export function normalizeExperienceType(value: unknown): ExperienceType {
  return isExperienceType(value) ? value : DEFAULT_EXPERIENCE_TYPE;
}
