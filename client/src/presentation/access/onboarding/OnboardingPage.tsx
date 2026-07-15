import { useCallback, useEffect, useMemo, useState, type TouchEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { createDefaultPreferences } from '@adapters/preferences/CapacitorPreferencesAdapter';
import { CompleteOnboardingUseCase } from '@domain/bootstrap/CompleteOnboardingUseCase';
import { LoadBootstrapUseCase } from '@domain/bootstrap/LoadBootstrapUseCase';
import { useI18n } from '../../../i18n/I18nContext';
import { Button } from '../../components/controls/Button';
import { AppLoader } from '../../components/feedback/AppLoader';
import { OnboardingIllustration } from '../../components/brand/OnboardingIllustration';
import { QuickGuideOverlay } from '../quick-guide/QuickGuideOverlay';
import styles from './OnboardingPage.module.css';

const TOTAL_STEPS = 4;

export function OnboardingPage() {
  const { t } = useI18n();
  const navigate = useNavigate();
  const preferences = useMemo(() => createDefaultPreferences(), []);
  const completeOnboarding = useMemo(
    () => new CompleteOnboardingUseCase(preferences),
    [preferences],
  );
  const loadBootstrap = useMemo(
    () => new LoadBootstrapUseCase(preferences),
    [preferences],
  );

  const [step, setStep] = useState(1);
  const [quickGuideOpen, setQuickGuideOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [guardReady, setGuardReady] = useState(false);

  useEffect(() => {
    loadBootstrap
      .execute()
      .then((destination) => {
        if (destination === 'auth') {
          navigate('/auth', { replace: true });
        } else {
          setGuardReady(true);
        }
      })
      .catch(() => setGuardReady(true));
  }, [loadBootstrap, navigate]);

  const finish = useCallback(async () => {
    setSubmitting(true);
    try {
      await completeOnboarding.execute();
      navigate('/auth', { replace: true });
    } finally {
      setSubmitting(false);
    }
  }, [completeOnboarding, navigate]);

  const onTouchStart = (event: TouchEvent<HTMLElement>) => {
    const touch = event.changedTouches[0];
    (event.currentTarget as HTMLElement).dataset.touchX = String(touch.clientX);
  };

  const onTouchEnd = (event: TouchEvent<HTMLElement>) => {
    const startX = Number(
      (event.currentTarget as HTMLElement).dataset.touchX ?? '0',
    );
    const endX = event.changedTouches[0]?.clientX ?? startX;
    const delta = endX - startX;

    if (delta > 60 && step > 1) {
      setStep((current) => current - 1);
    } else if (delta < -60 && step < TOTAL_STEPS) {
      setStep((current) => current + 1);
    }
  };

  if (!guardReady) {
    return (
      <main className={styles.page} aria-busy="true">
        <AppLoader label={t('bootstrap.loading')} size="lg" fullscreen />
      </main>
    );
  }

  const isLastStep = step === TOTAL_STEPS;

  return (
    <>
      <main
        className={styles.page}
        onTouchStart={onTouchStart}
        onTouchEnd={onTouchEnd}
      >
        <p className={styles.indicator}>
          {t('onboarding.stepIndicator', { current: step, total: TOTAL_STEPS })}
        </p>

        <div className={styles.progress} aria-hidden="true">
          {Array.from({ length: TOTAL_STEPS }, (_, index) => (
            <span
              key={index}
              className={index + 1 <= step ? styles.progressActive : ''}
            />
          ))}
        </div>

        <OnboardingIllustration step={step} />

        <div className={styles.copy}>
          <h1>{t(`onboarding.steps.${step}.title`)}</h1>
          <p>{t(`onboarding.steps.${step}.body`)}</p>
        </div>

        <div className={styles.actions}>
          {step > 1 && (
            <Button variant="secondary" onClick={() => setStep((current) => current - 1)}>
              {t('onboarding.back')}
            </Button>
          )}

          {!isLastStep && (
            <Button fullWidth onClick={() => setStep((current) => current + 1)}>
              {t('onboarding.next')}
            </Button>
          )}

          {isLastStep && (
            <>
              <Button
                variant="secondary"
                fullWidth
                onClick={() => setQuickGuideOpen(true)}
              >
                {t('onboarding.openQuickGuide')}
              </Button>
              <Button fullWidth disabled={submitting} onClick={() => void finish()}>
                {t('onboarding.getStarted')}
              </Button>
            </>
          )}
        </div>
      </main>

      <QuickGuideOverlay open={quickGuideOpen} onClose={() => setQuickGuideOpen(false)} />
    </>
  );
}
