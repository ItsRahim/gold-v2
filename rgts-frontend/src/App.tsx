import { Routes, Route, Navigate } from 'react-router-dom';
import RootLayout from './app/RouteLayout';
import DashboardManagementView from '@/app/dashboard/DashboardManagementView';
import CatalogManagementView from '@/app/catalog/CatalogManagementView';
import PortfolioManagementView from '@/app/portfolio/PortfolioManagementView';
import AlertsManagementView from '@/app/alerts/AlertsManagementView';
import ProtectedRoute from '@/shared/auth/ProtectedRoute.tsx';
import { LoginView } from '@/app/auth/LoginView.tsx';
import { RegisterView } from '@/app/auth/RegisterView.tsx';
import GuestRoute from '@/shared/auth/GuestRoute.tsx';
import LogoutRoute from '@/shared/auth/LogoutRoute.tsx';

export default function App() {
  return (
    <Routes>
      <Route element={<GuestRoute />}>
        <Route path='/login' element={<LoginView />} />
        <Route path='/register' element={<RegisterView />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route path='/' element={<RootLayout />}>
          <Route index element={<Navigate to='/dashboard' replace />} />
          <Route path='dashboard' element={<DashboardManagementView />} />
          <Route path='portfolio' element={<PortfolioManagementView />} />
          <Route path='catalog' element={<CatalogManagementView />} />
          <Route path='alerts' element={<AlertsManagementView />} />
          <Route path='/logout' element={<LogoutRoute />} />
        </Route>
      </Route>
    </Routes>
  );
}
