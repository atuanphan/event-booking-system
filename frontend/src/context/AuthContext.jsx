import { createContext, useContext, useState } from 'react';
import api, { setAccessToken, clearAccessToken } from '../api/axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('user');
      return saved ? JSON.parse(saved) : null;
    } catch {
      localStorage.removeItem('user');
      return null;
    }
  });
  const [loading, setLoading] = useState(false);

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password });

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

    clearAccessToken();
    localStorage.removeItem('user');
    setUser(null);
  };

  const register = async (fullname, email, password) => {
    await api.post('/users/register', { fullname, email, password, status: 1 });
  };

  const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('STAFF');

  return (
    <AuthContext.Provider value={{ user, login, logout, register, isAdmin, loading, setUserData }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
