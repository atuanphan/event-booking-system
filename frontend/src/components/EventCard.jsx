import { Link } from 'react-router-dom';
import { Calendar, MapPin } from 'lucide-react';

const STATUS_COLORS = {
  UPCOMING: 'bg-blue-100 text-blue-700',
  ONGOING: 'bg-green-100 text-green-700',
  FINISHED: 'bg-gray-100 text-gray-500',
  DRAFT: 'bg-yellow-100 text-yellow-700',
  CANCELLED: 'bg-red-100 text-red-700',
};

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
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

export default function EventCard({ event }) {
  const minPrice = event.ticketTypes?.length
    ? Math.min(...event.ticketTypes.map((t) => t.price))
    : null;

  return (
    <Link
      to={`/events/${event.id}`}
      className="bg-white rounded-xl shadow-sm hover:shadow-md transition overflow-hidden group"
    >
      <div className="aspect-video overflow-hidden bg-gray-100">
        <img
          src={event.imageUrl || '/placeholder.jpg'}
          alt={event.name}
          className="w-full h-full object-cover group-hover:scale-105 transition duration-300"
          onError={(e) => { e.target.src = 'https://placehold.co/600x400?text=Event'; }}
        />
      </div>
      <div className="p-4">
        <div className="flex items-center justify-between mb-2">
          <span className={`text-xs font-medium px-2 py-1 rounded-full ${STATUS_COLORS[event.status] || 'bg-gray-100'}`}>
            {STATUS_LABELS[event.status] || event.status}
          </span>
          {minPrice !== null && (
            <span className="text-sm font-semibold text-indigo-600">
              {minPrice.toLocaleString('vi-VN')}đ
            </span>
          )}
        </div>
        <h3 className="font-semibold text-gray-900 mb-1 line-clamp-1 group-hover:text-indigo-600 transition">
          {event.name}
        </h3>
        <div className="flex items-center gap-1 text-sm text-gray-500">
          <Calendar className="w-3.5 h-3.5" />
          {formatDateTime(event.startTime)}
        </div>
      </div>
    </Link>
  );
}
