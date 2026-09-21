                                                PROFESSIONAL WORK  — TRANG WEB ĐẶT VÉ SỰ KIỆN (SpringBoot - React)
												
# PROFESSIONAL WORK — TRANG WEB ĐẶT VÉ SỰ KIỆN

Website đặt vé sự kiện với 3 nhóm giao diện chính:
- **Customer** — Khách hàng
- **Admin** — Quản trị hệ thống
- **Organizer** — Đơn vị tổ chức

---

## A. Nhóm giao diện Khách hàng (Customer)
### 1. Trang chủ

- Banner sự kiện nổi bật
- Danh sách sự kiện
- Thanh tìm kiếm
- Bộ lọc:
  - Danh mục
  - Ngày
  - Địa điểm
  - Giá

### 2. Header

- Đăng nhập / Đăng xuất
- Avatar dropdown

### 3. Danh sách sự kiện

- Danh sách / kết quả tìm kiếm sự kiện
- Phân trang
- Bộ lọc

### 4. Chi tiết sự kiện

- Thông tin sự kiện
- Hình ảnh
- Mô tả
- Chọn loại vé
- Chọn số lượng
- Không cần sơ đồ ghế

### 5. Checkout

- Nhập mã giảm giá
- Tóm tắt đơn hàng
- Kiểm tra thông tin vé

### 6. Thanh toán online

- Chọn phương thức thanh toán:
  - VNPay
  - MoMo
  - Thẻ

### 7. Kết quả thanh toán

- Thanh toán thành công
- Thanh toán thất bại

### 8. Đăng nhập / Đăng ký

- Email / Password
- Quên mật khẩu
- Xác thực Email / OTP
- Đăng nhập Google / Facebook (OAuth)

### 9. Trang cá nhân

- Thông tin tài khoản
- Đổi mật khẩu

#### Vé của tôi

- Lịch sử đặt vé
- Xem vé điện tử
- Hiển thị mã QR
- Chưa cần chức năng quét QR check-in

### 10. Trang tĩnh

- Giới thiệu
- Liên hệ
- FAQ
- Điều khoản
- Chính sách

### 11. Trang lỗi / trạng thái

- 404
- Không có kết quả
- Loading
- Skeleton

---

## B. Nhóm giao diện Admin (Quản trị hệ thống)

### 1. Dashboard

- Thống kê doanh thu
- Số vé bán
- Biểu đồ theo thời gian

### 2. Quản lý sự kiện

- Duyệt / từ chối sự kiện
- CRUD toàn hệ thống

### 3. Quản lý địa điểm

- CRUD địa điểm

### 4. Quản lý người dùng

- Danh sách người dùng
- Khoá / mở tài khoản
- Phân quyền

### 5. Quản lý đơn vị tổ chức

- Duyệt đăng ký
- Theo dõi doanh thu
- Theo dõi hoa hồng

### 6. Quản lý đơn hàng / giao dịch

- Quản lý đơn hàng
- Quản lý giao dịch
- Quản lý mã giảm giá

---

## C. Nhóm giao diện Đơn vị tổ chức (Organizer)

### 1. Dashboard

- Doanh thu
- Số vé bán của các sự kiện do mình tổ chức

### 2. Quản lý sự kiện

- Tạo sự kiện
- Sửa sự kiện
- Xoá sự kiện
- Quản lý loại vé
- Quản lý giá vé

### 3. Quản lý đơn hàng / người mua

- Xem danh sách đơn hàng
- Xem người mua vé
- Quản lý đơn hàng của các sự kiện do mình tổ chức

### 4. Check-in QR

> Chức năng check-in QR sẽ được triển khai sau và có thể thêm route riêng khi cần.

---

## D. Lưu ý kỹ thuật quan trọng

### 1. Phân quyền và bảo vệ Route

Cần triển khai **Private / Protected Route** theo 3 role:

- `user`
- `admin`
- `organizer`

Mục tiêu:

- Chặn người dùng truy cập sai layout
- Chặn truy cập các trang không thuộc quyền hạn
- Điều hướng người dùng về trang phù hợp theo role

Ví dụ:

```text
user       → Customer Layout
admin      → Admin Layout
organizer  → Organizer Layout
```

### 2. Quản lý giỏ hàng

