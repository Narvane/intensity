import type { Experience } from '@domain/experience/experienceTypes';

export type ExperienceViewContext = 'EXPERIENCES_LIST' | 'DRAW_COVER' | 'DRAW_FACE';

export interface ListVisibilityOptions {
  isAuthor: boolean;
  previewAsOthers?: boolean;
}

export function hasFullContent(experience: Experience): boolean {
  return !experience.summaryOnly;
}

export function shouldShowDescription(
  experience: Experience,
  context: ExperienceViewContext,
  options?: ListVisibilityOptions,
): boolean {
  if (context === 'DRAW_COVER') {
    return false;
  }

  if (context === 'DRAW_FACE') {
    return Boolean(experience.description);
  }

  if (experience.summaryOnly) {
    return false;
  }

  if (options?.isAuthor && options.previewAsOthers) {
    return false;
  }

  return Boolean(experience.description);
}

export function shouldShowReflection(
  experience: Experience,
  context: ExperienceViewContext,
  options?: ListVisibilityOptions,
): boolean {
  if (context === 'DRAW_COVER') {
    return false;
  }

  if (context === 'DRAW_FACE') {
    return Boolean(experience.reflection);
  }

  if (experience.summaryOnly) {
    return false;
  }

  if (options?.isAuthor && options.previewAsOthers) {
    return false;
  }

  return Boolean(experience.reflection);
}

export function isSummaryOnlyView(
  experience: Experience,
  context: ExperienceViewContext,
  options?: ListVisibilityOptions,
): boolean {
  if (context === 'DRAW_COVER') {
    return true;
  }

  if (context === 'DRAW_FACE') {
    return false;
  }

  return (
    experience.summaryOnly || Boolean(options?.isAuthor && options?.previewAsOthers)
  );
}

export function canManageExperience(
  experience: Experience,
  participantId?: string,
): boolean {
  return Boolean(participantId && experience.authorId === participantId);
}
