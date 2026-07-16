import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { App } from '@capacitor/app';
import { Capacitor } from '@capacitor/core';
import { parseInviteDeepLinkUrl, resolveInviteHost } from '@domain/invite/invitePresentation';

function parsePasswordResetDeepLinkUrl(rawUrl: string): string | null {
  try {
    const expectedHost = resolveInviteHost();
    const parsed = rawUrl.includes('://')
      ? new URL(rawUrl)
      : new URL(rawUrl, `https://${expectedHost}`);

    if (parsed.host !== expectedHost) {
      return null;
    }
    if (!parsed.pathname.startsWith('/auth/reset-password')) {
      return null;
    }

    const token = parsed.searchParams.get('t')?.trim();
    if (!token) {
      return null;
    }
    return `/auth/reset-password?t=${encodeURIComponent(token)}`;
  } catch {
    return null;
  }
}

export function useInviteDeepLink() {
  const navigate = useNavigate();

  useEffect(() => {
    if (!Capacitor.isNativePlatform()) {
      return;
    }

    const openDeepLink = (url: string) => {
      const resetRoute = parsePasswordResetDeepLinkUrl(url);
      if (resetRoute) {
        navigate(resetRoute, { replace: true });
        return;
      }
      const inviteRoute = parseInviteDeepLinkUrl(url);
      if (inviteRoute) {
        navigate(inviteRoute, { replace: true });
      }
    };

    const listenerPromise = App.addListener('appUrlOpen', (event) => {
      openDeepLink(event.url);
    });

    return () => {
      void listenerPromise.then((listener) => listener.remove());
    };
  }, [navigate]);
}
