import axios from 'axios';
import { AUTH_ENDPOINTS } from '@/api/endpoints.ts';
import type { ApiError } from '@/services/apiError.ts';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  phoneNumber: string;
  password: string;
}

export interface VerificationRequest {
  email: string;
  verificationCode: string;
}

export interface AuthResponse {
  accessToken: string;
  username: string;
  userId: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

function isValidStatus(status: number): boolean {
  return status >= 200 && status < 300;
}

function handleApiError(error: unknown, fallbackMessage: string): ApiError {
  if (axios.isAxiosError(error)) {
    return {
      message: error.response?.data?.message ?? fallbackMessage,
    };
  }
  return { message: 'Network or server error occurred' };
}

export async function loginUser(data: LoginRequest): Promise<AuthResponse | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.LOGIN, data);

    if (!isValidStatus(response.status)) {
      return {
        message: response.data?.message ?? 'Login failed due to unexpected response status',
      };
    }

    return response.data as AuthResponse;
  } catch (error) {
    return handleApiError(error, 'Invalid credentials or user not found');
  }
}

export async function registerUser(data: RegisterRequest): Promise<{ message: string } | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.REGISTER, data);

    if (!isValidStatus(response.status)) {
      return {
        message: response.data?.message ?? 'Registration failed',
      };
    }

    return {
      message: response.data?.message ?? 'Registration successful',
    };
  } catch (error) {
    return handleApiError(error, 'Registration failed');
  }
}

export async function registerVerification(data: VerificationRequest): Promise<{ message: string }> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.VERIFY, data);

    if (response.status < 200 || response.status >= 300) {
      throw new Error(response.data?.message ?? 'Verification failed');
    }

    return {
      message: response.data?.message ?? 'Verification successful',
    };
  } catch (error) {
    const errMsg = axios.isAxiosError(error) ? (error.response?.data?.message ?? 'Verification failed') : 'Network or server error occurred';
    throw new Error(errMsg);
  }
}

export async function logoutUser(token: string): Promise<void> {
  await axios.post(
    AUTH_ENDPOINTS.LOGOUT,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );
}
