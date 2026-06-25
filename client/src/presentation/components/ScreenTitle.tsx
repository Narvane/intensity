import type { ReactNode } from 'react';
import styles from './ScreenTitle.module.css';

interface ScreenTitleProps {
  children: ReactNode;
  className?: string;
}

export function ScreenTitle({ children, className }: ScreenTitleProps) {
  return (
    <h1 className={[styles.title, className ?? ''].filter(Boolean).join(' ')}>{children}</h1>
  );
}
