import axios from 'axios';
import { API_ENDPOINTS } from '@/lib/api/endpoints.ts';
import type { AddGoldTypeRequest } from '@/app/catalog/catalogTypes.ts';

export interface ApiError {
  message: string;
}

export async function getAllGoldTypes() {
  const response = await axios.get(API_ENDPOINTS.GOLD_TYPE);
  return response.data;
}

export async function addGoldType(goldType: AddGoldTypeRequest, file: File): Promise<AddGoldTypeRequest | ApiError | null> {
  try {
    const formData = new FormData();

    formData.append(
      'request',
      new Blob([JSON.stringify(goldType)], {
        type: 'application/json',
      }),
    );

    formData.append('file', file);

    const response = await axios.post(API_ENDPOINTS.GOLD_TYPE, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data as AddGoldTypeRequest;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const message = error.response?.data?.message || 'Unknown error';
      return { message };
    }
    return null;
  }
}

export async function deleteGoldType(id: string): Promise<void | ApiError> {
  try {
    await axios.delete(`${API_ENDPOINTS.GOLD_TYPE}/${id}`);
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const message = error.response?.data?.message || 'Failed to delete gold type';
      return { message };
    }
    return { message: 'Network error occurred' };
  }
}
