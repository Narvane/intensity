import { useEffect, useState } from 'react';
import { Check, Lightbulb, Shuffle } from 'lucide-react';
import type { BoxType } from '@domain/box/boxTypes';
import {
  pickRandomSuggestion,
  type ExperienceSuggestion,
  type SuggestionIntensity,
} from '../../../content/suggestion-packs';
import { useI18n } from '../../../i18n/I18nContext';
import styles from './SuggestionExplorer.module.css';

interface SuggestionExplorerProps {
  boxType: BoxType;
  onAccept: (suggestion: ExperienceSuggestion) => void;
}

const INTENSITY_LEVELS: SuggestionIntensity[] = [1, 2, 3, 4, 5];

export function SuggestionExplorer({ boxType, onAccept }: SuggestionExplorerProps) {
  const { t, locale } = useI18n();
  const [filterIntensity, setFilterIntensity] = useState<SuggestionIntensity>(1);
  const [current, setCurrent] = useState<ExperienceSuggestion | null>(null);

  useEffect(() => {
    setCurrent(pickRandomSuggestion(locale, boxType, filterIntensity));
  }, [boxType, filterIntensity, locale]);

  const showAnother = () => {
    setCurrent(pickRandomSuggestion(locale, boxType, filterIntensity, current?.id));
  };

  if (!current) {
    return null;
  }

  return (
    <aside className={styles.explorer} aria-label={t('assistant.steps.suggestion.title')}>
      <div className={styles.titleRow}>
        <Lightbulb size={16} aria-hidden className={styles.titleIcon} />
        <span className={styles.titleLabel}>{t('suggestions.explorer.auxLabel')}</span>
      </div>

      <div className={styles.intensityBlock}>
        <div className={styles.chips} role="group" aria-label={t('suggestions.explorer.filterLabel')}>
          {INTENSITY_LEVELS.map((level) => (
            <button
              key={level}
              type="button"
              className={level === filterIntensity ? styles.chipActive : styles.chip}
              aria-pressed={level === filterIntensity}
              onClick={() => setFilterIntensity(level)}
            >
              {level}
            </button>
          ))}
        </div>
        <span className={styles.intensityCaption}>{t('suggestions.explorer.intensityCaption')}</span>
      </div>

      <blockquote className={styles.quote} aria-live="polite">
        {current.description}
      </blockquote>

      <div className={styles.actions}>
        <button type="button" className={styles.skipButton} onClick={showAnother}>
          <Shuffle size={15} aria-hidden />
          {t('suggestions.explorer.another')}
        </button>
        <button type="button" className={styles.pickButton} onClick={() => onAccept(current)}>
          <Check size={15} aria-hidden />
          {t('suggestions.explorer.use')}
        </button>
      </div>
    </aside>
  );
}
