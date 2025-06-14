import { Routes, Route } from "react-router-dom";
import RootLayout from "./components/RouteLayout.tsx";

export default function App() {
    return (
        <RootLayout>
            <Routes>
                <Route path="/" />
            </Routes>
        </RootLayout>
    );
}
