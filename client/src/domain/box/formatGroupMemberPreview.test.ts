import { describe, expect, it } from 'vitest';
import { formatGroupMemberPreview } from '@domain/box/formatGroupMemberPreview';

const t = (key: string, vars?: Record<string, string | number>) => {
  if (key === 'groups.memberPreview.pair') {
    return `${vars?.first} e ${vars?.second}`;
  }
  if (key === 'groups.memberPreview.overflow') {
    return `${vars?.names} e +${vars?.count}`;
  }
  return key;
};

describe('formatGroupMemberPreview', () => {
  it('returns empty for no members', () => {
    expect(formatGroupMemberPreview([], t)).toBe('');
  });

  it('returns single name', () => {
    expect(formatGroupMemberPreview(['Ana'], t)).toBe('Ana');
  });

  it('joins two names', () => {
    expect(formatGroupMemberPreview(['Ana', 'Bruno'], t)).toBe('Ana e Bruno');
  });

  it('abbreviates larger groups', () => {
    expect(formatGroupMemberPreview(['Ana', 'Bruno', 'Carol', 'Diana'], t)).toBe(
      'Ana, Bruno e +2',
    );
  });
});
