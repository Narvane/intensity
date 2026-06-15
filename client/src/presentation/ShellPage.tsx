import styles from './ShellPage.module.css';

export function ShellPage() {
  return (
    <main className={styles.shell} aria-label="Intensity app shell">
      <div className={styles.brandMark} aria-hidden="true">
        I
      </div>
      <h1 className={styles.title}>Intensity</h1>
      <p className={styles.subtitle}>
        Collect experiences alone. Draw and reveal together.
      </p>
      <p className={styles.meta}>API: {import.meta.env.VITE_API_URL}</p>
    </main>
  );
}
