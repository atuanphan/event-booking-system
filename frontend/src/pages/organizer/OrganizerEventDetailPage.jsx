import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, CalendarDays, MapPin, Pencil, Plus, Ticket, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { deleteTicketType, getEvent, saveTicketType } from '../../services/organizerApi';

const money = (value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value || 0);
const statusLabels = { UPCOMING: 'Sắp diễn ra', ONGOING: 'Đang diễn ra', FINISHED: 'Đã kết thúc', DRAFT: 'Bản nháp', CANCELLED: 'Đã hủy' };
const fieldClass = 'w-full px-3 py-2 border border-gray-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500';
const emptyTicket = { name: '', price: '', totalQuantity: '', availableQuantity: '' };

export default function OrganizerEventDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [ticketForm, setTicketForm] = useState(emptyTicket);
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const loadEvent = () => getEvent(id)
    .then(setEvent)
    .catch((error) => {
      setEvent(null);
      toast.error(error.response?.data?.message || 'Không thể tải thông tin sự kiện');
    })
    .finally(() => setLoading(false));
  useEffect(() => { loadEvent(); }, [id]);

  const startEdit = (ticket) => {
    setEditingId(ticket.id);
    setTicketForm({ name: ticket.name, price: ticket.price, totalQuantity: ticket.totalQuantity, availableQuantity: ticket.availableQuantity });
    setShowForm(true);
  };

  const openNew = () => { setEditingId(null); setTicketForm(emptyTicket); setShowForm(true); };

  const handleSaveTicket = async (formEvent) => {
    formEvent.preventDefault();
    await saveTicketType(id, {
      ...ticketForm,
      id: editingId || undefined,
      price: Number(ticketForm.price),
      totalQuantity: Number(ticketForm.totalQuantity),
      availableQuantity: Number(ticketForm.availableQuantity || ticketForm.totalQuantity),
    });
    await loadEvent();
    setShowForm(false);
    setTicketForm(emptyTicket);
    toast.success(editingId ? 'Đã cập nhật loại vé' : 'Đã thêm loại vé');
  };

  const handleDeleteTicket = async (ticket) => {
    if (!window.confirm(`Xóa loại vé "${ticket.name}"?`)) return;
    await deleteTicketType(id, ticket.id);
    await loadEvent();
    toast.success('Đã xóa loại vé');
  };

  if (loading) return <div className="py-12 text-center text-gray-400">Đang tải sự kiện...</div>;
  if (!event) return <div className="py-12 text-center text-gray-500">Không tìm thấy sự kiện</div>;

  const sold = event.ticketTypes.reduce((sum, ticket) => sum + ticket.totalQuantity - ticket.availableQuantity, 0);
  const total = event.ticketTypes.reduce((sum, ticket) => sum + ticket.totalQuantity, 0);

  return (
    <div className="max-w-5xl space-y-5">
      <button type="button" onClick={() => navigate('/organizer/events')} className="inline-flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900"><ArrowLeft className="w-4 h-4" />Danh sách sự kiện</button>
      <section className="bg-white border border-gray-200 rounded-md overflow-hidden">
        {event.imageUrl && <img src={event.imageUrl} alt="" className="w-full h-48 sm:h-64 object-cover" />}
        <div className="p-5 sm:p-6">
          <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
            <div className="min-w-0"><span className="inline-flex px-2 py-1 text-xs bg-emerald-50 text-emerald-800 rounded-sm">{statusLabels[event.status]}</span><h2 className="text-2xl font-semibold text-gray-900 mt-3">{event.name}</h2><p className="text-sm text-gray-600 mt-3 leading-6">{event.description}</p></div>
            <Link to={`/organizer/events/${id}/edit`} className="inline-flex items-center justify-center gap-2 px-3 py-2 border border-gray-200 rounded-md text-sm hover:bg-gray-50 shrink-0"><Pencil className="w-4 h-4" />Chỉnh sửa</Link>
          </div>
          <div className="grid sm:grid-cols-3 gap-4 mt-6 pt-5 border-t border-gray-100">
            <div className="flex gap-2 text-sm text-gray-600"><CalendarDays className="w-4 h-4 text-emerald-700 shrink-0 mt-0.5" /><span>{new Date(event.startTime).toLocaleString('vi-VN')}<br /><span className="text-xs text-gray-400">đến {new Date(event.endTime).toLocaleString('vi-VN')}</span></span></div>
            <div className="flex gap-2 text-sm text-gray-600"><MapPin className="w-4 h-4 text-emerald-700 shrink-0 mt-0.5" /><span>{event.venue?.name}<br /><span className="text-xs text-gray-400">{event.venue?.address}</span></span></div>
            <div className="flex gap-2 text-sm text-gray-600"><Ticket className="w-4 h-4 text-emerald-700 shrink-0 mt-0.5" /><span>{sold.toLocaleString('vi-VN')} / {total.toLocaleString('vi-VN')} vé đã bán</span></div>
          </div>
        </div>
      </section>

      <section className="bg-white border border-gray-200 rounded-md overflow-hidden">
        <div className="p-5 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"><div><h3 className="font-semibold text-gray-900">Loại vé</h3><p className="text-sm text-gray-500 mt-1">Giá bán và số lượng phát hành</p></div><button type="button" onClick={openNew} className="inline-flex items-center justify-center gap-2 px-3 py-2 bg-gray-900 text-white rounded-md text-sm hover:bg-gray-700"><Plus className="w-4 h-4" />Thêm loại vé</button></div>
        {showForm && <form onSubmit={handleSaveTicket} className="mx-5 mb-5 p-4 bg-gray-50 border border-gray-200 rounded-md space-y-4">
          <div className="flex items-center justify-between"><h4 className="font-medium text-sm">{editingId ? 'Sửa loại vé' : 'Loại vé mới'}</h4><button type="button" onClick={() => setShowForm(false)} className="text-sm text-gray-500 hover:text-gray-900">Đóng</button></div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
            <label className="text-xs font-medium text-gray-600">Tên loại vé<input required value={ticketForm.name} onChange={(e) => setTicketForm({ ...ticketForm, name: e.target.value })} className={`${fieldClass} mt-1`} placeholder="Vé tiêu chuẩn" /></label>
            <label className="text-xs font-medium text-gray-600">Giá (VNĐ)<input required min="0" type="number" value={ticketForm.price} onChange={(e) => setTicketForm({ ...ticketForm, price: e.target.value })} className={`${fieldClass} mt-1`} /></label>
            <label className="text-xs font-medium text-gray-600">Tổng số lượng<input required min="1" type="number" value={ticketForm.totalQuantity} onChange={(e) => setTicketForm({ ...ticketForm, totalQuantity: e.target.value })} className={`${fieldClass} mt-1`} /></label>
            {editingId && <label className="text-xs font-medium text-gray-600">Còn lại<input required min="0" type="number" value={ticketForm.availableQuantity} onChange={(e) => setTicketForm({ ...ticketForm, availableQuantity: e.target.value })} className={`${fieldClass} mt-1`} /></label>}
          </div>
          <button type="submit" className="px-3 py-2 bg-emerald-700 text-white rounded-md text-sm hover:bg-emerald-800">{editingId ? 'Lưu loại vé' : 'Thêm loại vé'}</button>
        </form>}
        {event.ticketTypes.length === 0 ? <div className="py-12 text-center text-sm text-gray-400">Chưa có loại vé. Thêm loại vé để bắt đầu mở bán.</div> : <div className="overflow-x-auto"><table className="w-full min-w-[640px] text-sm text-left"><thead className="bg-gray-50 text-xs uppercase text-gray-500"><tr><th className="px-5 py-3">Tên loại vé</th><th className="px-5 py-3">Giá</th><th className="px-5 py-3">Đã bán</th><th className="px-5 py-3">Còn lại / Tổng</th><th className="px-5 py-3 text-right">Thao tác</th></tr></thead><tbody>{event.ticketTypes.map((ticket) => <tr key={ticket.id} className="border-t border-gray-100"><td className="px-5 py-4 font-medium text-gray-800">{ticket.name}</td><td className="px-5 py-4 text-gray-600">{money(ticket.price)}</td><td className="px-5 py-4 text-gray-600">{(ticket.totalQuantity - ticket.availableQuantity).toLocaleString('vi-VN')}</td><td className="px-5 py-4 text-gray-600">{ticket.availableQuantity.toLocaleString('vi-VN')} / {ticket.totalQuantity.toLocaleString('vi-VN')}</td><td className="px-5 py-3"><div className="flex justify-end gap-1"><button type="button" title="Sửa loại vé" aria-label="Sửa loại vé" onClick={() => startEdit(ticket)} className="p-2 text-gray-500 hover:bg-gray-100 rounded-md"><Pencil className="w-4 h-4" /></button><button type="button" title={event.ticketTypes.length === 1 ? 'Sự kiện cần ít nhất một loại vé' : 'Xóa loại vé'} aria-label="Xóa loại vé" disabled={event.ticketTypes.length === 1} onClick={() => handleDeleteTicket(ticket)} className="p-2 text-gray-500 hover:bg-rose-50 hover:text-rose-700 rounded-md disabled:opacity-30 disabled:hover:bg-transparent disabled:hover:text-gray-500"><Trash2 className="w-4 h-4" /></button></div></td></tr>)}</tbody></table></div>}
      </section>
      <p className="text-xs text-gray-400">Thông tin sự kiện và loại vé được đồng bộ qua API Organizer.</p>
    </div>
  );
}