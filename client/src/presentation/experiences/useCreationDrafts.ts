import { useCallback, useMemo, useState } from 'react';
import type { Experience, ExperienceParameters } from '@domain/experience/experienceTypes';
import { DEFAULT_PARAMETERS, suggestIntensity } from '@domain/experience/experienceTypes';
import type { ExperienceType } from '@domain/experience/experienceType';
import { DEFAULT_EXPERIENCE_TYPE } from '@domain/experience/experienceType';

export const MAX_DRAFTS = 5;

export interface CreationDraft {
  /** Stable local id for list keys and paginator navigation. */
  uid: string;
  description: string;
  reflection: string;
  intensity: number;
  /** Once the author sets intensity by hand we stop auto-suggesting it. */
  intensityTouched: boolean;
  parameters: ExperienceParameters;
  type: ExperienceType;
}

let draftCounter = 0;

function nextUid(): string {
  draftCounter += 1;
  return `draft-${draftCounter}`;
}

export function createEmptyDraft(): CreationDraft {
  return {
    uid: nextUid(),
    description: '',
    reflection: '',
    intensity: 3,
    intensityTouched: false,
    parameters: DEFAULT_PARAMETERS,
    type: DEFAULT_EXPERIENCE_TYPE,
  };
}

export function draftFromExperience(experience: Experience): CreationDraft {
  return {
    uid: nextUid(),
    description: experience.description ?? '',
    reflection: experience.reflection ?? '',
    intensity: experience.intensity,
    intensityTouched: true,
    parameters: experience.parameters,
    type: experience.type,
  };
}

export interface CreationDraftsController {
  drafts: CreationDraft[];
  activeIndex: number;
  activeDraft: CreationDraft;
  isForked: boolean;
  canFork: boolean;
  setActiveIndex: (index: number) => void;
  updateDraft: (index: number, patch: Partial<CreationDraft>) => void;
  updateActiveDraft: (patch: Partial<CreationDraft>) => void;
  forkFromDraft: (index: number) => void;
  removeDraft: (index: number) => void;
  reset: (drafts: CreationDraft[]) => void;
}

export function useCreationDrafts(initial: () => CreationDraft[]): CreationDraftsController {
  const [drafts, setDrafts] = useState<CreationDraft[]>(initial);
  const [activeIndex, setActiveIndexState] = useState(0);

  const clampIndex = useCallback(
    (index: number, length: number) => Math.max(0, Math.min(index, length - 1)),
    [],
  );

  const setActiveIndex = useCallback(
    (index: number) => {
      setActiveIndexState((current) => {
        void current;
        return clampIndex(index, drafts.length);
      });
    },
    [clampIndex, drafts.length],
  );

  const updateDraft = useCallback((index: number, patch: Partial<CreationDraft>) => {
    setDrafts((current) =>
      current.map((draft, i) => (i === index ? { ...draft, ...patch } : draft)),
    );
  }, []);

  const updateActiveDraft = useCallback(
    (patch: Partial<CreationDraft>) => updateDraft(activeIndex, patch),
    [activeIndex, updateDraft],
  );

  const forkFromDraft = useCallback((index: number) => {
    setDrafts((current) => {
      if (current.length >= MAX_DRAFTS) {
        return current;
      }
      const source = current[index] ?? current[current.length - 1];
      const copy: CreationDraft = { ...source, uid: nextUid() };
      return [...current, copy];
    });
  }, []);

  const removeDraft = useCallback((index: number) => {
    setDrafts((current) => {
      if (current.length <= 1) {
        return current;
      }
      const next = current.filter((_, i) => i !== index);
      setActiveIndexState((active) => Math.max(0, Math.min(active, next.length - 1)));
      return next;
    });
  }, []);

  const reset = useCallback((nextDrafts: CreationDraft[]) => {
    setDrafts(nextDrafts.length > 0 ? nextDrafts : [createEmptyDraft()]);
    setActiveIndexState(0);
  }, []);

  const safeActiveIndex = clampIndex(activeIndex, drafts.length);
  const activeDraft = drafts[safeActiveIndex];

  return useMemo(
    () => ({
      drafts,
      activeIndex: safeActiveIndex,
      activeDraft,
      isForked: drafts.length > 1,
      canFork: drafts.length < MAX_DRAFTS,
      setActiveIndex,
      updateDraft,
      updateActiveDraft,
      forkFromDraft,
      removeDraft,
      reset,
    }),
    [
      drafts,
      safeActiveIndex,
      activeDraft,
      setActiveIndex,
      updateDraft,
      updateActiveDraft,
      forkFromDraft,
      removeDraft,
      reset,
    ],
  );
}

/** Recomputes the auto-suggested intensity unless the author has taken over. */
export function withSuggestedIntensity(draft: CreationDraft): Partial<CreationDraft> {
  if (draft.intensityTouched) {
    return {};
  }
  return { intensity: suggestIntensity(draft.parameters) };
}
