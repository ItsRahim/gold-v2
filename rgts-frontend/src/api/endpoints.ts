const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const API_ENDPOINTS = {
  GOLD_PRICE: `${BASE_URL}/price`,
  GOLD_TYPE: `${BASE_URL}/type`,
  AUTH: `${BASE_URL}/auth`,
};

export const AUTH_ENDPOINTS = {
  LOGIN: `${API_ENDPOINTS.AUTH}/login`,
  REGISTER: `${API_ENDPOINTS.AUTH}/register`,
  VALIDATE_TOKEN: `${API_ENDPOINTS.AUTH}/validate-token`,
  VERIFY: `${API_ENDPOINTS.AUTH}/verify-email`,
};

export { BASE_URL };
