import type { BoxType } from '@domain/box/boxTypes';
import { LayoutGrid, Pencil, Play, Trash2 } from 'lucide-react';
import { getBoxVisual } from './boxVisuals';
import styles from './BoxCard.module.css';

interface BoxCardProps {
  name: string;
  type: BoxType;
  typeLabel: string;
  typeHint: string;
  experienceCount?: number;
  myExperienceCount?: number;
  playLabel?: string;
  experiencesLabel?: string;
  editLabel?: string;
  deleteLabel?: string;
  yourCardsLabel?: string;
  totalCardsLabel?: string;
  onPlay?: () => void;
  onOpenExperiences?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
}

export function BoxCard({
  name,
  type,
  typeLabel,
  typeHint,
  experienceCount,
  myExperienceCount,
  playLabel = 'Play',
  experiencesLabel = 'Your experiences',
  editLabel = 'Edit',
  deleteLabel = 'Delete',
  yourCardsLabel,
  totalCardsLabel,
  onPlay,
  onOpenExperiences,
  onEdit,
  onDelete,
}: BoxCardProps) {
  const { family, Icon } = getBoxVisual(type);
  const hasActionBar = Boolean(onPlay || onOpenExperiences || onEdit || onDelete);
  const showCounts =
    hasActionBar && myExperienceCount !== undefined && experienceCount !== undefined;

  return (
    <article className={styles.card} data-type={type} data-family={family}>
      <div className={styles.body}>
        <span className={styles.iconWrap} aria-hidden="true">
          <Icon />
        </span>
        <strong className={styles.name}>{name}</strong>
        <span className={styles.type}>{typeLabel}</span>
        <span className={styles.hint}>{typeHint}</span>

        {hasActionBar && (
          <div className={styles.actions}>
            {onPlay && (
              <button
                type="button"
                className={styles.actionButton}
                aria-label={playLabel}
                onClick={onPlay}
              >
                <Play aria-hidden="true" />
              </button>
            )}
            {onOpenExperiences && (
              <button
                type="button"
                className={styles.actionButton}
                aria-label={experiencesLabel}
                onClick={onOpenExperiences}
              >
                <LayoutGrid aria-hidden="true" />
              </button>
            )}
            {onEdit && (
              <button
                type="button"
                className={styles.actionButton}
                aria-label={editLabel}
                onClick={onEdit}
              >
                <Pencil aria-hidden="true" />
              </button>
            )}
            {onDelete && (
              <button
                type="button"
                className={`${styles.actionButton} ${styles.actionButtonDanger}`}
                aria-label={deleteLabel}
                onClick={onDelete}
              >
                <Trash2 aria-hidden="true" />
              </button>
            )}
          </div>
        )}

        {showCounts && (
          <span className={styles.counts}>
            <span>{yourCardsLabel}</span>
            <span aria-hidden="true">·</span>
            <span>{totalCardsLabel}</span>
          </span>
        )}

        {!hasActionBar && experienceCount !== undefined && (
          <span className={styles.count}>{experienceCount} ideias</span>
        )}
      </div>
    </article>
  );
}
