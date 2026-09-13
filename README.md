                                                PROFESSIONAL WORK  — TRANG WEB ĐẶT VÉ SỰ KIỆN (SpringBoot - React)
												
A. Nhóm giao diện Khách hàng (Customer)
Trang chủ
Banner sự kiện nổi bật, danh sách theo danh mục
Thanh tìm kiếm + bộ lọc (danh mục, ngày, địa điểm, giá)
Header: đăng nhập/đăng xuất, avatar dropdown
Trang danh sách/kết quả tìm kiếm sự kiện (phân trang, lọc)
Trang chi tiết sự kiện
Thông tin, hình ảnh, mô tả
Chọn loại vé + số lượng (không cần sơ đồ ghế)
Nhập mã giảm giá, tóm tắt đơn hàng
Thanh toán online
Chọn phương thức thanh toán (VNPay/Momo/thẻ)
Trang kết quả thanh toán (thành công/thất bại)
Đăng nhập / Đăng ký
Email/password, quên mật khẩu, xác thực email/OTP
Đăng nhập Google/Facebook (OAuth)
Trang cá nhân
Thông tin tài khoản, đổi mật khẩu
"Vé của tôi": lịch sử đặt vé, xem vé điện tử (mã QR để hiển thị — chưa cần quét check-in)
Trang tĩnh: Giới thiệu, Liên hệ, FAQ, Điều khoản/Chính sách
Trang lỗi/trạng thái: 404, không có kết quả, loading/skeleton
B. Nhóm giao diện Admin (quản trị hệ thống)
Dashboard: thống kê doanh thu, số vé bán, biểu đồ theo thời gian
Quản lý sự kiện: duyệt/từ chối, CRUD toàn hệ thống
Quản lý địa điểm: CRUD
Quản lý người dùng: danh sách, khoá/mở tài khoản, phân quyền
Quản lý đơn vị tổ chức: duyệt đăng ký, theo dõi doanh thu/hoa hồng
Quản lý đơn hàng/giao dịch & mã giảm giá
C. Nhóm giao diện Đơn vị tổ chức (Organizer)
Dashboard riêng: doanh thu, số vé bán của sự kiện mình
Quản lý sự kiện của mình: tạo/sửa/xoá, loại vé, giá vé
Quản lý đơn hàng/người mua vé của sự kiện mình
(Check-in QR để sau — có thể thêm route riêng khi cần)

Lưu ý kỹ thuật quan trọng:
Cần Private/Protected Route phân theo 3 role: user, admin, organizer — chặn truy cập nhầm layout.
Giỏ hàng đặt vé nên giữ state tạm (session/local) để không mất khi chuyển sang trang thanh toán.
Trang kết quả thanh toán cần xử lý callback từ cổng thanh toán (redirect URL trả về từ VNPay/Momo).

Cấu trúc thư mục gợi ý:
src/
├── components/       # Button, Input, Modal, Card, TicketCard...
├── layouts/          # MainLayout, AuthLayout, AdminLayout, OrganizerLayout
├── pages/
│   ├── customer/     # Home, EventList, EventDetail, Checkout, PaymentResult, Profile, MyTickets
│   ├── auth/         # Login, Register, ForgotPassword
│   ├── admin/        # Dashboard, Events, Venues, Users, Organizers, Orders
│   └── organizer/    # Dashboard, MyEvents, MyOrders
├── routes/           # Định tuyến + phân quyền theo role (Private Route)
├── services/         # api.js, authService, eventService, paymentService...
├── store/            # Redux/Zustand: auth, cart, events
├── hooks/
└── utils/
