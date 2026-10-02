import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useFieldArray, useForm } from 'react-hook-form';
import { ArrowLeft, ImagePlus, Save, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { getEvent, getVenues, saveEvent } from '../../services/organizerApi';

const inputClass = 'w-full px-3 py-2.5 border border-gray-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500';

export default function OrganizerEventFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  const [venues, setVenues] = useState([]);
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [imageUrl, setImageUrl] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const { register, handleSubmit, control, reset, formState: { errors } } = useForm({
    defaultValues: {
      name: '', description: '', status: 'UPCOMING', venueId: '', startTime: '', endTime: '', imageUrl: '',
      ticketTypes: [{ name: '', price: 0, totalQuantity: 1, availableQuantity: 1, seats: [] }],
    },
  });
  const { fields: ticketFields, append: addTicket, remove: removeTicket } = useFieldArray({ control, name: 'ticketTypes' });

  useEffect(() => {
    getVenues().then(setVenues).catch(() => toast.error('Không thể tải danh sách địa điểm'));
    if (!isEdit) return;
    getEvent(id).then((event) => {
      if (!event) {
        toast.error('Không tìm thấy sự kiện');
        navigate('/organizer/events', { replace: true });
        return;
      }
      setImageUrl(event.imageUrl || '');
      reset({
        ...event,
        venueId: event.venue?.id || event.venueId || '',
        startTime: event.startTime?.slice(0, 16),
        endTime: event.endTime?.slice(0, 16),
        ticketTypes: event.ticketTypes?.map((ticket) => ({ ...ticket, seats: [] })) || [],
      });
    }).catch(() => toast.error('Không thể tải thông tin sự kiện')).finally(() => setLoading(false));
  }, [id]);

  const onSubmit = async (values) => {
    if (!isEdit && !imageFile) {
      toast.error('Vui lòng chọn ảnh sự kiện để tải lên');
      return;
    }
    if (!values.ticketTypes?.length) {
      toast.error('Sự kiện cần ít nhất một loại vé');
      return;
    }
    setSaving(true);
    try {
      const saved = await saveEvent({
        ...values,
        id,
        imageUrl,
        imageFile,
        startTime: `${values.startTime}:00`,
        endTime: `${values.endTime}:00`,
        ticketTypes: values.ticketTypes.map((ticket) => ({
          ...ticket,
          price: Number(ticket.price),
          totalQuantity: Number(ticket.totalQuantity),
          availableQuantity: Number(ticket.availableQuantity ?? ticket.totalQuantity),
          seats: ticket.seats || [],
        })),
      });
      toast.success(isEdit ? 'Đã cập nhật sự kiện' : 'Đã tạo sự kiện');
      navigate(isEdit ? `/organizer/events/${saved.id}` : '/organizer/events');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Không thể lưu sự kiện');
    } finally {
      setSaving(false);
    }
  };

  const handleImage = (file) => {
    if (!file) return;
    const reader = new FileReader();
    setImageFile(file);
    reader.onload = () => setImageUrl(reader.result);
    reader.readAsDataURL(file);
  };

  if (loading) return <div className="py-12 text-center text-gray-400">Đang tải sự kiện...</div>;

  return (
    <div className="max-w-4xl space-y-5">
      <button type="button" onClick={() => navigate(-1)} className="inline-flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900"><ArrowLeft className="w-4 h-4" />Quay lại</button>
      <div><p className="text-sm text-gray-500">Thông tin chương trình và lịch tổ chức</p><h2 className="text-2xl font-semibold text-gray-900 mt-1">{isEdit ? 'Chỉnh sửa sự kiện' : 'Tạo sự kiện mới'}</h2></div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6 space-y-5">
          <div><h3 className="font-semibold text-gray-900">Thông tin cơ bản</h3><p className="text-sm text-gray-500 mt-1">Tên, mô tả và trạng thái hiển thị</p></div>
          <div><label className="block text-sm font-medium text-gray-700 mb-1.5">Tên sự kiện *</label><input {...register('name', { required: 'Vui lòng nhập tên sự kiện' })} className={inputClass} placeholder="Ví dụ: Đêm nhạc mùa thu" />{errors.name && <p className="text-xs text-rose-600 mt-1">{errors.name.message}</p>}</div>
          <div><label className="block text-sm font-medium text-gray-700 mb-1.5">Mô tả *</label><textarea rows={5} {...register('description', { required: 'Vui lòng nhập mô tả' })} className={`${inputClass} resize-y`} placeholder="Giới thiệu nội dung và trải nghiệm của sự kiện" />{errors.description && <p className="text-xs text-rose-600 mt-1">{errors.description.message}</p>}</div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-700 mb-1.5">Trạng thái</label><select {...register('status')} className={inputClass}><option value="UPCOMING">Sắp diễn ra</option><option value="ONGOING">Đang diễn ra</option><option value="FINISHED">Đã kết thúc</option><option value="DRAFT">Bản nháp</option><option value="CANCELLED">Đã hủy</option></select></div>
            <div><label className="block text-sm font-medium text-gray-700 mb-1.5">Địa điểm *</label><select {...register('venueId', { required: 'Vui lòng chọn địa điểm' })} className={inputClass}><option value="">Chọn địa điểm có sẵn</option>{venues.map((venue) => <option key={venue.id} value={venue.id}>{venue.name} · {venue.address}</option>)}</select>{errors.venueId && <p className="text-xs text-rose-600 mt-1">{errors.venueId.message}</p>}</div>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-700 mb-1.5">Bắt đầu *</label><input type="datetime-local" {...register('startTime', { required: 'Vui lòng chọn thời gian bắt đầu' })} className={inputClass} />{errors.startTime && <p className="text-xs text-rose-600 mt-1">{errors.startTime.message}</p>}</div>
            <div><label className="block text-sm font-medium text-gray-700 mb-1.5">Kết thúc *</label><input type="datetime-local" {...register('endTime', { required: 'Vui lòng chọn thời gian kết thúc' })} className={inputClass} />{errors.endTime && <p className="text-xs text-rose-600 mt-1">{errors.endTime.message}</p>}</div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6 space-y-4">
          <div><h3 className="font-semibold text-gray-900">Ảnh sự kiện {!isEdit && '*'}</h3><p className="text-sm text-gray-500 mt-1">Ảnh sẽ được tải lên khi lưu sự kiện.</p></div>
          {imageUrl && <img src={imageUrl} alt="Xem trước ảnh sự kiện" className="w-full max-h-64 object-cover rounded-md bg-gray-100" />}
          <label className="flex items-center justify-center gap-2 px-4 py-5 border border-dashed border-gray-300 rounded-md text-sm text-gray-600 cursor-pointer hover:bg-gray-50"><ImagePlus className="w-4 h-4 text-emerald-700" />{imageFile ? imageFile.name : 'Chọn ảnh từ thiết bị'}<input type="file" accept="image/*" className="sr-only" onChange={(event) => handleImage(event.target.files?.[0])} /></label>
        </section>

        <section className="bg-white border border-gray-200 rounded-md p-5 sm:p-6">
          <div className="flex items-center justify-between gap-3 mb-4"><div><h3 className="font-semibold text-gray-900">Loại vé *</h3><p className="text-sm text-gray-500 mt-1">Cần ít nhất một loại vé để tạo sự kiện.</p></div><button type="button" onClick={() => addTicket({ name: '', price: 0, totalQuantity: 1, availableQuantity: 1, seats: [] })} className="inline-flex items-center gap-1 text-sm text-emerald-700 hover:text-emerald-900"><span className="text-lg leading-none">+</span>Thêm loại vé</button></div>
          <div className="space-y-3">{ticketFields.map((ticket, index) => <div key={ticket.id} className="grid grid-cols-1 sm:grid-cols-[1.5fr_1fr_1fr_auto] gap-3 items-end border border-gray-100 rounded-md p-3">
            <label className="text-xs font-medium text-gray-600">Tên loại vé<input {...register(`ticketTypes.${index}.name`, { required: 'Nhập tên loại vé' })} className={`${inputClass} mt-1`} placeholder="Vé tiêu chuẩn" /></label>
            <label className="text-xs font-medium text-gray-600">Giá (VNĐ)<input type="number" min="0" {...register(`ticketTypes.${index}.price`, { required: true, min: 0 })} className={`${inputClass} mt-1`} /></label>
            <label className="text-xs font-medium text-gray-600">Số lượng<input type="number" min="1" {...register(`ticketTypes.${index}.totalQuantity`, { required: true, min: 1 })} className={`${inputClass} mt-1`} /></label>
            <button type="button" disabled={ticketFields.length === 1} onClick={() => removeTicket(index)} title="Xóa loại vé" aria-label="Xóa loại vé" className="p-2 text-gray-400 hover:text-rose-600 disabled:opacity-30"><Trash2 className="w-4 h-4" /></button>
          </div>)}</div>
        </section>
        <div className="flex justify-end gap-3"><button type="button" onClick={() => navigate('/organizer/events')} className="px-4 py-2.5 border border-gray-200 rounded-md text-sm text-gray-700 hover:bg-gray-50">Hủy</button><button type="submit" disabled={saving} className="inline-flex items-center gap-2 px-4 py-2.5 bg-gray-900 text-white rounded-md text-sm hover:bg-gray-700 disabled:opacity-50"><Save className="w-4 h-4" />{saving ? 'Đang lưu...' : isEdit ? 'Lưu thay đổi' : 'Tạo sự kiện'}</button></div>
      </form>
    </div>
  );
}