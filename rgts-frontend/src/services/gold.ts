import { API_ENDPOINTS } from '@/lib/api/endpoints.ts';

export async function getAllGoldTypes() {
  const result = await fetch(API_ENDPOINTS.GOLD_TYPE);
  return result.json();
}
