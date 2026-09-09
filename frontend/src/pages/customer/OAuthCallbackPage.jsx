import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import api, { setAccessToken } from '../../api/axios';
import { useAuth } from '../../context/AuthContext';

export default function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing');
  const calledRef = useRef(false);
  const { setUserData } = useAuth();

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

        const returnUrl = sessionStorage.getItem('returnUrl');
        sessionStorage.removeItem('returnUrl'); 

        if (user.roles?.includes('ADMIN')) {
          navigate('/admin', { replace: true });
        } else {
          navigate(returnUrl || '/', { replace: true });
        }
      } catch (err) {
        console.error('OAuth callback error:', err);
        localStorage.removeItem('user');
        setStatus('error');
      }
    };

    fetchUser();
  }, [navigate, setUserData]);

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