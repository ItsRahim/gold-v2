import { Routes, Route } from 'react-router-dom';
import RootLayout from './app/RouteLayout.tsx';
import DashboardManagementView from '@/app/dashboard/DashboardManagementView.tsx';
import CatalogManagementView from '@/app/catalog/CatalogManagementView.tsx';
import PortfolioManagementView from '@/app/portfolio/PortfolioManagementView.tsx';
import MarketManagementView from '@/app/market/MarketManagementView.tsx';
import { Navigate } from 'react-router-dom';
import AlertsManagementView from '@/app/alerts/AlertsManagementView.tsx';

export default function App() {
  return (
    <Routes>
      <Route path='/' element={<RootLayout />}>
        <Route index element={<Navigate to='/dashboard' replace />} />
        <Route path='dashboard' element={<DashboardManagementView />} />
        <Route path='catalog' element={<CatalogManagementView />} />
        <Route path='portfolio' element={<PortfolioManagementView />} />
        <Route path='market' element={<MarketManagementView />} />
        <Route path='alerts' element={<AlertsManagementView />} />
      </Route>
    </Routes>
  );
}
