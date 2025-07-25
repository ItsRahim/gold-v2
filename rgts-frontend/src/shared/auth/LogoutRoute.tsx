import { useAuthStore } from '@/stores/AuthStore.ts';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function LogoutRoute() {
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      await logout();
      navigate('/login');
    })();
  }, []);
}
