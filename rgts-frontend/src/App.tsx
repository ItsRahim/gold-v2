import { Routes, Route } from 'react-router-dom';
import RootLayout from './RouteLayout.tsx';
import Dashboard from './pages/Dashboard/Dashboard';
import Catalog from '@/pages/Catalog/Catalog.tsx';
import Portfolio from '@/pages/Portfolio/Portfolio.tsx';
import Market from '@/pages/Market/Market.tsx';
import { Navigate } from 'react-router-dom';
import Alerts from '@/pages/Alerts/Alerts.tsx';

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
