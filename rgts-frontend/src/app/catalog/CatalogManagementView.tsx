import { useEffect, useState } from 'react';
import type { AddGoldTypeRequest } from '@/app/catalog/catalogTypes.ts';
import { PageHeader } from '@/components/shared/PageHeader.tsx';
import { AddGoldDialog } from '@/app/catalog/AddGoldTypeDialog.tsx';
import { RefreshButton } from '@/components/shared/RefreshButton.tsx';
import { GoldTypeCard } from '@/app/catalog/GoldTypeCard.tsx';
import { useGoldTypeStore } from '@/stores/GoldTypeStore.ts';

export default function CatalogManagementView() {
  const [open, setOpen] = useState(false);

  const { goldTypes, isRefreshing, fetchGoldTypes, addGoldType, deleteGoldType, refreshGoldTypes } = useGoldTypeStore();

  useEffect(() => {
    fetchGoldTypes();
  }, [fetchGoldTypes]);

  const handleAddGoldType = async (goldType: AddGoldTypeRequest, file: File) => {
    await addGoldType(goldType, file);
  };

  const handleDeleteGoldType = async (id: number, name: string) => {
    await deleteGoldType(id, name);
  };

  const handleRefresh = () => {
    refreshGoldTypes();
  };

  const hasGoldItems = goldTypes && goldTypes.length > 0;

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
          {goldTypes.map((item) => (
            <GoldTypeCard key={item.id} item={item} onDelete={handleDeleteGoldType} />
          ))}
        </div>
      )}
    </div>
  );
}
