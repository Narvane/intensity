import { useEffect, useRef, useState } from 'react';
import type { BoxType } from '@domain/box/boxTypes';
import { ChevronRight, LayoutGrid, MoreHorizontal, Play } from 'lucide-react';
import { getBoxVisual } from './boxVisuals';
import styles from './BoxCard.module.css';

interface BoxCardProps {
  name: string;
  type: BoxType;
  typeLabel: string;
  typeHint: string;
  experienceCount?: number;
  myExperienceCount?: number;
  openLabel?: string;
  deleteLabel?: string;
  menuLabel?: string;
  playLabel?: string;
  experiencesLabel?: string;
  yourCardsLabel?: string;
  totalCardsLabel?: string;
  onOpen?: () => void;
  onDelete?: () => void;
  onPlay?: () => void;
  onOpenExperiences?: () => void;
}

export function BoxCard({
  name,
  type,
  typeLabel,
  typeHint,
  experienceCount,
  myExperienceCount,
  openLabel = 'Open',
  deleteLabel = 'Delete',
  menuLabel = 'Box actions',
  playLabel = 'Play',
  experiencesLabel = 'Your experiences',
  yourCardsLabel,
  totalCardsLabel,
  onOpen,
  onDelete,
  onPlay,
  onOpenExperiences,
}: BoxCardProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const { family, Icon } = getBoxVisual(type);
  const hasActions = Boolean(onPlay && onOpenExperiences);

  useEffect(() => {
    if (!menuOpen) {
      return;
    }

    const onPointerDown = (event: MouseEvent) => {
      if (!menuRef.current?.contains(event.target as Node)) {
        setMenuOpen(false);
      }
    };

    window.addEventListener('mousedown', onPointerDown);
    return () => window.removeEventListener('mousedown', onPointerDown);
  }, [menuOpen]);

  const handleOpen = () => {
    setMenuOpen(false);
    onOpen?.();
  };

  const handleDelete = () => {
    setMenuOpen(false);
    onDelete?.();
  };

  const body = (
    <>
      <span className={styles.iconWrap} aria-hidden="true">
        <Icon />
      </span>
      <strong className={styles.name}>{name}</strong>
      <span className={styles.type}>{typeLabel}</span>
      <span className={styles.hint}>{typeHint}</span>

      {hasActions && (
        <div className={styles.actions}>
          <button
            type="button"
            className={styles.actionButton}
            aria-label={playLabel}
            onClick={onPlay}
          >
            <Play aria-hidden="true" />
          </button>
          <button
            type="button"
            className={styles.actionButton}
            aria-label={experiencesLabel}
            onClick={onOpenExperiences}
          >
            <LayoutGrid aria-hidden="true" />
          </button>
        </div>
      )}

      {hasActions && myExperienceCount !== undefined && experienceCount !== undefined && (
        <span className={styles.counts}>
          <span>{yourCardsLabel}</span>
          <span aria-hidden="true">·</span>
          <span>{totalCardsLabel}</span>
        </span>
      )}

      {!hasActions && experienceCount !== undefined && (
        <span className={styles.count}>
          {experienceCount} ideias <ChevronRight aria-hidden="true" />
        </span>
      )}
    </>
  );

  return (
    <article className={styles.card} data-type={type} data-family={family}>
      {hasActions ? (
        <div className={styles.body}>{body}</div>
      ) : (
        <button type="button" className={styles.open} onClick={handleOpen}>
          {body}
        </button>
      )}

      {onDelete && (
        <div className={styles.menuWrap} ref={menuRef}>
          <button
            type="button"
            className={styles.menuButton}
            aria-label={menuLabel}
            aria-haspopup="menu"
            aria-expanded={menuOpen}
            onClick={() => setMenuOpen((current) => !current)}
          >
            <MoreHorizontal aria-hidden="true" />
          </button>

          {menuOpen && (
            <div className={styles.menu} role="menu">
              <button type="button" role="menuitem" className={styles.menuItem} onClick={handleOpen}>
                {openLabel}
              </button>
              <button
                type="button"
                role="menuitem"
                className={styles.menuItemDanger}
                onClick={handleDelete}
              >
                {deleteLabel}
              </button>
            </div>
          )}
        </div>
      )}
    </article>
  );
}
