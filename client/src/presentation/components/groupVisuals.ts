import type { GroupAccent } from '@domain/box/boxTypes';

export type { GroupAccent };

const GROUP_ACCENTS: GroupAccent[] = ['coral', 'teal', 'purple', 'yellow'];

export const GROUP_ACCENT_CSS_VAR: Record<GroupAccent, string> = {
  coral: 'var(--coral)',
  teal: 'var(--teal)',
  purple: 'var(--purple)',
  yellow: 'var(--yellow)',
};

export function getGroupAccent(groupId: string): GroupAccent {
  let hash = 0;
  for (let index = 0; index < groupId.length; index += 1) {
    hash = (hash * 31 + groupId.charCodeAt(index)) >>> 0;
  }
  return GROUP_ACCENTS[hash % GROUP_ACCENTS.length];
}

export function resolveGroupAccent(group: { id: string; color?: string | null }): GroupAccent {
  if (group.color && GROUP_ACCENTS.includes(group.color as GroupAccent)) {
    return group.color as GroupAccent;
  }
  return getGroupAccent(group.id);
}

export function isGroupAccent(value: string): value is GroupAccent {
  return GROUP_ACCENTS.includes(value as GroupAccent);
}

export { GROUP_ACCENTS };
