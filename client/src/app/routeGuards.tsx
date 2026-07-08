import { Navigate, Outlet } from 'react-router-dom';
import { OfflineBanner } from '@presentation/components/OfflineBanner';
import { resolveGuestRouteRedirect } from '@domain/auth/guestRouteRedirect';
import { useSession } from './SessionProvider';

export function RequireGuestRoute() {
  const { experienceBoxSession, loading } = useSession();
  if (loading) {
    return null;
  }

  const redirectTo = resolveGuestRouteRedirect(experienceBoxSession);

  if (redirectTo) {
    return <Navigate to={redirectTo} replace />;
  }

  return <Outlet />;
}

export function RequireExperiencesSessionRoute() {
  const { experiencesSession, loading, invalid } = useSession();

  if (loading) {
    return null;
  }

  if (invalid) {
    return <Navigate to="/unknown-session" replace />;
  }

  if (experiencesSession?.accessMode !== 'EXPERIENCES') {
    return <Navigate to="/auth" replace />;
  }

  return (
    <>
      <OfflineBanner />
      <Outlet />
    </>
  );
}

export function RequireExperienceBoxSessionRoute() {
  const { experienceBoxSession, loading, invalid } = useSession();

  if (loading) {
    return null;
  }

  if (invalid) {
    return <Navigate to="/unknown-session" replace />;
  }

  if (experienceBoxSession?.accessMode !== 'EXPERIENCE_BOX') {
    return <Navigate to="/auth" replace />;
  }

  return (
    <>
      <OfflineBanner />
      <Outlet />
    </>
  );
}
