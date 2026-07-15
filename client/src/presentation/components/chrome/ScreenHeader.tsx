import { useLayoutEffect, useRef, type ReactNode } from 'react';
import { ToolbarBrand } from '../brand/ToolbarBrand';
import styles from './ScreenHeader.module.css';

interface ScreenHeaderProps {
  leading?: ReactNode;
  trailing?: ReactNode;
  children?: ReactNode;
  className?: string;
  showLogo?: boolean;
}

const HEADER_HEIGHT_VAR = '--app-sticky-header-height';

/**
 * Two-row screen chrome: sticky toolbar (leading / trailing actions), then
 * scrolling page title/content. Session mode chrome uses SessionModeFooter.
 *
 * Toolbar is a sibling of the title (not nested in a short wrapper) so sticky
 * can persist for the full page scroll.
 */
export function ScreenHeader({
  leading,
  trailing,
  children,
  className,
  showLogo = true,
}: ScreenHeaderProps) {
  const stickyBarRef = useRef<HTMLDivElement>(null);
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

  useLayoutEffect(() => {
    const stickyBar = stickyBarRef.current;
    if (!stickyBar) {
      document.documentElement.style.removeProperty(HEADER_HEIGHT_VAR);
      return;
    }

    const root = document.documentElement;
    const syncHeight = () => {
      root.style.setProperty(HEADER_HEIGHT_VAR, `${stickyBar.offsetHeight}px`);
    };

    syncHeight();
    const observer = new ResizeObserver(syncHeight);
    observer.observe(stickyBar);

    return () => {
      observer.disconnect();
      root.style.removeProperty(HEADER_HEIGHT_VAR);
    };
  }, [leading, trailing, showLogo, showToolbar]);

  return (
    <>
      {showToolbar ? (
        <div
          ref={stickyBarRef}
          className={[styles.stickyBar, className ?? ''].filter(Boolean).join(' ')}
        >
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
        </div>
      ) : null}
      {children ? <div className={styles.content}>{children}</div> : null}
    </>
  );
}
