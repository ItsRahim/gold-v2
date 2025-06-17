import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card.tsx';
import { Trash2 } from 'lucide-react';
import type { GoldTypeCardProps } from '@/app/catalog/catalogTypes.ts';

export function GoldTypeCard({ item, onDelete }: GoldTypeCardProps) {
  return (
    <Card key={item.id} className='shadow-md hover:shadow-lg transition-shadow'>
      <CardHeader className='space-y-1 flex flex-row items-start justify-between'>
        <div>
          <CardTitle className='text-lg font-semibold text-gold-dark'>{item.name}</CardTitle>
          <p className='text-sm text-muted-foreground'>{item.description}</p>
        </div>
        <Trash2 className='w-4 h-4 text-red-500 cursor-pointer hover:text-red-700 mt-1' onClick={() => onDelete(item.id, item.name)} />
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
  );
}
