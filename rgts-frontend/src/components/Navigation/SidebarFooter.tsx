import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { DarkMode } from "@/components/Utility/DarkMode.tsx";

interface SidebarFooterProps {
    isCollapsed: boolean;
}

export function SidebarFooter({ isCollapsed }: SidebarFooterProps) {
    return (
        <div className="p-4">
            <Separator className="mb-4 bg-gold-accent/10" />

            {/* Theme Toggle */}
            <div className={`flex items-center mb-3 ${isCollapsed ? 'justify-center' : 'gap-3'}`}>
                {isCollapsed ? (
                    <TooltipProvider>
                        <Tooltip>
                            <TooltipTrigger asChild>
                                <div>
                                    <DarkMode />
                                </div>
                            </TooltipTrigger>
                            <TooltipContent side="right" className="ml-2">
                                <p>Toggle Theme</p>
                            </TooltipContent>
                        </Tooltip>
                    </TooltipProvider>
                ) : (
                    <>
                        <DarkMode />
                        <span className="text-sm text-muted-foreground">Theme</span>
                    </>
                )}
            </div>

            {/* User Profile */}
            <div className={`flex items-center ${isCollapsed ? 'justify-center' : 'gap-3'}`}>
                {isCollapsed ? (
                    <TooltipProvider>
                        <Tooltip>
                            <TooltipTrigger asChild>
                                <Avatar className="w-8 h-8">
                                    <AvatarFallback className="bg-transparent text-primary font-semibold border-2 border-primary">
                                        FL
                                    </AvatarFallback>
                                </Avatar>
                            </TooltipTrigger>
                            <TooltipContent side="right" className="ml-2">
                                <div className="text-center">
                                    <p className="font-medium">First L.</p>
                                    <p className="text-xs text-muted-foreground">Username</p>
                                </div>
                            </TooltipContent>
                        </Tooltip>
                    </TooltipProvider>
                ) : (
                    <>
                        <Avatar className="w-8 h-8">
                            <AvatarFallback className="bg-transparent text-primary font-semibold border-2 border-primary">
                                FL
                            </AvatarFallback>
                        </Avatar>
                        <div className="flex flex-col">
                            <span className="text-sm font-medium text-foreground">First L.</span>
                            <span className="text-xs text-muted-foreground">Username</span>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}