import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

// Customer pages
import Layout from './components/Layout';
import HomePage from './pages/customer/HomePage';
import EventDetailPage from './pages/customer/EventDetailPage';
import CheckoutPage from './pages/customer/CheckoutPage';
import PaymentSuccessPage from './pages/customer/PaymentSuccessPage';
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

function ProtectedRoute({ children, requireAdmin = false }) {
  const { user, isAdmin } = useAuth();
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} />;
  }

  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default function App() {
  return (
    <Routes>
      {/* Customer routes */}
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/events/:id" element={<EventDetailPage />} />
        <Route path="/checkout" element={
          <ProtectedRoute><CheckoutPage /></ProtectedRoute>
        } />
        <Route path="/payment/success" element={<PaymentSuccessPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/oauth-callback" element={<OAuthCallbackPage />} />
      </Route>

      {/* Admin routes */}
      <Route path="/admin" element={
        <ProtectedRoute requireAdmin><AdminLayout /></ProtectedRoute>
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
    </Routes>
  );
}
