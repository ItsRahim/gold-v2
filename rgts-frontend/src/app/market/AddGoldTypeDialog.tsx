import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { PlusIcon } from 'lucide-react';
import { type FormEvent, useRef, useState } from 'react';
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from '@/components/ui/select';
import { Purities, WeightUnits } from '@/app/market/marketConstants.ts';
import type { GoldType } from '@/app/market/marketTypes.ts';

interface AddGoldDialogProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  onSubmit: (goldType: GoldType) => void;
}

function allGoldPurities() {
  return (
    <>
      {Purities.map((purity) => (
        <SelectItem key={purity} value={purity}>
          {purity}
        </SelectItem>
      ))}
    </>
  );
}

export function AddGoldDialog({ open, setOpen, onSubmit }: AddGoldDialogProps) {
  const [purity, setPurity] = useState<string>('');
  const [weightUnit, setWeightUnit] = useState<string>('');
  const formRef = useRef<HTMLFormElement>(null);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = event.currentTarget;
    const formData = new FormData(form);

    const goldType: GoldType = {
      name: formData.get('name') as string,
      weight: parseFloat(formData.get('weight') as string),
      purity: purity,
      unit: weightUnit,
      description: formData.get('description') as string,
    };

    onSubmit(goldType);
    handleReset();
    setOpen(false);
  }

  function handleReset() {
    setPurity('');
    setWeightUnit('');

    if (formRef.current) {
      formRef.current.reset();
    }
  }

  function handleCancel() {
    handleReset();
    setOpen(false);
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusIcon className='w-4 h-4' />
          Add
        </Button>
      </DialogTrigger>
      <DialogContent className='sm:max-w-[425px]'>
        <DialogHeader>
          <DialogTitle>Add Gold Item</DialogTitle>
          <DialogDescription>Enter gold type details below.</DialogDescription>
        </DialogHeader>
        <form ref={formRef} className='grid gap-4' onSubmit={handleSubmit}>
          <div className='grid gap-2'>
            <Label htmlFor='name'>Name</Label>
            <Input id='name' name='name' required />
          </div>

          <div className='grid gap-2'>
            <Label htmlFor='purity'>Purity</Label>
            <Select value={purity} onValueChange={setPurity} required>
              <SelectTrigger id='purity' className='w-full'>
                <SelectValue placeholder='Select purity' />
              </SelectTrigger>
              <SelectContent>{allGoldPurities()}</SelectContent>
            </Select>
          </div>

          <div className='grid gap-2'>
            <Label htmlFor='weight'>Weight</Label>
            <Input id='weight' name='weight' type='number' step='0.01' required />
          </div>

          <div className='grid gap-2'>
            <Label htmlFor='weightUnit'>Weight Unit</Label>
            <Select value={weightUnit} onValueChange={setWeightUnit} required>
              <SelectTrigger id='weightUnit' className='w-full'>
                <SelectValue placeholder='Select weight unit' />
              </SelectTrigger>
              <SelectContent>
                {WeightUnits.map(({ label, value }) => (
                  <SelectItem key={value} value={value}>
                    {label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className='grid gap-2'>
            <Label htmlFor='description'>Description</Label>
            <Input id='description' name='description' required />
          </div>

          <DialogFooter>
            <DialogClose asChild>
              <Button type='button' variant='outline' onClick={handleCancel}>
                Cancel
              </Button>
            </DialogClose>
            <Button type='submit'>Save</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
