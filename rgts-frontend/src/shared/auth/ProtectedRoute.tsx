import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/shared/auth/AuthContext.tsx';

const ProtectedRoute = () => {
  const { isAuthenticated } = useAuth();

  return isAuthenticated ? <Outlet /> : <Navigate to='/login' replace />;
};

export default ProtectedRoute;
