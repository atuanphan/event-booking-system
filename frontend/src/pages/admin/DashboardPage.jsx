import { useState, useEffect } from 'react';
import { Calendar, MapPin, Users, Ticket } from 'lucide-react';
import api from '../../api/axios';
import LoadingSpinner from '../../components/LoadingSpinner';

export default function DashboardPage() {
  const [stats, setStats] = useState({ events: 0, venues: 0, users: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [events, venues, users] = await Promise.all([
          api.post('/admin/events', { page: 1, pageSize: 1 }),
          api.post('/admin/venues', { page: 1, pageSize: 1 }),
          api.get('/users'),
        ]);
        setStats({
          events: events.data.totalPage * 10 || events.data.list?.length || 0,
          venues: venues.data.totalPage * 10 || venues.data.list?.length || 0,
          users: users.data?.length || 0,
        });
      } catch {
        // ignore
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) return <LoadingSpinner />;

  const cards = [
    { icon: Calendar, label: 'Sự kiện', value: stats.events, color: 'bg-blue-500' },
    { icon: MapPin, label: 'Địa điểm', value: stats.venues, color: 'bg-green-500' },
    { icon: Users, label: 'Người dùng', value: stats.users, color: 'bg-purple-500' },
  ];

  return (
    <div>
      <h3 className="text-xl font-semibold mb-6">Tổng quan</h3>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        {cards.map(({ icon: Icon, label, value, color }) => (
          <div key={label} className="bg-white rounded-xl shadow-sm p-6 flex items-center gap-4">
            <div className={`${color} p-3 rounded-xl text-white`}>
              <Icon className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-gray-500">{label}</p>
              <p className="text-2xl font-bold text-gray-900">{value}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
