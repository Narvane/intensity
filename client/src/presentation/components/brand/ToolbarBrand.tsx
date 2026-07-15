import { getBrandWordmarkUrl } from '../../../content/brandAssets';
import styles from './ToolbarBrand.module.css';

interface ToolbarBrandProps {
  className?: string;
}

export function ToolbarBrand({ className }: ToolbarBrandProps) {
  const logoUrl = getBrandWordmarkUrl();

  if (!logoUrl) {
    return null;
  }

  return (
    <img
      src={logoUrl}
      alt=""
      aria-hidden
      className={[styles.logo, className ?? ''].filter(Boolean).join(' ')}
      decoding="async"
    />
  );
}
