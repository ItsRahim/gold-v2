import { Button } from '@/components/ui/button';
import { RefreshCcw } from 'lucide-react';

type RefreshButtonProps = {
  onClick: () => void;
  label?: string;
};

export function RefreshButton({ onClick, label = 'Refresh' }: RefreshButtonProps) {
  return (
    <Button onClick={onClick} aria-label={label} title={label} variant='outline'>
      {<RefreshCcw className='w-4 h-4' />}
    </Button>
  );
}
