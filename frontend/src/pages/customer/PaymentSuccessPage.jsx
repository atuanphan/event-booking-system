import { useEffect, useState } from 'react';
import { useLocation, useSearchParams, Link } from 'react-router-dom';
import { CheckCircle, XCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { getAccessToken } from '../../api/axios';
import api from '../../api/axios';

export default function PaymentSuccessPage() {
  const { pathname } = useLocation();
  const [searchParams] = useSearchParams();
  const { authReady } = useAuth();
  const [paymentStatus, setPaymentStatus] = useState('loading');

  useEffect(() => {
    const params = Object.fromEntries(searchParams.entries());
    let isCurrentRequest = true;

    if (pathname === '/payment/momo/result') {
      if (!authReady) {
        return () => {
          isCurrentRequest = false;
        };
      }

      const orderId = params.orderId;
      if (!/^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(orderId || '')) {
        setPaymentStatus('error');
        return () => {
          isCurrentRequest = false;
        };
      }

      if (!getAccessToken()) {
        setPaymentStatus('error');
        return () => {
          isCurrentRequest = false;
        };
      }

      const checkOrderStatus = async () => {
        try {
          for (let attempt = 0; attempt < 15 && isCurrentRequest; attempt += 1) {
            const { data } = await api.get(`/orders/${orderId}/status`);
            const status = (typeof data === 'string' ? data : data?.status)?.toUpperCase();

            if (status === 'COMPLETED') {
              setPaymentStatus('completed');
              return;
            }
            if (['FAILED', 'CANCELLED', 'REFUNDED'].includes(status)) {
              setPaymentStatus('cancelled');
              return;
            }
            if (status !== 'PENDING' && status !== 'PROCESSING') {
              setPaymentStatus('error');
              return;
            }

            await new Promise((resolve) => setTimeout(resolve, 1000));
          }

          if (isCurrentRequest) {
            setPaymentStatus(params.resultCode && params.resultCode !== '0' ? 'cancelled' : 'pending');
          }
        } catch {
          if (isCurrentRequest) setPaymentStatus('error');
        }
      };

      checkOrderStatus();
      return () => {
        isCurrentRequest = false;
      };
    }

    if (Object.keys(params).length === 0) {
      setPaymentStatus('error');
      return () => {
        isCurrentRequest = false;
      };
    }

    api
      .get('/payment/vnpay/ipn', { params })
      .then(({ data }) => {
        if (!isCurrentRequest) return;

        const responseCode = typeof data === 'string'
          ? data
          : data?.RspCode ?? data?.rspCode;
        const status = responseCode?.toUpperCase();
        setPaymentStatus(
          status === '00'
            ? 'completed'
            : status === '02'
              ? 'cancelled'
              : 'error'
        );
      })
      .catch(() => {
        if (isCurrentRequest) setPaymentStatus('error');
      });

    return () => {
      isCurrentRequest = false;
    };
  }, [authReady, pathname, searchParams]);

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
  const isPending = paymentStatus === 'pending';

  return (
    <div className="max-w-md mx-auto px-4 py-20 text-center">
      {isCompleted ? (
        <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
      ) : isPending ? (
        <div className="w-16 h-16 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin mx-auto mb-4" />
      ) : (
        <XCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
      )}
      <h1 className="text-2xl font-bold text-gray-900 mb-2">
        {isCompleted
          ? 'Thanh toán thành công!'
          : isPending
            ? 'Đang cập nhật thanh toán'
            : 'Thanh toán không thành công'}
      </h1>
      <p className="text-gray-500 mb-8">
        {isCompleted
          ? 'Vé của bạn đã được xác nhận. Vui lòng kiểm tra email để nhận thông tin vé.'
          : isPending
            ? 'MoMo đã tiếp nhận giao dịch nhưng hệ thống chưa nhận được xác nhận cuối cùng. Vui lòng kiểm tra vé của bạn sau ít phút.'
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
