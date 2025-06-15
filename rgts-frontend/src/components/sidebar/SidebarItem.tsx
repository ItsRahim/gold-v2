import {Link, useLocation} from "react-router-dom";
import type { LucideIcon } from "lucide-react";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { SidebarMenuButton } from "../ui/sidebar";
import { getNavItemClasses, TOOLTIP_CONFIG } from "@/styles/sidebar";

interface SidebarItemProps {
    name: string;
    href: string;
    active?: boolean;
    icon: LucideIcon;
    isCollapsed: boolean;
}

export function SidebarItem({ name, href, icon: IconComponent, isCollapsed }: SidebarItemProps) {
    const location = useLocation();
    const isActive = location.pathname === href;

    const buttonContent = (
        <SidebarMenuButton
            asChild
            className={getNavItemClasses(isActive, isCollapsed)}
        >
            <Link to={href}>
                <IconComponent className="w-4 h-4 flex-shrink-0" />
                {!isCollapsed && <span className="text-sm">{name}</span>}
            </Link>
        </SidebarMenuButton>
    );

    if (isCollapsed) {
        return (
            <TooltipProvider>
                <Tooltip>
                    <TooltipTrigger asChild>
                        {buttonContent}
                    </TooltipTrigger>
                    <TooltipContent side={TOOLTIP_CONFIG.side} className={TOOLTIP_CONFIG.className}>
                        <p>{name}</p>
                    </TooltipContent>
                </Tooltip>
            </TooltipProvider>
        );
    }

    return buttonContent;
}