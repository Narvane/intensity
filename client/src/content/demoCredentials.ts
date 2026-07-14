/** Demo sample accounts (API DemoSeedService). */
export const DEMO_PASSWORD = 'demo1234';

export interface DemoPersona {
	id: 'leo' | 'maya' | 'nico';
	displayName: string;
	email: string;
}

export const DEMO_PERSONAS: readonly DemoPersona[] = [
	{ id: 'leo', displayName: 'Leo', email: 'leo@demo.intensity.app' },
	{ id: 'maya', displayName: 'Maya', email: 'maya@demo.intensity.app' },
	{ id: 'nico', displayName: 'Nico', email: 'nico@demo.intensity.app' },
] as const;

export function isDemoMode(): boolean {
	return import.meta.env.VITE_DEMO === 'true';
}

export function demoPersona(id: DemoPersona['id']): DemoPersona {
	const persona = DEMO_PERSONAS.find((entry) => entry.id === id);
	if (!persona) {
		throw new Error(`Unknown demo persona: ${id}`);
	}
	return persona;
}
