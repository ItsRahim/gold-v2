import { createRoot } from 'react-dom/client';
import './styles/index.css';
import App from './App.tsx';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@/shared/theme/theme-provider.tsx';
import { Toaster } from '@/components/ui/sonner.tsx';

createRoot(document.getElementById('root')!).render(
  <ThemeProvider>
    <BrowserRouter>
      <App />
      <Toaster position='bottom-right' />
    </BrowserRouter>
  </ThemeProvider>,
);
