import { useNavigate } from 'react-router-dom';
import { useNavigation } from './NavigationProvider';
import { useSession } from './SessionProvider';

export function useAppLogout(mode: 'EXPERIENCES' | 'EXPERIENCE_BOX') {
  const { logoutExperiences, logoutExperienceBox } = useSession();
  const { clearNavigation } = useNavigation();
  const navigate = useNavigate();

  return async () => {
    if (mode === 'EXPERIENCES') {
      await logoutExperiences();
    } else {
      await logoutExperienceBox();
    }
    await clearNavigation();
    navigate('/auth', {
      replace: true,
      state: mode === 'EXPERIENCE_BOX' ? { panel: 'experienceBox' } : undefined,
    });
  };
}
