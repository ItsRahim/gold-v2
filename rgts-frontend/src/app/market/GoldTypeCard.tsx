import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card.tsx';
import type { GoldItem } from '@/app/market/marketTypes.ts';

export function GoldTypeCard({ item }: { item: GoldItem }) {
  return (
    <Card key={item.id} className='shadow-md hover:shadow-lg transition-shadow'>
      <CardHeader className='space-y-1'>
        <CardTitle className='text-lg font-semibold text-gold-dark'>{item.name}</CardTitle>
        <p className='text-sm text-muted-foreground'>{item.description}</p>
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
