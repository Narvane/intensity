import { Star } from 'lucide-react';
import type { ParameterKey } from './parameterVisuals';
import styles from './StarRating.module.css';

interface StarRatingProps {
  parameterKey: ParameterKey;
  value: number;
  label: string;
  readOnly?: boolean;
  onChange?: (value: number) => void;
  size?: 'md' | 'sm' | 'xs';
}

export function StarRating({
  parameterKey,
  value,
  label,
  readOnly = false,
  onChange,
  size = 'md',
}: StarRatingProps) {
  const stars = [1, 2, 3, 4, 5] as const;
  const groupLabel = `${label}, ${value} de 5`;

  return (
    <div
      className={`${styles.rating} ${
        size === 'xs' ? styles.xs : size === 'sm' ? styles.small : ''
      }`}
      data-param={parameterKey}
      role="group"
      aria-label={groupLabel}
    >
      {stars.map((level) => {
        const filled = level <= value;

        if (readOnly) {
          return (
            <span
              key={level}
              className={`${styles.star} ${filled ? styles.filled : styles.empty}`}
              aria-hidden="true"
            >
              <Star />
            </span>
          );
        }

        return (
          <button
            key={level}
            type="button"
            className={`${styles.starButton} ${filled ? styles.filled : styles.empty}`}
            aria-label={`${label}, ${level} de 5 estrelas`}
            aria-pressed={level === value}
            onClick={() => onChange?.(level)}
          >
            <Star />
          </button>
        );
      })}
    </div>
  );
}
