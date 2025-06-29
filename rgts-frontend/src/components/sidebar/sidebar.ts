import { BarChart3, Package, Bell, TrendingUp, Settings, LogOut } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

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
    title: 'Overview',
    items: [
      { name: 'Dashboard', href: '/dashboard', active: false, icon: BarChart3 },
      { name: 'Portfolio', href: '/portfolio', active: false, icon: TrendingUp },
      { name: 'Catalog', href: '/catalog', active: false, icon: Package },
      { name: 'Alerts', href: '/alerts', active: false, icon: Bell },
    ],
  },
  {
    title: 'Account',
    items: [
      { name: 'Settings', href: '/settings', active: false, icon: Settings },
      { name: 'Logout', href: '/logout', active: false, icon: LogOut },
    ],
  },
];
