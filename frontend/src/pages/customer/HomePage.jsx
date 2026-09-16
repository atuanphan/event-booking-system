import { useState, useEffect } from 'react';
import { ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';
import EventCard from '../../components/EventCard';

function SkeletonEventCard() {
  return (
    <div className="rounded-2xl border border-slate-100 shadow-sm overflow-hidden animate-pulse">
      <div className="aspect-video bg-slate-200" />
      <div className="p-4 space-y-3">
        <div className="flex items-center justify-between">
          <span className="h-6 w-24 rounded-full bg-slate-200" />
          <span className="h-4 w-16 rounded bg-slate-200" />
        </div>
        <div className="h-5 w-3/4 rounded bg-slate-200" />
        <div className="h-4 w-1/2 rounded bg-slate-200" />
      </div>
    </div>
  );
}

export default function HomePage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const { data } = await api.get('/events/top');
      setEvents(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Failed to fetch events:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <section className="text-center mb-8 rounded-3xl border border-indigo-100 bg-gradient-to-r from-violet-50 to-indigo-50 py-8 px-4">
        <h1 className="text-3xl sm:text-4xl md:text-5xl font-extrabold tracking-tight text-gray-900 mb-3">Tìm & Đặt Vé Sự Kiện</h1>
        <p className="text-gray-600 text-lg">Khám phá các sự kiện hấp dẫn sắp diễn ra</p>
      </section>

      <section aria-labelledby="featured-events-heading">
        <div className="mb-6 flex items-center justify-between">
          <h2 id="featured-events-heading" className="text-2xl font-bold text-gray-900 sm:text-3xl">Sự kiện nổi bật</h2>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {Array.from({ length: 8 }).map((_, index) => (
              <SkeletonEventCard key={index} />
            ))}
          </div>
        ) : events.length === 0 ? (
          <div className="text-center py-16">
            <div className="max-w-md mx-auto rounded-3xl border border-dashed border-indigo-200 bg-indigo-50 px-8 py-10">
              <div className="text-5xl mb-4">🎫</div>
              <p className="text-gray-600 text-lg font-semibold">Hiện chưa có sự kiện nổi bật.</p>
              <p className="text-gray-500 mt-2">Hãy khám phá tất cả sự kiện để tìm trải nghiệm phù hợp.</p>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 md:gap-7">
            {events.slice(0, 12).map((event) => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>
        )}
      </section>

      <div className="mt-10 text-center">
        <Link to="/su-kien" className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-6 py-3 font-semibold text-white shadow-sm transition hover:bg-indigo-700">
          Khám phá tất cả sự kiện <ArrowRight className="h-4 w-4" />
        </Link>
      </div>
    </div>
  );
}
