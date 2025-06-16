import { useEffect } from 'react';
import { getAllGoldTypes } from '@/services/gold.ts';

export default function Dashboard() {
  useEffect(() => {
    getAllGoldTypes().then((data) => {
      console.log('Current price:', data);
    });
  }, []);

  return (
    <div className='flex justify-center items-center h-full'>
      <h1>Dashboard</h1>
    </div>
  );
}
