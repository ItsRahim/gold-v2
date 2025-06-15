import {useState} from "react";
import {SidebarHeader} from "./SidebarHeader";
import {SidebarFooter} from "./SidebarFooter";
import {SidebarContent} from "@/components/sidebar/SidebarContent.tsx";
import {SIDEBAR_CLASSES, getSidebarWidth} from "@/styles/sidebar";
import {cn} from "@/lib/utils";

export function AppSidebar() {
    const [isCollapsed, setIsCollapsed] = useState(false);

    return (
        <aside className={cn(SIDEBAR_CLASSES.container, getSidebarWidth(isCollapsed))}>
            <div className={SIDEBAR_CLASSES.flexColumn}>
                <SidebarHeader name="Rahim A"
                               initials="RA"
                               username="ItsRahim"
                               isCollapsed={isCollapsed}
                               onToggle={() => setIsCollapsed(!isCollapsed)}/>
                <SidebarContent isCollapsed={isCollapsed}/>
                <SidebarFooter isCollapsed={isCollapsed}
                />
            </div>
        </aside>
    );
}