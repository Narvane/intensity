import type { ReactNode } from 'react';
import styles from './ScreenHeader.module.css';

interface ScreenHeaderProps {
  leading?: ReactNode;
  trailing?: ReactNode;
  children: ReactNode;
  className?: string;
}

/**
 * Two-row screen chrome: toolbar (leading / trailing actions) then full-width content.
 * Page titles live in content; session mode chrome uses SessionModeFooter at the bottom.
 */
export function ScreenHeader({ leading, trailing, children, className }: ScreenHeaderProps) {
  const showToolbar = Boolean(leading) || Boolean(trailing);

  return (
    <header className={[styles.header, className ?? ''].filter(Boolean).join(' ')}>
      {showToolbar && (
        <div className={styles.toolbar}>
          <div className={styles.leading}>{leading}</div>
          <div className={styles.trailing}>{trailing}</div>
        </div>
      )}
      <div className={styles.content}>{children}</div>
    </header>
  );
}
