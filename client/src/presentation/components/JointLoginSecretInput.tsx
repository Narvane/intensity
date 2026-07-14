import type { InputHTMLAttributes } from 'react';
import styles from './JointLoginSecretInput.module.css';

type JointLoginSecretInputProps = Omit<
  InputHTMLAttributes<HTMLInputElement>,
  'type' | 'autoComplete' | 'autoCapitalize' | 'autoCorrect' | 'spellCheck'
>;

/**
 * Password-looking field for joint/shared login that avoids credential managers
 * offering "Save password" (Google / Samsung / etc.). Individual login should keep
 * a real type="password" + autocomplete.
 */
export function JointLoginSecretInput({ className, ...props }: JointLoginSecretInputProps) {
  return (
    <input
      {...props}
      type="text"
      inputMode="text"
      autoComplete="off"
      autoCapitalize="off"
      autoCorrect="off"
      spellCheck={false}
      data-1p-ignore="true"
      data-lpignore="true"
      data-form-type="other"
      className={[styles.masked, className ?? ''].filter(Boolean).join(' ')}
    />
  );
}

type JointLoginEmailInputProps = Omit<
  InputHTMLAttributes<HTMLInputElement>,
  'type' | 'autoComplete' | 'autoCapitalize' | 'autoCorrect' | 'spellCheck'
>;

/** Email field for joint login — same autofill suppression as the secret field. */
export function JointLoginEmailInput({ className, ...props }: JointLoginEmailInputProps) {
  return (
    <input
      {...props}
      type="text"
      inputMode="email"
      autoComplete="off"
      autoCapitalize="none"
      autoCorrect="off"
      spellCheck={false}
      data-1p-ignore="true"
      data-lpignore="true"
      data-form-type="other"
      className={className}
    />
  );
}
