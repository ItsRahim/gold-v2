import { Menu } from 'lucide-react';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import { SidebarTrigger } from '../ui/sidebar';
import {
  SIDEBAR_CLASSES,
  getFlexLayout,
  TOOLTIP_CONFIG,
  getUserProfileLayout,
  getUserInfoClasses,
} from '@/styles/sidebar';
import { cn } from '@/lib/utils';
import { Avatar, AvatarFallback } from '@/components/ui/avatar.tsx';
import type { ReactNode } from 'react';

interface SidebarHeaderProps {
  name: string;
  initials: string;
  username: string;
  isCollapsed: boolean;
  onToggle: () => void;
}

function renderUserProfile(
  isCollapsed: boolean,
  initials: string,
  name: string,
  username: string,
) {
  if (!isCollapsed) {
    return (
      <>
        <Avatar className='w-8 h-8'>
          <AvatarFallback className={SIDEBAR_CLASSES.avatar}>
            {initials}
          </AvatarFallback>
        </Avatar>
        <div className={getUserInfoClasses(isCollapsed)}>
          <span className='block text-sm font-medium text-foreground'>
            {name}
          </span>
          <span className='block text-xs text-muted-foreground'>
            {username}
          </span>
        </div>
      </>
    );
  }
}

function renderToggleButton(isCollapsed: boolean, toggleButton: ReactNode) {
  if (!isCollapsed) {
    return toggleButton;
  }

  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>{toggleButton}</TooltipTrigger>
        <TooltipContent
          side={TOOLTIP_CONFIG.side}
          className={TOOLTIP_CONFIG.className}
        >
          <p>Expand sidebar</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}

export function SidebarHeader({
  name,
  initials,
  username,
  isCollapsed,
  onToggle,
}: SidebarHeaderProps) {
  const toggleButton = (
    <SidebarTrigger
      variant='ghost'
      size='sm'
      onClick={onToggle}
      className={SIDEBAR_CLASSES.toggleButton}
    >
      <Menu className='w-4 h-4' />
    </SidebarTrigger>
  );

  return (
    <div
      className={cn(
        'p-4 border-b border-gold-accent/10',
        getFlexLayout(isCollapsed),
      )}
    >
      <div className='flex items-center gap-2'>
        <div className={getUserProfileLayout(isCollapsed)}>
          {renderUserProfile(isCollapsed, initials, name, username)}
        </div>
      </div>
      {renderToggleButton(isCollapsed, toggleButton)}
    </div>
  );
}
