import * as React from "react";
import "@/index.css";
import {SidebarProvider, SidebarTrigger} from "./ui/sidebar";
import {AppSidebar} from "@/components/Navigation/AppSidebar.tsx";

export default function RootLayout({children}: { children: React.ReactNode }) {
    return (
        <SidebarProvider>
            <AppSidebar />
            <main>
                <SidebarTrigger />
                {children}
            </main>
        </SidebarProvider>
    );
}
