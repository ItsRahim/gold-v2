import axios from 'axios';
import { AUTH_ENDPOINTS } from '@/api/endpoints.ts';
import type { ApiError } from '@/services/apiError.ts';

export interface AuthRequest {
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

export interface EmailVerificationRequest {
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

export interface RegisterResponse {
  id: string;
  username: string;
  email: string;
}

export interface EmailVerificationResponse {
  username: string;
  email: string;
  verifiedAt: string;
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
  return { message: fallbackMessage };
}

export async function loginUser(data: AuthRequest): Promise<AuthResponse | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.LOGIN, data, {
      validateStatus: () => true,
    });

    if (!isValidStatus(response.status)) {
      return handleApiError(null, 'Login failed. Please check your credentials.');
    }

    return response.data as AuthResponse;
  } catch (error) {
    return handleApiError(error, 'Login failed. Please try again.');
  }
}

export async function registerUser(data: RegisterRequest): Promise<RegisterResponse | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.REGISTER, data, {
      validateStatus: () => true,
    });

    if (!isValidStatus(response.status)) {
      return handleApiError(null, 'Registration failed. Please check your details.');
    }

    return response.data as RegisterResponse;
  } catch (error) {
    return handleApiError(error, 'Registration failed. Please try again.');
  }
}

export async function registerVerification(data: EmailVerificationRequest): Promise<EmailVerificationResponse | ApiError> {
  try {
    const response = await axios.post(AUTH_ENDPOINTS.VERIFY, data, {
      validateStatus: () => true,
    });

    if (!isValidStatus(response.status)) {
      return handleApiError(null, 'Verification failed. Please try again.');
    }

    return response.data as EmailVerificationResponse;
  } catch (error) {
    return handleApiError(error, 'Verification failed. Please try again.');
  }
}

export async function logoutUser(token: string): Promise<ApiError | void> {
  try {
    const response = await axios.post(
      AUTH_ENDPOINTS.LOGOUT,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        validateStatus: () => true,
      },
    );

    if (!isValidStatus(response.status)) {
      return handleApiError(null, 'Logout failed. Please try again.');
    }
  } catch (error) {
    return handleApiError(error, 'Logout failed. Please try again.');
  }
}
