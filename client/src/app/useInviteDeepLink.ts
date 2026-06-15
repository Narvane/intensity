import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { App } from '@capacitor/app';
import { Capacitor } from '@capacitor/core';
import { parseInviteDeepLinkUrl } from '@domain/convite/invitePresentation';

export function useInviteDeepLink() {
  const navigate = useNavigate();

  useEffect(() => {
    if (!Capacitor.isNativePlatform()) {
      return;
    }

    const openInvite = (url: string) => {
      const route = parseInviteDeepLinkUrl(url);
      if (route) {
        navigate(route, { replace: true });
      }
    };

    const listenerPromise = App.addListener('appUrlOpen', (event) => {
      openInvite(event.url);
    });

    return () => {
      void listenerPromise.then((listener) => listener.remove());
    };
  }, [navigate]);
}
