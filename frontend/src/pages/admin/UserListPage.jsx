import { useState, useEffect } from 'react';
import { Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';
import Modal from '../../components/Modal';

export default function UserListPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => { fetchUsers(); }, []);

  const fetchUsers = async () => {
    try {
      const { data } = await api.get('/admin/users');
      setUsers(data || []);
    } catch {
      toast.error('Không thể tải danh sách người dùng');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.delete(`/admin/users/${deleteTarget.id}/${deleteTarget.email}`);
      toast.success('Xóa thành công');
      setDeleteTarget(null);
      fetchUsers();
    } catch {
      toast.error('Xóa thất bại');
    }
  };

  return (
    <div>
      <h3 className="text-lg font-semibold mb-6">Danh sách người dùng</h3>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-left">
            <tr>
              <th className="p-3">Họ tên</th>
              <th className="p-3">Email</th>
              <th className="p-3">Vai trò</th>
              <th className="p-3">Nhà cung cấp</th>
              <th className="p-3 w-20">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={5} className="p-8 text-center text-gray-400">Đang tải...</td></tr>
            ) : users.length === 0 ? (
              <tr><td colSpan={5} className="p-8 text-center text-gray-400">Không có người dùng</td></tr>
            ) : users.map((user) => (
              <tr key={user.id} className="border-t hover:bg-gray-50">
                <td className="p-3 font-medium">{user.fullname}</td>
                <td className="p-3 text-gray-500">{user.email}</td>
                <td className="p-3">
                  <div className="flex gap-1 flex-wrap">
                    {user.roles?.map((role) => (
                      <span key={role} className="bg-indigo-100 text-indigo-700 text-xs px-2 py-0.5 rounded-full">{role}</span>
                    ))}
                  </div>
                </td>
                <td className="p-3 text-gray-500">{user.provider || 'LOCAL'}</td>
                <td className="p-3">
                  <button onClick={() => setDeleteTarget(user)} className="text-red-500 hover:text-red-700">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal isOpen={!!deleteTarget} onClose={() => setDeleteTarget(null)} title="Xác nhận xóa">
        <p className="mb-4">Bạn có chắc muốn xóa người dùng <strong>{deleteTarget?.fullname}</strong>?</p>
        <div className="flex justify-end gap-2">
          <button onClick={() => setDeleteTarget(null)} className="px-4 py-2 border rounded-lg text-sm">Hủy</button>
          <button onClick={handleDelete} className="px-4 py-2 bg-red-500 text-white rounded-lg text-sm hover:bg-red-600">Xóa</button>
        </div>
      </Modal>
    </div>
  );
}
