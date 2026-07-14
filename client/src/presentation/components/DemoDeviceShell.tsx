import type { PropsWithChildren } from 'react';
import { useEffect } from 'react';
import { isDemoMode } from '../../content/demoCredentials';
import { DemoBanner } from './DemoBanner';
import styles from './DemoDeviceShell.module.css';

/**
 * Desktop portfolio frame: constrains the Capacitor-oriented UI to a phone-sized
 * column. On narrow viewports it goes full-bleed. Only active when VITE_DEMO=true.
 */
export function DemoDeviceShell({ children }: PropsWithChildren) {
	const demo = isDemoMode();

	useEffect(() => {
		if (!demo) {
			return;
		}
		document.documentElement.dataset.demoShell = 'true';
		return () => {
			delete document.documentElement.dataset.demoShell;
		};
	}, [demo]);

	if (!demo) {
		return children;
	}

	return (
		<div className={styles.viewport}>
			<DemoBanner />
			<div className={styles.stage}>
				<div className={styles.phone} aria-label="Intensity demo">
					<div className={styles.notch} aria-hidden="true" />
					<div className={styles.screen}>{children}</div>
				</div>
			</div>
		</div>
	);
}
