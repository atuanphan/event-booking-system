import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';

export default function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing');

  useEffect(() => {
    const fetchUser = async () => {
      try {
        // cookie accessToken đã được browser tự đính kèm nếu withCredentials: true
        const res = await api.get('/auth/me');
        const user = res.data;
        localStorage.setItem('user', JSON.stringify(user)); // nếu vẫn cần cache thông tin hiển thị
        toast.success('Đăng nhập thành công!');
        setStatus('success');
        if (user.roles?.includes('ADMIN') || user.roles?.includes('CUSTOMER')) {
          navigate('/admin', { replace: true });
        } else {
          navigate('/', { replace: true });
        }
      } catch (err) {
        console.error('OAuth callback error:', err);
        setStatus('error');
      }
    };
    fetchUser();
  }, [navigate]);

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