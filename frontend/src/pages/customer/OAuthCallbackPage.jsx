import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import api, { setAccessToken } from '../../api/axios';
import { useAuth } from '../../context/AuthContext';

// Chỉ chấp nhận returnUrl dạng relative path nội bộ (chống open-redirect).
// Chặn cả "//evil.com" (browser hiểu là protocol-relative URL) và URL tuyệt đối.
const isSafeReturnUrl = (url) => {
  if (!url) return false;
  if (!url.startsWith('/')) return false;
  if (url.startsWith('//')) return false;
  return true;
};

export default function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing');
  const calledRef = useRef(false);
  const { setUserData, clearSession } = useAuth();

  useEffect(() => {
    if (calledRef.current) return;
    calledRef.current = true;

    const fetchUser = async () => {
      const params = new URLSearchParams(window.location.search);
      const code = params.get('code');

      if (!code) return navigate('/login');

      try {
        const { data } = await api.post('/auth/oauth/exchange', { code }, { _skipAuth: true });
        setAccessToken(data.accessToken); // lưu vào memory

        const { data: user } = await api.get('/auth/me');

        setUserData(user); // lưu vào localStorage và state

        toast.success('Đăng nhập thành công!');
        setStatus('success');

        window.history.replaceState({}, '', window.location.pathname);

        const rawReturnUrl = sessionStorage.getItem('returnUrl');
        sessionStorage.removeItem('returnUrl');
        const returnUrl = isSafeReturnUrl(rawReturnUrl) ? rawReturnUrl : null;

        if (user.roles?.includes('ADMIN')) {
          navigate('/admin', { replace: true });
        } else {
          navigate(returnUrl || '/', { replace: true });
        }
      } catch (err) {
        console.error('OAuth callback error:', err);
        toast.error('Đăng nhập thất bại, vui lòng thử lại.');
        clearSession(); // dọn cả localStorage lẫn user state, không chỉ localStorage
        setStatus('error');
      }
    };

    fetchUser();
  }, [navigate, setUserData, clearSession]);

  if (status === 'processing') {
    return (
      <div className="min-h-[70vh] flex items-center justify-center">
        <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="min-h-[70vh] flex items-center justify-center">
        <button onClick={() => navigate('/login')}>Quay lại đăng nhập</button>
      </div>
    );
  }

  return null;
}