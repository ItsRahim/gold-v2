import { Separator } from '@/components/ui/separator';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {
  SIDEBAR_CLASSES,
  getThemeToggleLayout,
  TOOLTIP_CONFIG,
} from '@/styles/sidebar';
import { DarkMode } from '@/components/utility/DarkMode.tsx';

interface SidebarFooterProps {
  isCollapsed: boolean;
}

export function SidebarFooter({ isCollapsed }: SidebarFooterProps) {
  return (
    <div className='p-4'>
      <Separator className={SIDEBAR_CLASSES.separator} />

      {/* Theme Toggle */}
      <div className={getThemeToggleLayout(isCollapsed)}>
        {isCollapsed ? (
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <div>
                  <DarkMode />
                </div>
              </TooltipTrigger>
              <TooltipContent
                side={TOOLTIP_CONFIG.side}
                className={TOOLTIP_CONFIG.className}
              >
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
    </div>
  );
}
