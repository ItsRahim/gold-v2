import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card.tsx';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar.tsx';
import type { GoldTypeCardProps } from '@/app/catalog/catalogTypes.ts';
import DeleteIcon from '@/components/shared/DeleteIcon';
import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

export function GoldTypeCard({ item, onDelete }: GoldTypeCardProps) {
  const [showConfirm, setShowConfirm] = useState<boolean>(false);
  const [showImageDialog, setShowImageDialog] = useState<boolean>(false);

  const handleDeleteClick = () => {
    setShowConfirm(true);
  };

  const handleImageClick = () => {
    setShowImageDialog(true);
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
      <Card className='h-full flex flex-col shadow-md hover:shadow-lg transition-all duration-200 hover:scale-[1.02]'>
        <CardHeader className='flex-shrink-0 pb-4'>
          <div className='flex items-start justify-between gap-3'>
            <div className='flex items-center gap-3 min-w-0 flex-1'>
              <Avatar
                className='w-12 h-12 flex-shrink-0 cursor-pointer hover:ring-2 hover:ring-gold-medium transition-all duration-200'
                onClick={handleImageClick}
              >
                <AvatarImage src={item.imageUrl} alt={item.name} className='object-cover' />
                <AvatarFallback className='bg-muted text-muted-foreground font-semibold'>{item.name.charAt(0).toUpperCase()}</AvatarFallback>
              </Avatar>
              <div className='min-w-0 flex-1'>
                <CardTitle className='text-lg font-semibold text-gold-dark line-clamp-1'>{item.name}</CardTitle>
              </div>
            </div>
            <div className='flex-shrink-0 pt-1'>
              <span className='opacity-60 hover:opacity-100 transition-opacity cursor-pointer'>
                <DeleteIcon onClick={handleDeleteClick} />
              </span>
            </div>
          </div>
        </CardHeader>

        <CardContent className='flex-1 flex flex-col justify-between'>
          <div className='space-y-3 mb-4'>
            <div className='flex justify-between items-center'>
              <span className='text-sm font-medium text-muted-foreground'>Purity:</span>
              <span className='text-sm font-semibold'>{item.purity}</span>
            </div>
            <div className='flex justify-between items-center'>
              <span className='text-sm font-medium text-muted-foreground'>Weight:</span>
              <span className='text-sm font-semibold'>{item.weight}</span>
            </div>
          </div>

          <div className='border-t pt-4 mt-auto'>
            <p className='text-xl font-bold text-gold-medium text-center'>£{item.price.toFixed(2)}</p>
          </div>
        </CardContent>
      </Card>

      <Dialog open={showConfirm} onOpenChange={setShowConfirm}>
        <DialogContent className='sm:max-w-[425px]'>
          <DialogHeader>
            <DialogTitle className='text-left'>Delete Gold Type</DialogTitle>
          </DialogHeader>
          <div className='py-4'>
            <p className='text-sm text-muted-foreground'>
              Are you sure you want to delete <span className='font-semibold text-foreground'>"{item.name}"</span>?
            </p>
            <p className='text-sm text-muted-foreground mt-2'>This action cannot be undone.</p>
          </div>
          <DialogFooter className='gap-2'>
            <Button variant='outline' onClick={handleCancel}>
              Cancel
            </Button>
            <Button variant='destructive' onClick={handleConfirm}>
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Image Expansion Dialog */}
      <Dialog open={showImageDialog} onOpenChange={setShowImageDialog}>
        <DialogContent className='sm:max-w-2xl p-0 overflow-hidden'>
          <DialogHeader className='p-6 pb-0'>
            <DialogTitle className='text-left'>{item.name}</DialogTitle>
          </DialogHeader>
          <div className='px-6 pb-6'>
            <div className='relative aspect-square w-full bg-muted rounded-lg overflow-hidden'>
              {item.imageUrl ? (
                <img src={item.imageUrl} alt={item.name} className='w-full h-full object-cover' />
              ) : (
                <div className='w-full h-full flex items-center justify-center bg-muted'>
                  <span className='text-6xl font-semibold text-muted-foreground'>{item.name.charAt(0).toUpperCase()}</span>
                </div>
              )}
            </div>
            <div className='mt-4 space-y-2'>
              <p className='text-sm text-muted-foreground'>{item.description}</p>
              <div className='flex justify-between text-sm'>
                <span>
                  <strong>Purity:</strong> {item.purity}
                </span>
                <span>
                  <strong>Weight:</strong> {item.weight}
                </span>
              </div>
              <p className='text-2xl font-bold text-gold-medium text-center pt-2'>£{item.price.toFixed(2)}</p>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
