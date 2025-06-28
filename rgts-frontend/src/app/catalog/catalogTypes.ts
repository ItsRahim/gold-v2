export type GoldItem = {
  id: number;
  name: string;
  purity: string;
  weight: string;
  description: string;
  price: number;
  imageUrl: string;
};

export interface AddGoldTypeRequest {
  name: string;
  weight: number;
  purity: string;
  unit: string;
  description: string;
}

export interface AddGoldDialogProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  onSubmit: (goldType: AddGoldTypeRequest, file: File) => void;
}

export interface GoldTypeCardProps {
  item: GoldItem;
  onDelete: (id: number, name: string) => void;
}
