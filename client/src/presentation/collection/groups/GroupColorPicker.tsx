import type { GroupAccent } from '@domain/box/boxTypes';
import { GROUP_ACCENTS } from '../../components/collection/groupVisuals';
import styles from './GroupColorPicker.module.css';

interface GroupColorPickerProps {
  value: GroupAccent;
  onChange: (color: GroupAccent) => void;
  label: string;
}

export function GroupColorPicker({ value, onChange, label }: GroupColorPickerProps) {
  return (
    <div className={styles.groupColorPicker} role="radiogroup" aria-label={label}>
      {GROUP_ACCENTS.map((accent) => (
        <button
          key={accent}
          type="button"
          role="radio"
          aria-checked={value === accent}
          className={value === accent ? `${styles.swatch} ${styles.swatchActive}` : styles.swatch}
          data-accent={accent}
          onClick={() => onChange(accent)}
        />
      ))}
    </div>
  );
}
