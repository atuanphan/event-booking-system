import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { LogIn } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext';

const GOOGLE_ICON = (
  <svg className="w-5 h-5" viewBox="0 0 24 24">
    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"/>
    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
  </svg>
);

const FACEBOOK_ICON = (
  <svg className="w-5 h-5" viewBox="0 0 24 24" fill="#1877F2">
    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
  </svg>
);

const API_BASE = 'http://localhost:8044/api';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const { register, handleSubmit, formState: { errors } } = useForm();

  const from = location.state?.from || '/';

  const handleOAuth = (provider) => {
    const from = location.state?.from || location.pathname;
    if (from !== '/login') {
      sessionStorage.setItem('returnUrl', from);
    }
    window.location.href = `${API_BASE}/oauth2/authorization/${provider}`;
  };

  const onSubmit = async (values) => {
    setLoading(true);
    try {
      const user = await login(values.email, values.password);
      toast.success('Đăng nhập thành công!');
      navigate(from, { replace: true });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Sai email hoặc mật khẩu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[70vh] flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Đăng nhập</h1>
          <p className="text-gray-500 mt-2">Chào mừng bạn đến với OmniTicket</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="bg-white rounded-2xl shadow-sm border p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              type="email"
              {...register('email', { required: 'Email là bắt buộc' })}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
              placeholder="you@example.com"
            />
            {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu</label>
            <input
              type="password"
              {...register('password', { required: 'Mật khẩu là bắt buộc' })}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
              placeholder="••••••••"
            />
            {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition disabled:opacity-50 flex items-center justify-center gap-2"
          >
            <LogIn className="w-5 h-5" />
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        {/* Divider */}
        <div className="flex items-center gap-4 my-6">
          <div className="flex-1 border-t border-gray-200" />
          <span className="text-sm text-gray-400">hoặc</span>
          <div className="flex-1 border-t border-gray-200" />
        </div>

        {/* Social login */}
        <div className="space-y-3">
          <button
            onClick={() => handleOAuth('google')}
            className="flex items-center justify-center gap-3 w-full py-3 border border-gray-300 rounded-xl hover:bg-gray-50 transition font-medium text-gray-700"
          >
            {GOOGLE_ICON}
            Đăng nhập với Google
          </button>
          <button
            onClick={() => handleOAuth('facebook')}
            className="flex items-center justify-center gap-3 w-full py-3 border border-gray-300 rounded-xl hover:bg-gray-50 transition font-medium text-gray-700"
          >
            {FACEBOOK_ICON}
            Đăng nhập với Facebook
          </button>
        </div>

        <p className="text-center mt-4 text-sm text-gray-500">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-indigo-600 hover:underline font-medium">Đăng ký</Link>
        </p>
      </div>
    </div>
  );
}
