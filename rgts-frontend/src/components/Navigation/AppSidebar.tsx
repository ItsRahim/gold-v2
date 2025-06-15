import {useState} from "react";
import {SidebarHeader} from "./SidebarHeader";
import {SidebarFooter} from "./SidebarFooter";
import {SidebarContent} from "@/components/Navigation/SidebarContent.tsx";

export function AppSidebar() {
    const [isCollapsed, setIsCollapsed] = useState(false);

    return (
        <aside
            className={`fixed left-0 top-0 h-screen bg-background/95 backdrop-blur-md border-r border-gold-accent/20 transition-all duration-300 z-50 ${
                isCollapsed ? "w-16" : "w-64"
            }`}
        >
            <div className="flex flex-col h-full">
                <SidebarHeader
                    isCollapsed={isCollapsed}
                    onToggle={() => setIsCollapsed(!isCollapsed)}
                />
                <SidebarContent isCollapsed={isCollapsed}/>
                <SidebarFooter isCollapsed={isCollapsed}/>
            </div>
        </aside>
    );
}