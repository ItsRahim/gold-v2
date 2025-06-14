import './index.css'
import * as React from "react";
import {Button} from "@/components/ui/button"
import {Plus, Download, Settings, Heart} from "lucide-react"
import {Switch} from "@/components/ui/switch.tsx";
import {Label} from "@/components/ui/label.tsx";
import {useEffect} from "react";

function App() {
    const [isDarkMode, setIsDarkMode] = React.useState(true);

    useEffect(() => {
        const storedDark = localStorage.getItem("darkMode");
        const preference = storedDark ? JSON.parse(storedDark) : true;

        setIsDarkMode(preference);
        document.documentElement.classList.toggle('dark', preference)

        if (!storedDark) {
            localStorage.setItem('darkMode', JSON.stringify(true))
        }
    }, [])

    const toggleDarkMode = () => {
        const newDarkMode = !isDarkMode
        setIsDarkMode(newDarkMode)

        document.documentElement.classList.toggle('dark', newDarkMode)
        localStorage.setItem('darkMode', JSON.stringify(newDarkMode))
    }

    return (
        <div className="min-h-screen bg-background p-8">
            <div className="max-w-4xl mx-auto space-y-8">
                <div className="text-center space-y-4">
                    <h1 className="text-4xl font-bold text-foreground">
                        shadcn/ui + Tailwind v4 Setup
                    </h1>
                    <p className="text-muted-foreground">
                        Testing your custom yellow/gold theme
                    </p>
                </div>

                <div className="grid gap-6">
                    {/* Button Variants */}
                    <div className="space-y-4">
                        <h2 className="text-2xl font-semibold">Button Variants</h2>
                        <div className="flex flex-wrap gap-4">
                            <Button>
                                <Plus/>
                                Default Button
                            </Button>
                            <Button variant="secondary">
                                <Download/>
                                Secondary
                            </Button>
                            <Button variant="outline">
                                <Settings/>
                                Outline
                            </Button>
                            <Button variant="ghost">
                                Ghost Button
                            </Button>
                            <Button variant="destructive">
                                Destructive
                            </Button>
                            <Button variant="link">
                                Link Button
                            </Button>
                        </div>
                    </div>

                    {/* Button Sizes */}
                    <div className="space-y-4">
                        <h2 className="text-2xl font-semibold">Button Sizes</h2>
                        <div className="flex items-center gap-4">
                            <Button size="sm">Small</Button>
                            <Button size="default">Default</Button>
                            <Button size="lg">Large</Button>
                            <Button size="icon">
                                <Heart/>
                            </Button>
                        </div>
                    </div>

                    {/* Color Test */}
                    <div className="space-y-4">
                        <h2 className="text-2xl font-semibold">Custom Theme Colors</h2>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            <div className="bg-primary text-primary-foreground p-4 rounded-lg text-center">
                                Primary
                            </div>
                            <div className="bg-secondary text-secondary-foreground p-4 rounded-lg text-center">
                                Secondary
                            </div>
                            <div className="bg-accent text-accent-foreground p-4 rounded-lg text-center">
                                Accent
                            </div>
                            <div className="bg-muted text-muted-foreground p-4 rounded-lg text-center">
                                Muted
                            </div>
                        </div>
                    </div>

                    {/* Card Example */}
                    <div className="space-y-4">
                        <h2 className="text-2xl font-semibold">Card Example</h2>
                        <div className="bg-card text-card-foreground p-6 rounded-lg border border-border shadow-sm">
                            <h3 className="text-lg font-semibold mb-2">Card Title</h3>
                            <p className="text-muted-foreground mb-4">
                                This is a card component using your custom theme colors.
                            </p>
                            <Button>
                                <Plus/>
                                Action Button
                            </Button>
                        </div>
                    </div>

                    {/* Dark Mode Toggle */}
                    <div className="space-y-4">
                        <h2 className="text-2xl font-semibold">Dark Mode</h2>
                        <div className="flex items-center space-x-2">
                            <Switch
                                id="dark-mode"
                                checked={isDarkMode}
                                onCheckedChange={toggleDarkMode}
                            />
                            <Label htmlFor="dark-mode">Dark Mode</Label>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default App