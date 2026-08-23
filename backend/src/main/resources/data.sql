-- ============================================================
-- EVENT BOOKING SYSTEM
-- MySQL 8
-- DATABASE + TABLES + DATA SEED
-- ============================================================

-- ============================================================
-- 1. CREATE DATABASE
-- ============================================================

CREATE DATABASE IF NOT EXISTS ticket_pro_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ticket_pro_db;

-- ============================================================
-- 2. DROP TABLES
-- Dùng để có thể chạy lại file SQL nhiều lần
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS ticket_types;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS venues;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS promotions;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 3. USERS
-- ============================================================

CREATE TABLE users (
    id BINARY(16) NOT NULL,
    email VARCHAR(100),
    fullname VARCHAR(150),
    password VARCHAR(255),
    status INT NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 4. ROLES
-- ============================================================

CREATE TABLE roles (
    id BINARY(16) NOT NULL,
    name VARCHAR(50),
    code VARCHAR(20) NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 5. USER ROLE
-- Many-to-Many:
-- UserEntity <-> RoleEntity
-- ============================================================

CREATE TABLE user_role (
    user_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 6. VENUES
-- ============================================================

CREATE TABLE venues (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    capacity INT,

    PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 7. EVENTS
-- EventEntity
-- ============================================================

CREATE TABLE events (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME,
    end_time DATETIME,
    status VARCHAR(50) NOT NULL,
    image_url VARCHAR(500),
    venue_id BINARY(16),

    PRIMARY KEY (id),

    CONSTRAINT fk_events_venue
        FOREIGN KEY (venue_id)
        REFERENCES venues(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 8. TICKET TYPES
-- ============================================================

CREATE TABLE ticket_types (
    id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    total_quantity INT NOT NULL,
    available_quantity INT,

    event_id BINARY(16),

    PRIMARY KEY (id),

    CONSTRAINT chk_ticket_available
        CHECK (available_quantity >= 0),

    CONSTRAINT fk_ticket_types_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 9. SEATS
-- ============================================================

CREATE TABLE seats (
    id BINARY(16) NOT NULL,
    seat_row VARCHAR(10) NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    status VARCHAR(50),
    version INT NOT NULL,

    tickettype_id BINARY(16),

    PRIMARY KEY (id),

    CONSTRAINT fk_seats_ticket_type
        FOREIGN KEY (tickettype_id)
        REFERENCES ticket_types(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 10. PROMOTIONS
-- ============================================================

CREATE TABLE promotions (
    id BINARY(16) NOT NULL,
    code VARCHAR(20),
    discount_type VARCHAR(255),
    discount_value DECIMAL(19,2),
    max_usage INT,
    used_count INT,

    PRIMARY KEY (id),

    UNIQUE KEY uk_promotions_code (code)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 11. ORDERS
-- ============================================================

CREATE TABLE orders (
    id BINARY(16) NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,

    user_id BINARY(16),
    promotion_id BINARY(16),

    PRIMARY KEY (id),

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_orders_promotion
        FOREIGN KEY (promotion_id)
        REFERENCES promotions(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 12. ORDER ITEMS
-- ============================================================

CREATE TABLE order_items (
    id BINARY(16) NOT NULL,
    quantity INT,
    price DECIMAL(19,2),
    seat_id BINARY(16),
    order_id BINARY(16),
    tickettype_id BINARY(16),

    PRIMARY KEY (id),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_order_items_ticket_type
        FOREIGN KEY (tickettype_id)
        REFERENCES ticket_types(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 13. PAYMENTS
-- ============================================================

CREATE TABLE payments (
    id BINARY(16) NOT NULL,
    order_id BINARY(16),
    transaction_id VARCHAR(100) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 14. INSERT DATA
-- ============================================================

START TRANSACTION;

-- ============================================================
-- ROLES
-- ============================================================

INSERT INTO roles
(id, name, code)
VALUES
(
    UUID_TO_BIN('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    'Administrator',
    'ADMIN'
),
(
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
    'User',
    'USER'
);


-- ============================================================
-- USERS
-- ============================================================

INSERT INTO users
(id, email, password, fullname, status)
VALUES
(
    UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
    'admin@eventhub.vn',
    '$2a$10$demo_admin_hash',
    'System Admin',
    1
),
(
    UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
    'nguyenan@gmail.com',
    '$2a$10$demo_user_hash_01',
    'Nguyen An',
    1
),
(
    UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
    'tranbinh@gmail.com',
    '$2a$10$demo_user_hash_02',
    'Tran Binh',
    1
),
(
    UUID_TO_BIN('44444444-4444-4444-4444-444444444444'),
    'lechi@gmail.com',
    '$2a$10$demo_user_hash_03',
    'Le Chi',
    1
),
(
    UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
    'phamduc@gmail.com',
    '$2a$10$demo_user_hash_04',
    'Pham Duc',
    1
),
(
    UUID_TO_BIN('66666666-6666-6666-6666-666666666666'),
    'hoangminh@gmail.com',
    '$2a$10$demo_user_hash_05',
    'Hoang Minh',
    1
),
(
    UUID_TO_BIN('77777777-7777-7777-7777-777777777777'),
    'vothu@gmail.com',
    '$2a$10$demo_user_hash_06',
    'Vo Thu',
    1
),
(
    UUID_TO_BIN('88888888-8888-8888-8888-888888888888'),
    'doanlong@gmail.com',
    '$2a$10$demo_user_hash_07',
    'Doan Long',
    1
);


-- ============================================================
-- USER ROLE
-- ============================================================

INSERT INTO user_role
(user_id, role_id)
VALUES
(
    UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
    UUID_TO_BIN('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa')
),
(
    UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
),
(
    UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
),
(
    UUID_TO_BIN('44444444-4444-4444-4444-444444444444'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
),
(
    UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
),
(
    UUID_TO_BIN('66666666-6666-6666-6666-666666666666'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
),
(
    UUID_TO_BIN('77777777-7777-7777-7777-777777777777'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
),
(
    UUID_TO_BIN('88888888-8888-8888-8888-888888888888'),
    UUID_TO_BIN('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb')
);


-- ============================================================
-- VENUES
-- ============================================================

INSERT INTO venues
(id, name, address, capacity)
VALUES
(
    UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),
    'National Convention Center',
    'Pham Hung, Nam Tu Liem, Ha Noi',
    4000
),
(
    UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),
    'Quan Ngua Sports Palace',
    'Van Cao, Ba Dinh, Ha Noi',
    3000
),
(
    UUID_TO_BIN('10000000-0000-0000-0000-000000000003'),
    'SECC',
    '799 Nguyen Van Linh, District 7, Ho Chi Minh City',
    5000
);


-- ============================================================
-- EVENTS
-- ============================================================

INSERT INTO events
(
    id,
    venue_id,
    name,
    description,
    start_time,
    end_time,
    status,
    image_url
)
VALUES
(
    UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),
    'Vietnam Music Festival 2026',
    'A large-scale live music festival featuring Vietnamese artists.',
    '2026-09-12 19:00:00',
    '2026-09-12 23:00:00',
    'UPCOMING',
    'https://example.com/images/music-festival-2026.jpg'
),
(
    UUID_TO_BIN('20000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),
    'Tech Conference Vietnam 2026',
    'Technology conference for developers, startups and technology companies.',
    '2026-09-20 08:30:00',
    '2026-09-20 17:30:00',
    'UPCOMING',
    'https://example.com/images/tech-conference-2026.jpg'
),
(
    UUID_TO_BIN('20000000-0000-0000-0000-000000000003'),
    UUID_TO_BIN('10000000-0000-0000-0000-000000000003'),
    'Business Networking Night',
    'Networking event for entrepreneurs and business professionals.',
    '2026-10-05 18:30:00',
    '2026-10-05 22:00:00',
    'UPCOMING',
    'https://example.com/images/networking-night.jpg'
),
(
    UUID_TO_BIN('20000000-0000-0000-0000-000000000004'),
    UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),
    'Comedy Night 2026',
    'A live comedy show with popular comedians.',
    '2026-10-18 19:30:00',
    '2026-10-18 22:00:00',
    'UPCOMING',
    'https://example.com/images/comedy-night.jpg'
),
(
    UUID_TO_BIN('20000000-0000-0000-0000-000000000005'),
    UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),
    'New Year Countdown 2027',
    'Countdown party welcoming the new year.',
    '2026-12-31 20:00:00',
    '2027-01-01 00:30:00',
    'UPCOMING',
    'https://example.com/images/countdown-2027.jpg'
);


-- ============================================================
-- TICKET TYPES
-- ============================================================

INSERT INTO ticket_types
(
    id,
    event_id,
    name,
    price,
    total_quantity,
    available_quantity
)
VALUES
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),
    'VIP',
    1500000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),
    'Standard',
    800000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000003'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),
    'Economy',
    500000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000004'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000002'),
    'Premium',
    1200000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000005'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000002'),
    'Standard',
    700000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000006'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000003'),
    'VIP',
    900000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000007'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000003'),
    'Standard',
    500000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000008'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000004'),
    'VIP',
    1000000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000009'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000004'),
    'Standard',
    600000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000010'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000005'),
    'VIP',
    2000000.00,
    10,
    10
),
(
    UUID_TO_BIN('30000000-0000-0000-0000-000000000011'),
    UUID_TO_BIN('20000000-0000-0000-0000-000000000005'),
    'Standard',
    1000000.00,
    10,
    10
);


-- ============================================================
-- SEATS
-- ============================================================

INSERT INTO seats
(
    id,
    tickettype_id,
    seat_row,
    seat_number,
    status,
    version
)
VALUES
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    'A',
    '01',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    'A',
    '02',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000003'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    'A',
    '03',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000004'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    'A',
    '04',
    'AVAILABLE',
    0
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000005'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    'A',
    '05',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000006'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),
    'A',
    '06',
    'AVAILABLE',
    0
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000007'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),
    'B',
    '01',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000008'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),
    'B',
    '02',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000009'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),
    'B',
    '03',
    'AVAILABLE',
    0
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000010'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),
    'B',
    '04',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000011'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000003'),
    'C',
    '01',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000012'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000003'),
    'C',
    '02',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000013'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000003'),
    'C',
    '03',
    'SOLD',
    1
),
(
    UUID_TO_BIN('40000000-0000-0000-0000-000000000014'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000003'),
    'C',
    '04',
    'AVAILABLE',
    0
);


-- ============================================================
-- PROMOTIONS
-- ============================================================

INSERT INTO promotions
(
    id,
    code,
    discount_type,
    discount_value,
    max_usage,
    used_count
)
VALUES
(
    UUID_TO_BIN('50000000-0000-0000-0000-000000000001'),
    'WELCOME10',
    'PERCENTAGE',
    10.00,
    100,
    2
),
(
    UUID_TO_BIN('50000000-0000-0000-0000-000000000002'),
    'SUMMER100K',
    'FIXED',
    100000.00,
    50,
    1
),
(
    UUID_TO_BIN('50000000-0000-0000-0000-000000000003'),
    'VIP20',
    'PERCENTAGE',
    20.00,
    20,
    1
),
(
    UUID_TO_BIN('50000000-0000-0000-0000-000000000004'),
    'EVENT50K',
    'FIXED',
    50000.00,
    100,
    2
);


-- ============================================================
-- ORDERS
-- ============================================================

INSERT INTO orders
(
    id,
    user_id,
    promotion_id,
    total_amount,
    status,
    expires_at
)
VALUES
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
    UUID_TO_BIN('50000000-0000-0000-0000-000000000001'),
    2700000.00,
    'PAID',
    '2026-09-01 09:15:00'
),
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
    NULL,
    800000.00,
    'PAID',
    '2026-09-01 10:20:00'
),
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000003'),
    UUID_TO_BIN('44444444-4444-4444-4444-444444444444'),
    UUID_TO_BIN('50000000-0000-0000-0000-000000000002'),
    700000.00,
    'PAID',
    '2026-09-01 11:05:00'
),
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000004'),
    UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
    UUID_TO_BIN('50000000-0000-0000-0000-000000000003'),
    1200000.00,
    'PAID',
    '2026-09-01 13:30:00'
),
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000005'),
    UUID_TO_BIN('66666666-6666-6666-6666-666666666666'),
    NULL,
    500000.00,
    'PENDING',
    '2026-08-22 23:30:00'
),
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000006'),
    UUID_TO_BIN('77777777-7777-7777-7777-777777777777'),
    UUID_TO_BIN('50000000-0000-0000-0000-000000000004'),
    750000.00,
    'PAID',
    '2026-09-01 14:10:00'
),
(
    UUID_TO_BIN('60000000-0000-0000-0000-000000000007'),
    UUID_TO_BIN('88888888-8888-8888-8888-888888888888'),
    NULL,
    1200000.00,
    'PAID',
    '2026-09-01 15:40:00'
);


