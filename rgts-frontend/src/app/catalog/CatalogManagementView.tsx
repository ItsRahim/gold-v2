import { useEffect, useState } from 'react';
import type { GoldItem, AddGoldTypeRequest } from '@/app/catalog/catalogTypes.ts';
import { addGoldType, deleteGoldType, getAllGoldTypes } from '@/services/gold.ts';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification.ts';
import { PageHeader } from '@/components/shared/PageHeader.tsx';
import { AddGoldDialog } from '@/app/catalog/AddGoldTypeDialog.tsx';
import { RefreshButton } from '@/components/shared/RefreshButton.tsx';
import { GoldTypeCard } from '@/app/catalog/GoldTypeCard.tsx';

export default function CatalogManagementView() {
  const [goldItems, setGoldItems] = useState<GoldItem[]>([]);
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);

  useEffect(() => {
    fetchGoldItems();
  }, []);

  async function fetchGoldItems(isRefresh = false) {
    if (isRefresh) {
      setIsRefreshing(true);
    }

    try {
      const res = await getAllGoldTypes();
      setGoldItems(res.content);
    } catch (err) {
      console.error('Failed to fetch gold types:', err);
      showToast(TOAST_TYPES.ERROR, 'Failed to load gold items. Please try again.');
    } finally {
      setIsLoading(false);
      if (isRefresh) {
        setIsRefreshing(false);
      }
    }
  }

  async function handleAddGoldType(goldType: AddGoldTypeRequest, file: File) {
    const result = await addGoldType(goldType, file);

    if (result && 'name' in result) {
      fetchGoldItems();
      showToast(TOAST_TYPES.SUCCESS, `Gold type "${result.name}" added successfully!`);
    } else if (result && 'message' in result) {
      showToast(TOAST_TYPES.ERROR, result.message);
    } else {
      showToast(TOAST_TYPES.ERROR, 'Failed to add gold type. Please try again.');
    }
  }

  async function handleDeleteGoldType(id: number, name: string) {
    const result = await deleteGoldType(id.toString());

    if (result) {
      showToast(TOAST_TYPES.ERROR, `Failed to delete gold type: "${name}"`);
    } else {
      fetchGoldItems();
      showToast(TOAST_TYPES.SUCCESS, `Successfully deleted gold type "${name}"`);
    }
  }

  function handleRefresh() {
    fetchGoldItems(true);
  }

  if (isLoading) {
    return (
      <div className='p-6 space-y-6'>
        <PageHeader
          title='Gold Market'
          actions={
            <div className='flex space-x-2'>
              <AddGoldDialog open={open} setOpen={setOpen} onSubmit={handleAddGoldType} />
              <RefreshButton onClick={handleRefresh} label='Refresh Gold Items' disabled />
            </div>
          }
        />
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
          {Array.from({ length: 6 }).map((_, index) => (
            <div key={index} className='h-48 bg-muted rounded-lg animate-pulse' />
          ))}
        </div>
      </div>
    );
  }

  const hasGoldItems = goldItems && goldItems.length > 0;

  return (
    <div className='p-6 space-y-6'>
      <PageHeader
        title='Gold Market'
        actions={
          hasGoldItems && (
            <div className='flex space-x-2'>
              <AddGoldDialog open={open} setOpen={setOpen} onSubmit={handleAddGoldType} />
              <RefreshButton onClick={handleRefresh} label='Refresh Gold Items' disabled={isRefreshing} />
            </div>
          )
        }
      />

      {!hasGoldItems ? (
        <div className='flex flex-col items-center justify-center py-12 text-center'>
          <div className='text-6xl mb-4'>🪙</div>
          <h3 className='text-lg font-semibold text-muted-foreground mb-2'>No Gold Items Found</h3>
          <p className='text-sm text-muted-foreground mb-4'>Start building your gold catalog by adding your first item.</p>
          <AddGoldDialog open={open} setOpen={setOpen} onSubmit={handleAddGoldType} />
        </div>
      ) : (
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
          {goldItems.map((item) => (
            <GoldTypeCard key={item.id} item={item} onDelete={handleDeleteGoldType} />
          ))}
        </div>
      )}
    </div>
  );
}
