import { Navigate, Outlet } from 'react-router-dom';
import { OfflineBanner } from '@presentation/components/OfflineBanner';
import { resolveGuestRouteRedirect } from '@domain/auth/guestRouteRedirect';
import { useSession } from './SessionProvider';

export function RequireGuestRoute() {
  const { session, loading } = useSession();
  if (loading) {
    return null;
  }

  const redirectTo = resolveGuestRouteRedirect(session);

  if (redirectTo) {
    return <Navigate to={redirectTo} replace />;
  }

  return <Outlet />;
}

export function RequireExperiencesSessionRoute() {
  const { session, loading, invalid } = useSession();

  if (loading) {
    return null;
  }

  if (invalid) {
    return <Navigate to="/unknown-session" replace />;
  }

  if (session?.accessMode !== 'EXPERIENCES') {
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
  const { session, loading, invalid } = useSession();

  if (loading) {
    return null;
  }

  if (invalid) {
    return <Navigate to="/unknown-session" replace />;
  }

  if (session?.accessMode !== 'EXPERIENCE_BOX') {
    return <Navigate to="/auth" replace />;
  }

  return (
    <>
      <OfflineBanner />
      <Outlet />
    </>
  );
}
