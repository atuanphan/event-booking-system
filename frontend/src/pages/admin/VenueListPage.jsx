import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Trash2, Edit } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

export default function VenueListPage() {
  const [venues, setVenues] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [selectedIds, setSelectedIds] = useState([]);
  const [deleteModal, setDeleteModal] = useState(false);

  useEffect(() => { fetchVenues(); }, [page]);

  const fetchVenues = async () => {
    setLoading(true);
    try {
      const { data } = await api.post('/admin/venues', { page, pageSize: 10 });
      setVenues(data.list || []);
      setTotalPages(data.totalPage || 1);
    } catch {
      toast.error('Không thể tải danh sách địa điểm');
    } finally {
      setLoading(false);
    }
  };

  const toggleSelect = (id) => {
    setSelectedIds((prev) => prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]);
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/admin/venues/${selectedIds.join(',')}`);
      toast.success('Xóa thành công');
      setSelectedIds([]);
      setDeleteModal(false);
      fetchVenues();
    } catch {
      toast.error('Xóa thất bại');
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-lg font-semibold">Danh sách địa điểm</h3>
        <div className="flex gap-2">
          {selectedIds.length > 0 && (
            <button onClick={() => setDeleteModal(true)}
              className="flex items-center gap-1 px-4 py-2 bg-red-500 text-white rounded-lg text-sm hover:bg-red-600">
              <Trash2 className="w-4 h-4" /> Xóa ({selectedIds.length})
            </button>
          )}
          <Link to="/admin/venues/create"
            className="flex items-center gap-1 px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm hover:bg-indigo-700">
            <Plus className="w-4 h-4" /> Thêm địa điểm
          </Link>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-left">
            <tr>
              <th className="p-3 w-10">
                <input type="checkbox" checked={selectedIds.length === venues.length && venues.length > 0}
                  onChange={() => setSelectedIds(selectedIds.length === venues.length ? [] : venues.map((v) => v.id))} />
              </th>
              <th className="p-3">Tên</th>
              <th className="p-3">Địa chỉ</th>
              <th className="p-3">Sức chứa</th>
              <th className="p-3 w-20">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={5} className="p-8 text-center text-gray-400">Đang tải...</td></tr>
            ) : venues.length === 0 ? (
              <tr><td colSpan={5} className="p-8 text-center text-gray-400">Không có địa điểm</td></tr>
            ) : venues.map((venue) => (
              <tr key={venue.id} className="border-t hover:bg-gray-50">
                <td className="p-3">
                  <input type="checkbox" checked={selectedIds.includes(venue.id)} onChange={() => toggleSelect(venue.id)} />
                </td>
                <td className="p-3 font-medium">{venue.name}</td>
                <td className="p-3 text-gray-500">{venue.address}</td>
                <td className="p-3">{venue.capacity?.toLocaleString()}</td>
                <td className="p-3">
                  <Link to={`/admin/venues/edit/${venue.id}`} className="text-indigo-600 hover:text-indigo-800">
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
        <p className="mb-4">Bạn có chắc muốn xóa {selectedIds.length} địa điểm?</p>
        <div className="flex justify-end gap-2">
          <button onClick={() => setDeleteModal(false)} className="px-4 py-2 border rounded-lg text-sm">Hủy</button>
          <button onClick={handleDelete} className="px-4 py-2 bg-red-500 text-white rounded-lg text-sm hover:bg-red-600">Xóa</button>
        </div>
      </Modal>
    </div>
  );
}
