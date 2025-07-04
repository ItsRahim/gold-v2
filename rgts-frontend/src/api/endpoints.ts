const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const API_ENDPOINTS = {
  GOLD_PRICE: `${BASE_URL}/price`,
  GOLD_TYPE: `${BASE_URL}/type`,
};

export { BASE_URL };
