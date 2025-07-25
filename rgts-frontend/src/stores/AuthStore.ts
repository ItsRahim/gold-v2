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
  isAuthenticated: boolean;
};

type AuthActions = {
  login: (data: LoginRequest) => Promise<boolean>;
  register: (data: RegisterRequest) => Promise<boolean>;
  logout: () => void;
  setError: (error: string | null) => void;
  setLoading: (loading: boolean) => void;
  reset: () => void;
};

type AuthStore = AuthState & AuthActions;

const initialToken = localStorage.getItem('accessToken');

const initialState: AuthState = {
  user: null,
  isLoading: false,
  error: null,
  token: initialToken,
  isAuthenticated: !!initialToken,
};

export const useAuthStore = create<AuthStore>((set) => ({
  ...initialState,

  login: async (data: LoginRequest) => {
    set({ isLoading: true, error: null });

    const result = await loginUser(data);

    set({ isLoading: false });

    if (!result || !('accessToken' in result)) {
      const errorMessage = (result as ApiError)?.message || 'Login failed. Please try again.';
      set({ error: errorMessage, isAuthenticated: false });
      showToast(TOAST_TYPES.ERROR, errorMessage);
      return false;
    }

    localStorage.setItem('accessToken', result.accessToken);

    set({
      user: result,
      token: result.accessToken,
      isAuthenticated: true,
    });

    const fullName = `${result.firstName} ${result.lastName}`;
    showToast(TOAST_TYPES.SUCCESS, `Welcome back, ${fullName}!`);
    return true;
  },

  register: async (data: RegisterRequest) => {
    set({ isLoading: true, error: null });

    const result = await registerUser(data);

    set({ isLoading: false });

    if ('error' in result || (result as ApiError)?.message?.toLowerCase().includes('fail')) {
      const errorMessage = (result as ApiError).message || 'Registration failed';
      set({ error: errorMessage, isAuthenticated: false });
      showToast(TOAST_TYPES.ERROR, errorMessage);
      return false;
    }

    showToast(TOAST_TYPES.SUCCESS, result.message);
    return true;
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    set({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
    });
  },

  setError: (error: string | null) => set({ error }),
  setLoading: (loading: boolean) => set({ isLoading: loading }),
  reset: () => set(initialState),
}));
