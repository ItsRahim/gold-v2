import {useEffect, useState} from "react";
import type {GoldItem, GoldType} from "@/app/catalog/catalogTypes.ts";
import {addGoldType, getAllGoldTypes} from "@/services/gold.ts";
import {showToast, TOAST_TYPES} from "@/components/shared/ToastNotification.ts";
import {PageHeader} from "@/components/shared/PageHeader.tsx";
import {AddGoldDialog} from "@/app/catalog/AddGoldTypeDialog.tsx";
import {RefreshButton} from "@/components/shared/RefreshButton.tsx";
import {GoldTypeCard} from "@/app/catalog/GoldTypeCard.tsx";

export default function CatalogManagementView() {
    const [goldItems, setGoldItems] = useState<GoldItem[]>([]);
    const [open, setOpen] = useState(false);

    useEffect(() => {
        fetchGoldItems();
    }, []);

    function fetchGoldItems() {
        getAllGoldTypes()
            .then((res) => {
                setGoldItems(res.content);
            })
            .catch((err) => {
                console.error('Failed to fetch gold types:', err);
            });
    }

    async function handleAddGoldType(goldType: GoldType) {
        const result = await addGoldType(goldType);

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
        try {
            setGoldItems((prev) => prev.filter((item) => item.id !== id));
            showToast(TOAST_TYPES.SUCCESS, `Gold type "${name}" deleted successfully!`);
        } catch (error) {
            console.error('Failed to delete gold type:', error);
            showToast(TOAST_TYPES.ERROR, 'Failed to delete gold type. Please try again.');
        }
    }

    return (
        <div className='p-6 space-y-6'>
            <PageHeader
                title='Gold Market'
                actions={
                    <div className='flex space-x-2'>
                        <AddGoldDialog open={open} setOpen={setOpen} onSubmit={handleAddGoldType} />
                        <RefreshButton onClick={fetchGoldItems} label={'Refresh Gold Items'} />
                    </div>
                }
            />
            <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
                {goldItems.map((item) => (
                    <GoldTypeCard key={item.id} item={item} onDelete={handleDeleteGoldType} />
                ))}
            </div>
        </div>
    );
}
