import { useEffect, useRef } from 'react';
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

  useEffect(() => {
    const footer = footerRef.current;
    if (!footer) {
      return;
    }

    const syncHeight = () => {
      document.documentElement.style.setProperty(
        '--session-mode-footer-height',
        `${footer.offsetHeight}px`,
      );
    };

    syncHeight();
    const observer = new ResizeObserver(syncHeight);
    observer.observe(footer);
    return () => observer.disconnect();
  }, []);

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
