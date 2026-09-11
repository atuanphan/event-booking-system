import { useEffect, useState } from 'react';
import { Filter, Search, SlidersHorizontal } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';
import api from '../../api/axios';
import EventCard from '../../components/EventCard';

const CATEGORY_RULES = {
  'Âm nhạc': /music|nhạc|concert|festival|show|band|live/i,
  'Hội nghị': /tech|conference|seminar|workshop|dev|software|ai|cloud/i,
  'Giải trí': /comedy|fun|hài|entertainment|show|game/i,
  'Thể thao': /sport|football|tennis|running|marathon|athlete/i,
};

const CATEGORY_SLUGS = {
  'am-nhac': 'Âm nhạc',
  'hoi-nghi': 'Hội nghị',
  'giai-tri': 'Giải trí',
  'the-thao': 'Thể thao',
};

function matchesCategory(event, category) {
  if (!category || category === 'Tất cả') return true;
  return CATEGORY_RULES[category]?.test(`${event.name || ''} ${event.description || ''}`) ?? true;
}

export default function EventsPage() {
  const [searchParams] = useSearchParams();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    name: '',
    category: CATEGORY_SLUGS[searchParams.get('category')] || searchParams.get('category') || 'Tất cả',
    date: '',
    maxPrice: '',
    location: '',
  });

  useEffect(() => {
    const loadEvents = async () => {
      setLoading(true);
      try {
        const { data } = await api.get('/events', { params: { name: filters.name || undefined } });
        setEvents(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error('Failed to fetch events:', error);
        setEvents([]);
      } finally {
        setLoading(false);
      }
    };
    loadEvents();
  }, [filters.name]);

  const visibleEvents = events.filter((event) => {
    const price = event.ticketTypes?.length ? Math.min(...event.ticketTypes.map((ticket) => ticket.price)) : 0;
    const dateMatches = !filters.date || `${event.startTime || ''}`.startsWith(filters.date);
    const locationMatches = !filters.location || `${event.location || event.venue?.name || ''}`.toLowerCase().includes(filters.location.toLowerCase());
    const priceMatches = !filters.maxPrice || price <= Number(filters.maxPrice);
    return matchesCategory(event, filters.category) && dateMatches && locationMatches && priceMatches;
  });

  const updateFilter = (key, value) => setFilters((current) => ({ ...current, [key]: value }));

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <div className="mb-8 rounded-3xl border border-indigo-100 bg-gradient-to-br from-violet-50 to-indigo-50 px-6 py-10 sm:px-10">
        <p className="mb-2 text-sm font-bold uppercase tracking-wider text-indigo-600">Khám phá</p>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">Tất cả sự kiện</h1>
        <p className="mt-3 max-w-2xl text-slate-600">Tìm trải nghiệm tiếp theo phù hợp với lịch trình và sở thích của bạn.</p>
      </div>

      <div className="mb-8 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
        <div className="mb-4 flex items-center gap-2 font-bold text-slate-900"><SlidersHorizontal className="h-5 w-5 text-indigo-600" /> Bộ lọc</div>
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-5">
          <label className="relative lg:col-span-2">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
            <input value={filters.name} onChange={(event) => updateFilter('name', event.target.value)} placeholder="Tìm sự kiện..." className="w-full rounded-xl border border-slate-200 py-2.5 pl-9 pr-3 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100" />
          </label>
          <select value={filters.category} onChange={(event) => updateFilter('category', event.target.value)} className="rounded-xl border border-slate-200 px-3 py-2.5 outline-none focus:border-indigo-500">
            {['Tất cả', 'Âm nhạc', 'Hội nghị', 'Giải trí', 'Thể thao'].map((category) => <option key={category}>{category}</option>)}
          </select>
          <input type="date" value={filters.date} onChange={(event) => updateFilter('date', event.target.value)} className="rounded-xl border border-slate-200 px-3 py-2.5 outline-none focus:border-indigo-500" />
          <input type="number" min="0" value={filters.maxPrice} onChange={(event) => updateFilter('maxPrice', event.target.value)} placeholder="Giá tối đa" className="rounded-xl border border-slate-200 px-3 py-2.5 outline-none focus:border-indigo-500" />
          <input value={filters.location} onChange={(event) => updateFilter('location', event.target.value)} placeholder="Địa điểm" className="rounded-xl border border-slate-200 px-3 py-2.5 outline-none focus:border-indigo-500" />
        </div>
      </div>

      {loading ? <div className="py-16 text-center text-slate-500">Đang tải sự kiện...</div> : visibleEvents.length === 0 ? (
        <div className="rounded-3xl border border-dashed border-indigo-200 bg-indigo-50 px-8 py-16 text-center"><Filter className="mx-auto mb-4 h-10 w-10 text-indigo-400" /><p className="font-semibold text-slate-700">Không tìm thấy sự kiện phù hợp.</p><p className="mt-2 text-slate-500">Thử thay đổi từ khóa hoặc bộ lọc.</p></div>
      ) : <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">{visibleEvents.map((event) => <EventCard key={event.id} event={event} />)}</div>}
    </div>
  );
}