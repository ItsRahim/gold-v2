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
            {name: "Dashboard", href: "/", active: false, icon: BarChart3},
            {name: "Catalog", href: "/", active: false, icon: Package},
            {name: "Portfolio", href: "/", active: false, icon: Briefcase},
            {name: "Market", href: "/", active: false, icon: TrendingUp},
            {name: "Alerts", href: "/", active: false, icon: Bell},
        ]
    },
    {
        title: "Account",
        items: [
            {name: "Settings", href: "/", active: false, icon: Settings},
            {name: "Logout", href: "/", active: false, icon: LogOut},
        ]
    }
];