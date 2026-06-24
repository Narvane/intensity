import type { ExperienceParameters } from '@domain/experience/experienceTypes';
import { useI18n } from '../../i18n/I18nContext';
import { getParameterVisual, type ParameterKey } from './parameterVisuals';
import { StarRating } from './StarRating';
import styles from './ParameterStarField.module.css';

interface ParameterStarFieldProps {
  parameterKey: ParameterKey;
  value: number;
  onChange?: (value: number) => void;
  showHint?: boolean;
  layout?: 'picker' | 'cover' | 'inline';
}

export function ParameterStarField({
  parameterKey,
  value,
  onChange,
  showHint = false,
  layout = 'picker',
}: ParameterStarFieldProps) {
  const { t } = useI18n();
  const visual = getParameterVisual(parameterKey);
  const ParameterIcon = visual.Icon;
  const label = t(`assistant.fields.${parameterKey}`);
  const hint = showHint ? t(`parameters.${parameterKey}.hints.${value}`) : null;
  const readOnly = !onChange;

  return (
    <div
      className={`${styles.field} ${styles[layout]}`}
      data-param={parameterKey}
    >
      <div className={styles.icon} aria-hidden="true">
        <ParameterIcon />
      </div>
      <p className={styles.label}>{label}</p>
      <StarRating
        parameterKey={parameterKey}
        value={value}
        label={label}
        readOnly={readOnly}
        onChange={onChange}
        size={layout === 'cover' || layout === 'inline' ? 'sm' : 'md'}
      />
      {hint && (
        <p className={styles.hint} aria-live="polite">
          {hint}
        </p>
      )}
    </div>
  );
}

interface ParameterStarsGroupProps {
  parameters: ExperienceParameters;
  layout?: 'inline' | 'cover';
}

export function ParameterStarsGroup({ parameters, layout = 'inline' }: ParameterStarsGroupProps) {
  const { t } = useI18n();

  return (
    <div
      className={layout === 'cover' ? styles.coverGroup : styles.inlineGroup}
      aria-label={t('intensity.parametersLabel')}
    >
      {(['effort', 'openness', 'novelty'] as ParameterKey[]).map((key) => (
        <ParameterStarField
          key={key}
          parameterKey={key}
          value={parameters[key]}
          layout={layout === 'cover' ? 'cover' : 'inline'}
        />
      ))}
    </div>
  );
}
