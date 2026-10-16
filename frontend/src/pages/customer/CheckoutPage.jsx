import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, CreditCard, Smartphone } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';

export default function CheckoutPage() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [paymentMethod, setPaymentMethod] = useState('vnpay');
  const [paymentLoading, setPaymentLoading] = useState(false);

  if (!state?.event || !state?.orderItems || !state?.userId) {
    return <Navigate to="/" replace />;
  }

  const { event, orderItems, totalPrice, userId } = state;

  const handlePayment = async () => {
    setPaymentLoading(true);
    try {
      const { data: paymentUrl } = await api.post('/orders', {
        userId,
        orderItems,
        paymentMethod: paymentMethod.toUpperCase(),
      });

      if (typeof paymentUrl !== 'string' || !paymentUrl) {
        throw new Error('Backend không trả về liên kết thanh toán');
      }
      window.location.assign(paymentUrl);
    } catch (err) {
      toast.error(err.response?.data?.message || err.message || 'Không thể tạo liên kết thanh toán');
      setPaymentLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <button onClick={() => navigate(-1)} className="flex items-center gap-1 text-gray-500 hover:text-indigo-600 mb-6">
        <ArrowLeft className="w-4 h-4" /> Quay lại
      </button>

      <h1 className="text-2xl font-bold mb-6">Xác nhận đặt vé</h1>

      {/* Event info */}
      <div className="bg-white rounded-xl border p-4 mb-6 flex gap-4">
        <img
          src={event.imageUrl || 'https://placehold.co/120x80?text=Event'}
          alt={event.name}
          className="w-24 h-16 object-cover rounded-lg"
        />
        <div>
          <h3 className="font-semibold">{event.name}</h3>
          <p className="text-sm text-gray-500">{event.venue?.name}</p>
        </div>
      </div>

      {/* Order items */}
      <div className="bg-white rounded-xl border mb-6">
        <div className="p-4 border-b font-semibold">Chi tiết vé</div>
        {orderItems.map((item) => {
          const ticketType = event.ticketTypes.find((t) => t.id === item.ticketTypeId);
          return (
            <div key={item.ticketTypeId} className="p-4 flex justify-between items-center border-b last:border-0">
              <div>
                <p className="font-medium">{ticketType?.name}</p>
                <p className="text-sm text-gray-500">
                  {ticketType?.price.toLocaleString('vi-VN')}đ x {item.quantity}
                </p>
              </div>
              <p className="font-semibold">
                {(ticketType?.price * item.quantity).toLocaleString('vi-VN')}đ
              </p>
            </div>
          );
        })}
        <div className="p-4 flex justify-between items-center bg-gray-50 rounded-b-xl">
          <span className="font-semibold text-lg">Tổng cộng</span>
          <span className="text-xl font-bold text-indigo-600">
            {totalPrice.toLocaleString('vi-VN')}đ
          </span>
        </div>
      </div>

      {/* Payment */}
      <div className="mb-4">
        <h2 className="font-semibold text-gray-900 mb-3">Phương thức thanh toán</h2>
        <div className="grid sm:grid-cols-2 gap-3">
          <label className={`flex items-center gap-3 p-4 bg-white rounded-xl border-2 cursor-pointer transition ${
            paymentMethod === 'vnpay' ? 'border-indigo-600' : 'border-gray-200 hover:border-gray-300'
          }`}>
            <input
              type="radio"
              name="paymentMethod"
              value="vnpay"
              checked={paymentMethod === 'vnpay'}
              onChange={() => setPaymentMethod('vnpay')}
              className="accent-indigo-600"
            />
            <span className="flex items-center justify-center w-10 h-10 rounded-lg bg-blue-50 text-blue-700">
              <CreditCard className="w-5 h-5" />
            </span>
            <span>
              <span className="block font-medium text-gray-900">VNPay</span>
              <span className="block text-sm text-gray-500">Thanh toán trực tuyến</span>
            </span>
          </label>

          <label className={`flex items-center gap-3 p-4 bg-white rounded-xl border-2 cursor-pointer transition ${
            paymentMethod === 'momo' ? 'border-pink-500' : 'border-gray-200 hover:border-gray-300'
          }`}>
            <input
              type="radio"
              name="paymentMethod"
              value="momo"
              checked={paymentMethod === 'momo'}
              onChange={() => setPaymentMethod('momo')}
              className="accent-pink-500"
            />
            <span className="flex items-center justify-center w-10 h-10 rounded-lg bg-pink-100 text-pink-600">
              <Smartphone className="w-5 h-5" />
            </span>
            <span>
              <span className="block font-medium text-gray-900">Ví MoMo</span>
              <span className="block text-sm text-pink-600">Thanh toán trực tuyến</span>
            </span>
          </label>
        </div>
      </div>

      <button
        onClick={handlePayment}
        disabled={paymentLoading}
        className={`w-full py-3 text-white rounded-xl font-medium transition flex items-center justify-center gap-2 ${
          paymentMethod === 'momo'
            ? 'bg-pink-600 hover:bg-pink-700'
            : 'bg-indigo-600 hover:bg-indigo-700'
        } disabled:opacity-60 disabled:cursor-wait`}
      >
        {paymentMethod === 'momo' ? <Smartphone className="w-5 h-5" /> : <CreditCard className="w-5 h-5" />}
        {paymentLoading
          ? 'Đang tạo giao dịch...'
          : `Thanh toán qua ${paymentMethod === 'momo' ? 'MoMo' : 'VNPay'}`}
      </button>
    </div>
  );
}
