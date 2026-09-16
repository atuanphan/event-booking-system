import { useEffect, useState } from 'react';
import { CalendarDays, Filter, Search, SlidersHorizontal } from 'lucide-react';
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
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const pageSize = 8;
  const [filters, setFilters] = useState({
    name: '',
    category: CATEGORY_SLUGS[searchParams.get('category')] || searchParams.get('category') || 'Tất cả',
    date: '',
    maxPrice: '',
    location: '',
  });

  useEffect(() => {
    loadEvents(0);
  }, [filters.name]);

  const loadEvents = async (nextPage) => {
    if (nextPage === 0) setLoading(true);
    else setLoadingMore(true);

    try {
      const { data } = await api.get('/events', {
        params: {
          name: filters.name || undefined,
          page: nextPage,
          pageSize,
        },
      });
      const responseEvents = Array.isArray(data)
        ? data
        : data?.list || data?.content || data?.items || data?.data || [];
      const totalPages = data?.totalPage ?? data?.totalPages;
      const isLastPage = data?.last === true || (
        Number.isInteger(totalPages) && nextPage >= totalPages - 1
      );

      let appendedCount = responseEvents.length;
      setEvents((currentEvents) => {
        if (nextPage === 0) return responseEvents;
        const existingIds = new Set(currentEvents.map((event) => event.id));
        const newEvents = responseEvents.filter((event) => !existingIds.has(event.id));
        appendedCount = newEvents.length;
        return [
          ...currentEvents,
          ...newEvents,
        ];
      });
      setPage(nextPage);
      setHasMore(isLastPage || responseEvents.length < pageSize || appendedCount === 0 ? false : true);
    } catch (error) {
      console.error('Failed to fetch events:', error);
      if (nextPage === 0) setEvents([]);
    } finally {
      if (nextPage === 0) setLoading(false);
      else setLoadingMore(false);
    }
  };

  const handleLoadMore = () => {
    if (!loadingMore && hasMore) loadEvents(page + 1);
  };

  const visibleEvents = events.filter((event) => {
    const price = event.ticketTypes?.length ? Math.min(...event.ticketTypes.map((ticket) => ticket.price)) : 0;
    const dateMatches = !filters.date || `${event.startTime || ''}`.startsWith(filters.date);
    const locationMatches = !filters.location || `${event.location || event.venue?.name || ''}`.toLowerCase().includes(filters.location.toLowerCase());
    const priceMatches = !filters.maxPrice || price <= Number(filters.maxPrice);
    return matchesCategory(event, filters.category) && dateMatches && locationMatches && priceMatches;
  });

  const updateFilter = (key, value) => setFilters((current) => ({ ...current, [key]: value }));

  return (
    <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6 lg:px-8">
      <header className="mb-5 pt-2">
        <p className="mb-1 text-xs font-bold uppercase tracking-[0.18em] text-indigo-600">Khám phá</p>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">Tất cả sự kiện</h1>
        <p className="mt-2 max-w-2xl text-sm text-slate-600 sm:text-[15px]">Tìm trải nghiệm tiếp theo phù hợp với lịch trình và sở thích của bạn.</p>
      </header>

      <div className="mb-6 rounded-xl border border-slate-200 bg-white p-2 shadow-sm shadow-slate-200/40">
        <div className="flex flex-wrap items-center gap-2 lg:flex-nowrap">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-slate-200 bg-slate-50 text-indigo-600">
            <SlidersHorizontal className="h-4 w-4" />
          </div>

          <label className="relative min-w-[180px] flex-[2.2]">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
            <input value={filters.name} onChange={(event) => updateFilter('name', event.target.value)} placeholder="Tìm sự kiện..." className="h-10 w-full rounded-xl border border-slate-200 bg-white py-2 pl-9 pr-3 text-sm text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100" />
          </label>

          <select value={filters.category} onChange={(event) => updateFilter('category', event.target.value)} className="h-10 min-w-[150px] flex-1 rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100">
            {['Tất cả', 'Âm nhạc', 'Hội nghị', 'Giải trí', 'Thể thao'].map((category) => <option key={category}>{category}</option>)}
          </select>

          <label className="relative min-w-[150px] flex-1">
            <CalendarDays className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
            <input type="date" value={filters.date} onChange={(event) => updateFilter('date', event.target.value)} className="h-10 w-full rounded-xl border border-slate-200 bg-white py-2 pl-9 pr-3 text-sm text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100" />
          </label>

          <input type="number" min="0" value={filters.maxPrice} onChange={(event) => updateFilter('maxPrice', event.target.value)} placeholder="Giá tối đa" className="h-10 min-w-[150px] flex-1 rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100" />
          <input value={filters.location} onChange={(event) => updateFilter('location', event.target.value)} placeholder="Địa điểm" className="h-10 min-w-[150px] flex-1 rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100" />
        </div>
      </div>

      {loading ? <div className="py-16 text-center text-slate-500">Đang tải sự kiện...</div> : visibleEvents.length === 0 ? (
        <div className="rounded-3xl border border-dashed border-indigo-200 bg-indigo-50 px-8 py-16 text-center"><Filter className="mx-auto mb-4 h-10 w-10 text-indigo-400" /><p className="font-semibold text-slate-700">Không tìm thấy sự kiện phù hợp.</p><p className="mt-2 text-slate-500">Thử thay đổi từ khóa hoặc bộ lọc.</p></div>
      ) : (
        <>
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">{visibleEvents.map((event) => <EventCard key={event.id} event={event} />)}</div>
          {hasMore && (
            <div className="mt-10 flex items-center gap-4 text-slate-300">
              <span aria-hidden="true" className="h-px flex-1 bg-slate-200" />
              <button
                type="button"
                onClick={handleLoadMore}
                disabled={loadingMore}
                className="shrink-0 bg-transparent px-1 font-semibold text-slate-600 transition hover:text-indigo-600 hover:underline disabled:cursor-not-allowed disabled:text-slate-400"
              >
                {loadingMore ? 'Đang tải...' : 'Xem thêm'}
              </button>
              <span aria-hidden="true" className="h-px flex-1 bg-slate-200" />
            </div>
          )}
        </>
      )}
    </div>
  );
}
