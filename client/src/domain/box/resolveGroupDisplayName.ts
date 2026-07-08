import type { Group } from '@domain/box/boxTypes';
import { formatGroupMemberPreview } from '@domain/box/formatGroupMemberPreview';

type Translate = (key: string, vars?: Record<string, string | number>) => string;

export function resolveGroupDisplayName(group: Group, t: Translate): string {
  const trimmed = group.name.trim();
  if (trimmed.length > 0) {
    return trimmed;
  }

  return formatGroupMemberPreview(
    group.members.map((member) => member.displayName),
    t,
  );
}
