import * as React from 'react';
import '@/index.css';
import { SidebarProvider, SidebarTrigger } from './components/ui/sidebar.tsx';
import { AppSidebar } from '@/components/sidebar/AppSidebar.tsx';

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className='flex h-screen'>
      <SidebarProvider>
        <AppSidebar />
        <main className='flex-1 transition-all duration-300'>
          <div className='p-4 border-b border-border'>
            <SidebarTrigger />
          </div>
          <div className='h-full overflow-auto'>{children}</div>
        </main>
      </SidebarProvider>
    </div>
  );
}
