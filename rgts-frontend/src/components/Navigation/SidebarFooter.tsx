import {Avatar, AvatarFallback} from "@radix-ui/react-avatar";
import {DarkMode} from "@/components/Utility/DarkMode.tsx";

interface SidebarFooterProps {
    isCollapsed: boolean;
}

export function SidebarFooter({isCollapsed}: SidebarFooterProps) {
    return (
        <div className="p-4 border-t border-gold-accent/10">
            {/* Theme Toggle */}
            <div className={`flex items-center mb-3 ${
                isCollapsed ? 'justify-center' : 'gap-3'
            }`}>
                <DarkMode/>
                {!isCollapsed && (
                    <span className="text-sm text-muted-foreground">Theme</span>
                )}
            </div>

            {/* User Profile */}
            <div className={`flex items-center mb-3 ${
                isCollapsed ? 'justify-center' : 'gap-3'
            }`}>
                <Avatar>
                    <AvatarFallback
                        className="bg-transparent text-primary font-semibold rounded-full w-8 h-8 flex items-center justify-center border-2 border-primary">
                        RT
                    </AvatarFallback>
                </Avatar>
                {!isCollapsed && (
                    <div className="flex flex-col">
                        <span className="text-sm font-medium text-foreground">Rahim T.</span>
                        <span className="text-xs text-muted-foreground">Gold Trader</span>
                    </div>
                )}
            </div>
        </div>
    );
}