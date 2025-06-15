import { cn } from "@/lib/utils";

export const SIDEBAR_COLORS = {
    background: "bg-background/95 backdrop-blur-md",
    border: "border-gold-accent/20",
    borderLight: "border-gold-accent/10",
    borderHover: "border-gold-accent/30",

    goldLight: "bg-gold-light/10",
    goldLightActive: "bg-gold-light/20",
    goldAccentBorder: "border-gold-accent/20",

    primary: "text-primary",
    muted: "text-muted-foreground",
    foreground: "text-foreground",

    hoverBg: "hover:bg-gold-light/10",
    hoverText: "hover:text-primary",
    hoverBorder: "hover:border-gold-accent/20",
} as const;

export const SIDEBAR_ANIMATIONS = {
    transition: "transition-all duration-300",
    transitionEase: "transition-all duration-300 ease-in-out",
    transformCollapse: "group-data-[state=open]/collapsible:rotate-180",
} as const;

export const SIDEBAR_SIZES = {
    collapsed: "w-16",
    expanded: "w-64",
    avatar: "w-8 h-8",
    icon: "w-4 h-4",
    maxWidth: "max-w-[180px]",
} as const;

export const SIDEBAR_CLASSES = {
    container: cn(
        "fixed left-0 top-0 h-screen z-50",
        SIDEBAR_COLORS.background,
        "border-r",
        SIDEBAR_COLORS.border,
        SIDEBAR_ANIMATIONS.transition
    ),

    flexColumn: "flex flex-col h-full",
    flexCenter: "flex items-center justify-center",
    flexBetween: "flex items-center justify-between",
    flexStart: "flex items-center justify-start",

    separator: cn("mb-4", SIDEBAR_COLORS.borderLight),

    sectionHeader: cn(
        "flex items-center text-xs font-semibold uppercase tracking-wider w-full",
        SIDEBAR_COLORS.muted
    ),

    navItemBase: cn(
        "w-full justify-start",
        SIDEBAR_ANIMATIONS.transition
    ),

    navItemActive: cn(
        SIDEBAR_COLORS.primary,
        "font-semibold",
        SIDEBAR_COLORS.goldLightActive,
        "border",
        SIDEBAR_COLORS.borderHover,
        "shadow-sm gold-glow"
    ),

    navItemInactive: cn(
        SIDEBAR_COLORS.muted,
        SIDEBAR_COLORS.hoverText,
        SIDEBAR_COLORS.hoverBg,
        SIDEBAR_COLORS.hoverBorder
    ),

    avatar: cn(
        SIDEBAR_SIZES.avatar,
        "bg-transparent font-semibold border-2 border-primary",
        SIDEBAR_COLORS.primary
    ),

    userInfoExpanded: cn(
        "overflow-hidden whitespace-nowrap",
        SIDEBAR_ANIMATIONS.transitionEase,
        "max-w-[180px] opacity-100 ml-3"
    ),

    userInfoCollapsed: cn(
        "overflow-hidden whitespace-nowrap",
        SIDEBAR_ANIMATIONS.transitionEase,
        "max-w-0 opacity-0 ml-0"
    ),

    toggleButton: cn(
        "p-2",
        SIDEBAR_COLORS.hoverBg,
        SIDEBAR_COLORS.muted,
        SIDEBAR_COLORS.hoverText
    ),
} as const;

export const getSidebarWidth = (isCollapsed: boolean) =>
    isCollapsed ? SIDEBAR_SIZES.collapsed : SIDEBAR_SIZES.expanded;

export const getFlexLayout = (isCollapsed: boolean) =>
    isCollapsed ? SIDEBAR_CLASSES.flexCenter : SIDEBAR_CLASSES.flexBetween;

export const getNavItemClasses = (active: boolean, isCollapsed: boolean) => cn(
    SIDEBAR_CLASSES.navItemBase,
    active ? SIDEBAR_CLASSES.navItemActive : SIDEBAR_CLASSES.navItemInactive,
    isCollapsed ? "justify-center px-3" : "gap-3 px-3"
);

export const getUserInfoClasses = (isCollapsed: boolean) =>
    isCollapsed ? SIDEBAR_CLASSES.userInfoCollapsed : SIDEBAR_CLASSES.userInfoExpanded;

export const getThemeToggleLayout = (isCollapsed: boolean) =>
    cn("flex items-center mb-3", isCollapsed ? "justify-center" : "gap-3");

export const getUserProfileLayout = (isCollapsed: boolean) =>
    cn("flex items-center", isCollapsed ? "justify-center" : "gap-3");

export const TOOLTIP_CONFIG = {
    side: "right" as const,
    className: "ml-2",
} as const;