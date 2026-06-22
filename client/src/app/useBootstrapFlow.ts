import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { App } from '@capacitor/app';
import { Capacitor } from '@capacitor/core';
import { createDefaultPreferences } from '@adapters/preferences/CapacitorPreferencesAdapter';
import { LoadBootstrapUseCase } from '@domain/bootstrap/LoadBootstrapUseCase';
import {
  initialBootstrapState,
  type BootstrapState,
} from '@domain/bootstrap/BootstrapState';
import { parseInviteDeepLinkUrl } from '@domain/invite/invitePresentation';

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
      if (Capacitor.isNativePlatform()) {
        const launch = await App.getLaunchUrl();
        const inviteRoute = launch?.url ? parseInviteDeepLinkUrl(launch.url) : null;
        if (inviteRoute) {
          setState({ status: 'ready', destination: null, errorMessage: null });
          navigate(inviteRoute, { replace: true });
          return;
        }
      }

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
