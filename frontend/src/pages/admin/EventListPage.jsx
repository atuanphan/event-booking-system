import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Trash2, Edit, Search } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const STATUS_LABELS = {
  UPCOMING: 'Sắp diễn ra', ONGOING: 'Đang diễn ra', FINISHED: 'Đã kết thúc',
  DRAFT: 'Dự kiến', CANCELLED: 'Đã hủy',
};

function formatDateTime(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

export default function EventListPage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [search, setSearch] = useState('');
  const [selectedIds, setSelectedIds] = useState([]);
  const [deleteModal, setDeleteModal] = useState(false);

  useEffect(() => { fetchEvents(); }, [page]);

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const { data } = await api.get('/admin/events', {
        params: {
          page,
          pageSize: 10,
          name: search || undefined,
        },
      });

      setEvents(data.list || []);
      setTotalPages(data.totalPage || 1);
    } catch {
      toast.error('Không thể tải danh sách sự kiện');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => { e.preventDefault(); setPage(1); fetchEvents(); };

  const toggleSelect = (id) => {
    setSelectedIds((prev) => prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]);
  };

  const toggleSelectAll = () => {
    if (selectedIds.length === events.length) setSelectedIds([]);
    else setSelectedIds(events.map((e) => e.id));
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/admin/events/${selectedIds.join(',')}`);
      toast.success('Xóa thành công');
      setSelectedIds([]);
      setDeleteModal(false);
      fetchEvents();
    } catch {
      toast.error('Xóa thất bại');
    }
  };

  return (
    <div>
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
        <form onSubmit={handleSearch} className="flex gap-2">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Tìm sự kiện..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="pl-9 pr-4 py-2 border rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
            />
          </div>
          <button type="submit" className="px-4 py-2 bg-gray-100 rounded-lg text-sm hover:bg-gray-200">Tìm</button>
        </form>
        <div className="flex gap-2">
          {selectedIds.length > 0 && (
            <button
              onClick={() => setDeleteModal(true)}
              className="flex items-center gap-1 px-4 py-2 bg-red-500 text-white rounded-lg text-sm hover:bg-red-600"
            >
              <Trash2 className="w-4 h-4" /> Xóa ({selectedIds.length})
            </button>
          )}
          <Link
            to="/admin/events/create"
            className="flex items-center gap-1 px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm hover:bg-indigo-700"
          >
            <Plus className="w-4 h-4" /> Thêm sự kiện
          </Link>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-left">
            <tr>
              <th className="p-3 w-10">
                <input type="checkbox" checked={selectedIds.length === events.length && events.length > 0} onChange={toggleSelectAll} />
              </th>
              <th className="p-3">Tên sự kiện</th>
              <th className="p-3">Trạng thái</th>
              <th className="p-3">Ngày bắt đầu</th>
              <th className="p-3">Ngày kết thúc</th>
              <th className="p-3 w-20">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6} className="p-8 text-center text-gray-400">Đang tải...</td></tr>
            ) : events.length === 0 ? (
              <tr><td colSpan={6} className="p-8 text-center text-gray-400">Không có sự kiện</td></tr>
            ) : events.map((event) => (
              <tr key={event.id} className="border-t hover:bg-gray-50">
                <td className="p-3">
                  <input type="checkbox" checked={selectedIds.includes(event.id)} onChange={() => toggleSelect(event.id)} />
                </td>
                <td className="p-3 font-medium">{event.name}</td>
                <td className="p-3">
                  <span className="bg-indigo-100 text-indigo-700 text-xs px-2 py-1 rounded-full">
                    {STATUS_LABELS[event.status] || event.status}
                  </span>
                </td>
                <td className="p-3 text-gray-500">{formatDateTime(event.startTime)}</td>
                <td className="p-3 text-gray-500">{formatDateTime(event.endTime)}</td>
                <td className="p-3">
                  <Link to={`/admin/events/edit/${event.id}`} className="text-indigo-600 hover:text-indigo-800">
                    <Edit className="w-4 h-4" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />

      <Modal isOpen={deleteModal} onClose={() => setDeleteModal(false)} title="Xác nhận xóa">
        <p className="mb-4">Bạn có chắc muốn xóa {selectedIds.length} sự kiện?</p>
        <div className="flex justify-end gap-2">
          <button onClick={() => setDeleteModal(false)} className="px-4 py-2 border rounded-lg text-sm">Hủy</button>
          <button onClick={handleDelete} className="px-4 py-2 bg-red-500 text-white rounded-lg text-sm hover:bg-red-600">Xóa</button>
        </div>
      </Modal>
    </div>
  );
}
