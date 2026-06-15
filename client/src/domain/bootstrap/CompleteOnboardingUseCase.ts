import type { PreferencesPort } from '../preferences/PreferencesPort';

export class CompleteOnboardingUseCase {
  constructor(private readonly preferences: PreferencesPort) {}

  async execute(): Promise<void> {
    await this.preferences.completeOnboarding();
  }
}
