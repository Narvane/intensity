export type GroupAccent = 'coral' | 'teal' | 'purple' | 'yellow';

const GROUP_ACCENTS: GroupAccent[] = ['coral', 'teal', 'purple', 'yellow'];

export function getGroupAccent(groupId: string): GroupAccent {
  let hash = 0;
  for (let index = 0; index < groupId.length; index += 1) {
    hash = (hash * 31 + groupId.charCodeAt(index)) >>> 0;
  }
  return GROUP_ACCENTS[hash % GROUP_ACCENTS.length];
}
