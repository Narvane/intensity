import { describe, expect, it } from 'vitest';
import {
  resolveExperiencesSessionContinuePath,
  resolveGuestRouteRedirect,
} from '@domain/auth/guestRouteRedirect';
import type { SessionState } from '@domain/session/SessionPort';

const boxSession: SessionState = {
  token: 'token',
  accessMode: 'EXPERIENCE_BOX',
  groupId: 'g1',
  members: [],
};

describe('resolveGuestRouteRedirect', () => {
  it('returns null when there is no experience box session', () => {
    expect(resolveGuestRouteRedirect(null)).toBeNull();
  });

  it('redirects experience box session to box home', () => {
    expect(resolveGuestRouteRedirect(boxSession)).toBe('/box-home');
  });
});

describe('resolveExperiencesSessionContinuePath', () => {
  it('defaults to groups', () => {
    expect(resolveExperiencesSessionContinuePath()).toBe('/groups');
  });

  it('honors returnTo for invite join paths', () => {
    expect(
      resolveExperiencesSessionContinuePath({
        returnTo: '/join?code=AB23CD',
      }),
    ).toBe('/join?code=AB23CD');
  });

  it('honors pending return path when router state is missing', () => {
    expect(
      resolveExperiencesSessionContinuePath({
        pendingReturnPath: '/join?t=abc',
      }),
    ).toBe('/join?t=abc');
  });

  it('ignores invalid returnTo values', () => {
    expect(
      resolveExperiencesSessionContinuePath({
        returnTo: '/groups/secret',
      }),
    ).toBe('/groups');
  });
});
