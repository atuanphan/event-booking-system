import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Calendar, MapPin, Users, ArrowLeft, ShoppingCart } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from '../../components/LoadingSpinner';

const STATUS_LABELS = {
  UPCOMING: 'Sắp diễn ra',
  ONGOING: 'Đang diễn ra',
  FINISHED: 'Đã kết thúc',
  DRAFT: 'Dự kiến',
  CANCELLED: 'Đã hủy',
};

function formatDateTime(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

export default function EventDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedTickets, setSelectedTickets] = useState({});

  useEffect(() => {
    fetchEvent();
  }, [id]);

  const fetchEvent = async () => {
    try {
      const { data } = await api.get(`/events/${id}`);
      setEvent(data);
    } catch (err) {
      toast.error('Không thể tải thông tin sự kiện');
    } finally {
      setLoading(false);
    }
  };

  const handleQuantityChange = (ticketTypeId, delta) => {
    setSelectedTickets((prev) => {
      const current = prev[ticketTypeId] || 0;
      const ticketType = event.ticketTypes.find((t) => t.id === ticketTypeId);
      const maxQty = ticketType?.availableQuantity || 0;
      const newQty = Math.max(0, Math.min(maxQty, current + delta));
      if (newQty === 0) {
        const { [ticketTypeId]: _, ...rest } = prev;
        return rest;
      }
      return { ...prev, [ticketTypeId]: newQty };
    });
  };

  const totalItems = Object.values(selectedTickets).reduce((a, b) => a + b, 0);
  const totalPrice = event?.ticketTypes?.reduce((sum, t) => {
    return sum + (selectedTickets[t.id] || 0) * t.price;
  }, 0) || 0;

  const handleBooking = () => {
    if (!user) {
      toast.error('Vui lòng đăng nhập để đặt vé');
      navigate('/login');
      return;
    }
    if (totalItems === 0) {
      toast.error('Vui lòng chọn ít nhất 1 vé');
      return;
    }
    const orderItems = Object.entries(selectedTickets).map(([ticketTypeId, quantity]) => ({
      ticketTypeId,
      quantity,
    }));
    navigate('/checkout', { state: { event, orderItems, totalPrice } });
  };

  if (loading) return <LoadingSpinner />;
  if (!event) return <div className="text-center py-12 text-gray-500">Không tìm thấy sự kiện</div>;

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <button onClick={() => navigate(-1)} className="flex items-center gap-1 text-gray-500 hover:text-indigo-600 mb-6">
        <ArrowLeft className="w-4 h-4" /> Quay lại
      </button>

      {/* Banner */}
      <div className="rounded-2xl overflow-hidden mb-8 bg-gray-100">
        <img
          src={event.imageUrl || 'https://placehold.co/1000x400?text=Event'}
          alt={event.name}
          className="w-full h-64 md:h-80 object-cover"
          onError={(e) => { e.target.src = 'https://placehold.co/1000x400?text=Event'; }}
        />
      </div>

      <div className="grid md:grid-cols-3 gap-8">
        {/* Info */}
        <div className="md:col-span-2">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">{event.name}</h1>
          <span className="inline-block bg-indigo-100 text-indigo-700 text-sm font-medium px-3 py-1 rounded-full mb-4">
            {STATUS_LABELS[event.status] || event.status}
          </span>

          <div className="space-y-3 mb-6">
            <div className="flex items-center gap-2 text-gray-600">
              <Calendar className="w-5 h-5 text-indigo-500" />
              <span>{formatDateTime(event.startTime)} - {formatDateTime(event.endTime)}</span>
            </div>
            {event.venue && (
              <div className="flex items-center gap-2 text-gray-600">
                <MapPin className="w-5 h-5 text-indigo-500" />
                <span>{event.venue.name} - {event.venue.address}</span>
              </div>
            )}
            <div className="flex items-center gap-2 text-gray-600">
              <Users className="w-5 h-5 text-indigo-500" />
              <span>Tổng số vé: {event.totalQuantity}</span>
            </div>
          </div>

          <div className="prose max-w-none">
            <h3 className="text-lg font-semibold mb-2">Mô tả</h3>
            <p className="text-gray-600 whitespace-pre-line">{event.description}</p>
          </div>
        </div>

        {/* Ticket selection sidebar */}
        <div className="bg-white border rounded-xl p-6 h-fit sticky top-24">
          <h3 className="font-semibold text-lg mb-4">Chọn vé</h3>
          <div className="space-y-4">
            {event.ticketTypes?.map((tt) => (
              <div key={tt.id} className="border rounded-lg p-3">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <p className="font-medium">{tt.name}</p>
                    <p className="text-indigo-600 font-semibold">
                      {tt.price.toLocaleString('vi-VN')}đ
                    </p>
                  </div>
                  <span className="text-xs text-gray-500">
                    Còn {tt.availableQuantity} vé
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handleQuantityChange(tt.id, -1)}
                      className="w-8 h-8 rounded-lg border hover:bg-gray-100 flex items-center justify-center font-bold"
                    >
                      -
                    </button>
                    <span className="w-8 text-center font-medium">
                      {selectedTickets[tt.id] || 0}
                    </span>
                    <button
                      onClick={() => handleQuantityChange(tt.id, 1)}
                      disabled={(selectedTickets[tt.id] || 0) >= tt.availableQuantity}
                      className="w-8 h-8 rounded-lg border hover:bg-gray-100 flex items-center justify-center font-bold disabled:opacity-30"
                    >
                      +
                    </button>
                  </div>
                  <span className="text-sm font-medium text-gray-700">
                    {((selectedTickets[tt.id] || 0) * tt.price).toLocaleString('vi-VN')}đ
                  </span>
                </div>
              </div>
            ))}
          </div>

          <div className="border-t mt-4 pt-4">
            <div className="flex justify-between mb-4">
              <span className="font-medium">Tổng cộng</span>
              <span className="text-xl font-bold text-indigo-600">
                {totalPrice.toLocaleString('vi-VN')}đ
              </span>
            </div>
            <button
              onClick={handleBooking}
              disabled={totalItems === 0}
              className="w-full py-3 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <ShoppingCart className="w-5 h-5" />
              Đặt vé ({totalItems})
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
