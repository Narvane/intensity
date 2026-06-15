import { describe, expect, it } from 'vitest';
import type { Experience } from '@domain/experience/experienceTypes';
import {
  canManageExperience,
  hasFullContent,
  isSummaryOnlyView,
  shouldShowDescription,
} from '@domain/experience/experienceVisibility';

const ownExperience: Experience = {
  id: '1',
  boxId: 'box',
  authorId: 'author-1',
  authorDisplayName: 'Alice',
  description: 'Secret idea',
  reflection: 'Because we need this',
  intensity: 3,
  parameters: { effort: 3, openness: 3, novelty: 3 },
  seal: 'ABCD1234',
  summaryOnly: false,
  createdAt: '2026-01-01T00:00:00.000Z',
};

const otherExperience: Experience = {
  ...ownExperience,
  id: '2',
  authorId: 'author-2',
  description: undefined,
  reflection: undefined,
  summaryOnly: true,
};

describe('experienceVisibility', () => {
  it('hides description on draw cover', () => {
    expect(shouldShowDescription(ownExperience, 'DRAW_COVER')).toBe(false);
    expect(shouldShowDescription(ownExperience, 'DRAW_FACE')).toBe(true);
  });

  it('shows full content for author in list', () => {
    expect(
      shouldShowDescription(ownExperience, 'EXPERIENCES_LIST', {
        isAuthor: true,
      }),
    ).toBe(true);
  });

  it('supports preview-as-others for author items', () => {
    expect(
      isSummaryOnlyView(ownExperience, 'EXPERIENCES_LIST', {
        isAuthor: true,
        previewAsOthers: true,
      }),
    ).toBe(true);
    expect(
      shouldShowDescription(ownExperience, 'EXPERIENCES_LIST', {
        isAuthor: true,
        previewAsOthers: true,
      }),
    ).toBe(false);
  });

  it('hides other members content in list', () => {
    expect(hasFullContent(otherExperience)).toBe(false);
    expect(
      shouldShowDescription(otherExperience, 'EXPERIENCES_LIST', {
        isAuthor: false,
      }),
    ).toBe(false);
  });

  it('allows manage actions only for author', () => {
    expect(canManageExperience(ownExperience, 'author-1')).toBe(true);
    expect(canManageExperience(otherExperience, 'author-1')).toBe(false);
  });
});
