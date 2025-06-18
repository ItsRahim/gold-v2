import { API_ENDPOINTS } from '@/lib/api/endpoints.ts';
import type { GoldType } from '@/app/catalog/catalogTypes.ts';
import { HTTP_METHODS } from '@/services/payloadConstants.tsx';

export interface ApiError {
  message: string;
}

export async function getAllGoldTypes() {
  const result = await fetch(API_ENDPOINTS.GOLD_TYPE);
  return result.json();
}

export async function addGoldType(goldType: GoldType): Promise<GoldType | ApiError | null> {
  try {
    const response = await fetch(API_ENDPOINTS.GOLD_TYPE, {
      method: HTTP_METHODS.POST,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(goldType),
    });

    const data = await response.json();

    if (response.ok) {
      return data as GoldType;
    } else {
      return { message: data.message || 'Unknown error' };
    }
  } catch {
    return null;
  }
}

export async function deleteGoldType(id: string): Promise<void | ApiError> {
  try {
    const response = await fetch(`${API_ENDPOINTS.GOLD_TYPE}/${id}`, {
      method: HTTP_METHODS.DELETE,
    });

    if (response.ok) {
      return;
    }

    const data = await response.json().catch(() => null);
    return { message: data?.message || 'Unknown error occurred' };
  } catch (error) {
    return { message: error instanceof Error ? error.message : 'Network error' };
  }
}
