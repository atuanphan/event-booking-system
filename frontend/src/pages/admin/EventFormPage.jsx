import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { Plus, Trash2, ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';

export default function EventFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  const [venues, setVenues] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(isEdit);

  const { register, handleSubmit, control, watch, reset, formState: { errors } } = useForm({
    defaultValues: {
      name: '', description: '', status: 'UPCOMING', venueId: '', imageUrl: '',
      startTime: '', endTime: '',
      ticketTypes: [{ name: '', price: 0, totalQuantity: 0, seats: [] }],
    },
  });

  const { fields: ticketFields, append: addTicket, remove: removeTicket } = useFieldArray({ control, name: 'ticketTypes' });

  useEffect(() => {
    api.post('/admin/venues', { page: 1, pageSize: 100 }).then(({ data }) => {
      setVenues(data.list || []);
    });
    if (isEdit) {
      api.get(`/events/${id}`).then(({ data }) => {
        reset({
          id: data.id,
          name: data.name,
          description: data.description,
          status: data.status,
          venueId: data.venue?.id || '',
          imageUrl: data.imageUrl || '',
          startTime: data.startTime?.slice(0, 16) || '',
          endTime: data.endTime?.slice(0, 16) || '',
          ticketTypes: data.ticketTypes?.map((t) => ({
            id: t.id, name: t.name, price: t.price,
            totalQuantity: t.totalQuantity, seats: [],
          })) || [],
        });
      }).finally(() => setFetching(false));
    }
  }, [id]);

  const onSubmit = async (values) => {
    setLoading(true);
    try {
      const payload = {
        ...values,
        startTime: values.startTime ? values.startTime + ':00' : null,
        endTime: values.endTime ? values.endTime + ':00' : null,
        ticketTypes: values.ticketTypes.map((t) => ({
          ...t,
          price: Number(t.price),
          totalQuantity: Number(t.totalQuantity),
          seats: t.seats || [],
        })),
      };
      if (isEdit) {
        await api.put('/admin/events', payload);
        toast.success('Cập nhật thành công');
      } else {
        await api.post('/admin/events', payload);
        toast.success('Tạo thành công');
      }
      navigate('/admin/events');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Thao tác thất bại');
    } finally {
      setLoading(false);
    }
  };

  if (fetching) return <div className="text-center py-12 text-gray-400">Đang tải...</div>;

  return (
    <div className="max-w-3xl">
      <button onClick={() => navigate(-1)} className="flex items-center gap-1 text-gray-500 hover:text-indigo-600 mb-4">
        <ArrowLeft className="w-4 h-4" /> Quay lại
      </button>
      <h2 className="text-xl font-bold mb-6">{isEdit ? 'Sửa sự kiện' : 'Thêm sự kiện mới'}</h2>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Basic info */}
        <div className="bg-white rounded-xl border p-6 space-y-4">
          <h3 className="font-semibold">Thông tin cơ bản</h3>
          <div>
            <label className="block text-sm font-medium mb-1">Tên sự kiện *</label>
            <input {...register('name', { required: 'Bắt buộc' })} className="w-full px-4 py-2 border rounded-lg" />
            {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Mô tả *</label>
            <textarea {...register('description', { required: 'Bắt buộc' })} rows={4} className="w-full px-4 py-2 border rounded-lg" />
            {errors.description && <p className="text-red-500 text-xs">{errors.description.message}</p>}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Trạng thái</label>
              <select {...register('status')} className="w-full px-4 py-2 border rounded-lg">
                <option value="UPCOMING">Sắp diễn ra</option>
                <option value="ONGOING">Đang diễn ra</option>
                <option value="FINISHED">Đã kết thúc</option>
                <option value="DRAFT">Dự kiến</option>
                <option value="CANCELLED">Đã hủy</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Địa điểm *</label>
              <select {...register('venueId', { required: 'Bắt buộc' })} className="w-full px-4 py-2 border rounded-lg">
                <option value="">Chọn địa điểm</option>
                {venues.map((v) => (
                  <option key={v.id} value={v.id}>{v.name}</option>
                ))}
              </select>
              {errors.venueId && <p className="text-red-500 text-xs">{errors.venueId.message}</p>}
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">URL hình ảnh</label>
            <input {...register('imageUrl')} className="w-full px-4 py-2 border rounded-lg" placeholder="https://..." />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Thời gian bắt đầu *</label>
              <input type="datetime-local" {...register('startTime', { required: 'Bắt buộc' })} className="w-full px-4 py-2 border rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Thời gian kết thúc *</label>
              <input type="datetime-local" {...register('endTime', { required: 'Bắt buộc' })} className="w-full px-4 py-2 border rounded-lg" />
            </div>
          </div>
        </div>

        {/* Ticket types */}
        <div className="bg-white rounded-xl border p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-semibold">Loại vé</h3>
            <button type="button" onClick={() => addTicket({ name: '', price: 0, totalQuantity: 0, seats: [] })}
              className="flex items-center gap-1 text-sm text-indigo-600 hover:text-indigo-800">
              <Plus className="w-4 h-4" /> Thêm loại vé
            </button>
          </div>

          <div className="space-y-4">
            {ticketFields.map((field, index) => (
              <div key={field.id} className="border rounded-lg p-4 relative">
                {ticketFields.length > 1 && (
                  <button type="button" onClick={() => removeTicket(index)}
                    className="absolute top-2 right-2 text-red-400 hover:text-red-600">
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Tên loại vé</label>
                    <input {...register(`ticketTypes.${index}.name`, { required: 'Bắt buộc' })}
                      className="w-full px-3 py-2 border rounded-lg text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Giá (VNĐ)</label>
                    <input type="number" {...register(`ticketTypes.${index}.price`, { required: 'Bắt buộc', min: 0 })}
                      className="w-full px-3 py-2 border rounded-lg text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Số lượng</label>
                    <input type="number" {...register(`ticketTypes.${index}.totalQuantity`, { required: 'Bắt buộc', min: 1 })}
                      className="w-full px-3 py-2 border rounded-lg text-sm" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full py-3 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition disabled:opacity-50"
        >
          {loading ? 'Đang lưu...' : isEdit ? 'Cập nhật' : 'Tạo sự kiện'}
        </button>
      </form>
    </div>
  );
}
