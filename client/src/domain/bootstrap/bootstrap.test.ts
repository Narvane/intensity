import { describe, expect, it } from 'vitest';
import { InMemoryPreferencesAdapter } from '@adapters/preferences/CapacitorPreferencesAdapter';
import { CompleteOnboardingUseCase } from '@domain/bootstrap/CompleteOnboardingUseCase';
import { LoadBootstrapUseCase } from '@domain/bootstrap/LoadBootstrapUseCase';

describe('LoadBootstrapUseCase', () => {
  it('routes first-time users to onboarding', async () => {
    const preferences = new InMemoryPreferencesAdapter();
    const useCase = new LoadBootstrapUseCase(preferences);

    await expect(useCase.execute()).resolves.toBe('onboarding');
  });

  it('routes returning users to auth after onboarding is complete', async () => {
    const preferences = new InMemoryPreferencesAdapter({ onboardingCompleted: true });
    const useCase = new LoadBootstrapUseCase(preferences);

    await expect(useCase.execute()).resolves.toBe('auth');
  });
});

describe('CompleteOnboardingUseCase', () => {
  it('persists onboarding completion flag', async () => {
    const preferences = new InMemoryPreferencesAdapter();
    const complete = new CompleteOnboardingUseCase(preferences);
    const load = new LoadBootstrapUseCase(preferences);

    await complete.execute();

    await expect(load.execute()).resolves.toBe('auth');
  });
});

describe('Preferences load failure', () => {
  it('surfaces bootstrap error when preferences are unavailable', async () => {
    const preferences = new InMemoryPreferencesAdapter();
    preferences.failOnLoad(true);
    const useCase = new LoadBootstrapUseCase(preferences);

    await expect(useCase.execute()).rejects.toThrow('preferences_unavailable');
  });
});
