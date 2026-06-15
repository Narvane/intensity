import styles from './OnboardingIllustration.module.css';

const STEP_THEMES = [
  styles.step1,
  styles.step2,
  styles.step3,
  styles.step4,
] as const;

interface OnboardingIllustrationProps {
  step: number;
}

export function OnboardingIllustration({ step }: OnboardingIllustrationProps) {
  const theme = STEP_THEMES[step - 1] ?? STEP_THEMES[0];

  return (
    <div className={`${styles.frame} ${theme}`} aria-hidden="true">
      <div className={styles.orbit} />
      <div className={styles.core} />
    </div>
  );
}
