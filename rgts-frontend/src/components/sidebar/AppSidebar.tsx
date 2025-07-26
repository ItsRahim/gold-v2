import { useState } from 'react';
import { SidebarHeader } from './SidebarHeader';
import { SidebarFooter } from './SidebarFooter';
import { SidebarContent } from '@/components/sidebar/SidebarContent.tsx';
import { SIDEBAR_CLASSES, getSidebarWidth } from '@/styles/sidebar';
import { cn } from '@/shared/utils.ts';
import { useAuthStore } from '@/stores/AuthStore';

export function AppSidebar() {
  const [isCollapsed, setIsCollapsed] = useState(false);

  const user = useAuthStore((state) => state.user);

  const fullName = user?.firstName && user?.lastName ? `${user.firstName} ${user.lastName}` : user?.username || 'User';

  const initials =
    user?.firstName && user?.lastName ? `${user.firstName[0]}${user.lastName[0]}`.toUpperCase() : user?.username?.slice(0, 2).toUpperCase() || 'U';

  const username = user?.username || 'username';

  return (
    <aside className={cn(SIDEBAR_CLASSES.container, getSidebarWidth(isCollapsed))}>
      <div className={SIDEBAR_CLASSES.flexColumn}>
        <SidebarHeader isCollapsed={isCollapsed} onToggle={() => setIsCollapsed(!isCollapsed)} />
        <SidebarContent isCollapsed={isCollapsed} />
        <SidebarFooter name={fullName} initials={initials} username={username} isCollapsed={isCollapsed} />
      </div>
    </aside>
  );
}
