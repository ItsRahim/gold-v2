import { Button } from '@/components/ui/button';
import { RefreshCcw } from 'lucide-react';

type RefreshButtonProps = {
  onClick: () => void;
  label?: string;
  disabled?: boolean;
  isLoading?: boolean;
};

export function RefreshButton({ onClick, label = 'Refresh', disabled = false, isLoading = false }: RefreshButtonProps) {
  return (
    <Button onClick={onClick} aria-label={label} title={label} variant='outline' disabled={disabled || isLoading}>
      <RefreshCcw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
    </Button>
  );
}
