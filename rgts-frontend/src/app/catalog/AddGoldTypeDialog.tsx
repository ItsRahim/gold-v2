import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog.tsx';
import { Button } from '@/components/ui/button.tsx';
import { Input } from '@/components/ui/input.tsx';
import { Label } from '@/components/ui/label.tsx';
import { PlusIcon, Upload, X } from 'lucide-react';
import { type FormEvent, useRef, useState } from 'react';
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from '@/components/ui/select.tsx';
import { Purities, WeightUnits } from '@/app/catalog/catalogConstants.ts';
import type { AddGoldTypeRequest } from '@/app/catalog/catalogTypes.ts';
import type { AddGoldDialogProps } from '@/app/catalog/catalogTypes.ts';

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
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const formRef = useRef<HTMLFormElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    // Validate that a file is selected
    if (!selectedFile) {
      alert('Please select an image file');
      return;
    }

    const form = event.currentTarget;
    const formData = new FormData(form);

    const goldType: AddGoldTypeRequest = {
      name: formData.get('name') as string,
      weight: parseFloat(formData.get('weight') as string),
      purity: purity,
      unit: weightUnit,
      description: formData.get('description') as string,
    };

    onSubmit(goldType, selectedFile);
    handleReset();
    setOpen(false);
  }

  function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0] || null;
    setSelectedFile(file);
  }

  function handleRemoveFile() {
    setSelectedFile(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  }

  function handleReset() {
    setPurity('');
    setWeightUnit('');
    setSelectedFile(null);

    if (formRef.current) {
      formRef.current.reset();
    }
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
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

          <div className='grid gap-2'>
            <Label htmlFor='image'>Image</Label>
            <div className='flex items-center gap-2'>
              <Input
                id='image'
                name='image'
                type='file'
                accept='image/*'
                ref={fileInputRef}
                onChange={handleFileChange}
                className='file:border-0 file:bg-transparent file:text-sm file:font-medium'
                required
              />
              {selectedFile && (
                <Button type='button' variant='outline' size='sm' onClick={handleRemoveFile} className='shrink-0'>
                  <X className='w-4 h-4' />
                </Button>
              )}
            </div>
            {selectedFile && (
              <div className='flex items-center gap-2 text-sm text-muted-foreground'>
                <Upload className='w-4 h-4' />
                <span>{selectedFile.name}</span>
                <span>({(selectedFile.size / 1024).toFixed(1)} KB)</span>
              </div>
            )}
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
