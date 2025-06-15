import { NavigationItem } from "./NavigationItem";
import type { LucideIcon } from "lucide-react";
import { Separator } from "@/components/ui/separator";

interface NavigationItemData {
    name: string;
    href: string;
    active?: boolean;
    icon: LucideIcon;
}

interface NavigationSectionProps {
    title: string;
    items: NavigationItemData[];
    isCollapsed: boolean;
    showSeparator?: boolean;
}

export function NavigationSection({ title, items, isCollapsed, showSeparator = false }: NavigationSectionProps) {
    return (
        <div>
            {/* Section Title */}
            {!isCollapsed && (
                <div className="px-3 mb-3">
                    <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                        {title}
                    </h3>
                </div>
            )}

            {/* Section Items */}
            <div className="space-y-1">
                {items.map((item) => (
                    <NavigationItem
                        key={item.name}
                        name={item.name}
                        href={item.href}
                        active={item.active}
                        icon={item.icon}
                        isCollapsed={isCollapsed}
                    />
                ))}
            </div>

            {/* Section Separator */}
            {!isCollapsed && showSeparator && (
                <div className="mt-4">
                    <Separator className="bg-gold-accent/10" />
                </div>
            )}
        </div>
    );
}