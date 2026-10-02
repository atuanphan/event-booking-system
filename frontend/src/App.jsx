import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import LoadingSpinner from './components/LoadingSpinner';

// Customer pages
import Layout from './components/Layout';
import HomePage from './pages/customer/HomePage';
import EventsPage from './pages/customer/EventsPage';
import EventDetailPage from './pages/customer/EventDetailPage';
import CheckoutPage from './pages/customer/CheckoutPage';
import PaymentSuccessPage from './pages/customer/PaymentSuccessPage';
import MyTicketsPage from './pages/customer/MyTicketsPage';
import LoginPage from './pages/customer/LoginPage';
import RegisterPage from './pages/customer/RegisterPage';

// Admin pages
import AdminLayout from './pages/admin/AdminLayout';
import DashboardPage from './pages/admin/DashboardPage';
import EventListPage from './pages/admin/EventListPage';
import EventFormPage from './pages/admin/EventFormPage';
import VenueListPage from './pages/admin/VenueListPage';
import VenueFormPage from './pages/admin/VenueFormPage';
import UserListPage from './pages/admin/UserListPage';
import StaffManagementPage from './pages/admin/StaffManagementPage';
import OAuthCallbackPage from './pages/customer/OAuthCallbackPage';
import OrganizerLayout from './pages/organizer/OrganizerLayout';
import OrganizerDashboardPage from './pages/organizer/OrganizerDashboardPage';
import OrganizerEventListPage from './pages/organizer/OrganizerEventListPage';
import OrganizerEventFormPage from './pages/organizer/OrganizerEventFormPage';
import OrganizerEventDetailPage from './pages/organizer/OrganizerEventDetailPage';
import OrganizerOrdersPage from './pages/organizer/OrganizerOrdersPage';
import OrganizerStatisticsPage from './pages/organizer/OrganizerStatisticsPage';

function ProtectedRoute({ children, allowedRoles = [] }) {
  const { user, isAdmin, isOrganizer, authReady } = useAuth();
  const location = useLocation();

  // Đang thử khôi phục phiên (silent refresh) lúc app khởi động / F5.
  // Chưa biết chắc user còn đăng nhập hay không -> chưa vội redirect/render.
  if (!authReady) {
    return <LoadingSpinner size="lg" />;
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  if (allowedRoles.length > 0 && !user.roles?.some((role) => allowedRoles.includes(role))) {
    return <Navigate to={isOrganizer ? '/organizer' : isAdmin ? '/admin' : '/'} replace />;
  }

  return children;
}

export default function App() {
  return (
    <Routes>
      {/* Customer routes */}
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/events" element={<EventsPage />} />
        <Route path="/events/:id" element={<EventDetailPage />} />
        <Route path="/checkout" element={
          <ProtectedRoute><CheckoutPage /></ProtectedRoute>
        } />
        <Route path="/my-tickets" element={
          <ProtectedRoute><MyTicketsPage /></ProtectedRoute>
        } />
        <Route path="/payment/vnpay/callback" element={<PaymentSuccessPage />} />
        <Route path="/payment/vnpay/ipn" element={<PaymentSuccessPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/oauth-callback" element={<OAuthCallbackPage />} />
      </Route>

      {/* Admin routes */}
      <Route path="/admin" element={
        <ProtectedRoute allowedRoles={['ADMIN', 'STAFF']}><AdminLayout /></ProtectedRoute>
      }>
        <Route index element={<DashboardPage />} />
        <Route path="events" element={<EventListPage />} />
        <Route path="events/create" element={<EventFormPage />} />
        <Route path="events/edit/:id" element={<EventFormPage />} />
        <Route path="venues" element={<VenueListPage />} />
        <Route path="venues/create" element={<VenueFormPage />} />
        <Route path="venues/edit/:id" element={<VenueFormPage />} />
        <Route path="users" element={<UserListPage />} />
        <Route path="staff" element={<StaffManagementPage />} />
      </Route>

      {/* Organizer routes */}
      <Route path="/organizer" element={
        <ProtectedRoute allowedRoles={['ORGANIZER']}><OrganizerLayout /></ProtectedRoute>
      }>
        <Route index element={<OrganizerDashboardPage />} />
        <Route path="events" element={<OrganizerEventListPage />} />
        <Route path="events/create" element={<OrganizerEventFormPage />} />
        <Route path="events/:id/edit" element={<OrganizerEventFormPage />} />
        <Route path="events/:id" element={<OrganizerEventDetailPage />} />
        <Route path="orders" element={<OrganizerOrdersPage />} />
        <Route path="statistics" element={<OrganizerStatisticsPage />} />
      </Route>
    </Routes>
  );
}
