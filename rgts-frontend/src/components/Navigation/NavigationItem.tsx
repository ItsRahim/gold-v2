import { Link } from "react-router-dom";
import type { LucideIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { cn } from "@/lib/utils";

interface NavigationItemProps {
    name: string;
    href: string;
    active?: boolean;
    icon: LucideIcon;
    isCollapsed: boolean;
}

export function NavigationItem({ name, href, active = false, icon: IconComponent, isCollapsed }: NavigationItemProps) {
    const buttonContent = (
        <Button
            asChild
            variant={active ? "default" : "ghost"}
            className={cn(
                "w-full justify-start transition-all duration-300",
                active
                    ? "text-primary font-semibold bg-gold-light/20 border border-gold-accent/30 shadow-sm gold-glow"
                    : "text-muted-foreground hover:text-primary hover:bg-gold-light/10 hover:border-gold-accent/20",
                isCollapsed ? "justify-center px-3" : "gap-3 px-3"
            )}
        >
            <Link to={href}>
                <IconComponent className="w-4 h-4 flex-shrink-0" />
                {!isCollapsed && <span className="text-sm">{name}</span>}
            </Link>
        </Button>
    );

    if (isCollapsed) {
        return (
            <TooltipProvider>
                <Tooltip>
                    <TooltipTrigger asChild>
                        {buttonContent}
                    </TooltipTrigger>
                    <TooltipContent side="right" className="ml-2">
                        <p>{name}</p>
                    </TooltipContent>
                </Tooltip>
            </TooltipProvider>
        );
    }

    return buttonContent;
}