import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { CheckCircle, XCircle } from 'lucide-react';
import api from '../../api/axios';

export default function PaymentSuccessPage() {
  const [searchParams] = useSearchParams();
  const [paymentStatus, setPaymentStatus] = useState('loading');

  useEffect(() => {
    const params = Object.fromEntries(searchParams.entries());
    let isCurrentRequest = true;

    if (Object.keys(params).length === 0) {
      setPaymentStatus('error');
      return () => {
        isCurrentRequest = false;
      };
    }

    api
      .get('/payment/vnpay/callback', { params })
      .then(({ data }) => {
        if (!isCurrentRequest) return;

        const status = typeof data === 'string' ? data.toUpperCase() : data?.status?.toUpperCase();
        setPaymentStatus(status === 'COMPLETED' ? 'completed' : status === 'CANCELLED' ? 'cancelled' : 'error');
      })
      .catch(() => {
        if (isCurrentRequest) setPaymentStatus('error');
      });

    return () => {
      isCurrentRequest = false;
    };
  }, [searchParams]);

  if (paymentStatus === 'loading') {
    return (
      <div className="max-w-md mx-auto px-4 py-20 text-center">
        <div className="w-16 h-16 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Đang xác nhận thanh toán</h1>
        <p className="text-gray-500">Vui lòng chờ trong giây lát.</p>
      </div>
    );
  }

  const isCompleted = paymentStatus === 'completed';

  return (
    <div className="max-w-md mx-auto px-4 py-20 text-center">
      {isCompleted ? (
        <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
      ) : (
        <XCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
      )}
      <h1 className="text-2xl font-bold text-gray-900 mb-2">
        {isCompleted ? 'Thanh toán thành công!' : 'Thanh toán không thành công'}
      </h1>
      <p className="text-gray-500 mb-8">
        {isCompleted
          ? 'Vé của bạn đã được xác nhận. Vui lòng kiểm tra email để nhận thông tin vé.'
          : paymentStatus === 'cancelled'
            ? 'Giao dịch đã bị hủy hoặc không hợp lệ. Vui lòng thử thanh toán lại.'
            : 'Không thể xác nhận giao dịch. Vui lòng kiểm tra lại hoặc thử thanh toán lại.'}
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
