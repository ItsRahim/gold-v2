import { create } from 'zustand';
import type { AddGoldTypeRequest, GoldItem } from '@/app/catalog/catalogTypes.ts';
import { getAllGoldTypes, addGoldType as addGoldTypeApi, deleteGoldType as deleteGoldTypeApi } from '@/services/gold.ts';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification.ts';

type GoldTypeState = {
  goldTypes: GoldItem[];
  isLoading: boolean;
  isRefreshing: boolean;
  error: string | null;
};

type GoldTypeActions = {
  fetchGoldTypes: (isRefresh?: boolean) => Promise<void>;
  addGoldType: (goldType: AddGoldTypeRequest, file: File) => Promise<void>;
  deleteGoldType: (id: number, name: string) => Promise<void>;
  refreshGoldTypes: () => void;
  setLoading: (loading: boolean) => void;
  setRefreshing: (refreshing: boolean) => void;
  setError: (error: string | null) => void;
  reset: () => void;
};

type GoldTypeStore = GoldTypeState & GoldTypeActions;

const initialState: GoldTypeState = {
  goldTypes: [],
  isLoading: true,
  isRefreshing: false,
  error: null,
};

export const useGoldTypeStore = create<GoldTypeStore>((set, get) => ({
  ...initialState,

  fetchGoldTypes: async (isRefresh = false) => {
    const { setLoading, setRefreshing, setError } = get();

    if (isRefresh) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError(null);

    try {
      const res = await getAllGoldTypes();
      set({ goldTypes: res.content });
    } catch (err) {
      console.error('Failed to fetch gold types:', err);
      const errorMessage = 'Failed to load gold items. Please try again.';
      setError(errorMessage);
      showToast(TOAST_TYPES.ERROR, errorMessage);
    } finally {
      setLoading(false);
      if (isRefresh) {
        setRefreshing(false);
      }
    }
  },

  addGoldType: async (goldType: AddGoldTypeRequest, file: File) => {
    const { fetchGoldTypes } = get();

    try {
      const result = await addGoldTypeApi(goldType, file);

      if (result && 'name' in result) {
        await fetchGoldTypes();
        showToast(TOAST_TYPES.SUCCESS, `Gold type "${result.name}" added successfully!`);
      } else if (result && 'message' in result) {
        showToast(TOAST_TYPES.ERROR, result.message);
      } else {
        showToast(TOAST_TYPES.ERROR, 'Failed to add gold type. Please try again.');
      }
    } catch (err) {
      console.error('Failed to add gold type:', err);
      showToast(TOAST_TYPES.ERROR, 'Failed to add gold type. Please try again.');
    }
  },

  deleteGoldType: async (id: number, name: string) => {
    const { fetchGoldTypes } = get();

    try {
      const result = await deleteGoldTypeApi(id.toString());

      if (result) {
        const errorMessage = 'message' in result ? result.message : `Failed to delete gold type: "${name}"`;
        showToast(TOAST_TYPES.ERROR, errorMessage);
      } else {
        await fetchGoldTypes();
        showToast(TOAST_TYPES.SUCCESS, `Successfully deleted gold type "${name}"`);
      }
    } catch (err) {
      console.error('Failed to delete gold type:', err);
      showToast(TOAST_TYPES.ERROR, `Failed to delete gold type: "${name}"`);
    }
  },

  refreshGoldTypes: () => {
    const { fetchGoldTypes } = get();
    fetchGoldTypes(true);
  },

  setLoading: (loading: boolean) => {
    set({ isLoading: loading });
  },

  setRefreshing: (refreshing: boolean) => {
    set({ isRefreshing: refreshing });
  },

  setError: (error: string | null) => {
    set({ error });
  },

  reset: () => {
    set(initialState);
  },
}));
