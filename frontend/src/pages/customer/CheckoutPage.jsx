import { useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, CreditCard } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';
import { useAuth } from '../../context/AuthContext';
import { useState } from 'react';

export default function CheckoutPage() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);

  if (!state?.event || !state?.orderItems) {
    navigate('/');
    return null;
  }

  const { event, orderItems, totalPrice } = state;

  const handlePayment = async () => {
    setLoading(true);
    try {
      const { data } = await api.post('/orders', {
        userId: user.id,
        status: 'PENDING',
        orderItems: orderItems.map((item) => ({
          ticketTypeId: item.ticketTypeId,
          quantity: item.quantity,
        })),
      });
      // data is the VNPay URL string
      if (data) {
        window.location.href = data;
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Đặt vé thất bại');
    } finally {
      setLoading(false);
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
      <button
        onClick={handlePayment}
        disabled={loading}
        className="w-full py-3 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition disabled:opacity-50 flex items-center justify-center gap-2"
      >
        <CreditCard className="w-5 h-5" />
        {loading ? 'Đang xử lý...' : 'Thanh toán qua VNPay'}
      </button>
    </div>
  );
}
