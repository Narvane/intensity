import Lottie from 'lottie-react';
import loadingAnimation from '../../assets/loading-spinner.json';
import styles from './AppLoader.module.css';

type AppLoaderSize = 'sm' | 'md' | 'lg';

interface AppLoaderProps {
  label?: string;
  size?: AppLoaderSize;
  fullscreen?: boolean;
  className?: string;
}

export function AppLoader({
  label,
  size = 'md',
  fullscreen = false,
  className,
}: AppLoaderProps) {
  const rootClassName = [
    styles.loader,
    styles[size],
    fullscreen ? styles.fullscreen : null,
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div className={rootClassName} role="status" aria-busy="true" aria-live="polite">
      <Lottie
        animationData={loadingAnimation}
        loop
        className={styles.animation}
        aria-hidden
      />
      {label ? <p className={styles.label}>{label}</p> : null}
    </div>
  );
}
