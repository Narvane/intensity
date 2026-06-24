import { useMemo } from 'react';
import type { BoxType } from '@domain/box/boxTypes';
import {
  listSuggestions,
  suggestionDescriptionSummary,
  type SuggestionIntensity,
} from '../../content/suggestion-packs';
import { useI18n } from '../../i18n/I18nContext';
import styles from './SuggestionPickerList.module.css';

interface SuggestionPickerListProps {
  boxType: BoxType;
  selectedIds: ReadonlySet<string>;
  onChange: (next: Set<string>) => void;
}

const INTENSITY_LEVELS: SuggestionIntensity[] = [1, 2, 3, 4, 5];

export function SuggestionPickerList({
  boxType,
  selectedIds,
  onChange,
}: SuggestionPickerListProps) {
  const { t, locale } = useI18n();
  const suggestions = useMemo(() => listSuggestions(locale, boxType), [boxType, locale]);

  const toggle = (id: string, checked: boolean) => {
    const next = new Set(selectedIds);
    if (checked) {
      next.add(id);
    } else {
      next.delete(id);
    }
    onChange(next);
  };

  return (
    <div className={styles.list}>
      {INTENSITY_LEVELS.map((level) => {
        const group = suggestions.filter((item) => item.intensity === level);
        if (group.length === 0) {
          return null;
        }

        return (
          <section key={level} className={styles.group} aria-labelledby={`suggestion-group-${level}`}>
            <h3 id={`suggestion-group-${level}`} className={styles.groupTitle}>
              {t('suggestions.createBox.groupIntensity', { level })}
            </h3>
            <ul className={styles.items}>
              {group.map((suggestion) => {
                const checked = selectedIds.has(suggestion.id);
                const inputId = `suggestion-${suggestion.id}`;

                return (
                  <li key={suggestion.id}>
                    <label className={styles.item} htmlFor={inputId}>
                      <input
                        id={inputId}
                        type="checkbox"
                        checked={checked}
                        onChange={(event) => toggle(suggestion.id, event.target.checked)}
                      />
                      <span className={styles.itemBody}>
                        <span className={styles.itemDescription}>
                          {suggestionDescriptionSummary(suggestion.description)}
                        </span>
                        <span className={styles.itemMeta}>
                          {t('suggestions.intensityLabel', { level: suggestion.intensity })}
                        </span>
                      </span>
                    </label>
                  </li>
                );
              })}
            </ul>
          </section>
        );
      })}
    </div>
  );
}
