import axios from 'axios';

/*
 *  1. AXIOS INSTANCE
 */
const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/v1`,
  withCredentials: true
});

api.interceptors.request.use((config) => {
  if (config.data instanceof FormData) {
    delete config.headers['Content-Type']; // xóa hẳn, không để sót giá trị cũ từ request trước
  } else {
    config.headers['Content-Type'] = 'application/json';
  }
  return config;
});

/*
 *  2. ACCESS TOKEN — lưu in-memory (KHÔNG dùng localStorage)
 */
let accessTokenMemory = null;

// Kênh đồng bộ giữa các tab trong cùng origin
const authChannel = typeof BroadcastChannel !== 'undefined' ? new BroadcastChannel('auth') : null;

export const setAccessToken = (token, { broadcast = true } = {}) => {
  accessTokenMemory = token || null;
  if (broadcast && authChannel) {
    authChannel.postMessage({ type: 'TOKEN_UPDATED', token: accessTokenMemory });
  }
};

export const getAccessToken = () => accessTokenMemory;

export const clearAccessToken = ({ broadcast = true } = {}) => {
  accessTokenMemory = null;
  if (broadcast && authChannel) {
    authChannel.postMessage({ type: 'LOGGED_OUT' });
  }
};

/*
 *  3. THÔNG BÁO "PHIÊN HẾT HẠN" RA NGOÀI (cho React xử lý)
 */
const authExpireListeners = new Set();

export const onAuthExpire = (callback) => {
  authExpireListeners.add(callback);
  return () => authExpireListeners.delete(callback); // gọi để unsubscribe
};

const notifyAuthExpire = () => {
  authExpireListeners.forEach((cb) => cb());
};

// Nhận sự kiện từ các tab khác — không tự phát lại (broadcast: false) để tránh loop
if (authChannel) {
  authChannel.onmessage = (event) => {
    const { type, token } = event.data || {};
    if (type === 'TOKEN_UPDATED') {
      setAccessToken(token, { broadcast: false });
    }
    if (type === 'LOGGED_OUT') {
      clearAccessToken({ broadcast: false });
      notifyAuthExpire();
    }
  };
}

/*
 *  4. GẮN ACCESS TOKEN VÀO REQUEST
 */
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

/*
 *  5. REFRESH TOKEN — DEDUPE CÁC LỆNH REFRESH ĐỒNG THỜI
*/
let refreshPromise = null;

const refreshAccessToken = () => {
  if (!refreshPromise) {
    refreshPromise = api
      .post('/auth/refresh', null, { _skipAuth: true })
      .then(({ data }) => {
        const newToken = data?.accessToken ?? data; // hỗ trợ cả 2 kiểu response
        setAccessToken(newToken);
        return newToken;
      })
      .catch((err) => {
        clearAccessToken();
        throw err;
      })
      .finally(() => {
        refreshPromise = null; // reset để lần 401 tiếp theo vẫn refresh được
      });
  }
  return refreshPromise;
};

/*
 *  6. RESPONSE INTERCEPTOR — TỰ ĐỘNG REFRESH KHI 401
 */
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config || {};

    // Không retry cho chính request refresh, hoặc request đã retry rồi
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest._skipAuth
    ) {
      originalRequest._retry = true;

      try {
        const newAccessToken = await refreshAccessToken();
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        notifyAuthExpire();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

/*
 *  7. SILENT REFRESH LÚC APP KHỞI ĐỘNG
 */
export const initAuth = async () => {
  try {
    await refreshAccessToken();
    return true;
  } catch {
    return false;
  }
};

export default api;
