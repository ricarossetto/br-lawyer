import { apiClient, setAccessToken } from './client';
import { LoginRequestV8, TokenResponseV8 } from '../types/auth';

let inFlightRefresh: Promise<TokenResponseV8> | null = null;

export const authService = {
  async login(credentials: LoginRequestV8): Promise<TokenResponseV8> {
    const response = await apiClient.post<TokenResponseV8>('/v8/auth/login', credentials);
    setAccessToken(response.data.accessToken);
    return response.data;
  },

  async refresh(): Promise<TokenResponseV8> {
    if (inFlightRefresh) {
      return inFlightRefresh;
    }

    inFlightRefresh = (async () => {
      try {
        const response = await apiClient.post<TokenResponseV8>('/v8/auth/refresh');
        setAccessToken(response.data.accessToken);
        return response.data;
      } finally {
        inFlightRefresh = null;
      }
    })();

    return inFlightRefresh;
  },

  async logout(): Promise<void> {
    try {
      await apiClient.post('/v8/auth/logout');
    } finally {
      setAccessToken(null);
    }
  },
};