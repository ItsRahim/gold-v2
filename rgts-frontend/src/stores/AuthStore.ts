import { create } from 'zustand';
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/services/auth.ts';
import { loginUser, registerUser } from '@/services/auth.ts';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification.ts';
import type { ApiError } from '@/services/apiError.ts';

type AuthState = {
  user: AuthResponse | null;
  isLoading: boolean;
  error: string | null;
  token: string | null;
};

type AuthActions = {
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<boolean>;
  logout: () => void;
  setError: (error: string | null) => void;
  setLoading: (loading: boolean) => void;
  reset: () => void;
};

type AuthStore = AuthState & AuthActions;

const initialState: AuthState = {
  user: null,
  isLoading: false,
  error: null,
  token: localStorage.getItem('accessToken') ?? null,
};

export const useAuthStore = create<AuthStore>((set) => ({
  ...initialState,

  login: async (data: LoginRequest) => {
    set({ isLoading: true, error: null });
    const result = await loginUser(data);

    if (!result) {
      set({ isLoading: false });
      showToast(TOAST_TYPES.ERROR, 'Something went wrong');
      return;
    }

    if ('accessToken' in result) {
      localStorage.setItem('accessToken', result.accessToken);
      set({ user: result, token: result.accessToken });
      showToast(TOAST_TYPES.SUCCESS, `Welcome back, ${result.username}!`);
    } else {
      set({ error: result.message });
      showToast(TOAST_TYPES.ERROR, result.message);
    }

    set({ isLoading: false });
  },

  register: async (data: RegisterRequest) => {
    set({ isLoading: true, error: null });

    const result = await registerUser(data);

    set({ isLoading: false });

    if ('error' in result || (result as ApiError)?.message?.toLowerCase().includes('fail')) {
      const errorMessage = (result as ApiError).message || 'Registration failed';
      set({ error: errorMessage });
      showToast(TOAST_TYPES.ERROR, errorMessage);
      return false;
    }

    showToast(TOAST_TYPES.SUCCESS, result.message);
    return true;
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    set({ ...initialState, token: null });
  },

  setError: (error: string | null) => set({ error }),
  setLoading: (loading: boolean) => set({ isLoading: loading }),
  reset: () => set(initialState),
}));
