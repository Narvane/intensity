import {
	DEMO_PASSWORD,
	demoPersona,
	type DemoPersona,
} from '@content/demoCredentials';
import { useI18n } from '../../../i18n/I18nContext';
import styles from './DemoAuthShortcuts.module.css';

interface DemoAuthShortcutsProps {
	mode: 'experiences' | 'experienceBox';
	onPickExperiences: (persona: DemoPersona) => void;
	onPickJoint: (personas: DemoPersona[]) => void;
}

export function DemoAuthShortcuts({
	mode,
	onPickExperiences,
	onPickJoint,
}: DemoAuthShortcutsProps) {
	const { t } = useI18n();
	const leo = demoPersona('leo');
	const maya = demoPersona('maya');
	const nico = demoPersona('nico');

	return (
		<div className={styles.wrap}>
			<p className={styles.label}>{t('demo.shortcutsLabel')}</p>
			<div className={styles.row} role="group" aria-label={t('demo.shortcutsLabel')}>
				{mode === 'experiences' ? (
					<>
						<button type="button" className={styles.chip} onClick={() => onPickExperiences(leo)}>
							{leo.displayName}
						</button>
						<button type="button" className={styles.chip} onClick={() => onPickExperiences(maya)}>
							{maya.displayName}
						</button>
						<button type="button" className={styles.chip} onClick={() => onPickExperiences(nico)}>
							{nico.displayName}
						</button>
					</>
				) : (
					<>
						<button
							type="button"
							className={styles.chip}
							onClick={() => onPickJoint([leo, maya])}
						>
							{t('demo.coupleShortcut')}
						</button>
						<button
							type="button"
							className={styles.chip}
							onClick={() => onPickJoint([leo, maya, nico])}
						>
							{t('demo.trioShortcut')}
						</button>
					</>
				)}
			</div>
			<p className={styles.hint}>{t('demo.passwordHint', { password: DEMO_PASSWORD })}</p>
		</div>
	);
}
