import {Link} from "react-router-dom";
import type {LucideIcon} from "lucide-react";

interface NavigationItemProps {
    name: string;
    href: string;
    active?: boolean;
    icon: LucideIcon;
    isCollapsed: boolean;
}

export function NavigationItem({name, href, active = false, icon: IconComponent, isCollapsed}: NavigationItemProps) {
    return (
        <Link
            to={href}
            className={`flex items-center w-full rounded-lg transition-all duration-300 ${
                active
                    ? "text-primary font-semibold bg-gold-light/20 border border-gold-accent/30"
                    : "text-muted-foreground hover:text-primary hover:bg-gold-light/10 hover:border-gold-accent/20 border border-transparent"
            } ${
                active ? 'shadow-sm gold-glow' : ''
            } ${
                isCollapsed ? 'justify-center px-3 py-2.5' : 'gap-3 px-3 py-2.5'
            }`}
            title={isCollapsed ? name : ''}
        >
            <IconComponent className="w-4 h-4 flex-shrink-0"/>
            {!isCollapsed && (
                <span className="text-sm">{name}</span>
            )}
        </Link>
    );
}