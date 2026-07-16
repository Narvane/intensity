import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type PropsWithChildren,
} from 'react';
import { flushSync } from 'react-dom';
import { useLocation, useNavigate } from 'react-router-dom';
import { registerUnauthorizedHandler } from '@adapters/api/apiUnauthorizedBridge';
import { createApiClient } from '@adapters/api/ApiClient';
import { createDefaultSessionAdapter } from '@adapters/session/SessionPreferencesAdapter';
import { setExperienceBoxSessionEndReason } from '@domain/session/experienceBoxSessionEnd';
import { isDrawLimitReached } from '@domain/session/experienceBoxSessionPolicy';
import { isTokenExpired } from '@domain/session/jwtToken';
import type { SessionPort, SessionState } from '@domain/session/SessionPort';
import { isValidSession } from '@domain/session/sessionStorage';

interface SessionContextValue {
  experiencesSession: SessionState | null;
  experienceBoxSession: SessionState | null;
  loading: boolean;
  invalid: boolean;
  refresh: () => Promise<void>;
  saveExperiencesSession: (session: SessionState) => Promise<void>;
  saveExperienceBoxSession: (session: SessionState) => Promise<void>;
  logoutExperiences: () => Promise<void>;
  logoutExperienceBox: () => Promise<void>;
}

const SessionContext = createContext<SessionContextValue | null>(null);

interface SessionProviderProps extends PropsWithChildren {
  sessionPort?: SessionPort;
}

function sanitizeSession(session: SessionState | null): SessionState | null {
  if (!isValidSession(session)) {
    return null;
  }

  if (isTokenExpired(session.token)) {
    return null;
  }

  return session;
}

