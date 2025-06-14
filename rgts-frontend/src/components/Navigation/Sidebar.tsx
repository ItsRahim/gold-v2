import {useState} from "react";
import {BarChart3, Package, Briefcase, Bell, TrendingUp, Settings, LogOut} from "lucide-react";
import {SidebarHeader} from "./SidebarHeader";
import {NavigationSection} from "./NavigationSection";
import {SidebarFooter} from "./SidebarFooter";

export function Sidebar() {
    const [isCollapsed, setIsCollapsed] = useState(false);

    const navigationSections = [
        {
            title: "Overview",
            items: [
                {name: "Dashboard", href: "/", active: true, icon: BarChart3},
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

    return (
        <aside
            className={`fixed left-0 top-0 h-screen bg-background/95 backdrop-blur-md border-r border-gold-accent/20 transition-all duration-300 z-50 ${
                isCollapsed ? 'w-16' : 'w-64'
            }`}>
            <div className="flex flex-col h-full">
                <SidebarHeader
                    isCollapsed={isCollapsed}
                    onToggle={() => setIsCollapsed(!isCollapsed)}
                />

                <nav className="flex-1 p-4 overflow-y-auto">
                    {navigationSections.map((section, sectionIndex) => (
                        <div key={section.title} className={sectionIndex > 0 ? 'mt-8' : ''}>
                            <NavigationSection
                                title={section.title}
                                items={section.items}
                                isCollapsed={isCollapsed}
                                showSeparator={sectionIndex < navigationSections.length - 1}
                            />
                        </div>
                    ))}
                </nav>

                <SidebarFooter isCollapsed={isCollapsed}/>
            </div>
        </aside>
    );
}