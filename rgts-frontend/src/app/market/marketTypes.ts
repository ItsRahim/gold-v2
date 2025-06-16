export type GoldItem = {
  id: number;
  name: string;
  purity: string;
  weight: string;
  description: string;
  price: number;
};

export interface GoldType {
  name: string;
  weight: number;
  purity: string;
  unit: string;
  description: string;
  price?: number;
}

export interface AddGoldDialogProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  onSubmit: (goldType: GoldType) => void;
}

export interface GoldTypeCardProps {
  item: GoldItem;
  onDelete: (id: number, name: string) => void;
}
