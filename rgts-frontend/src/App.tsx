import { Routes, Route } from 'react-router-dom';
import RootLayout from './RouteLayout.tsx';
import Dashboard from './pages/Dashboard/Dashboard';

export default function App() {
  return (
    <RootLayout>
      <Routes>
        <Route path='/' />
        <Route path='/dashboard' element={<Dashboard />} />
      </Routes>
    </RootLayout>
  );
}
