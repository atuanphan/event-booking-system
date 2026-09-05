import { useState, useEffect } from 'react';
import { Search } from 'lucide-react';
import api from '../../api/axios';
import EventCard from '../../components/EventCard';
import Pagination from '../../components/Pagination';
import LoadingSpinner from '../../components/LoadingSpinner';

export default function HomePage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchEvents();
  }, [page]);

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const { data } = await api.post('/admin/events', {
        page,
        pageSize: 8,
        name: search || undefined,
      });
      setEvents(data.list || []);
      setTotalPages(data.totalPage || 1);
    } catch (err) {
      console.error('Failed to fetch events:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(1);
    fetchEvents();
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Hero */}
      <div className="text-center mb-10">
        <h1 className="text-4xl font-bold text-gray-900 mb-3">Tìm & Đặt Vé Sự Kiện</h1>
        <p className="text-gray-500 text-lg">Khám phá các sự kiện hấp dẫn sắp diễn ra</p>
      </div>

      {/* Search */}
      <form onSubmit={handleSearch} className="max-w-xl mx-auto mb-10">
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

      {/* Events grid */}
      {loading ? (
        <LoadingSpinner />
      ) : events.length === 0 ? (
        <p className="text-center text-gray-500 py-12">Không tìm thấy sự kiện nào.</p>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {events.map((event) => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>
          <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
