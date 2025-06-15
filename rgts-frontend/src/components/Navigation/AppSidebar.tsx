import {useState} from "react";
import {SidebarHeader} from "./SidebarHeader";
import {NavigationSection} from "./NavigationSection";
import {SidebarFooter} from "./SidebarFooter";
import {NAVIGATION_SECTIONS} from "@/components/Navigation/navigation.ts";

export function AppSidebar() {
    const [isCollapsed, setIsCollapsed] = useState(false);

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
                    {NAVIGATION_SECTIONS.map((section, sectionIndex) => (
                        <div key={section.title} className={sectionIndex > 0 ? 'mt-8' : ''}>
                            <NavigationSection
                                title={section.title}
                                items={section.items}
                                isCollapsed={isCollapsed}
                                showSeparator={sectionIndex < NAVIGATION_SECTIONS.length - 1}
                            />
                        </div>
                    ))}
                </nav>

                <SidebarFooter isCollapsed={isCollapsed}/>
            </div>
        </aside>
    );
}