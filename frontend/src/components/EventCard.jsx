import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar } from 'lucide-react';

const STATUS_COLORS = {
  UPCOMING: 'bg-indigo-50 text-indigo-700 border border-indigo-200',
  ONGOING: 'bg-emerald-50 text-emerald-700 border border-emerald-200',
  FINISHED: 'bg-slate-100 text-slate-500 border border-slate-200',
  DRAFT: 'bg-amber-50 text-amber-700 border border-amber-200',
  CANCELLED: 'bg-rose-50 text-rose-700 border border-rose-200',
};

const STATUS_LABELS = {
  UPCOMING: 'Sắp diễn ra',
  ONGOING: 'Đang diễn ra',
  FINISHED: 'Đã kết thúc',
  DRAFT: 'Dự kiến',
  CANCELLED: 'Đã hủy',
};

const CATEGORY_MAP = {
  music: { label: 'Âm nhạc', icon: '🎵', gradient: 'linear-gradient(135deg, #a78bfa, #f472b6)' },
  tech: { label: 'Hội nghị/Công nghệ', icon: '💻', gradient: 'linear-gradient(135deg, #60a5fa, #3b82f6)' },
  business: { label: 'Networking/Kinh doanh', icon: '🤝', gradient: 'linear-gradient(135deg, #34d399, #10b981)' },
  comedy: { label: 'Hài kịch/Giải trí', icon: '🎤', gradient: 'linear-gradient(135deg, #fbbf24, #f97316)' },
};

function getCategory(event) {
  const name = `${event?.name || ''}`.toLowerCase();
  const desc = `${event?.description || ''}`.toLowerCase();
  const text = `${name} ${desc}`;
  if (/music|nhạc|concert|festival|show|band|live/.test(text)) return 'music';
  if (/tech|conference|seminar|workshop|dev|software|ai|cloud/.test(text)) return 'tech';
  if (/business|network|networking|startup|commerce|sale/.test(text)) return 'business';
  if (/comedy|fun|hài|entertainment|show/.test(text)) return 'comedy';
  return 'tech';
}

function formatDateTime(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

export default function EventCard({ event }) {
  const [imageLoaded, setImageLoaded] = useState(false);
  const [imageFailed, setImageFailed] = useState(false);
  const category = getCategory(event);
  const categoryInfo = CATEGORY_MAP[category] || CATEGORY_MAP.tech;
  const minPrice = event.ticketTypes?.length
    ? Math.min(...event.ticketTypes.map((t) => t.price))
    : null;
  const hasImage = Boolean(event.imageUrl) && !imageFailed;

  return (
    <Link
      to={`/events/${event.id}`}
      className="block bg-white rounded-2xl border border-slate-100 shadow-sm hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden group"
    >
      <div className="relative aspect-video overflow-hidden bg-slate-100">
        {!imageLoaded && hasImage && (
          <div className="absolute inset-0 z-10 animate-pulse bg-slate-200" />
        )}
        {event.imageUrl && !imageFailed ? (
          <img
            src={event.imageUrl}
            alt={event.name}
            className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
            onLoad={() => setImageLoaded(true)}
            onError={(e) => {
              e.target.style.display = 'none';
              setImageFailed(true);
            }}
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center" style={{ background: categoryInfo.gradient }}>
            <span className="text-4xl text-white drop-shadow-sm">{categoryInfo.icon}</span>
          </div>
        )}

        {!hasImage && (
          <span className="absolute left-3 top-3 px-2 py-1 rounded-full bg-white/80 text-slate-800 text-xs font-semibold backdrop-blur-sm">
            {categoryInfo.label}
          </span>
        )}
      </div>

      <div className="p-4">
        <div className="flex items-center justify-between gap-2 mb-3">
          <span className={`text-[11px] font-semibold px-2.5 py-1 rounded-full ${STATUS_COLORS[event.status] || 'bg-gray-100 text-gray-700'}`}>
            {STATUS_LABELS[event.status] || event.status}
          </span>
          {minPrice !== null && (
            <span className="text-sm font-bold text-indigo-700">
              {minPrice.toLocaleString('vi-VN')}đ
            </span>
          )}
        </div>

        <h3 className="font-bold text-gray-900 mb-2 line-clamp-1 group-hover:text-indigo-600 transition">
          {event.name}
        </h3>

        <div className="flex items-center gap-1 text-sm text-gray-500">
          <Calendar className="w-3.5 h-3.5" />
          <span>{formatDateTime(event.startTime)}</span>
        </div>
      </div>
    </Link>
  );
}
