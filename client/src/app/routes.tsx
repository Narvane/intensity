import { Navigate, Route, Routes } from 'react-router-dom';
import { useInviteDeepLink } from '@app/useInviteDeepLink';
import {
  RequireExperienceBoxSessionRoute,
  RequireExperiencesSessionRoute,
  RequireGuestRoute,
} from '@app/routeGuards';
import { AuthPage } from '@presentation/access/auth/AuthPage';
import { BoxSelectionPage } from '@presentation/collection/boxes/BoxSelectionPage';
import { CreateBoxExperiencesPage } from '@presentation/collection/boxes/CreateBoxExperiencesPage';
import { BoxHomePage } from '@presentation/moment/experience-box/BoxHomePage';
import { CreateBoxPage } from '@presentation/moment/experience-box/CreateBoxPage';
import { SharedMomentPage } from '@presentation/moment/shared-moment/SharedMomentPage';
import { BootstrapPage } from '@presentation/access/bootstrap/BootstrapPage';
import { ExperienceListPage } from '@presentation/collection/experiences/ExperienceListPage';
import { GroupSelectionPage } from '@presentation/collection/groups/GroupSelectionPage';
import { OnboardingPage } from '@presentation/access/onboarding/OnboardingPage';
import { InvitePreviewPage } from '@presentation/invite/InvitePreviewPage';
import { UnknownSessionPage } from '@presentation/access/unknown-session/UnknownSessionPage';

export function AppRouter() {
  useInviteDeepLink();

  return (
    <Routes>
      <Route path="/" element={<BootstrapPage />} />
      <Route path="/onboarding" element={<OnboardingPage />} />
      <Route path="/join" element={<InvitePreviewPage />} />
      <Route element={<RequireGuestRoute />}>
        <Route path="/auth" element={<AuthPage />} />
      </Route>
      <Route path="/unknown-session" element={<UnknownSessionPage />} />
      <Route element={<RequireExperiencesSessionRoute />}>
        <Route path="/groups" element={<GroupSelectionPage />} />
        <Route path="/groups/:groupId/boxes" element={<BoxSelectionPage />} />
        <Route path="/groups/:groupId/boxes/create" element={<CreateBoxExperiencesPage />} />
        <Route
          path="/groups/:groupId/boxes/:boxId/experiences"
          element={<ExperienceListPage />}
        />
      </Route>
      <Route element={<RequireExperienceBoxSessionRoute />}>
        <Route path="/box-home" element={<BoxHomePage />} />
        <Route path="/box-home/create" element={<CreateBoxPage />} />
        <Route path="/box-home/:boxId/moment" element={<SharedMomentPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