Giỏ hàng đặt vé nên giữ state tạm bằng:

- `sessionStorage`
- hoặc `localStorage`
- hoặc state management như Redux / Zustand

Mục tiêu là tránh mất thông tin vé khi người dùng chuyển sang trang thanh toán.

### 3. Thanh toán

Trang kết quả thanh toán cần xử lý **callback từ cổng thanh toán**.

Ví dụ:

```text
Customer
   │
   ▼
Checkout
   │
   ▼
Payment Gateway
   │
   ├── VNPay
   └── MoMo
   │
   ▼
Redirect URL
   │
   ▼
Payment Result
   │
   ├── Success
   └── Failed
```

Cần xử lý redirect URL trả về từ VNPay / MoMo để xác định trạng thái giao dịch.

---

## E. Cấu trúc thư mục gợi ý

```text
src/
├── components/
│   ├── Button
│   ├── Input
│   ├── Modal
│   ├── Card
│   └── TicketCard
│
├── layouts/
│   ├── MainLayout
│   ├── AuthLayout
│   ├── AdminLayout
│   └── OrganizerLayout
│
├── pages/
│   ├── customer/
│   │   ├── Home
│   │   ├── EventList
│   │   ├── EventDetail
│   │   ├── Checkout
│   │   ├── PaymentResult
│   │   ├── Profile
│   │   └── MyTickets
│   │
│   ├── auth/
│   │   ├── Login
│   │   ├── Register
│   │   └── ForgotPassword
│   │
│   ├── admin/
│   │   ├── Dashboard
│   │   ├── Events
│   │   ├── Venues
│   │   ├── Users
│   │   ├── Organizers
│   │   └── Orders
│   │
│   └── organizer/
│       ├── Dashboard
│       ├── MyEvents
│       └── MyOrders
│
├── routes/
│   └── PrivateRoute
│
├── services/
│   ├── api.js
│   ├── authService
│   ├── eventService
│   └── paymentService
│
├── store/
│   ├── auth
│   ├── cart
│   └── events
│
├── hooks/
│
└── utils/
```

---

## F. Tổng quan Flow

### Customer Flow

```text
Home
 │
 ├── Event List
 │      │
 │      └── Event Detail
 │              │
 │              ▼
 │           Checkout
 │              │
 │              ├── Coupon
 │              ├── Ticket
 │              └── Payment
 │                    │
 │                    ▼
 │              Payment Gateway
 │                    │
 │                    ▼
 │              Payment Result
 │
 └── Profile
        └── My Tickets
```

### Admin Flow

```text
Admin Login
    │
    ▼
Dashboard
    │
    ├── Events
    ├── Venues
    ├── Users
    ├── Organizers
    └── Orders / Transactions
```

### Organizer Flow

```text
Organizer Login
      │
      ▼
  Dashboard
      │
      ├── My Events
      │      ├── Create
      │      ├── Update
      │      └── Delete
      │
      └── My Orders
             └── Ticket Buyers
```

---

## G. Phân quyền hệ thống

| Role | Customer | Admin | Organizer |
|------|----------|-------|-----------|
| Xem sự kiện | ✅ | ✅ | ✅ |
| Đặt vé | ✅ | ❌ | ❌ |
| Thanh toán | ✅ | ❌ | ❌ |
| Xem vé của mình | ✅ | ❌ | ❌ |
| Quản lý người dùng | ❌ | ✅ | ❌ |
| Quản lý toàn bộ sự kiện | ❌ | ✅ | ❌ |
| Quản lý địa điểm | ❌ | ✅ | ❌ |
| Quản lý Organizer | ❌ | ✅ | ❌ |
| Quản lý sự kiện của mình | ❌ | ❌ | ✅ |
| Xem đơn hàng sự kiện của mình | ❌ | ❌ | ✅ |
| Check-in QR | ❌ | ❌ | 🔜 |

---

## H. Công nghệ

### Frontend

- React
- Vite
- React Router
- Redux / Zustand
- Axios

### Backend

- Java
- Spring Boot
- Spring Security
- REST API

### Authentication

- JWT
- OAuth2
- Google / Facebook Login

### Payment

- VNPay
- MoMo
- Thẻ

### Deployment

- Frontend: Vercel
- Backend: Railway
