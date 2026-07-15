import type { GroupHeadingEntry } from '@domain/box/sessionGroups';
import { useI18n } from '../../../i18n/I18nContext';
import { GROUP_ACCENT_CSS_VAR } from '../../components/collection/groupVisuals';
import styles from './GroupHeading.module.css';

interface GroupHeadingProps {
  groups: GroupHeadingEntry[];
}

export function GroupHeading({ groups }: GroupHeadingProps) {
  const { t } = useI18n();
  const label =
    groups.length > 1 ? t('groups.headingLabelPlural') : t('groups.headingLabel');

  return (
    <h1 className={styles.heading}>
      <span className={styles.label}>{label}</span>{' '}
      {groups.map((group, index) => (
        <span key={`${group.name}-${index}`}>
          {index > 0 && <span className={styles.separator}>, </span>}
          <span className={styles.name} style={{ color: GROUP_ACCENT_CSS_VAR[group.accent] }}>
            {group.name}
          </span>
        </span>
      ))}
    </h1>
  );
}
