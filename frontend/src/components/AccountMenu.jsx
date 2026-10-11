import { useEffect, useRef, useState } from 'react';
import { ChevronDown, KeyRound, LogOut, UserRound } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../api/axios';
import Modal from './Modal';

export default function AccountMenu({
  user,
  onLogout,
  variant = 'navbar',
  collapsed = false,
  showChangePassword = true,
}) {
  const [menuOpen, setMenuOpen] = useState(false);
  const [passwordModalOpen, setPasswordModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [passwords, setPasswords] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const menuRef = useRef(null);

  useEffect(() => {
    const handlePointerDown = (event) => {
      if (!menuRef.current?.contains(event.target)) setMenuOpen(false);
    };
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        setMenuOpen(false);
        setPasswordModalOpen(false);
      }
    };

    document.addEventListener('pointerdown', handlePointerDown);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('pointerdown', handlePointerDown);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  const closePasswordModal = () => {
    setPasswordModalOpen(false);
    setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' });
  };

  const handleChangePassword = async (event) => {
    event.preventDefault();
    if (passwords.newPassword !== passwords.confirmPassword) {
      toast.error('Mật khẩu xác nhận không khớp');
      return;
    }
    if (passwords.currentPassword === passwords.newPassword) {
      toast.error('Mật khẩu mới phải khác mật khẩu hiện tại');
      return;
    }

    setLoading(true);
    try {
      await api.post('/auth/change-password', {
        currentPassword: passwords.currentPassword,
        newPassword: passwords.newPassword,
      });
      toast.success('Đổi mật khẩu thành công');
      closePasswordModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Không thể đổi mật khẩu. Vui lòng kiểm tra lại mật khẩu hiện tại.');
    } finally {
      setLoading(false);
    }
  };

  const isSidebar = variant === 'sidebar';
  const triggerClassName = isSidebar
    ? 'flex items-center gap-2 text-gray-400 hover:text-white transition w-full'
    : variant === 'mobile'
      ? 'flex items-center justify-between py-2 text-gray-600 w-full'
      : 'flex items-center gap-2 text-sm text-gray-700 hover:text-indigo-600 transition';

  return (
    <>
      <div ref={menuRef} className={`relative ${isSidebar ? 'w-full' : ''}`}>
        <button
          type="button"
          onClick={() => setMenuOpen((open) => !open)}
          className={triggerClassName}
          aria-haspopup="menu"
          aria-expanded={menuOpen}
          aria-label={collapsed ? 'Mở menu tài khoản' : undefined}
          title={collapsed ? 'Tài khoản' : undefined}
        >
          <span className="flex min-w-0 items-center gap-2">
            {isSidebar && !collapsed && <UserRound className="w-5 h-5 shrink-0" />}
            {!collapsed && (isSidebar ? <span className="text-sm truncate">Tài khoản</span> : (
              <>
                {variant === 'navbar' && <UserRound className="w-4 h-4" />}
                <span className="truncate">{user?.fullname || 'Tài khoản'}</span>
              </>
            ))}
            {collapsed && <UserRound className="w-5 h-5 shrink-0" />}
          </span>
          {!collapsed && <ChevronDown className="w-4 h-4 shrink-0" />}
        </button>

        {menuOpen && (
          <div
            role="menu"
            className={`absolute z-50 min-w-48 rounded-lg border border-gray-200 bg-white py-1 shadow-lg ${
              isSidebar
                ? collapsed ? 'left-full top-0 ml-2' : 'bottom-full right-0 mb-2'
                : variant === 'mobile' ? 'left-0 top-full mt-1 w-full' : 'right-0 top-full mt-2'
            }`}
          >
            {showChangePassword && user?.provider === 'LOCAL' && (
              <button
                type="button"
                role="menuitem"
                onClick={() => {
                  setMenuOpen(false);
                  setPasswordModalOpen(true);
                }}
                className="flex w-full items-center gap-2 px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
              >
                <KeyRound className="w-4 h-4" />
                Đổi mật khẩu
              </button>
            )}
            <button
              type="button"
              role="menuitem"
              onClick={onLogout}
              className="flex w-full items-center gap-2 px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50"
            >
              <LogOut className="w-4 h-4" />
              Đăng xuất
            </button>
          </div>
        )}
      </div>

      <Modal isOpen={passwordModalOpen} onClose={closePasswordModal} title="Đổi mật khẩu">
        <form onSubmit={handleChangePassword} className="space-y-4">
          <div>
            <label htmlFor="currentPassword" className="mb-1 block text-sm font-medium text-gray-700">
              Mật khẩu hiện tại
            </label>
            <input
              id="currentPassword"
              type="password"
              autoComplete="current-password"
              required
              value={passwords.currentPassword}
              onChange={(event) => setPasswords({ ...passwords, currentPassword: event.target.value })}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
            />
          </div>
          <div>
            <label htmlFor="newPassword" className="mb-1 block text-sm font-medium text-gray-700">
              Mật khẩu mới
            </label>
            <input
              id="newPassword"
              type="password"
              autoComplete="new-password"
              required
              value={passwords.newPassword}
              onChange={(event) => setPasswords({ ...passwords, newPassword: event.target.value })}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
            />
          </div>
          <div>
            <label htmlFor="confirmPassword" className="mb-1 block text-sm font-medium text-gray-700">
              Xác nhận mật khẩu mới
            </label>
            <input
              id="confirmPassword"
              type="password"
              autoComplete="new-password"
              required
              value={passwords.confirmPassword}
              onChange={(event) => setPasswords({ ...passwords, confirmPassword: event.target.value })}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
            />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={closePasswordModal}
              disabled={loading}
              className="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 disabled:opacity-50"
            >
              Hủy
            </button>
            <button
              type="submit"
              disabled={loading}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
            >
              {loading ? 'Đang cập nhật...' : 'Cập nhật mật khẩu'}
            </button>
          </div>
        </form>
      </Modal>
    </>
  );
}
