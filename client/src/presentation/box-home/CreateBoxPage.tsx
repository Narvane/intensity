import { useNavigate } from 'react-router-dom';
import { useSession } from '@app/SessionProvider';
import { useI18n } from '../../i18n/I18nContext';
import { NavButton } from '../components/NavButton';
import { ScreenHeader } from '../components/ScreenHeader';
import { ScreenTitle } from '../components/ScreenTitle';
import { SessionModeFooter } from '../components/SessionModeFooter';
import { CreateBoxForm } from '../boxes/CreateBoxForm';
import styles from './CreateBoxPage.module.css';

export function CreateBoxPage() {
  const { t } = useI18n();
  const { experienceBoxSession } = useSession();
  const navigate = useNavigate();

  if (!experienceBoxSession?.token || !experienceBoxSession.groupId) {
    return null;
  }

  return (
    <>
    <main className={styles.page}>
      <ScreenHeader
        leading={<NavButton action="back" onClick={() => navigate('/box-home')} />}
      >
        <ScreenTitle>{t('createBox.title')}</ScreenTitle>
      </ScreenHeader>

      <p className={styles.intro}>{t('createBox.intro')}</p>

      <CreateBoxForm
        groupId={experienceBoxSession.groupId}
        token={experienceBoxSession.token}
        variant="experienceBox"
        onSuccess={() => navigate('/box-home')}
      />
    </main>
      <SessionModeFooter mode="EXPERIENCE_BOX" members={experienceBoxSession.members} />
    </>
  );
}
