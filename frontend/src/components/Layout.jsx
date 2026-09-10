import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Ticket, User, LogOut, Menu, X } from 'lucide-react';
import { useEffect, useState } from 'react';

export default function Layout() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 8);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      {/* Navbar */}
      <nav className={`bg-white sticky top-0 z-50 transition-shadow ${scrolled ? 'shadow-md' : 'shadow-sm'}`}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <Link to="/" className="flex items-center gap-2 text-indigo-600 font-bold text-xl">
              <Ticket className="w-6 h-6" />
              OmniTicket
            </Link>

            {/* Desktop nav */}
            <div className="hidden md:flex items-center gap-4">
              <Link to="/" className="relative text-gray-600 hover:text-indigo-600 transition after:absolute after:left-0 after:-bottom-2 after:h-0.5 after:w-0 after:bg-indigo-600 after:transition-all hover:after:w-full">Sự kiện</Link>
              {!user && (
                <Link to="/register" className="border border-indigo-600 text-indigo-700 px-4 py-2 rounded-lg hover:bg-indigo-50 transition">
                  Đăng ký
                </Link>
              )}
              {user ? (
                <>
                  {isAdmin && (
                    <Link to="/admin" className="text-gray-600 hover:text-indigo-600 transition">
                      Quản trị
                    </Link>
                  )}
                  <div className="flex items-center gap-2 text-sm text-gray-700">
                    <User className="w-4 h-4" />
                    {user.fullname}
                  </div>
                  <button
                    onClick={handleLogout}
                    className="flex items-center gap-1 text-gray-500 hover:text-red-600 transition"
                  >
                    <LogOut className="w-4 h-4" />
                  </button>
                </>
              ) : (
                <Link
                  to="/login"
                  className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition"
                >
                  Đăng nhập
                </Link>
              )}
            </div>

            {/* Mobile menu button */}
            <button className="md:hidden" onClick={() => setMenuOpen(!menuOpen)}>
              {menuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile nav */}
        {menuOpen && (
          <div className="md:hidden border-t bg-white px-4 py-3 space-y-2">
            <Link to="/" className="block py-2 text-gray-600" onClick={() => setMenuOpen(false)}>Sự kiện</Link>
            {user ? (
              <>
                {isAdmin && (
                  <Link to="/admin" className="block py-2 text-gray-600" onClick={() => setMenuOpen(false)}>
                    Quản trị
                  </Link>
                )}
                <span className="block py-2 text-sm text-gray-500">{user.fullname}</span>
                <button onClick={handleLogout} className="block py-2 text-red-500">Đăng xuất</button>
              </>
            ) : (
              <>
                <Link to="/register" className="block py-2 text-indigo-600 font-medium" onClick={() => setMenuOpen(false)}>Đăng ký</Link>
                <Link to="/login" className="block py-2 text-indigo-600 font-medium" onClick={() => setMenuOpen(false)}>Đăng nhập</Link>
              </>
            )}
          </div>
        )}
      </nav>

      {/* Main content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-white border-t mt-auto">
        <div className="max-w-7xl mx-auto px-4 py-6 text-center text-sm text-gray-500">
          &copy; 2026 OmniTicket. Hệ thống đặt vé sự kiện.
        </div>
      </footer>
    </div>
  );
}
