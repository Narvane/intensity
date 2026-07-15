import { Check } from 'lucide-react';
import type { ExperienceType } from '@domain/experience/experienceType';
import {
  DEFAULT_EXPERIENCE_TYPE,
  EXPERIENCE_TYPE_EMOJI,
  SELECTABLE_EXPERIENCE_TYPES,
} from '@domain/experience/experienceType';
import { useI18n } from '../../../i18n/I18nContext';
import styles from './ExperienceTypePicker.module.css';

interface ExperienceTypePickerProps {
  value: ExperienceType;
  onChange: (value: ExperienceType) => void;
}

export function ExperienceTypePicker({ value, onChange }: ExperienceTypePickerProps) {
  const { t } = useI18n();

  const renderOption = (type: ExperienceType, name: string, hint: string, emoji: string) => {
    const selected = value === type;
    return (
      <button
        key={type}
        type="button"
        className={selected ? styles.optionSelected : styles.option}
        aria-pressed={selected}
        onClick={() => onChange(type)}
      >
        <span className={styles.emoji} aria-hidden>
          {emoji || '—'}
        </span>
        <span className={styles.text}>
          <span className={styles.name}>{name}</span>
          <span className={styles.hint}>{hint}</span>
        </span>
        {selected && <Check className={styles.check} size={16} aria-hidden />}
      </button>
    );
  };

  return (
    <div className={styles.grid} role="group" aria-label={t('experienceType.coverLabel')}>
      {SELECTABLE_EXPERIENCE_TYPES.map((type) =>
        renderOption(
          type,
          t(`experienceType.options.${type}.name`),
          t(`experienceType.options.${type}.hint`),
          EXPERIENCE_TYPE_EMOJI[type],
        ),
      )}
      {renderOption(
        DEFAULT_EXPERIENCE_TYPE,
        t('experienceType.none'),
        t('experienceType.noneHint'),
        EXPERIENCE_TYPE_EMOJI[DEFAULT_EXPERIENCE_TYPE],
      )}
    </div>
  );
}
