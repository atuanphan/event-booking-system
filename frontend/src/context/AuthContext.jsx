import { createContext, useContext, useEffect, useState } from 'react';
import api, { setAccessToken, clearAccessToken, initAuth, onAuthExpire } from '../api/axios';

const AuthContext = createContext(null);

const readUserFromStorage = () => {
  try {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  } catch {
    localStorage.removeItem('user');
    return null;
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUserFromStorage);
  const [loading, setLoading] = useState(false);
  // authReady = đã thử silent refresh xong (thành công hay thất bại đều được),
  // dùng để chặn render route cần đăng nhập trước khi biết trạng thái auth thật.
  const [authReady, setAuthReady] = useState(false);

  // Dọn dẹp state khi phiên hết hạn (refresh thất bại, hoặc tab khác logout)
  const clearSession = () => {
    localStorage.removeItem('user');
    setUser(null);
  };

  useEffect(() => {
    // Các trang này TỰ lo việc lấy/loại bỏ token (OAuth exchange, form login/register).
    // Gọi silent refresh song song ở đây là thừa, và với /oauth-callback còn gây race
    // condition nguy hiểm: nếu refresh (dùng cookie CŨ, có thể chưa hợp lệ) fail SAU
    // khi exchange đã login thành công, nhánh `!ok` bên dưới sẽ xóa mất session vừa tạo.
    const skipSilentRefresh = ['/oauth-callback', '/login', '/register'].includes(
      window.location.pathname
    );

    // 1) Khôi phục phiên lúc app khởi động (F5, mở lại tab)
    (async () => {
      if (skipSilentRefresh) {
        setAuthReady(true);
        return;
      }
      const ok = await initAuth();
      if (!ok) {
        clearSession();
      }
      setAuthReady(true);
    })();

    // 2) Lắng nghe "phiên hết hạn" phát ra từ api.js
    //    (refresh thất bại giữa chừng, hoặc tab khác vừa logout)
    const unsubscribe = onAuthExpire(() => {
      clearAccessToken({ broadcast: false }); // đã broadcast/rỗng token rồi, tránh lặp
      clearSession();
    });

    // 3) Đồng bộ user giữa các tab khi có tab khác login/logout
    //    (localStorage 'storage' event chỉ bắn ở các tab KHÁC tab vừa ghi)
    const handleStorage = (event) => {
      if (event.key !== 'user') return;
      setUser(event.newValue ? JSON.parse(event.newValue) : null);
    };
    window.addEventListener('storage', handleStorage);

    return () => {
      unsubscribe();
      window.removeEventListener('storage', handleStorage);
    };
  }, []);

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password }, { _skipAuth: true });

    if (data?.accessToken) {
      setAccessToken(data.accessToken);
    }

    if (data?.user) {
      localStorage.setItem('user', JSON.stringify(data.user));
      setUser(data.user);
    }

    return data.user;
  };

  const setUserData = (userData) => {
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  };

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } catch {
      // Ignore server-side logout error and still clear local frontend state.
    }

    clearAccessToken(); // sẽ broadcast LOGGED_OUT cho các tab khác
    clearSession();
  };

  const register = async (fullname, email, password) => {
    await api.post('/auth/register', { fullname, email, password, status: 1 }, { _skipAuth: true });
  };

  const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('STAFF');

  return (
    <AuthContext.Provider
      value={{ user, login, logout, register, isAdmin, loading, authReady, setUserData, clearSession }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