export function SessionProvider({ children, sessionPort }: SessionProviderProps) {
  const port = useMemo(() => sessionPort ?? createDefaultSessionAdapter(), [sessionPort]);
  const navigate = useNavigate();
  const location = useLocation();
  const [experiencesSession, setExperiencesSession] = useState<SessionState | null>(null);
  const [experienceBoxSession, setExperienceBoxSession] = useState<SessionState | null>(null);
  const [loading, setLoading] = useState(true);
  const [invalid, setInvalid] = useState(false);
  const sessionsRef = useRef({ experiencesSession, experienceBoxSession });

  useEffect(() => {
    sessionsRef.current = { experiencesSession, experienceBoxSession };
  }, [experiencesSession, experienceBoxSession]);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const stored = await port.load();
      let nextExperiences = sanitizeSession(stored.experiences);
      let nextExperienceBox = sanitizeSession(stored.experienceBox);

      if (stored.experiences && !nextExperiences) {
        await port.clearExperiences();
      }

      if (stored.experienceBox && !nextExperienceBox) {
        await port.clearExperienceBox();
      }

      if (
        nextExperienceBox?.accessMode === 'EXPERIENCE_BOX' &&
        isDrawLimitReached(nextExperienceBox.experienceBox?.drawCount ?? 0)
      ) {
        setExperienceBoxSessionEndReason('draw_limit');
        await port.clearExperienceBox();
        nextExperienceBox = null;
      }

      setExperiencesSession(nextExperiences);
      setExperienceBoxSession(nextExperienceBox);
      setInvalid(false);

      const hadCorruptStorage =
        (stored.experiences !== null && !isValidSession(stored.experiences)) ||
        (stored.experienceBox !== null && !isValidSession(stored.experienceBox));
      if (hadCorruptStorage) {
        setInvalid(true);
      }
    } catch {
      setExperiencesSession(null);
      setExperienceBoxSession(null);
      setInvalid(true);
    } finally {
      setLoading(false);
    }
  }, [port]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  const redirectAfterUnauthorized = useCallback(
    (clearedExperiences: boolean, clearedExperienceBox: boolean) => {
      const path = location.pathname;
      const onExperiencesRoute = path.startsWith('/groups');
      const onExperienceBoxRoute = path.startsWith('/box-home');

      if (clearedExperiences && onExperiencesRoute) {
        navigate('/auth', { replace: true });
        return;
      }

      if (clearedExperienceBox && onExperienceBoxRoute) {
        navigate('/auth', { replace: true, state: { panel: 'experienceBox' } });
      }
    },
    [location.pathname, navigate],
  );

  useEffect(() => {
    registerUnauthorizedHandler(async (token) => {
      const { experiencesSession: currentExperiences, experienceBoxSession: currentBox } =
        sessionsRef.current;
      let clearedExperiences = false;
      let clearedExperienceBox = false;

      // Only clear the session whose token the failed request actually used.
      // Never wipe everything on a token-less 401 (false positives from
      // CapacitorHttp / CORS / missing Authorization).
      if (!token) {
        return;
      }

      if (currentExperiences?.token === token) {
        await port.clearExperiences();
        setExperiencesSession(null);
        clearedExperiences = true;
      }

      if (currentBox?.token === token) {
        await port.clearExperienceBox();
        setExperienceBoxSession(null);
        clearedExperienceBox = true;
      }

      redirectAfterUnauthorized(clearedExperiences, clearedExperienceBox);
    });

    return () => {
      registerUnauthorizedHandler(null);
    };
  }, [port, redirectAfterUnauthorized]);

  useEffect(() => {
    if (loading) {
      return;
    }

    const path = location.pathname;
    const onExperiencesRoute = path.startsWith('/groups');
    const onExperienceBoxRoute = path.startsWith('/box-home');

    if (onExperiencesRoute && experiencesSession && isTokenExpired(experiencesSession.token)) {
      void port.clearExperiences().then(() => {
        setExperiencesSession(null);
        navigate('/auth', { replace: true });
      });
    }

    if (onExperienceBoxRoute && experienceBoxSession && isTokenExpired(experienceBoxSession.token)) {
      void port.clearExperienceBox().then(() => {
        setExperienceBoxSession(null);
        navigate('/auth', { replace: true, state: { panel: 'experienceBox' } });
      });
    }
  }, [
    experienceBoxSession,
    experiencesSession,
    loading,
    location.pathname,
    navigate,
    port,
  ]);

  const saveExperiencesSession = useCallback(
    async (next: SessionState) => {
      await port.saveExperiences(next);
      sessionsRef.current = { ...sessionsRef.current, experiencesSession: next };
      // Flush before callers navigate — otherwise route guards can still see null.
      flushSync(() => {
        setExperiencesSession(next);
        setInvalid(false);
      });
    },
    [port],
  );

  const saveExperienceBoxSession = useCallback(
    async (next: SessionState) => {
      await port.saveExperienceBox(next);
      sessionsRef.current = { ...sessionsRef.current, experienceBoxSession: next };
      flushSync(() => {
        setExperienceBoxSession(next);
        setInvalid(false);
      });
    },
    [port],
  );

  const logoutExperiences = useCallback(async () => {
    await port.clearExperiences();
    setExperiencesSession(null);
    setInvalid(false);
  }, [port]);

  const logoutExperienceBox = useCallback(async () => {
    await port.clearExperienceBox();
    setExperienceBoxSession(null);
    setInvalid(false);
  }, [port]);

  const value = useMemo(
    () => ({
      experiencesSession,
      experienceBoxSession,
      loading,
      invalid,
      refresh,
      saveExperiencesSession,
      saveExperienceBoxSession,
      logoutExperiences,
      logoutExperienceBox,
    }),
    [
      experiencesSession,
      experienceBoxSession,
      loading,
      invalid,
      refresh,
      saveExperiencesSession,
      saveExperienceBoxSession,
      logoutExperiences,
      logoutExperienceBox,
    ],
  );

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession(): SessionContextValue {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error('useSession must be used within SessionProvider');
  }
  return context;
}

export { createApiClient, createDefaultSessionAdapter };
