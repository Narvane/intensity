export type BootstrapStatus = 'loading' | 'ready' | 'error';

export type BootstrapDestination = 'onboarding' | 'auth';

export interface BootstrapState {
  status: BootstrapStatus;
  destination: BootstrapDestination | null;
  errorMessage: string | null;
}

export const initialBootstrapState: BootstrapState = {
  status: 'loading',
  destination: null,
  errorMessage: null,
};
