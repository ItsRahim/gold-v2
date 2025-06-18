import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card.tsx';
import type { GoldTypeCardProps } from '@/app/catalog/catalogTypes.ts';
import DeleteIcon from '@/components/shared/DeleteIcon';
import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

export function GoldTypeCard({ item, onDelete }: GoldTypeCardProps) {
  const [showConfirm, setShowConfirm] = useState<boolean>(false);

  const handleDeleteClick = () => {
    setShowConfirm(true);
  };

  const handleConfirm = () => {
    onDelete(item.id, item.name);
    setShowConfirm(false);
  };

  const handleCancel = () => {
    setShowConfirm(false);
  };

  return (
    <>
      <Card key={item.id} className='shadow-md hover:shadow-lg transition-shadow'>
        <CardHeader className='space-y-1 flex flex-row items-start justify-between'>
          <div>
            <CardTitle className='text-lg font-semibold text-gold-dark'>{item.name}</CardTitle>
            <p className='text-sm text-muted-foreground'>{item.description}</p>
          </div>
          <span className='group'>
            <DeleteIcon onClick={handleDeleteClick} />
          </span>
        </CardHeader>
        <CardContent className='text-sm space-y-1'>
          <p>
            <span className='font-medium text-muted-foreground'>Purity:</span> {item.purity}
          </p>
          <p>
            <span className='font-medium text-muted-foreground'>Weight:</span> {item.weight}
          </p>
          <p className='text-gold-medium font-bold'>£{item.price.toFixed(2)}</p>
        </CardContent>
      </Card>
      <Dialog open={showConfirm} onOpenChange={setShowConfirm}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Are you sure you want to delete "{item.name}"?</DialogTitle>
          </DialogHeader>
          <p>This action cannot be undone.</p>
          <DialogFooter>
            <Button variant='outline' onClick={handleCancel}>
              Cancel
            </Button>
            <Button variant='destructive' onClick={handleConfirm}>
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
