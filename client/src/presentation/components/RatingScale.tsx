import styles from './RatingScale.module.css';

interface RatingScaleProps {
  label: string;
  value: number;
  onChange: (value: number) => void;
}

export function RatingScale({ label, value, onChange }: RatingScaleProps) {
  return (
    <div className={styles.field}>
      <span className={styles.label}>{label}</span>
      <div className={styles.scale} role="group" aria-label={label}>
        {[1, 2, 3, 4, 5].map((level) => (
          <button
            key={level}
            type="button"
            className={level === value ? styles.active : styles.inactive}
            aria-pressed={level === value}
            onClick={() => onChange(level)}
          >
            {level}
          </button>
        ))}
      </div>
    </div>
  );
}
