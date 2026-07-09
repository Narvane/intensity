import type { ReactNode } from 'react';
import { ToolbarBrand } from './ToolbarBrand';
import styles from './ScreenHeader.module.css';

interface ScreenHeaderProps {
  leading?: ReactNode;
  trailing?: ReactNode;
  children: ReactNode;
  className?: string;
  showLogo?: boolean;
}

/**
 * Two-row screen chrome: toolbar (leading / trailing actions) then full-width content.
 * Page titles live in content; session mode chrome uses SessionModeFooter at the bottom.
 */
export function ScreenHeader({
  leading,
  trailing,
  children,
  className,
  showLogo = true,
}: ScreenHeaderProps) {
  const hasLeading = Boolean(leading);
  const hasTrailing = Boolean(trailing);
  const showToolbar = hasLeading || hasTrailing;
  const logoPlacement =
    showLogo && showToolbar
      ? hasLeading && hasTrailing
        ? 'center'
        : hasLeading
          ? 'trailing'
          : 'leading'
      : null;

  return (
    <header className={[styles.header, className ?? ''].filter(Boolean).join(' ')}>
      {showToolbar && (
        <div
          className={[
            styles.toolbar,
            logoPlacement === 'center' ? styles.toolbarCentered : null,
          ]
            .filter(Boolean)
            .join(' ')}
        >
          <div className={styles.leading}>
            {logoPlacement === 'leading' ? <ToolbarBrand /> : null}
            {leading}
          </div>
          {logoPlacement === 'center' ? (
            <div className={styles.center}>
              <ToolbarBrand />
            </div>
          ) : null}
          <div className={styles.trailing}>
            {trailing}
            {logoPlacement === 'trailing' ? <ToolbarBrand /> : null}
          </div>
        </div>
      )}
      <div className={styles.content}>{children}</div>
    </header>
  );
}
