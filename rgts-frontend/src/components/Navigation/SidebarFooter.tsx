import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { DarkMode } from '@/components/Utility/DarkMode.tsx';
import { cn } from '@/lib/utils.ts';

interface SidebarFooterProps {
  name: string;
  initials: string;
  username: string;
  isCollapsed: boolean;
}

export function SidebarFooter({ name, initials, username, isCollapsed }: SidebarFooterProps) {
  return (
    <div className='p-4'>
      <Separator className='mb-4 bg-gold-accent/10' />

      {/* Theme Toggle */}
      <div className={`flex items-center mb-3 ${isCollapsed ? 'justify-center' : 'gap-3'}`}>
        {isCollapsed ? (
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <div>
                  <DarkMode />
                </div>
              </TooltipTrigger>
              <TooltipContent side='right' className='ml-2'>
                <p>Toggle Theme</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        ) : (
          <>
            <DarkMode />
            <span className='text-sm text-muted-foreground'>Theme</span>
          </>
        )}
      </div>

      {/* User Profile */}
      <div className={`flex items-center ${isCollapsed ? 'justify-center' : 'gap-3'}`}>
        {isCollapsed ? (
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <Avatar className='w-8 h-8'>
                  <AvatarFallback className='bg-transparent text-primary font-semibold border-2 border-primary'>{initials}</AvatarFallback>
                </Avatar>
              </TooltipTrigger>
              <TooltipContent side='right' className='ml-2'>
                <div className='text-center'>
                  <p className='font-medium'>{name}</p>
                  <p className='text-xs text-muted-foreground'>{username}</p>
                </div>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        ) : (
          <>
            <Avatar className='w-8 h-8'>
              <AvatarFallback className='bg-transparent text-primary font-semibold border-2 border-primary'>{initials}</AvatarFallback>
            </Avatar>
            <div
              className={cn(
                'overflow-hidden whitespace-nowrap transition-all duration-300 ease-in-out',
                isCollapsed ? 'max-w-0 opacity-0 ml-0' : 'max-w-[180px] opacity-100 ml-3',
              )}
            >
              <span className='block text-sm font-medium text-foreground'>{name}</span>
              <span className='block text-xs text-muted-foreground'>{username}</span>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
