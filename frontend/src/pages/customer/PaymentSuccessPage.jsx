import { useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import api from '../../api/axios';

export default function PaymentSuccessPage() {
  const [searchParams] = useSearchParams();

  useEffect(() => {
    // Notify backend of payment success
    const params = Object.fromEntries(searchParams.entries());
    if (Object.keys(params).length > 0) {
      api.get('/payment/success', { params }).catch(() => {});
    }
  }, [searchParams]);

  return (
    <div className="max-w-md mx-auto px-4 py-20 text-center">
      <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
      <h1 className="text-2xl font-bold text-gray-900 mb-2">Thanh toán thành công!</h1>
      <p className="text-gray-500 mb-8">
        Vé của bạn đã được xác nhận. Vui lòng kiểm tra email để nhận thông tin vé.
      </p>
      <Link
        to="/"
        className="inline-block px-6 py-3 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition font-medium"
      >
        Về trang chủ
      </Link>
    </div>
  );
}
