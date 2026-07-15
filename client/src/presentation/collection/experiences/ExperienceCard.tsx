import { FlipHorizontal2, Pencil, Trash2 } from 'lucide-react';
import type { Experience } from '@domain/experience/experienceTypes';
import {
  canManageExperience,
  hasRevealableAuthorContent,
} from '@domain/experience/experienceVisibility';
import { useI18n } from '../../../i18n/I18nContext';
import { ExperienceContentBlock } from '../../components/experience/ExperienceContentBlock';
import { ExperienceTypePill } from '../../components/experience/ExperienceTypePill';
import { IntegritySeal } from '../../components/experience/IntegritySeal';
import { IntensityHero } from '../../components/experience/IntensityHero';
import { ParameterStarsGroup } from '../../components/rating/ParameterStarField';
import styles from './ExperienceCard.module.css';

interface ExperienceCardProps {
  experience: Experience;
  participantId?: string;
  flipped?: boolean;
  onFlipToggle?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
}

function ExperienceCardCover({ experience }: { experience: Experience }) {
  const hasType = experience.type !== 'none';

  return (
    <div className={`${styles.cover} ${hasType ? styles.coverWithType : ''}`}>
      {hasType && (
        <div className={styles.typeSlot}>
          <ExperienceTypePill type={experience.type} />
        </div>
      )}
      <IntensityHero level={experience.intensity} />
      <ParameterStarsGroup parameters={experience.parameters} layout="drawCover" />
      <div className={styles.sealSlot}>
        <IntegritySeal seal={experience.seal} variant="minimal" />
      </div>
    </div>
  );
}

function FlipToggleButton({
  flipped,
  onFlipToggle,
}: {
  flipped: boolean;
  onFlipToggle?: () => void;
}) {
  const { t } = useI18n();

  return (
    <button
      type="button"
      className={styles.flipButton}
      aria-pressed={flipped}
      aria-label={flipped ? t('experiences.unflipCard') : t('experiences.flipCard')}
      onClick={onFlipToggle}
    >
      <FlipHorizontal2 size={20} strokeWidth={2.25} aria-hidden />
    </button>
  );
}

export function ExperienceCard({
  experience,
  participantId,
  flipped = false,
  onFlipToggle,
  onEdit,
  onDelete,
}: ExperienceCardProps) {
  const { t } = useI18n();
  const isAuthor = canManageExperience(experience, participantId);
  const canFlip = isAuthor && hasRevealableAuthorContent(experience);

  if (canFlip) {
    return (
      <article className={styles.card}>
        <div className={styles.flipShell}>
          <div className={styles.flipInner} data-flipped={flipped ? 'true' : 'false'}>
            <div className={styles.front}>
              <div className={styles.metaBar}>
                <FlipToggleButton flipped={flipped} onFlipToggle={onFlipToggle} />
              </div>
              <ExperienceCardCover experience={experience} />
              <div className={styles.actions}>
                <button type="button" className={styles.actionButton} onClick={onEdit}>
                  <Pencil size={18} strokeWidth={2.25} aria-hidden />
                  <span>{t('experiences.edit')}</span>
                </button>
                <button type="button" className={styles.actionButton} onClick={onDelete}>
                  <Trash2 size={18} strokeWidth={2.25} aria-hidden />
                  <span>{t('experiences.delete')}</span>
                </button>
              </div>
            </div>

            <div className={styles.back}>
              <div className={styles.metaBar}>
                <FlipToggleButton flipped={flipped} onFlipToggle={onFlipToggle} />
              </div>
              <ExperienceContentBlock experience={experience} />
            </div>
          </div>
        </div>
      </article>
    );
  }

  return (
    <article className={styles.card}>
      <div className={styles.staticBody}>
        <ExperienceCardCover experience={experience} />

        {isAuthor && (
          <div className={styles.actions}>
            <button type="button" className={styles.actionButton} onClick={onEdit}>
              <Pencil size={18} strokeWidth={2.25} aria-hidden />
              <span>{t('experiences.edit')}</span>
            </button>
            <button type="button" className={styles.actionButton} onClick={onDelete}>
              <Trash2 size={18} strokeWidth={2.25} aria-hidden />
              <span>{t('experiences.delete')}</span>
            </button>
          </div>
        )}
      </div>
    </article>
  );
}
