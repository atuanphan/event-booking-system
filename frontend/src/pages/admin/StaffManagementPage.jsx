import { useMemo, useState } from 'react';
import { KeyRound, Search, Shield, Users } from 'lucide-react';
import toast from 'react-hot-toast';
import Modal from '../../components/Modal';

const INITIAL_STAFF = [
  { id: 1, fullname: 'Nguyễn Minh Anh', email: 'minhanh@example.com', role: 'STAFF', status: 'Đang hoạt động' },
  { id: 2, fullname: 'Trần Quốc Bảo', email: 'quocbao@example.com', role: 'ADMIN', status: 'Đang hoạt động' },
  { id: 3, fullname: 'Lê Thu Hà', email: 'thuha@example.com', role: 'STAFF', status: 'Đang hoạt động' },
  { id: 4, fullname: 'Phạm Đức Long', email: 'duclong@example.com', role: 'STAFF', status: 'Tạm khóa' },
];

const ROLE_LABELS = {
  ADMIN: 'Quản trị viên',
  STAFF: 'Nhân viên',
};

export default function StaffManagementPage() {
  const [staff, setStaff] = useState(INITIAL_STAFF);
  const [query, setQuery] = useState('');
  const [resetTarget, setResetTarget] = useState(null);
  const [newPassword, setNewPassword] = useState('');

  const filteredStaff = useMemo(() => {
    const normalizedQuery = query.trim().toLocaleLowerCase('vi');
    if (!normalizedQuery) return staff;

    return staff.filter(({ fullname, email }) =>
      `${fullname} ${email}`.toLocaleLowerCase('vi').includes(normalizedQuery),
    );
  }, [query, staff]);

  const updateRole = (id, role) => {
    setStaff((currentStaff) =>
      currentStaff.map((member) => (member.id === id ? { ...member, role } : member)),
    );
    toast.success('Đã cập nhật vai trò trên giao diện');
  };

  const handleResetPassword = (event) => {
    event.preventDefault();
    if (!newPassword.trim()) return;

    toast.success('Đã đặt lại mật khẩu trên giao diện');
    setResetTarget(null);
    setNewPassword('');
  };

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h3 className="text-lg font-semibold">Danh sách nhân viên</h3>
          <p className="mt-1 text-sm text-gray-500">Quản lý vai trò và mật khẩu nhân viên.</p>
        </div>
        <span className="w-fit rounded-full bg-amber-100 px-3 py-1 text-xs font-medium text-amber-800">
          Giao diện xem trước · Dữ liệu mẫu
        </span>
      </div>

      <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center">
        <label className="relative block w-full sm:max-w-md">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Tìm theo tên hoặc email..."
            className="w-full rounded-lg border border-gray-300 bg-white py-2.5 pl-10 pr-4 text-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
          />
        </label>
        <span className="text-sm text-gray-500">
          {filteredStaff.length} / {staff.length} nhân viên
        </span>
      </div>

      <div className="overflow-hidden rounded-xl bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[720px] text-left text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="px-4 py-3 font-medium">Nhân viên</th>
                <th className="px-4 py-3 font-medium">Email</th>
                <th className="px-4 py-3 font-medium">Vai trò</th>
                <th className="px-4 py-3 font-medium">Trạng thái</th>
                <th className="px-4 py-3 text-right font-medium">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {filteredStaff.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-12 text-center text-gray-500">
                    <Users className="mx-auto mb-2 h-6 w-6 text-gray-400" />
                    Không tìm thấy nhân viên phù hợp.
                  </td>
                </tr>
              ) : filteredStaff.map((member) => (
                <tr key={member.id} className="border-t border-gray-100 hover:bg-gray-50">
                  <td className="px-4 py-4">
                    <div className="flex items-center gap-3">
                      <div className="flex h-9 w-9 items-center justify-center rounded-full bg-indigo-50 text-indigo-600">
                        <Users className="h-4 w-4" />
                      </div>
                      <span className="font-medium text-gray-900">{member.fullname}</span>
                    </div>
                  </td>
                  <td className="px-4 py-4 text-gray-500">{member.email}</td>
                  <td className="px-4 py-4">
                    <label className="sr-only" htmlFor={`role-${member.id}`}>Vai trò của {member.fullname}</label>
                    <select
                      id={`role-${member.id}`}
                      value={member.role}
                      onChange={(event) => updateRole(member.id, event.target.value)}
                      className="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                    >
                      {Object.entries(ROLE_LABELS).map(([role, label]) => (
                        <option key={role} value={role}>{label}</option>
                      ))}
                    </select>
                  </td>
                  <td className="px-4 py-4">
                    <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium ${
                      member.status === 'Đang hoạt động'
                        ? 'bg-green-50 text-green-700'
                        : 'bg-gray-100 text-gray-600'
                    }`}>
                      <span className={`h-1.5 w-1.5 rounded-full ${
                        member.status === 'Đang hoạt động' ? 'bg-green-500' : 'bg-gray-400'
                      }`} />
                      {member.status}
                    </span>
                  </td>
                  <td className="px-4 py-4 text-right">
                    <button
                      type="button"
                      onClick={() => {
                        setResetTarget(member);
                        setNewPassword('');
                      }}
                      className="inline-flex items-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm font-medium text-gray-700 transition hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-700"
                    >
                      <KeyRound className="h-4 w-4" />
                      Đặt lại mật khẩu
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="mt-4 flex items-start gap-2 rounded-lg border border-indigo-100 bg-indigo-50 p-3 text-sm text-indigo-800">
        <Shield className="mt-0.5 h-4 w-4 shrink-0" />
        <p>Các thao tác đổi vai trò và đặt lại mật khẩu hiện chỉ minh họa trên giao diện, chưa lưu dữ liệu.</p>
      </div>

      <Modal
        isOpen={!!resetTarget}
        onClose={() => {
          setResetTarget(null);
          setNewPassword('');
        }}
        title="Đặt lại mật khẩu"
      >
        <form onSubmit={handleResetPassword}>
          <p className="mb-4 text-sm text-gray-600">
            Tạo mật khẩu mới cho <strong>{resetTarget?.fullname}</strong>.
          </p>
          <label htmlFor="new-password" className="mb-1 block text-sm font-medium text-gray-700">
            Mật khẩu mới
          </label>
          <input
            id="new-password"
            type="password"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
            minLength={6}
            required
            autoComplete="new-password"
            className="mb-5 w-full rounded-lg border border-gray-300 px-4 py-2 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
            placeholder="Nhập ít nhất 6 ký tự"
          />
          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={() => {
                setResetTarget(null);
                setNewPassword('');
              }}
              className="rounded-lg border px-4 py-2 text-sm"
            >
              Hủy
            </button>
            <button
              type="submit"
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white transition hover:bg-indigo-700"
            >
              Xác nhận
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
