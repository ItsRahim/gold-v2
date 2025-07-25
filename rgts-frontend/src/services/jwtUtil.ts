import { useAuthStore } from '@/stores/AuthStore.ts';

export function getAuthHeaders() {
  const token = useAuthStore.getState().token;
  return token ? { Authorization: `Bearer ${token}` } : {};
}
