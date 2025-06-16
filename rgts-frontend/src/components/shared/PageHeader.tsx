import type { ReactNode } from 'react';

type PageHeaderProps = {
  title: string;
  actions?: ReactNode;
};

export function PageHeader({ title, actions }: PageHeaderProps) {
  return (
    <div className='flex items-center justify-between'>
      <h1 className='text-2xl font-bold text-foreground'>{title}</h1>
      {actions}
    </div>
  );
}
