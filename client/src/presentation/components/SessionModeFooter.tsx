import { useLayoutEffect, useRef } from 'react';
import type { SessionMember } from '@domain/session/SessionPort';
import type { AccessMode } from '@domain/session/SessionPort';
import { useI18n } from '../../i18n/I18nContext';
import { SessionModeChrome } from './SessionModeChrome';
import styles from './SessionModeFooter.module.css';

interface SessionModeFooterProps {
  mode: AccessMode;
  participantDisplayName?: string;
  members?: SessionMember[];
}

export function SessionModeFooter({
  mode,
  participantDisplayName,
  members,
}: SessionModeFooterProps) {
  const { t } = useI18n();
  const footerRef = useRef<HTMLElement>(null);
  const isExperiences = mode === 'EXPERIENCES';
  const ariaLabel = t(
    isExperiences ? 'session.experiencesMode' : 'session.experienceBoxMode',
  );

  useLayoutEffect(() => {
    const footer = footerRef.current;
    if (!footer) {
      return;
    }

    const root = document.documentElement;
    root.dataset.sessionFooter = 'true';

    const syncHeight = () => {
      root.style.setProperty('--session-mode-footer-height', `${footer.offsetHeight}px`);
    };

    syncHeight();
    const observer = new ResizeObserver(syncHeight);
    observer.observe(footer);

    return () => {
      observer.disconnect();
      delete root.dataset.sessionFooter;
      root.style.removeProperty('--session-mode-footer-height');
    };
  }, [mode, participantDisplayName, members]);

  return (
    <footer
      ref={footerRef}
      className={styles.footer}
      role="region"
      aria-label={ariaLabel}
    >
      <SessionModeChrome
        placement="footer"
        mode={mode}
        participantDisplayName={participantDisplayName}
        members={members}
      />
    </footer>
  );
}
