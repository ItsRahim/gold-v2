import {BarChart3, Package, Briefcase, Bell, TrendingUp, Settings, LogOut} from "lucide-react";
import type {LucideIcon} from "lucide-react";

export interface NavigationItemData {
    name: string;
    href: string;
    active?: boolean;
    icon: LucideIcon;
}

export interface NavigationSection {
    title: string;
    items: NavigationItemData[];
}

export const NAVIGATION_SECTIONS: NavigationSection[] = [
    {
        title: "Overview",
        items: [
            {name: "Dashboard", href: "/", icon: BarChart3},
            {name: "Catalog", href: "/", icon: Package},
            {name: "Portfolio", href: "/", icon: Briefcase},
            {name: "Market", href: "/", icon: TrendingUp},
            {name: "Alerts", href: "/", icon: Bell},
        ]
    },
    {
        title: "Account",
        items: [
            {name: "Settings", href: "/", icon: Settings},
            {name: "Logout", href: "/", icon: LogOut},
        ]
    }
];