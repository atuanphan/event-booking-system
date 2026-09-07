import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Save } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../api/axios';

export default function StaffManagementPage() {
  const [loading, setLoading] = useState(false);
  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    defaultValues: { email: '', fullname: '', password: '' },
  });

  const onSubmit = async (values) => {
    setLoading(true);
    try {
      await api.put('/admin/staff', values);
      toast.success('Cập nhật thành công');
      reset();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Thao tác thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl">
      <h3 className="text-lg font-semibold mb-6">Quản lý nhân viên</h3>

      <div className="bg-white rounded-xl border p-6">
        <h4 className="font-medium mb-4">Đổi mật khẩu nhân viên</h4>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Email nhân viên *</label>
            <input type="email" {...register('email', { required: 'Bắt buộc' })}
              className="w-full px-4 py-2 border rounded-lg" placeholder="staff@example.com" />
            {errors.email && <p className="text-red-500 text-xs">{errors.email.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Họ và tên *</label>
            <input {...register('fullname', { required: 'Bắt buộc' })}
              className="w-full px-4 py-2 border rounded-lg" />
            {errors.fullname && <p className="text-red-500 text-xs">{errors.fullname.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Mật khẩu mới *</label>
            <input type="password" {...register('password', { required: 'Bắt buộc', minLength: { value: 6, message: 'Tối thiểu 6 ký tự' } })}
              className="w-full px-4 py-2 border rounded-lg" />
            {errors.password && <p className="text-red-500 text-xs">{errors.password.message}</p>}
          </div>
          <button type="submit" disabled={loading}
            className="flex items-center gap-2 px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition disabled:opacity-50">
            <Save className="w-4 h-4" />
            {loading ? 'Đang lưu...' : 'Cập nhật'}
          </button>
        </form>
      </div>
    </div>
  );
}
