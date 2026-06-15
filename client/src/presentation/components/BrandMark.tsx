import styles from './BrandMark.module.css';

interface BrandMarkProps {
  size?: 'md' | 'lg';
}

export function BrandMark({ size = 'md' }: BrandMarkProps) {
  return (
    <div
      className={`${styles.mark} ${size === 'lg' ? styles.large : ''}`}
      aria-hidden="true"
    >
      I
    </div>
  );
}
