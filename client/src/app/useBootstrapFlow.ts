import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createDefaultPreferences } from '@adapters/preferences/CapacitorPreferencesAdapter';
import { LoadBootstrapUseCase } from '@domain/bootstrap/LoadBootstrapUseCase';
import {
  initialBootstrapState,
  type BootstrapState,
} from '@domain/bootstrap/BootstrapState';

export function useBootstrapFlow() {
  const navigate = useNavigate();
  const preferences = useMemo(() => createDefaultPreferences(), []);
  const loadBootstrap = useMemo(
    () => new LoadBootstrapUseCase(preferences),
    [preferences],
  );
  const [state, setState] = useState<BootstrapState>(initialBootstrapState);

  const run = useCallback(async () => {
    setState({ status: 'loading', destination: null, errorMessage: null });

    try {
      const destination = await loadBootstrap.execute();
      setState({ status: 'ready', destination, errorMessage: null });
      navigate(destination === 'onboarding' ? '/onboarding' : '/auth', {
        replace: true,
      });
    } catch {
      setState({
        status: 'error',
        destination: null,
        errorMessage: 'bootstrap.error',
      });
    }
  }, [loadBootstrap, navigate]);

  useEffect(() => {
    void run();
  }, [run]);

  return { state, retry: run };
}
