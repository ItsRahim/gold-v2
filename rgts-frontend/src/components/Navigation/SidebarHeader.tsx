import { Menu } from "lucide-react";

interface SidebarHeaderProps {
    isCollapsed: boolean;
    onToggle: () => void;
}

export function SidebarHeader({ isCollapsed, onToggle }: SidebarHeaderProps) {
    return (
        <div className={`flex items-center p-4 border-b border-gold-accent/10 ${
            isCollapsed ? 'justify-center' : 'justify-between'
        }`}>
            {!isCollapsed && (
                <div className="flex items-center gap-2">
                    <div className="text-lg font-semibold">
                        <span className="text-primary gold-text-glow">Rahim's</span>
                        <span className="font-normal text-muted-foreground ml-1 text-sm">Gold Tracker</span>
                    </div>
                </div>
            )}
            <button
                onClick={onToggle}
                className="p-2 rounded-md hover:bg-gold-light/10 transition-colors duration-200 text-muted-foreground hover:text-primary"
                aria-label="Toggle sidebar"
            >
                <Menu className="w-4 h-4" />
            </button>
        </div>
    );
}
