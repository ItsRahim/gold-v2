import { Separator } from '@/components/ui/separator';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { SIDEBAR_CLASSES, getThemeToggleLayout, TOOLTIP_CONFIG, getUserProfileLayout, getUserInfoClasses } from '@/styles/sidebar';
import { DarkMode } from '@/shared/theme/DarkMode.tsx';
import { Avatar, AvatarFallback } from '@/components/ui/avatar.tsx';

interface SidebarFooterProps {
  name: string;
  initials: string;
  username: string;
  isCollapsed: boolean;
}

function CollapsedToggleTheme() {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <div>
            <DarkMode />
          </div>
        </TooltipTrigger>
        <TooltipContent side={TOOLTIP_CONFIG.side} className={TOOLTIP_CONFIG.className}>
          <p>Toggle Theme</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}

function ExpandedToggleTheme() {
  return (
    <>
      <DarkMode />
      <span className='text-sm text-muted-foreground'>Theme</span>
    </>
  );
}

function CollapsedUserProfile(name: string, initials: string, username: string) {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Avatar className='w-8 h-8'>
            <AvatarFallback className={SIDEBAR_CLASSES.avatar}>{initials}</AvatarFallback>
          </Avatar>
        </TooltipTrigger>
        <TooltipContent side={TOOLTIP_CONFIG.side} className={TOOLTIP_CONFIG.className}>
          <div className='text-center'>
            <p className='font-medium'>{name}</p>
            <p className='text-xs text-muted-foreground'>{username}</p>
          </div>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}

function ExpandedUserProfile(isCollapsed: boolean, name: string, initials: string, username: string) {
  return (
    <>
      <Avatar className='w-8 h-8'>
        <AvatarFallback className={SIDEBAR_CLASSES.avatar}>{initials}</AvatarFallback>
      </Avatar>
      <div className={getUserInfoClasses(isCollapsed)}>
        <span className='block text-sm font-medium text-foreground'>{name}</span>
        <span className='block text-xs text-muted-foreground'>{username}</span>
      </div>
    </>
  );
}

export function SidebarFooter({ name, initials, username, isCollapsed }: SidebarFooterProps) {
  return (
    <div className='p-4'>
      <Separator className={SIDEBAR_CLASSES.separator} />

      {/* Theme Toggle */}
      <div className={getThemeToggleLayout(isCollapsed)}>{isCollapsed ? <CollapsedToggleTheme /> : <ExpandedToggleTheme />}</div>

      {/* User Profile */}
      <div className={getUserProfileLayout(isCollapsed)}>
        {isCollapsed ? CollapsedUserProfile(name, initials, username) : ExpandedUserProfile(isCollapsed, name, initials, username)}
      </div>
    </div>
  );
}
