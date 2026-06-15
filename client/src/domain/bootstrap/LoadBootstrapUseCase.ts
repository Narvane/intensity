import type { PreferencesPort } from '../preferences/PreferencesPort';
import type { BootstrapDestination } from './BootstrapState';

export class LoadBootstrapUseCase {
  constructor(private readonly preferences: PreferencesPort) {}

  async execute(): Promise<BootstrapDestination> {
    const prefs = await this.preferences.load();
    return prefs.onboardingCompleted ? 'auth' : 'onboarding';
  }
}
