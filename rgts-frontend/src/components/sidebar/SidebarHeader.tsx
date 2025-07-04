import { Menu } from 'lucide-react';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { SidebarGroupLabel, SidebarTrigger } from '../ui/sidebar';
import { SIDEBAR_CLASSES, getFlexLayout, TOOLTIP_CONFIG } from '@/styles/sidebar';
import { cn } from '@/shared/utils.ts';

interface SidebarHeaderProps {
  isCollapsed: boolean;
  onToggle: () => void;
}

function ToggleButton(onToggle: () => void) {
  return (
    <SidebarTrigger variant='ghost' size='sm' onClick={onToggle} className={SIDEBAR_CLASSES.toggleButton}>
      <Menu className='w-4 h-4' />
    </SidebarTrigger>
  );
}

function CollapsedToggleButtonWithTooltip(onToggle: () => void) {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>{ToggleButton(onToggle)}</TooltipTrigger>
        <TooltipContent side={TOOLTIP_CONFIG.side} className={TOOLTIP_CONFIG.className}>
          <p>Expand sidebar</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}

function ExpandedHeaderTitle() {
  return (
    <div className='flex items-center gap-2'>
      <div className='text-lg font-semibold'>
        <SidebarGroupLabel className='text-primary gold-text-glow'>RGTS</SidebarGroupLabel>
      </div>
    </div>
  );
}

export function SidebarHeader({ isCollapsed, onToggle }: SidebarHeaderProps) {
  return (
    <div className={cn('p-4 border-b border-gold-accent/10', getFlexLayout(isCollapsed))}>
      {!isCollapsed && <ExpandedHeaderTitle />}
      {isCollapsed ? CollapsedToggleButtonWithTooltip(onToggle) : ToggleButton(onToggle)}
    </div>
  );
}
