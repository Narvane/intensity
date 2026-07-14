import { useI18n } from '../../i18n/I18nContext';
import styles from './DemoBanner.module.css';

export function DemoBanner() {
	const { t } = useI18n();

	return (
		<div className={styles.banner} role="status">
			{t('demo.banner')}
		</div>
	);
}
