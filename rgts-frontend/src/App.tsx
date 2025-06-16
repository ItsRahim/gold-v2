import { Routes, Route } from 'react-router-dom';
import RootLayout from './app/RouteLayout.tsx';
import Dashboard from '@/app/dashboard/Dashboard';
import Catalog from '@/app/catalog/Catalog.tsx';
import Portfolio from '@/app/portfolio/Portfolio.tsx';
import Market from '@/app/market/Market.tsx';
import { Navigate } from 'react-router-dom';
import Alerts from '@/app/alerts/Alerts.tsx';

export default function App() {
  return (
    <Routes>
      <Route path='/' element={<RootLayout />}>
        <Route index element={<Navigate to='/dashboard' replace />} />
        <Route path='dashboard' element={<Dashboard />} />
        <Route path='catalog' element={<Catalog />} />
        <Route path='portfolio' element={<Portfolio />} />
        <Route path='market' element={<Market />} />
        <Route path='alerts' element={<Alerts />} />
      </Route>
    </Routes>
  );
}
