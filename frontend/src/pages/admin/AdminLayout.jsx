import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard, Calendar, MapPin, Users, Shield, LogOut, ChevronLeft, Menu,
} from 'lucide-react';
import { useState } from 'react';

const NAV_ITEMS = [
  { to: '/admin', icon: LayoutDashboard, label: 'Dashboard', exact: true },
  { to: '/admin/events', icon: Calendar, label: 'Sự kiện' },
  { to: '/admin/venues', icon: MapPin, label: 'Địa điểm' },
  { to: '/admin/users', icon: Users, label: 'Người dùng' },
  { to: '/admin/staff', icon: Shield, label: 'Nhân viên' },
];

export default function AdminLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const isActive = (path, exact) => {
    return exact ? location.pathname === path : location.pathname.startsWith(path);
  };

  return (
    <div className="min-h-screen flex bg-gray-100">
      {/* Sidebar */}
      <aside className={`${collapsed ? 'w-16' : 'w-64'} bg-gray-900 text-white flex flex-col transition-all duration-200`}>
        <div className="flex items-center justify-between p-4 border-b border-gray-700">
          {!collapsed && <span className="font-bold text-lg">Admin</span>}
          <button onClick={() => setCollapsed(!collapsed)} className="p-1 hover:bg-gray-700 rounded">
            {collapsed ? <Menu className="w-5 h-5" /> : <ChevronLeft className="w-5 h-5" />}
          </button>
        </div>

        <nav className="flex-1 py-4 space-y-1 px-2">
          {NAV_ITEMS.map(({ to, icon: Icon, label, exact }) => (
            <Link
              key={to}
              to={to}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition ${
                isActive(to, exact)
                  ? 'bg-indigo-600 text-white'
                  : 'text-gray-400 hover:bg-gray-800 hover:text-white'
              }`}
              title={collapsed ? label : undefined}
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              {!collapsed && <span className="text-sm">{label}</span>}
            </Link>
          ))}
        </nav>

        <div className="p-4 border-t border-gray-700">
          {!collapsed && (
            <p className="text-xs text-gray-400 mb-2 truncate">{user?.fullname}</p>
          )}
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 text-gray-400 hover:text-red-400 transition w-full"
            title="Đăng xuất"
          >
            <LogOut className="w-5 h-5" />
            {!collapsed && <span className="text-sm">Đăng xuất</span>}
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col min-w-0">
        <header className="bg-white shadow-sm px-6 py-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-800">
            {NAV_ITEMS.find((n) => isActive(n.to, n.exact))?.label || 'Dashboard'}
          </h2>
          <span className="text-sm text-gray-500">{user?.email}</span>
        </header>
        <main className="flex-1 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
