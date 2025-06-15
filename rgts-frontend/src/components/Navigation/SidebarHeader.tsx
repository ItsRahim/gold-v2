import { Menu } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";

interface SidebarHeaderProps {
    isCollapsed: boolean;
    onToggle: () => void;
}

export function SidebarHeader({ isCollapsed, onToggle }: SidebarHeaderProps) {
    const toggleButton = (
        <Button
            variant="ghost"
            size="sm"
            onClick={onToggle}
            className="p-2 hover:bg-gold-light/10 text-muted-foreground hover:text-primary"
        >
            <Menu className="w-4 h-4" />
        </Button>
    );

    return (
        <div className={`flex items-center p-4 border-b border-gold-accent/10 ${
            isCollapsed ? 'justify-center' : 'justify-between'
        }`}>
            {!isCollapsed && (
                <div className="flex items-center gap-2">
                    <div className="text-lg font-semibold">
                        <span className="text-primary gold-text-glow">Rahim's</span>
                        <span className="font-normal text-muted-foreground ml-1 text-sm">Gold Tracker</span>
                    </div>
                </div>
            )}

            {isCollapsed ? (
                <TooltipProvider>
                    <Tooltip>
                        <TooltipTrigger asChild>
                            {toggleButton}
                        </TooltipTrigger>
                        <TooltipContent side="right" className="ml-2">
                            <p>Expand sidebar</p>
                        </TooltipContent>
                    </Tooltip>
                </TooltipProvider>
            ) : (
                toggleButton
            )}
        </div>
    );
}