import { useState, useEffect } from 'react';
import { ArrowRight, Search } from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';
import EventCard from '../../components/EventCard';

const CATEGORY_CHIPS = ['Tất cả', 'Âm nhạc', 'Hội nghị', 'Giải trí', 'Thể thao'];

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
  const [search, setSearch] = useState('');
  const [activeCategory, setActiveCategory] = useState('Tất cả');

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async (category = activeCategory) => {
    setLoading(true);
    try {
      const { data } = await api.get('/events', {
        params: {
          name: search || undefined,
        },
      });
      const source = Array.isArray(data) ? data : [];
      const filtered = category === 'Tất cả'
        ? source
        : source.filter((event) => {
            const categoryText = `${event.name || ''} ${event.description || ''}`.toLowerCase();
        if (category === 'Âm nhạc') return /music|nhạc|concert|festival|show|band|live/.test(categoryText);
        if (category === 'Hội nghị') return /tech|conference|seminar|workshop|dev|software|ai|cloud/.test(categoryText);
        if (category === 'Giải trí') return /comedy|fun|hài|entertainment|show|music|game/.test(categoryText);
        if (category === 'Thể thao') return /sport|football|tennis|running|marathon|athlete/.test(categoryText);
            return true;
          });
      setEvents(filtered);
    } catch (err) {
      console.error('Failed to fetch events:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchEvents();
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <section className="text-center mb-8 rounded-3xl border border-indigo-100 bg-gradient-to-r from-violet-50 to-indigo-50 py-8 px-4">
        <h1 className="text-3xl sm:text-4xl md:text-5xl font-extrabold tracking-tight text-gray-900 mb-3">Tìm & Đặt Vé Sự Kiện</h1>
        <p className="text-gray-600 text-lg">Khám phá các sự kiện hấp dẫn sắp diễn ra</p>
      </section>

      <form onSubmit={handleSearch} className="max-w-2xl mx-auto mb-6">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Tìm sự kiện..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
            />
          </div>
          <button
            type="submit"
            className="px-6 py-3 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition font-medium"
          >
            Tìm kiếm
          </button>
        </div>
      </form>

      <div className="flex flex-wrap justify-center gap-2 mb-8">
        {CATEGORY_CHIPS.map((chip) => (
          <button
            key={chip}
            type="button"
            className={`px-4 py-2 rounded-full border text-sm font-medium transition ${activeCategory === chip ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-white text-gray-600 border-gray-200 hover:bg-indigo-50 hover:text-indigo-600'}`}
            onClick={() => {
              setActiveCategory(chip);
              fetchEvents(chip);
            }}
          >
            {chip}
          </button>
        ))}
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
            <p className="text-gray-600 text-lg font-semibold">Không tìm thấy sự kiện nào.</p>
            <p className="text-gray-500 mt-2">Thử thay đổi từ khóa hoặc chọn một danh mục khác.</p>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 md:gap-7">
          {events.slice(0, 12).map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </div>
      )}

      {!loading && events.length > 0 && (
        <div className="mt-10 text-center">
          <Link to="/su-kien" className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-6 py-3 font-semibold text-white shadow-sm transition hover:bg-indigo-700">
            Khám phá tất cả sự kiện <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      )}
    </div>
  );
}
