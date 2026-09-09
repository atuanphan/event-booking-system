import axios from 'axios';

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/v1`,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

let accessTokenMemory = null;

export const setAccessToken = (token) => {
  accessTokenMemory = token || null;
};

export const getAccessToken = () => accessTokenMemory;

export const clearAccessToken = () => {
  accessTokenMemory = null;
};

api.interceptors.request.use((config) => {
  if (config._skipAuth) {
    return config;
  }

  const token = getAccessToken();
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config || {};

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const { data: newAccessToken } = await api.post('/auth/refresh', null, {
          withCredentials: true,
          _skipAuth: true,
        });

        setAccessToken(newAccessToken);
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return api(originalRequest);
      } catch (refreshError) {
        clearAccessToken();
        localStorage.removeItem('user');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
