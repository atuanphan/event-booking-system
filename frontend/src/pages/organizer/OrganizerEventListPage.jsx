import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { CalendarDays, MapPin, Plus, Search, Ticket, Eye, Pencil, Ban } from 'lucide-react';
import toast from 'react-hot-toast';
import { cancelEvent, getMyEvents } from '../../services/organizerApi';

const STATUS_LABELS = { UPCOMING: 'Sắp diễn ra', ONGOING: 'Đang diễn ra', FINISHED: 'Đã kết thúc', DRAFT: 'Bản nháp', CANCELLED: 'Đã hủy' };
const STATUS_STYLES = { UPCOMING: 'bg-blue-50 text-blue-700', ONGOING: 'bg-emerald-50 text-emerald-700', FINISHED: 'bg-gray-100 text-gray-600', DRAFT: 'bg-amber-50 text-amber-700', CANCELLED: 'bg-rose-50 text-rose-700' };
const dateTime = (value) => new Date(value).toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });

export default function OrganizerEventListPage() {
  const [events, setEvents] = useState([]);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');

  const loadEvents = () => getMyEvents().then(setEvents);
  useEffect(() => { loadEvents(); }, []);

  const filteredEvents = useMemo(() => events.filter((event) => (
    (!status || event.status === status) && event.name.toLowerCase().includes(search.trim().toLowerCase())
  )), [events, search, status]);

  const handleCancel = async (event) => {
    if (!window.confirm(`Hủy sự kiện "${event.name}"?`)) return;
    await cancelEvent(event.id);
    await loadEvents();
    toast.success('Đã cập nhật trạng thái sự kiện');
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3">
        <div><p className="text-sm text-gray-500">Quản lý và theo dõi các chương trình của bạn</p><h2 className="text-2xl font-semibold text-gray-900 mt-1">Sự kiện của tôi</h2></div>
        <Link to="/organizer/events/create" className="inline-flex items-center justify-center gap-2 bg-gray-900 text-white px-4 py-2.5 rounded-md text-sm hover:bg-gray-700"><Plus className="w-4 h-4" />Tạo sự kiện mới</Link>
      </div>

      <div className="bg-white border border-gray-200 rounded-md p-4 flex flex-col sm:flex-row gap-3">
        <label className="relative flex-1"><Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Tìm theo tên sự kiện" className="w-full pl-9 pr-3 py-2 border border-gray-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" /></label>
        <select value={status} onChange={(event) => setStatus(event.target.value)} className="sm:w-52 px-3 py-2 border border-gray-200 rounded-md text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500" aria-label="Lọc theo trạng thái">
          <option value="">Tất cả trạng thái</option>{Object.entries(STATUS_LABELS).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
        </select>
      </div>

      <div className="bg-white border border-gray-200 rounded-md overflow-hidden">
        <div className="hidden lg:grid grid-cols-[minmax(0,2fr)_1.2fr_1.2fr_1fr_150px] gap-4 px-5 py-3 bg-gray-50 text-xs font-semibold uppercase text-gray-500">
          <span>Sự kiện</span><span>Thời gian</span><span>Địa điểm</span><span>Vé đã bán</span><span>Trạng thái</span>
        </div>
        {filteredEvents.length === 0 ? <div className="py-14 text-center text-sm text-gray-400">Không tìm thấy sự kiện phù hợp</div> : filteredEvents.map((event) => {
          const total = event.ticketTypes.reduce((sum, ticket) => sum + ticket.totalQuantity, 0);
          const sold = event.ticketTypes.reduce((sum, ticket) => sum + ticket.totalQuantity - ticket.availableQuantity, 0);
          return <article key={event.id} className="grid grid-cols-1 lg:grid-cols-[minmax(0,2fr)_1.2fr_1.2fr_1fr_150px] gap-3 lg:gap-4 px-4 sm:px-5 py-4 border-t border-gray-100 items-center">
            <div className="min-w-0"><Link to={`/organizer/events/${event.id}`} className="font-medium text-gray-900 hover:text-emerald-700 line-clamp-2">{event.name}</Link><div className="flex lg:hidden items-center gap-2 mt-2"><span className={`text-xs px-2 py-1 rounded-sm ${STATUS_STYLES[event.status]}`}>{STATUS_LABELS[event.status]}</span><span className="text-xs text-gray-500">{sold.toLocaleString('vi-VN')} / {total.toLocaleString('vi-VN')} vé</span></div></div>
            <div className="hidden lg:flex items-center gap-2 text-sm text-gray-600"><CalendarDays className="w-4 h-4 text-gray-400 shrink-0" />{dateTime(event.startTime)}</div>
            <div className="flex items-center gap-2 text-sm text-gray-500 lg:text-gray-600"><MapPin className="w-4 h-4 text-gray-400 shrink-0" />{event.venue?.name || 'Chưa chọn địa điểm'}</div>
            <div className="hidden lg:flex items-center gap-2 text-sm text-gray-600"><Ticket className="w-4 h-4 text-gray-400" />{sold.toLocaleString('vi-VN')} / {total.toLocaleString('vi-VN')}</div>
            <div className="hidden lg:block"><span className={`text-xs px-2 py-1 rounded-sm ${STATUS_STYLES[event.status]}`}>{STATUS_LABELS[event.status]}</span></div>
            <div className="flex items-center gap-1 lg:justify-end pt-2 lg:pt-0 border-t lg:border-0 border-gray-100">
              <Link to={`/organizer/events/${event.id}`} title="Xem chi tiết" aria-label="Xem chi tiết" className="p-2 text-gray-500 hover:bg-gray-100 rounded-md"><Eye className="w-4 h-4" /></Link>
              <Link to={`/organizer/events/${event.id}/edit`} title="Sửa sự kiện" aria-label="Sửa sự kiện" className="p-2 text-gray-500 hover:bg-gray-100 rounded-md"><Pencil className="w-4 h-4" /></Link>
              {!['CANCELLED', 'FINISHED'].includes(event.status) && <button type="button" onClick={() => handleCancel(event)} title="Hủy sự kiện" aria-label="Hủy sự kiện" className="p-2 text-gray-500 hover:bg-rose-50 hover:text-rose-700 rounded-md"><Ban className="w-4 h-4" /></button>}
            </div>
          </article>;
        })}
      </div>
      <p className="text-xs text-gray-400">Dữ liệu minh họa, các thay đổi chỉ được lưu trong phiên hiện tại.</p>
    </div>
  );
}