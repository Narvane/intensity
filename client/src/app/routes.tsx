import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthPage } from '@presentation/auth/AuthPage';
import { BootstrapPage } from '@presentation/bootstrap/BootstrapPage';
import { OnboardingPage } from '@presentation/onboarding/OnboardingPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<BootstrapPage />} />
      <Route path="/onboarding" element={<OnboardingPage />} />
      <Route path="/auth" element={<AuthPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
