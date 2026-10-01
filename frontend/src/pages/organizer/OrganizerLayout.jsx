import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LayoutDashboard, CalendarDays, ReceiptText, ChartNoAxesCombined, LogOut, ChevronLeft, Menu, Ticket } from 'lucide-react';
import { useState } from 'react';

const NAV_ITEMS = [
  { to: '/organizer', icon: LayoutDashboard, label: 'Tổng quan', exact: true },
  { to: '/organizer/events', icon: CalendarDays, label: 'Sự kiện của tôi' },
  { to: '/organizer/orders', icon: ReceiptText, label: 'Đơn hàng' },
  { to: '/organizer/statistics', icon: ChartNoAxesCombined, label: 'Thống kê' },
];

export default function OrganizerLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const isActive = (path, exact) => exact ? location.pathname === path : location.pathname.startsWith(path);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const currentPage = location.pathname.endsWith('/create')
    ? 'Tạo sự kiện'
    : location.pathname.endsWith('/edit')
      ? 'Sửa sự kiện'
      : /^\/organizer\/events\/[^/]+$/.test(location.pathname)
        ? 'Chi tiết sự kiện'
        : NAV_ITEMS.find((item) => isActive(item.to, item.exact))?.label || 'Tổng quan';

  return (
    <div className="min-h-screen flex bg-gray-100">
      <aside className={`${collapsed ? 'w-16' : 'w-64'} bg-gray-900 text-white flex flex-col transition-all duration-200 shrink-0`}>
        <div className="flex items-center justify-between p-4 border-b border-gray-700 min-h-16">
          {!collapsed && <Link to="/organizer" className="flex items-center gap-2 font-bold text-lg"><Ticket className="w-5 h-5 text-emerald-400" />EventHub</Link>}
          <button type="button" onClick={() => setCollapsed(!collapsed)} className="p-1 hover:bg-gray-700 rounded" aria-label={collapsed ? 'Mở rộng menu' : 'Thu gọn menu'}>
            {collapsed ? <Menu className="w-5 h-5" /> : <ChevronLeft className="w-5 h-5" />}
          </button>
        </div>
        {!collapsed && <p className="px-5 pt-5 pb-2 text-xs font-semibold uppercase tracking-wider text-gray-500">Không gian tổ chức</p>}
        <nav className="flex-1 py-2 space-y-1 px-2">
          {NAV_ITEMS.map(({ to, icon: Icon, label, exact }) => (
            <Link key={to} to={to} title={collapsed ? label : undefined}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition ${isActive(to, exact) ? 'bg-emerald-600 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white'}`}>
              <Icon className="w-5 h-5 shrink-0" />
              {!collapsed && <span className="text-sm">{label}</span>}
            </Link>
          ))}
        </nav>
        <div className="p-4 border-t border-gray-700">
          {!collapsed && <p className="text-xs text-gray-400 mb-2 truncate">{user?.fullname}</p>}
          <button type="button" onClick={handleLogout} className="flex items-center gap-2 text-gray-400 hover:text-red-400 transition w-full" title="Đăng xuất">
            <LogOut className="w-5 h-5" />{!collapsed && <span className="text-sm">Đăng xuất</span>}
          </button>
        </div>
      </aside>
      <div className="flex-1 flex flex-col min-w-0">
        <header className="bg-white border-b px-5 sm:px-7 py-4 flex items-center justify-between gap-4">
          <div className="min-w-0"><p className="text-xs text-gray-400">Organizer / EventHub</p><h1 className="text-lg font-semibold text-gray-800 truncate">{currentPage}</h1></div>
          <div className="text-right shrink-0"><p className="text-sm font-medium text-gray-700">{user?.fullname}</p><p className="text-xs text-gray-500">{user?.email}</p></div>
        </header>
        <main className="flex-1 p-4 sm:p-6 lg:p-8 overflow-auto"><Outlet /></main>
      </div>
    </div>
  );
}