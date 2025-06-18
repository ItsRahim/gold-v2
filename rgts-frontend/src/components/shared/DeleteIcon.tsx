import React from 'react';
import { Trash2 } from 'lucide-react';

interface DeleteIconProps {
  onClick: () => void;
}

const DeleteIcon: React.FC<DeleteIconProps> = ({ onClick }) => (
  <Trash2
    className='w-4 h-4 text-red-500 cursor-pointer group-hover:scale-125 group-hover:text-red-700 transition-transform duration-150 mt-1'
    onClick={onClick}
  />
);

export default DeleteIcon;
