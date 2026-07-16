import { describe, expect, it } from 'vitest';
import type { SessionState } from '@domain/session/SessionPort';
import { resolveUnauthorizedDecision } from '@domain/session/unauthorizedPolicy';

const experiences: SessionState = {
  token: 'experiences-token',
  accessMode: 'EXPERIENCES',
  participantId: 'p1',
};

const experienceBox: SessionState = {
  token: 'box-token',
  accessMode: 'EXPERIENCE_BOX',
  groupId: 'g1',
};

describe('resolveUnauthorizedDecision', () => {
  it('clears only the session whose token was rejected', () => {
    expect(resolveUnauthorizedDecision(experiences, experienceBox, 'experiences-token')).toEqual({
      clearExperiences: true,
      clearExperienceBox: false,
    });
    expect(resolveUnauthorizedDecision(experiences, experienceBox, 'box-token')).toEqual({
      clearExperiences: false,
      clearExperienceBox: true,
    });
  });

  it('clears nothing when the rejected token matches no stored session', () => {
    expect(resolveUnauthorizedDecision(experiences, experienceBox, 'stale-token')).toEqual({
      clearExperiences: false,
      clearExperienceBox: false,
    });
  });

  it('clears nothing on a token-less 401', () => {
    expect(resolveUnauthorizedDecision(experiences, experienceBox, undefined)).toEqual({
      clearExperiences: false,
      clearExperienceBox: false,
    });
  });

  it('handles missing sessions', () => {
    expect(resolveUnauthorizedDecision(null, null, 'experiences-token')).toEqual({
      clearExperiences: false,
      clearExperienceBox: false,
    });
  });
});
