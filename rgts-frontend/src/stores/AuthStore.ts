import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthResponse, LoginRequest, RegisterRequest, VerificationRequest } from '@/services/auth';
import { loginUser, registerUser, registerVerification } from '@/services/auth';
import { showToast, TOAST_TYPES } from '@/components/shared/ToastNotification';
import type { ApiError } from '@/services/apiError';

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
  verify: (data: VerificationRequest) => Promise<boolean>;
  logout: () => void;
  setError: (error: string | null) => void;
  setLoading: (loading: boolean) => void;
  reset: () => void;
};

type AuthStore = AuthState & AuthActions;

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      user: null,
      isLoading: false,
      error: null,
      token: null,
      isAuthenticated: false,

      login: async (data) => {
        set({ isLoading: true, error: null });
        const result = await loginUser(data);
        set({ isLoading: false });

        if (!result || 'message' in result) {
          const message = (result as ApiError).message || 'Login failed';
          set({ error: message, isAuthenticated: false });
          showToast(TOAST_TYPES.ERROR, message);
          return false;
        }

        set({
          user: result,
          token: result.accessToken,
          isAuthenticated: true,
        });

        showToast(TOAST_TYPES.SUCCESS, `Welcome back, ${result.firstName} ${result.lastName}!`);
        return true;
      },

      register: async (data) => {
        set({ isLoading: true, error: null });
        const result = await registerUser(data);
        set({ isLoading: false });

        if (!result || ('message' in result && result.message.toLowerCase().includes('fail'))) {
          const message = (result as ApiError).message || 'Registration failed';
          set({ error: message, isAuthenticated: false });
          showToast(TOAST_TYPES.ERROR, message);
          return false;
        }

        showToast(TOAST_TYPES.SUCCESS, result.message);
        return true;
      },

      verify: async (data) => {
        set({ isLoading: true, error: null });
        const result = await registerVerification(data);
        set({ isLoading: false });

        if (!result || ('message' in result && result.message.toLowerCase().includes('fail'))) {
          const message = (result as ApiError).message || 'Verification failed';
          set({ error: message });
          showToast(TOAST_TYPES.ERROR, message);
          return false;
        }

        showToast(TOAST_TYPES.SUCCESS, result.message);
        return true;
      },

      logout: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
        });
      },

      setError: (error) => set({ error }),
      setLoading: (isLoading) => set({ isLoading }),
      reset: () =>
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
        }),
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
