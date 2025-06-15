import { SidebarGroup, SidebarGroupContent, SidebarGroupLabel } from "@/components/ui/sidebar.tsx";
import { NAVIGATION_SECTIONS } from "@/components/Navigation/navigation.ts";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible.tsx";
import { ChevronDown } from "lucide-react";
import { SidebarItem } from "@/components/Navigation/SidebarItem.tsx";
import { SIDEBAR_CLASSES } from "@/styles/sidebar";

interface SidebarContentProps {
    isCollapsed: boolean;
}

export function SidebarContent({ isCollapsed }: SidebarContentProps) {
    return (
        <SidebarGroupContent className="flex-1 p-4 overflow-y-auto space-y-6">
            {NAVIGATION_SECTIONS.map((navigationSection) => (
                <Collapsible
                    key={navigationSection.title}
                    defaultOpen
                    className="group/collapsible"
                >
                    <SidebarGroup>
                        {!isCollapsed && (
                            <SidebarGroupLabel asChild>
                                <CollapsibleTrigger className={SIDEBAR_CLASSES.sectionHeader}>
                                    {navigationSection.title}
                                    <ChevronDown className="ml-auto h-4 w-4 transition-transform group-data-[state=open]/collapsible:rotate-180" />
                                </CollapsibleTrigger>
                            </SidebarGroupLabel>
                        )}

                        <CollapsibleContent>
                            <SidebarGroupContent className="space-y-1 mt-2">
                                {navigationSection.items.map((item) => (
                                    <SidebarItem
                                        key={item.name}
                                        name={item.name}
                                        href={item.href}
                                        active={item.active}
                                        icon={item.icon}
                                        isCollapsed={isCollapsed}
                                    />
                                ))}
                            </SidebarGroupContent>
                        </CollapsibleContent>
                    </SidebarGroup>
                </Collapsible>
            ))}
        </SidebarGroupContent>
    );
}