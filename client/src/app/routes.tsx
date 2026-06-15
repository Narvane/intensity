import { Navigate, Route, Routes } from 'react-router-dom';
import {
  RequireExperienceBoxSessionRoute,
  RequireExperiencesSessionRoute,
  RequireGuestRoute,
} from '@app/routeGuards';
import { AuthPage } from '@presentation/auth/AuthPage';
import { BoxHomePage } from '@presentation/box-home/BoxHomePage';
import { BootstrapPage } from '@presentation/bootstrap/BootstrapPage';
import { GroupSelectionPage } from '@presentation/groups/GroupSelectionPage';
import { OnboardingPage } from '@presentation/onboarding/OnboardingPage';
import { UnknownSessionPage } from '@presentation/unknown-session/UnknownSessionPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<BootstrapPage />} />
      <Route path="/onboarding" element={<OnboardingPage />} />
      <Route element={<RequireGuestRoute />}>
        <Route path="/auth" element={<AuthPage />} />
      </Route>
      <Route path="/unknown-session" element={<UnknownSessionPage />} />
      <Route element={<RequireExperiencesSessionRoute />}>
        <Route path="/groups" element={<GroupSelectionPage />} />
      </Route>
      <Route element={<RequireExperienceBoxSessionRoute />}>
        <Route path="/box-home" element={<BoxHomePage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
