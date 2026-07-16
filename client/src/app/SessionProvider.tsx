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
import { getApiClient } from '@adapters/http/apiClient';
import { createDefaultSessionAdapter } from '@adapters/session/SessionPreferencesAdapter';
import { setExperienceBoxSessionEndReason } from '@domain/session/experienceBoxSessionEnd';
import { isDrawLimitReached } from '@domain/session/experienceBoxSessionPolicy';
import { isTokenExpired } from '@domain/session/jwtToken';
import type { SessionPort, SessionState } from '@domain/session/SessionPort';
import { isValidSession } from '@domain/session/sessionStorage';
import { resolveUnauthorizedDecision } from '@domain/session/unauthorizedPolicy';

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
  if (!isValidSession(session) || isTokenExpired(session.token)) {
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

  // Refs so the unauthorized listener always sees the current values without
  // re-registering on every render/navigation.
  const sessionsRef = useRef({ experiencesSession, experienceBoxSession });
  sessionsRef.current = { experiencesSession, experienceBoxSession };
  const locationPathRef = useRef(location.pathname);
  locationPathRef.current = location.pathname;

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

      const hadCorruptStorage =
        (stored.experiences !== null && !isValidSession(stored.experiences)) ||
        (stored.experienceBox !== null && !isValidSession(stored.experienceBox));

      setExperiencesSession(nextExperiences);
      setExperienceBoxSession(nextExperienceBox);
      setInvalid(hadCorruptStorage);
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

  // Rejected-token interceptor: clear a session only when the token the API
  // rejected is exactly that session's token, then leave the protected area
  // the user is currently on. A 401 without a matching token changes nothing.
  useEffect(() => {
    const client = getApiClient();
    client.setUnauthorizedListener((failedToken) => {
      const { experiencesSession: currentExperiences, experienceBoxSession: currentBox } =
        sessionsRef.current;
      const decision = resolveUnauthorizedDecision(currentExperiences, currentBox, failedToken);

      void (async () => {
        if (decision.clearExperiences) {
          await port.clearExperiences();
          setExperiencesSession(null);
        }
        if (decision.clearExperienceBox) {
          await port.clearExperienceBox();
          setExperienceBoxSession(null);
        }

        const path = locationPathRef.current;
        if (decision.clearExperiences && path.startsWith('/groups')) {
          navigate('/auth', { replace: true });
        } else if (decision.clearExperienceBox && path.startsWith('/box-home')) {
          navigate('/auth', { replace: true, state: { panel: 'experienceBox' } });
        }
      })();
    });

    return () => {
      client.setUnauthorizedListener(null);
    };
  }, [navigate, port]);

  // Local expiry watchdog: when a token expires while the user is inside its
  // protected area, drop only that session and return to /auth.
  useEffect(() => {
    if (loading) {
      return;
    }

    const path = location.pathname;

    if (path.startsWith('/groups') && experiencesSession && isTokenExpired(experiencesSession.token)) {
      void port.clearExperiences().then(() => {
        setExperiencesSession(null);
        navigate('/auth', { replace: true });
      });
    }

    if (path.startsWith('/box-home') && experienceBoxSession && isTokenExpired(experienceBoxSession.token)) {
      void port.clearExperienceBox().then(() => {
        setExperienceBoxSession(null);
        navigate('/auth', { replace: true, state: { panel: 'experienceBox' } });
      });
    }
  }, [experienceBoxSession, experiencesSession, loading, location.pathname, navigate, port]);

  const saveExperiencesSession = useCallback(
    async (next: SessionState) => {
      await port.saveExperiences(next);
      sessionsRef.current = { ...sessionsRef.current, experiencesSession: next };
      // Commit synchronously so route guards see the session before callers navigate.
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
