import { useEffect, useMemo, useState } from 'react';
import type { BoxType } from '@domain/box/boxTypes';
import {
  pickRandomSuggestion,
  type ExperienceSuggestion,
  type SuggestionIntensity,
} from '../../content/suggestion-packs';
import { useI18n } from '../../i18n/I18nContext';
import { getBoxVisual } from '../components/boxVisuals';
import { Button } from '../components/Button';
import { RatingScale } from '../components/RatingScale';
import styles from './SuggestionExplorer.module.css';

interface SuggestionExplorerProps {
  boxType: BoxType;
  onAccept: (suggestion: ExperienceSuggestion) => void;
}

export function SuggestionExplorer({ boxType, onAccept }: SuggestionExplorerProps) {
  const { t, locale } = useI18n();
  const [filterIntensity, setFilterIntensity] = useState<SuggestionIntensity>(1);
  const [current, setCurrent] = useState<ExperienceSuggestion | null>(null);
  const visual = useMemo(() => getBoxVisual(boxType), [boxType]);
  const TypeIcon = visual.Icon;

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
    <div className={styles.explorer}>
      <div className={styles.typeBadge} data-family={visual.family}>
        <TypeIcon size={18} aria-hidden />
        <span>{t(`boxTypes.${boxType}.title`)}</span>
      </div>

      <RatingScale
        label={t('suggestions.explorer.filterLabel')}
        value={filterIntensity}
        tone="intensity"
        onChange={(value) => setFilterIntensity(value as SuggestionIntensity)}
      />

      <article className={styles.card} aria-live="polite">
        <p className={styles.description}>{current.description}</p>
        <p className={styles.meta}>
          {t('suggestions.explorer.intensity', { level: current.intensity })}
        </p>
        <p className={styles.meta}>
          {t('experiences.paramsSummary', {
            effort: current.parameters.effort,
            openness: current.parameters.openness,
            novelty: current.parameters.novelty,
          })}
        </p>
      </article>

      <div className={styles.actions}>
        <Button variant="secondary" onClick={showAnother}>
          {t('suggestions.explorer.another')}
        </Button>
        <Button onClick={() => onAccept(current)}>{t('suggestions.explorer.use')}</Button>
      </div>
    </div>
  );
}
