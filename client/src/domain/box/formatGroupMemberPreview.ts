type Translate = (key: string, vars?: Record<string, string | number>) => string;

export function formatGroupMemberPreview(
  displayNames: string[],
  t: Translate,
  maxVisible = 2,
): string {
  if (displayNames.length === 0) {
    return '';
  }

  if (displayNames.length === 1) {
    return t('groups.memberPreview.solo');
  }

  if (displayNames.length === 2) {
    return t('groups.memberPreview.pair', {
      first: displayNames[0],
      second: displayNames[1],
    });
  }

  const visible = displayNames.slice(0, maxVisible).join(', ');
  return t('groups.memberPreview.overflow', {
    names: visible,
    count: displayNames.length - maxVisible,
  });
}
