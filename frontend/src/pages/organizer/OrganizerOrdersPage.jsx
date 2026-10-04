import { useEffect, useState } from 'react';
import { Download, Search } from 'lucide-react';
import toast from 'react-hot-toast';
import Pagination from '../../components/Pagination';
import { getMyEvents, getOrders } from '../../services/organizerApi';

const money = (value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value || 0);
const statuses = { PENDING: 'Chờ thanh toán', PROCESSING: 'Đang xử lý', COMPLETED: 'Đã thanh toán', CANCELLED: 'Đã hủy', REFUNDED: 'Đã hoàn tiền' };
const statusStyles = { COMPLETED: 'bg-emerald-50 text-emerald-700', PENDING: 'bg-amber-50 text-amber-700', PROCESSING: 'bg-blue-50 text-blue-700', CANCELLED: 'bg-gray-100 text-gray-600', REFUNDED: 'bg-purple-50 text-purple-700' };
const PAGE_SIZE = 8;

export default function OrganizerOrdersPage() {
  const [events, setEvents] = useState([]);
  const [orders, setOrders] = useState([]);
  const [filters, setFilters] = useState({ eventId: '', status: '', startDate: '', endDate: '' });
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyEvents().then(setEvents).catch((error) => {
      toast.error(error.response?.data?.message || 'Không thể tải danh sách sự kiện');
    });
  }, []);

  useEffect(() => {
    setLoading(true);
    getOrders(filters, page, PAGE_SIZE).then((result) => {
      setOrders(result.content);
      setTotalPages(Math.max(1, result.totalPages));
      setTotalElements(result.totalElements);
    }).catch((error) => {
      setOrders([]);
      setTotalPages(1);
      setTotalElements(0);
      toast.error(error.response?.data?.message || 'Không thể tải danh sách đơn hàng');
    }).finally(() => setLoading(false));
  }, [filters, page]);

  const updateFilter = (key, value) => {
    setFilters((current) => ({ ...current, [key]: value }));
    setPage(1);
  };

  const exportCsv = () => {
    const rows = [['Mã đơn', 'Sự kiện', 'Khách hàng', 'Email', 'Số vé', 'Tổng tiền', 'Trạng thái', 'Thời gian'], ...orders.map((order) => [order.id, order.eventName, order.customerName, order.customerEmail, order.ticketQuantity, order.totalAmount, statuses[order.status], order.createdAt])];
    const csv = `\uFEFF${rows.map((row) => row.map((cell) => `"${String(cell).replaceAll('"', '""')}"`).join(',')).join('\n')}`;
    const link = document.createElement('a');
    link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }));
    link.download = 'don-hang-organizer.csv';
    link.click();
    URL.revokeObjectURL(link.href);
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3"><div><p className="text-sm text-gray-500">Theo dõi thanh toán từ các sự kiện của bạn</p><h2 className="text-2xl font-semibold text-gray-900 mt-1">Đơn hàng</h2></div><button type="button" onClick={exportCsv} className="inline-flex items-center justify-center gap-2 px-3 py-2.5 border border-gray-200 rounded-md text-sm hover:bg-white"><Download className="w-4 h-4" />Xuất CSV</button></div>
      <section className="bg-white border border-gray-200 rounded-md p-4 grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-3">
        <label className="text-xs font-medium text-gray-500">Sự kiện<select value={filters.eventId} onChange={(e) => updateFilter('eventId', e.target.value)} className="mt-1 w-full px-3 py-2 border border-gray-200 rounded-md text-sm bg-white"><option value="">Tất cả sự kiện</option>{events.map((event) => <option key={event.id} value={event.id}>{event.name}</option>)}</select></label>
        <label className="text-xs font-medium text-gray-500">Trạng thái<select value={filters.status} onChange={(e) => updateFilter('status', e.target.value)} className="mt-1 w-full px-3 py-2 border border-gray-200 rounded-md text-sm bg-white"><option value="">Tất cả trạng thái</option>{Object.entries(statuses).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></label>
        <label className="text-xs font-medium text-gray-500">Từ ngày<input type="date" value={filters.startDate} onChange={(e) => updateFilter('startDate', e.target.value)} className="mt-1 w-full px-3 py-2 border border-gray-200 rounded-md text-sm" /></label>
        <label className="text-xs font-medium text-gray-500">Đến ngày<input type="date" value={filters.endDate} onChange={(e) => updateFilter('endDate', e.target.value)} className="mt-1 w-full px-3 py-2 border border-gray-200 rounded-md text-sm" /></label>
      </section>
      <section className="bg-white border border-gray-200 rounded-md overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between"><h3 className="font-medium text-gray-900">Danh sách giao dịch</h3><span className="text-sm text-gray-400">{totalElements} đơn</span></div>
        {loading ? <div className="py-12 text-center text-sm text-gray-400">Đang tải đơn hàng...</div> : orders.length === 0 ? <div className="py-12 text-center text-sm text-gray-400"><Search className="w-5 h-5 mx-auto mb-2" />Không có đơn hàng phù hợp</div> : <div className="overflow-x-auto"><table className="w-full min-w-[850px] text-sm text-left"><thead className="bg-gray-50 text-xs uppercase text-gray-500"><tr><th className="px-5 py-3">Mã đơn</th><th className="px-5 py-3">Sự kiện / Khách hàng</th><th className="px-5 py-3">Số vé</th><th className="px-5 py-3">Tổng tiền</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3">Thời gian đặt</th></tr></thead><tbody>{orders.map((order) => <tr key={order.id} className="border-t border-gray-100 hover:bg-gray-50/60"><td className="px-5 py-4 font-medium text-gray-800">{order.id}</td><td className="px-5 py-4"><p className="font-medium text-gray-800">{order.eventName}</p><p className="text-xs text-gray-500 mt-1">{order.customerName}{order.customerEmail && ` · ${order.customerEmail}`}</p></td><td className="px-5 py-4 text-gray-600">{order.ticketQuantity}</td><td className="px-5 py-4 font-medium text-gray-800">{money(order.totalAmount)}</td><td className="px-5 py-4"><span className={`px-2 py-1 text-xs rounded-sm ${statusStyles[order.status] || 'bg-gray-100 text-gray-600'}`}>{statuses[order.status] || order.status}</span></td><td className="px-5 py-4 text-gray-500">{order.createdAt ? new Date(order.createdAt).toLocaleString('vi-VN') : '—'}</td></tr>)}</tbody></table></div>}
      </section>
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      <p className="text-xs text-gray-400">Đơn hàng được lọc theo các sự kiện của Organizer đang đăng nhập.</p>
    </div>
  );
}