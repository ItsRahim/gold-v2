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

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  username: string;
  roles: string[];
}

export async function loginUser(data: LoginRequest): Promise<AuthResponse | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.LOGIN, data);

    if (response.status !== 200 && response.status !== 201) {
      return {
        message: response.data?.message || 'Login failed due to unexpected response status',
      };
    }

    return response.data as AuthResponse;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const message = error.response?.data?.message || 'Invalid credentials or user not found';
      return { message };
    }
    return { message: 'Network or server error occurred' };
  }
}

export async function registerUser(data: RegisterRequest): Promise<{ message: string } | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.REGISTER, data);

    if (response.status !== 200 && response.status !== 201) {
      return { message: response?.data?.message || 'Registration failed' };
    }

    const message = response.data?.message || 'Registration successful';
    return { message };
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const message = error.response?.data?.message || 'Registration failed';
      return { message };
    }
    return { message: 'Network or server error occurred' };
  }
}
