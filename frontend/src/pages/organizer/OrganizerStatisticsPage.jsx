import { useEffect, useMemo, useState } from 'react';
import { ArrowDownRight, ArrowUpRight, ChartColumnIncreasing, Ticket, Wallet } from 'lucide-react';
import { getSalesStatistics } from '../../services/organizerApi';

const money = (value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value || 0);

export default function OrganizerStatisticsPage() {
  const [statistics, setStatistics] = useState(null);
  const [sort, setSort] = useState('sold');

  useEffect(() => { getSalesStatistics().then(setStatistics); }, []);

  const topEvents = useMemo(() => {
    if (!statistics) return [];
    return [...statistics.topEvents].sort((a, b) => sort === 'name' ? a.eventName.localeCompare(b.eventName, 'vi') : b.soldQuantity - a.soldQuantity);
  }, [statistics, sort]);

  if (!statistics) return <div className="py-12 text-center text-gray-400">Đang tải thống kê...</div>;

  const maxRevenue = Math.max(...statistics.timeline.map((item) => item.revenue));
  const metrics = [
    { label: 'Doanh thu đã thanh toán', value: money(statistics.revenue), icon: Wallet, tone: 'text-emerald-700 bg-emerald-50', change: '+12,8%', positive: true },
    { label: 'Vé bán thành công', value: statistics.soldQuantity.toLocaleString('vi-VN'), icon: Ticket, tone: 'text-blue-700 bg-blue-50', change: '+8,3%', positive: true },
    { label: 'Sự kiện có phát sinh vé', value: statistics.topEvents.filter((event) => event.soldQuantity > 0).length, icon: ChartColumnIncreasing, tone: 'text-orange-700 bg-orange-50', change: '6 tháng gần nhất', positive: null },
  ];

  return (
    <div className="space-y-6">
      <div><p className="text-sm text-gray-500">Hiệu quả kinh doanh các sự kiện</p><h2 className="text-2xl font-semibold text-gray-900 mt-1">Thống kê bán vé</h2></div>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {metrics.map(({ label, value, icon: Icon, tone, change, positive }) => <section key={label} className="bg-white border border-gray-200 rounded-md p-5"><div className="flex justify-between items-start gap-3"><div><p className="text-sm text-gray-500">{label}</p><p className="text-2xl font-semibold text-gray-900 mt-2">{value}</p></div><span className={`p-2.5 rounded-md ${tone}`}><Icon className="w-5 h-5" /></span></div><p className={`flex items-center gap-1 text-xs mt-4 ${positive ? 'text-emerald-700' : 'text-gray-400'}`}>{positive ? <ArrowUpRight className="w-3.5 h-3.5" /> : null}{change}</p></section>)}
      </div>

      <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3"><div><h3 className="font-semibold text-gray-900">Doanh thu theo tháng</h3><p className="text-sm text-gray-500 mt-1">Tổng tiền từ đơn đã thanh toán</p></div><span className="text-xs text-gray-400">T.04 – T.09, 2026</span></div>
        <div className="mt-8 flex items-end gap-3 sm:gap-6 h-56 border-b border-gray-200">
          {statistics.timeline.map((item) => <div key={item.label} className="flex-1 h-full flex flex-col justify-end items-center gap-2"><div className="relative group w-full max-w-16 bg-emerald-600 hover:bg-emerald-700 rounded-t-sm transition-colors" style={{ height: `${Math.max(7, item.revenue / maxRevenue * 82)}%` }}><span className="absolute -top-7 left-1/2 -translate-x-1/2 whitespace-nowrap text-[10px] text-gray-500 opacity-0 group-hover:opacity-100">{money(item.revenue)}</span></div><span className="text-xs text-gray-400 pb-2">{item.label}</span></div>)}
        </div>
        <div className="flex items-center gap-2 mt-4 text-xs text-gray-500"><span className="w-2.5 h-2.5 rounded-sm bg-emerald-600" />Doanh thu ghi nhận</div>
      </section>

      <section className="bg-white border border-gray-200 rounded-md overflow-hidden">
        <div className="p-5 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"><div><h3 className="font-semibold text-gray-900">Sự kiện bán chạy</h3><p className="text-sm text-gray-500 mt-1">Xếp hạng theo số vé đã bán</p></div><select value={sort} onChange={(event) => setSort(event.target.value)} className="px-3 py-2 border border-gray-200 rounded-md text-sm bg-white"><option value="sold">Vé bán nhiều nhất</option><option value="name">Tên sự kiện A–Z</option></select></div>
        <div className="overflow-x-auto"><table className="w-full min-w-[540px] text-sm text-left"><thead className="bg-gray-50 text-xs uppercase text-gray-500"><tr><th className="px-5 py-3 w-16">#</th><th className="px-5 py-3">Sự kiện</th><th className="px-5 py-3">Vé đã bán</th><th className="px-5 py-3">Tỷ trọng</th></tr></thead><tbody>{topEvents.map((event, index) => { const share = statistics.soldQuantity ? Math.round(event.soldQuantity / statistics.soldQuantity * 100) : 0; return <tr key={event.eventId} className="border-t border-gray-100"><td className="px-5 py-4 text-gray-400">{String(index + 1).padStart(2, '0')}</td><td className="px-5 py-4 font-medium text-gray-800">{event.eventName}</td><td className="px-5 py-4 text-gray-700">{event.soldQuantity.toLocaleString('vi-VN')}</td><td className="px-5 py-4"><div className="flex items-center gap-3"><div className="h-1.5 bg-gray-100 rounded-full flex-1"><div className="h-full bg-emerald-600 rounded-full" style={{ width: `${share}%` }} /></div><span className="text-xs text-gray-500 w-9 text-right">{share}%</span></div></td></tr>; })}</tbody></table></div>
      </section>
      <div className="flex items-center gap-2 text-xs text-gray-400"><ArrowDownRight className="w-4 h-4" />Số liệu minh họa, cấu trúc top event tương thích `eventId`, `eventName`, `soldQuantity`.</div>
    </div>
  );
}