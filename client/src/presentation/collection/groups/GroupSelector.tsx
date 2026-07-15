import type { Group } from '@domain/box/boxTypes';
import { resolveGroupDisplayName } from '@domain/box/resolveGroupDisplayName';
import { useI18n } from '../../../i18n/I18nContext';
import { resolveGroupAccent } from '../../components/collection/groupVisuals';
import styles from './GroupSelector.module.css';

interface GroupSelectorProps {
  groups: Group[];
  value: string;
  onChange: (groupId: string) => void;
}

export function GroupSelector({ groups, value, onChange }: GroupSelectorProps) {
  const { t } = useI18n();

  if (groups.length <= 1) {
    return null;
  }

  return (
    <div className={styles.selector} role="group" aria-label={t('groups.selectorLabel')}>
      <span className={styles.label}>{t('groups.selectorLabel')}</span>
      <div className={styles.options}>
        {groups.map((group) => {
          const accent = resolveGroupAccent(group);
          const name = resolveGroupDisplayName(group, t);
          const selected = group.id === value;

          return (
            <button
              key={group.id}
              type="button"
              className={selected ? styles.optionActive : styles.option}
              data-accent={accent}
              aria-pressed={selected}
              onClick={() => onChange(group.id)}
            >
              {name}
            </button>
          );
        })}
      </div>
    </div>
  );
}
