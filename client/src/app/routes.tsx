import { Route, Routes } from 'react-router-dom';
import { ShellPage } from '@presentation/ShellPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="*" element={<ShellPage />} />
    </Routes>
  );
}
