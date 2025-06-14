import * as React from "react";
import {Sidebar} from "@/components/Navigation/Sidebar.tsx";
import "@/index.css";

export default function RootLayout({children}: { children: React.ReactNode }) {
    return (
        <div className="flex flex-col min-h-screen antialiased">
            <header>
                <Sidebar/>
            </header>
            <main className="flex-1 w-full px-4">{children}</main>
        </div>
    );
}
