import { useEffect, useMemo, useState } from 'react';
import { KeyRound, Search, Shield, Users } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const ROLE_LABELS = {
  ADMIN: 'Quản trị viên',
  STAFF: 'Nhân viên',
  CUSTOMER: 'Khách hàng',
  ORGANIZER: 'Đơn vị tổ chức',
};

export default function StaffManagementPage() {
  const [staff, setStaff] = useState([]);
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [roleLoadingId, setRoleLoadingId] = useState(null);
  const [resetTarget, setResetTarget] = useState(null);
  const [resetLoading, setResetLoading] = useState(false);

  useEffect(() => {
    const controller = new AbortController();
    const email = query.trim();
    setLoading(true);

    const fetchStaff = async () => {
      try {
        const { data } = await api.get('/admin/users/staff', {
          params: {
            page: page - 1,
            ...(email ? { email } : {}),
          },
          signal: controller.signal,
        });
        setStaff(Array.isArray(data?.content) ? data.content : []);
        setTotalPages(Math.max(1, data?.totalPages || 1));
        setTotalElements(data?.totalElements || 0);
      } catch (error) {
        if (error.code !== 'ERR_CANCELED') {
          toast.error(error.response?.data?.message || 'Không thể tải danh sách nhân viên');
          setStaff([]);
          setTotalPages(1);
          setTotalElements(0);
        }
      } finally {
        if (!controller.signal.aborted) setLoading(false);
      }
    };

    const timeoutId = setTimeout(fetchStaff, email ? 350 : 0);
    return () => {
      clearTimeout(timeoutId);
      controller.abort();
    };
  }, [page, query]);

  const filteredStaff = useMemo(() => {
    const normalizedQuery = query.trim().toLocaleLowerCase('vi');
    if (!normalizedQuery) return staff;

    return staff.filter((member) =>
      `${member.fullname || ''} ${member.email || ''}`.toLocaleLowerCase('vi').includes(normalizedQuery),
    );
  }, [query, staff]);

  useEffect(() => {
    if (page > totalPages) setPage(totalPages);
  }, [page, totalPages]);

  const updateRole = async (member, role) => {
    if (role === member.roles?.[0]) return;

    setRoleLoadingId(member.id);
    try {
      await api.put(`/admin/staff/${member.id}/role`, null, { params: { role } });
      setStaff((currentStaff) =>
        currentStaff.map((current) =>
          current.id === member.id ? { ...current, roles: [role] } : current,
        ),
      );
      toast.success('Cập nhật vai trò thành công');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Không thể cập nhật vai trò');
    } finally {
      setRoleLoadingId(null);
    }
  };

  const resetPassword = async () => {
    if (!resetTarget) return;

    setResetLoading(true);
    try {
      await api.put(`/admin/users/${resetTarget.id}/password/reset`);
      toast.success(`Mật khẩu tạm thời đã được gửi đến ${resetTarget.email}`);
      setResetTarget(null);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Không thể đặt lại mật khẩu');
    } finally {
      setResetLoading(false);
    }
  };

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h3 className="text-lg font-semibold">Danh sách nhân viên</h3>
          <p className="mt-1 text-sm text-gray-500">Khi tìm bằng email, có thể tìm cả người dùng để phân quyền nhân viên.</p>
        </div>
      </div>

      <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center">
        <label className="relative block w-full sm:max-w-md">
          <span className="sr-only">Tìm nhân viên</span>
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <input
            type="search"
            value={query}
            onChange={(event) => {
              setQuery(event.target.value);
              setPage(1);
            }}
            placeholder="Tìm theo email..."
            className="w-full rounded-lg border border-gray-300 bg-white py-2.5 pl-10 pr-4 text-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
          />
        </label>
        {!loading && (
          <span className="text-sm text-gray-500">
            Trang {page} / {totalPages} · {totalElements} nhân viên
          </span>
        )}
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
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-4 py-12 text-center text-gray-500">Đang tải danh sách nhân viên...</td>
                </tr>
              ) : filteredStaff.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-12 text-center text-gray-500">
                    <Users className="mx-auto mb-2 h-6 w-6 text-gray-400" />
                    {query ? 'Không tìm thấy tài khoản phù hợp với email.' : 'Chưa có nhân viên.'}
                  </td>
                </tr>
              ) : filteredStaff.map((member) => {
                const currentRole = member.roles?.[0] || 'STAFF';
                return (
                  <tr key={member.id} className="border-t border-gray-100 hover:bg-gray-50">
                    <td className="px-4 py-4">
                      <div className="flex items-center gap-3">
                        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-indigo-50 text-indigo-600">
                          <Users className="h-4 w-4" />
                        </div>
                        <span className="font-medium text-gray-900">{member.fullname}</span>
                      </div>
                    </td>
                    <td className="px-4 py-4 text-gray-500">{member.email || 'Chưa có email'}</td>
                    <td className="px-4 py-4">
                      <label className="sr-only" htmlFor={`role-${member.id}`}>
                        Vai trò của {member.fullname}
                      </label>
                      <select
                        id={`role-${member.id}`}
                        value={currentRole}
                        disabled={roleLoadingId === member.id}
                        onChange={(event) => updateRole(member, event.target.value)}
                        className="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:opacity-50"
                      >
                        {!ROLE_LABELS[currentRole] && <option value={currentRole}>{currentRole}</option>}
                        {Object.entries(ROLE_LABELS).map(([role, label]) => (
                          <option key={role} value={role}>{label}</option>
                        ))}
                      </select>
                    </td>
                    <td className="px-4 py-4">
                      <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium ${
                        member.status === 1 ? 'bg-green-50 text-green-700' : 'bg-gray-100 text-gray-600'
                      }`}>
                        <span className={`h-1.5 w-1.5 rounded-full ${
                          member.status === 1 ? 'bg-green-500' : 'bg-gray-400'
                        }`} />
                        {member.status === 1 ? 'Đang hoạt động' : 'Tạm khóa'}
                      </span>
                    </td>
                    <td className="px-4 py-4 text-right">
                      <button
                        type="button"
                        disabled={!member.email?.trim()}
                        title={!member.email?.trim() ? 'Không thể gửi mật khẩu tạm thời vì tài khoản chưa có email' : undefined}
                        onClick={() => setResetTarget(member)}
                        className="inline-flex items-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm font-medium text-gray-700 transition hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        <KeyRound className="h-4 w-4" />
                        Đặt lại mật khẩu
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />

      <div className="mt-4 flex items-start gap-2 rounded-lg border border-indigo-100 bg-indigo-50 p-3 text-sm text-indigo-800">
        <Shield className="mt-0.5 h-4 w-4 shrink-0" />
        <p>Đặt lại mật khẩu sẽ tạo mật khẩu tạm thời và gửi qua email của tài khoản. Tài khoản chưa có email sẽ không thể nhận mật khẩu tạm thời.</p>
      </div>

      <Modal
        isOpen={!!resetTarget}
        onClose={() => {
          if (!resetLoading) setResetTarget(null);
        }}
        title="Xác nhận đặt lại mật khẩu"
      >
        <p className="mb-5 text-sm text-gray-600">
          Hệ thống sẽ tạo mật khẩu tạm thời và gửi đến <strong>{resetTarget?.email}</strong>. Bạn có muốn tiếp tục?
        </p>
        <div className="flex justify-end gap-2">
          <button
            type="button"
            disabled={resetLoading}
            onClick={() => setResetTarget(null)}
            className="rounded-lg border px-4 py-2 text-sm disabled:opacity-50"
          >
            Hủy
          </button>
          <button
            type="button"
            disabled={resetLoading}
            onClick={resetPassword}
            className="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {resetLoading ? 'Đang xử lý...' : 'Đặt lại và gửi email'}
          </button>
        </div>
      </Modal>
    </div>
  );
}
