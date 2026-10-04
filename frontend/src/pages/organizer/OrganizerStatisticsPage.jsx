import { useEffect, useMemo, useState } from 'react';
import { ChartColumnIncreasing, Ticket, Wallet } from 'lucide-react';
import toast from 'react-hot-toast';
import { getSalesStatistics } from '../../services/organizerApi';

const money = (value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value || 0);

export default function OrganizerStatisticsPage() {
  const [statistics, setStatistics] = useState(null);
  const [sort, setSort] = useState('sold');
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError(false);
    getSalesStatistics().then((data) => {
      if (active) setStatistics(data);
    }).catch((error) => {
      if (!active) return;
      setLoadError(true);
      toast.error(error.response?.data?.message || 'Không thể tải thống kê');
    }).finally(() => {
      if (active) setLoading(false);
    });
    return () => { active = false; };
  }, [reloadKey]);

  const topEvents = useMemo(() => {
    if (!statistics) return [];
    return [...statistics.topEvents].sort((a, b) => sort === 'name' ? a.eventName.localeCompare(b.eventName, 'vi') : b.soldQuantity - a.soldQuantity);
  }, [statistics, sort]);

  if (loading) return <div className="py-12 text-center text-gray-400">Đang tải thống kê...</div>;
  if (loadError || !statistics) return <div className="py-12 text-center"><p className="text-sm text-gray-500">Không tải được dữ liệu thống kê.</p><button type="button" onClick={() => setReloadKey((key) => key + 1)} className="mt-3 px-3 py-2 bg-gray-900 text-white rounded-md text-sm">Thử lại</button></div>;

  const maxRevenue = Math.max(1, ...statistics.timeline.map((item) => item.revenue));
  const metrics = [
    { label: 'Doanh thu đã thanh toán', value: money(statistics.revenue), icon: Wallet, tone: 'text-emerald-700 bg-emerald-50', note: 'Đơn hàng đã hoàn tất' },
    { label: 'Vé bán thành công', value: statistics.soldQuantity.toLocaleString('vi-VN'), icon: Ticket, tone: 'text-blue-700 bg-blue-50', note: 'Tổng số vé trong đơn hoàn tất' },
    { label: 'Sự kiện có phát sinh vé', value: statistics.eventsWithTicketSales.toLocaleString('vi-VN'), icon: ChartColumnIncreasing, tone: 'text-orange-700 bg-orange-50', note: 'Có vé trong đơn đã thanh toán' },
  ];

  return (
    <div className="space-y-6">
      <div><p className="text-sm text-gray-500">Hiệu quả kinh doanh các sự kiện</p><h2 className="text-2xl font-semibold text-gray-900 mt-1">Thống kê bán vé</h2></div>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {metrics.map(({ label, value, icon: Icon, tone, note }) => <section key={label} className="bg-white border border-gray-200 rounded-md p-5"><div className="flex justify-between items-start gap-3"><div><p className="text-sm text-gray-500">{label}</p><p className="text-2xl font-semibold text-gray-900 mt-2">{value}</p></div><span className={`p-2.5 rounded-md ${tone}`}><Icon className="w-5 h-5" /></span></div><p className="text-xs text-gray-400 mt-4">{note}</p></section>)}
      </div>

      <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3"><div><h3 className="font-semibold text-gray-900">Doanh thu theo tháng</h3><p className="text-sm text-gray-500 mt-1">Tổng tiền từ đơn đã thanh toán</p></div><span className="text-xs text-gray-400">Năm {new Date().getFullYear()}</span></div>
        <div className="mt-8 flex items-end gap-3 sm:gap-6 h-56 border-b border-gray-200">
          {statistics.timeline.map((item) => <div key={item.label} className="flex-1 h-full flex flex-col justify-end items-center gap-2"><div className="relative group w-full max-w-16 bg-emerald-600 hover:bg-emerald-700 rounded-t-sm transition-colors" style={{ height: `${Math.max(7, item.revenue / maxRevenue * 82)}%` }}><span className="absolute -top-7 left-1/2 -translate-x-1/2 whitespace-nowrap text-[10px] text-gray-500 opacity-0 group-hover:opacity-100">{money(item.revenue)}</span></div><span className="text-xs text-gray-400 pb-2">{item.label}</span></div>)}
        </div>
        <div className="flex items-center gap-2 mt-4 text-xs text-gray-500"><span className="w-2.5 h-2.5 rounded-sm bg-emerald-600" />Doanh thu ghi nhận</div>
      </section>

      <section className="bg-white border border-gray-200 rounded-md overflow-hidden">
        <div className="p-5 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"><div><h3 className="font-semibold text-gray-900">Sự kiện bán chạy</h3><p className="text-sm text-gray-500 mt-1">Xếp hạng theo số vé đã bán</p></div><select value={sort} onChange={(event) => setSort(event.target.value)} className="px-3 py-2 border border-gray-200 rounded-md text-sm bg-white"><option value="sold">Vé bán nhiều nhất</option><option value="name">Tên sự kiện A–Z</option></select></div>
        <div className="overflow-x-auto"><table className="w-full min-w-[540px] text-sm text-left"><thead className="bg-gray-50 text-xs uppercase text-gray-500"><tr><th className="px-5 py-3 w-16">#</th><th className="px-5 py-3">Sự kiện</th><th className="px-5 py-3">Vé đã bán</th><th className="px-5 py-3">Tỷ lệ vé bán</th></tr></thead><tbody>{topEvents.length === 0 ? <tr><td colSpan={4} className="px-5 py-10 text-center text-gray-400">Chưa có dữ liệu bán vé</td></tr> : topEvents.map((event, index) => { const share = Math.min(100, Math.max(0, event.ticketSalesPercentage)); return <tr key={event.eventId} className="border-t border-gray-100"><td className="px-5 py-4 text-gray-400">{String(index + 1).padStart(2, '0')}</td><td className="px-5 py-4 font-medium text-gray-800">{event.eventName}</td><td className="px-5 py-4 text-gray-700">{event.soldQuantity.toLocaleString('vi-VN')}</td><td className="px-5 py-4"><div className="flex items-center gap-3"><div className="h-1.5 bg-gray-100 rounded-full flex-1"><div className="h-full bg-emerald-600 rounded-full" style={{ width: `${share}%` }} /></div><span className="text-xs text-gray-500 w-9 text-right">{event.ticketSalesPercentage}%</span></div></td></tr>; })}</tbody></table></div>
      </section>
      <p className="text-xs text-gray-400">Số liệu lấy từ các đơn hàng đã thanh toán của Organizer.</p>
    </div>
  );
}