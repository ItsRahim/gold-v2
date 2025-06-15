import { Button } from '@/components/ui/button.tsx';
import { Moon, Sun } from 'lucide-react';
import * as React from 'react';
import { useEffect } from 'react';

export function DarkMode() {
  const [isDarkMode, setIsDarkMode] = React.useState(true);

  useEffect(() => {
    document.documentElement.classList.toggle('dark', isDarkMode);
  }, [isDarkMode]);

  const toggleDarkMode = () => {
    const newDarkMode = !isDarkMode;
    setIsDarkMode(newDarkMode);
    document.documentElement.classList.toggle('dark', newDarkMode);
  };

  return (
    <div className='flex items-center space-x-2'>
      <Button
        variant='outline'
        size='icon'
        aria-pressed={isDarkMode}
        aria-label='Toggle dark mode'
        onClick={toggleDarkMode}
        className={`border-gold-accent/40 hover:border-primary/60 hover:bg-gold-light/20 transition-all duration-300 ${
          isDarkMode ? 'bg-gold-light/10 text-gold-medium hover:text-primary' : 'hover:text-primary'
        } hover:gold-glow`}
      >
        {isDarkMode ? (
          <Sun className='w-5 h-5 text-gold-medium transition-colors duration-300' />
        ) : (
          <Moon className='w-5 h-5 text-gold-dark transition-colors duration-300' />
        )}
      </Button>
    </div>
  );
}
