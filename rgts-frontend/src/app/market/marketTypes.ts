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