-- ============================================================
-- ORDER ITEMS
-- ============================================================

INSERT INTO order_items
(
    id,
    quantity,
    price,
    seat_id,
    order_id,
    tickettype_id
)
VALUES
(
    UUID_TO_BIN('70000000-0000-0000-0000-000000000001'),
    1,
    1500000.00,
    UUID_TO_BIN('40000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001')
),
(
    UUID_TO_BIN('70000000-0000-0000-0000-000000000002'),
    1,
    1500000.00,
    UUID_TO_BIN('40000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000001')
),
(
    UUID_TO_BIN('70000000-0000-0000-0000-000000000003'),
    1,
    800000.00,
    UUID_TO_BIN('40000000-0000-0000-0000-000000000007'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002')
),
(
    UUID_TO_BIN('70000000-0000-0000-0000-000000000004'),
    1,
    800000.00,
    UUID_TO_BIN('40000000-0000-0000-0000-000000000008'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000003'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000002')
),
(
    UUID_TO_BIN('70000000-0000-0000-0000-000000000005'),
    1,
    1200000.00,
    UUID_TO_BIN('40000000-0000-0000-0000-000000000009'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000004'),
    UUID_TO_BIN('30000000-0000-0000-0000-000000000004')
);


-- ============================================================
-- PAYMENTS
-- ============================================================

INSERT INTO payments
(
    id,
    order_id,
    transaction_id,
    payment_method,
    amount,
    payment_status
)
VALUES
(
    UUID_TO_BIN('80000000-0000-0000-0000-000000000001'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000001'),
    'TXN202608200001',
    'VNPAY',
    2700000.00,
    'SUCCESS'
),
(
    UUID_TO_BIN('80000000-0000-0000-0000-000000000002'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000002'),
    'TXN202608200002',
    'MOMO',
    800000.00,
    'SUCCESS'
),
(
    UUID_TO_BIN('80000000-0000-0000-0000-000000000003'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000003'),
    'TXN202608200003',
    'BANK_TRANSFER',
    700000.00,
    'SUCCESS'
),
(
    UUID_TO_BIN('80000000-0000-0000-0000-000000000004'),
    UUID_TO_BIN('60000000-0000-0000-0000-000000000004'),
    'TXN202608200004',
    'VNPAY',
    1200000.00,
    'SUCCESS'
);

COMMIT;


-- ============================================================
-- 15. CHECK DATA
-- ============================================================

SELECT 'users' AS table_name, COUNT(*) AS total FROM users
UNION ALL
SELECT 'roles', COUNT(*) FROM roles
UNION ALL
SELECT 'user_role', COUNT(*) FROM user_role
UNION ALL
SELECT 'venues', COUNT(*) FROM venues
UNION ALL
SELECT 'events', COUNT(*) FROM events
UNION ALL
SELECT 'ticket_types', COUNT(*) FROM ticket_types
UNION ALL
SELECT 'seats', COUNT(*) FROM seats
UNION ALL
SELECT 'promotions', COUNT(*) FROM promotions
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL
SELECT 'payments', COUNT(*) FROM payments;