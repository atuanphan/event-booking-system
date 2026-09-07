import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';

export default function VenueFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    defaultValues: { name: '', address: '', capacity: 0 },
  });

  useEffect(() => {
    if (isEdit) {
      api.post('/admin/venues', { page: 1, pageSize: 100 }).then(({ data }) => {
        const venue = (data.list || []).find((v) => v.id === id);
        if (venue) reset({ id: venue.id, name: venue.name, address: venue.address, capacity: venue.capacity });
      });
    }
  }, [id]);

  const onSubmit = async (values) => {
    setLoading(true);
    try {
      const payload = { ...values, capacity: Number(values.capacity) };
      if (isEdit) {
        await api.put('/admin/venues', payload);
        toast.success('Cập nhật thành công');
      } else {
        await api.post('/admin/venues', payload);
        toast.success('Tạo thành công');
      }
      navigate('/admin/venues');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Thao tác thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl">
      <button onClick={() => navigate(-1)} className="flex items-center gap-1 text-gray-500 hover:text-indigo-600 mb-4">
        <ArrowLeft className="w-4 h-4" /> Quay lại
      </button>
      <h2 className="text-xl font-bold mb-6">{isEdit ? 'Sửa địa điểm' : 'Thêm địa điểm mới'}</h2>

      <form onSubmit={handleSubmit(onSubmit)} className="bg-white rounded-xl border p-6 space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Tên địa điểm *</label>
          <input {...register('name', { required: 'Bắt buộc' })} className="w-full px-4 py-2 border rounded-lg" />
          {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Địa chỉ *</label>
          <input {...register('address', { required: 'Bắt buộc' })} className="w-full px-4 py-2 border rounded-lg" />
          {errors.address && <p className="text-red-500 text-xs">{errors.address.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Sức chứa *</label>
          <input type="number" {...register('capacity', { required: 'Bắt buộc', min: 1 })} className="w-full px-4 py-2 border rounded-lg" />
          {errors.capacity && <p className="text-red-500 text-xs">{errors.capacity.message}</p>}
        </div>
        <button type="submit" disabled={loading}
          className="w-full py-3 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition disabled:opacity-50">
          {loading ? 'Đang lưu...' : isEdit ? 'Cập nhật' : 'Tạo địa điểm'}
        </button>
      </form>
    </div>
  );
}
