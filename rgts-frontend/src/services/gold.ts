import { API_ENDPOINTS } from '@/lib/api/endpoints.ts';
import type { GoldType } from '@/app/catalog/catalogTypes.ts';
import {HTTP_METHODS} from "@/services/payloadConstants.tsx";

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
