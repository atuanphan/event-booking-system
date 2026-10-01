import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { CalendarDays, Ticket, Wallet, Clock3, ArrowUpRight, MapPin } from 'lucide-react';
import { getDashboard } from '../../services/organizerApi';

const money = (value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value || 0);
const date = (value) => new Date(value).toLocaleDateString('vi-VN', { day: '2-digit', month: 'short' });

export default function OrganizerDashboardPage() {
  const [dashboard, setDashboard] = useState(null);

  useEffect(() => { getDashboard().then(setDashboard); }, []);
  if (!dashboard) return <div className="py-12 text-center text-gray-400">Đang tải tổng quan...</div>;

  const cards = [
    { label: 'Sự kiện đang tổ chức', value: dashboard.eventCount, icon: CalendarDays, note: 'Sự kiện sắp diễn ra và đang diễn ra', color: 'text-emerald-700 bg-emerald-50' },
    { label: 'Vé đã bán', value: dashboard.soldQuantity.toLocaleString('vi-VN'), icon: Ticket, note: 'Tổng vé trên các sự kiện', color: 'text-blue-700 bg-blue-50' },
    { label: 'Doanh thu ghi nhận', value: money(dashboard.revenue), icon: Wallet, note: 'Từ các đơn đã thanh toán', color: 'text-orange-700 bg-orange-50' },
    { label: 'Sắp diễn ra', value: dashboard.upcomingCount, icon: Clock3, note: 'Sự kiện trong thời gian tới', color: 'text-rose-700 bg-rose-50' },
  ];
  const max = Math.max(...dashboard.timeline.map((item) => item.soldQuantity));

  return (
    <div className="space-y-7">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3">
        <div><p className="text-sm font-medium text-emerald-700">Thứ Năm, 01 tháng 10, 2026</p><h2 className="text-2xl font-semibold text-gray-900 mt-1">Tổng quan hoạt động</h2></div>
        <Link to="/organizer/events/create" className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-gray-900 text-white rounded-md text-sm hover:bg-gray-700"><CalendarDays className="w-4 h-4" />Tạo sự kiện</Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {cards.map(({ label, value, icon: Icon, note, color }) => (
          <div key={label} className="bg-white border border-gray-200 p-5 rounded-md">
            <div className="flex items-start justify-between gap-3"><div><p className="text-sm text-gray-500">{label}</p><p className="text-2xl font-semibold text-gray-900 mt-2">{value}</p></div><span className={`p-2.5 rounded-md ${color}`}><Icon className="w-5 h-5" /></span></div>
            <p className="text-xs text-gray-400 mt-4">{note}</p>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-[1.5fr_1fr] gap-5">
        <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6">
          <div className="flex items-start justify-between gap-3"><div><h3 className="font-semibold text-gray-900">Xu hướng bán vé</h3><p className="text-sm text-gray-500 mt-1">Số vé bán theo tháng</p></div><span className="text-xs text-gray-400">2026</span></div>
          <div className="mt-7 flex items-end gap-3 sm:gap-5 h-44 border-b border-gray-200">
            {dashboard.timeline.map((item) => <div key={item.label} className="flex-1 h-full flex flex-col justify-end items-center gap-2"><div title={`${item.soldQuantity.toLocaleString('vi-VN')} vé`} className="w-full max-w-12 bg-emerald-500 hover:bg-emerald-600 rounded-t-sm transition-colors" style={{ height: `${Math.max(8, item.soldQuantity / max * 85)}%` }} /><span className="text-xs text-gray-400 pb-2">{item.label}</span></div>)}
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6">
          <div className="flex items-center justify-between gap-3 mb-4"><div><h3 className="font-semibold text-gray-900">Lịch sự kiện</h3><p className="text-sm text-gray-500 mt-1">Các mốc gần nhất</p></div><Link to="/organizer/events" className="text-sm font-medium text-emerald-700 hover:text-emerald-900">Tất cả</Link></div>
          <div className="divide-y divide-gray-100">
            {dashboard.recentEvents.map((event) => <Link key={event.id} to={`/organizer/events/${event.id}`} className="flex items-center gap-3 py-3 group"><div className="w-12 h-12 rounded-md bg-emerald-50 text-emerald-800 flex flex-col items-center justify-center shrink-0"><span className="text-xs">{date(event.startTime).split(' ')[1]}</span><span className="text-sm font-semibold">{new Date(event.startTime).getDate()}</span></div><div className="min-w-0 flex-1"><p className="text-sm font-medium text-gray-800 truncate group-hover:text-emerald-700">{event.name}</p><p className="flex items-center gap-1 text-xs text-gray-400 mt-1"><MapPin className="w-3 h-3" />{event.venue?.name}</p></div><ArrowUpRight className="w-4 h-4 text-gray-300 group-hover:text-emerald-600 shrink-0" /></Link>)}
          </div>
        </section>
      </div>
      <p className="text-xs text-gray-400">Dữ liệu minh họa, chưa đồng bộ với hệ thống bán vé.</p>
    </div>
  );
}