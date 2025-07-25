import { useAuthStore } from '@/stores/AuthStore.ts';
import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification.ts';

export default function LogoutRoute() {
  const logout = useAuthStore((state) => state.logout);

  useEffect(() => {
    logout();
    showToast(TOAST_TYPES.INFO, 'You have been logged out.');
  }, [logout]);

  return <Navigate to='/login' replace />;
}
