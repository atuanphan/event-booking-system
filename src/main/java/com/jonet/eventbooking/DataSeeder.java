//package com.jonet.eventbooking;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import com.github.javafaker.Faker;
//import com.jonet.eventbooking.entity.EventEntity;
//import com.jonet.eventbooking.entity.OrderEntity;
//import com.jonet.eventbooking.entity.OrderItem;
//import com.jonet.eventbooking.entity.PaymentEntity;
//import com.jonet.eventbooking.entity.PromotionEntity;
//import com.jonet.eventbooking.entity.RoleEntity;
//import com.jonet.eventbooking.entity.SeatEntity;
//import com.jonet.eventbooking.entity.TicketType;
//import com.jonet.eventbooking.entity.UserEntity;
//import com.jonet.eventbooking.entity.VenueEntity;
//import com.jonet.eventbooking.repository.EventRepository;
//import com.jonet.eventbooking.repository.OrderItemRepository;
//import com.jonet.eventbooking.repository.OrderRepository;
//import com.jonet.eventbooking.repository.PaymentRepository;
//import com.jonet.eventbooking.repository.PromotionRepository;
//import com.jonet.eventbooking.repository.RoleRepository;
//import com.jonet.eventbooking.repository.SeatRepository;
//import com.jonet.eventbooking.repository.TicketTypeRepository;
//import com.jonet.eventbooking.repository.UserRepository;
//import com.jonet.eventbooking.repository.VenueRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class DataSeeder implements CommandLineRunner {
//	private final VenueRepository venueRepository;
//	private final RoleRepository roleRepository;
//	private final UserRepository userRepository;
//	private final PromotionRepository promotionRepository;
//	private final EventRepository eventRepository;
//	private final TicketTypeRepository ticketTypeRepository;
//	private final SeatRepository seatRepository;
//	private final OrderRepository orderRepository;
//	private final OrderItemRepository orderItemRepository;
//	private final PaymentRepository paymentRepository;
//	
//
//	@Override
//	public void run(String... args) throws Exception {
//		Faker faker = new Faker();
//		List<RoleEntity> roles = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            RoleEntity role = new RoleEntity();
//            role.setName(faker.job().title());
//            role.setCode("ROLE_" + i);
//            roles.add(role);
//            roleRepository.save(role);
//        }
//
//        // 2. Tạo 10 Users
//        List<UserEntity> users = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            UserEntity user = new UserEntity();
//            user.setFullname(faker.name().fullName());
//            user.setEmail(faker.internet().emailAddress());
//            user.setPassword(faker.internet().password());
//            user.setRoles(Collections.singletonList(roles.get(i))); // Mỗi user 1 role
//            users.add(user);
//            userRepository.save(user);
//        }
//
//        // 3. Tạo 10 Venues (Địa điểm)
//        List<VenueEntity> venues = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            VenueEntity v = new VenueEntity();
//            v.setName(faker.company().name() + " Center");
//            v.setAddress(faker.address().fullAddress());
//            v.setCapacity(faker.number().numberBetween(500, 10000));
//            venues.add(v);
//            venueRepository.save(v);
//        }
//
//        // 4. Tạo 10 Promotions
//        List<PromotionEntity> promotions = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            PromotionEntity p = new PromotionEntity();
//            p.setCode("GIFT" + faker.number().digits(4));
//            p.setDiscountType("FIXED_AMOUNT");
//            p.setDiscountValue(new BigDecimal("50000.00"));
//            p.setMaxUsage(100);
//            p.setUsed_count(faker.number().numberBetween(0, 50));
//            promotions.add(p);
//            promotionRepository.save(p);
//        }
//
//        // 5. Tạo 10 Events (Mỗi event gắn vào 1 Venue tương ứng)
//        List<EventEntity> events = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            EventEntity e = new EventEntity();
//            e.setName("Sự kiện âm nhạc " + faker.artist().name());
//            e.setDescription(faker.lorem().sentence(20));
//            e.setStartTime(LocalDateTime.now().plusDays(i + 1));
//            e.setEndTime(e.getStartTime().plusHours(4));
//            e.setStatus("OPEN_FOR_SALE");
//            e.setImage_url(faker.internet().image());
//            e.setVenue(venues.get(i));
//            events.add(e);
//            eventRepository.save(e);
//        }
//
//        // 6. Tạo 10 TicketTypes cho Event đầu tiên (để ví dụ)
//        List<TicketType> ticketTypes = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            TicketType t = new TicketType();
//            t.setName("Hạng vé " + (i + 1));
//            t.setPrice(new BigDecimal(faker.commerce().price(100000, 2000000)));
//            t.setTotalQuantity(100);
//            t.setEvent(events.get(0)); // Gắn tạm vào event 0
//            ticketTypes.add(t);
//            ticketTypeRepository.save(t);
//        }
//
//        // 7. Tạo 10 Seats cho TicketType đầu tiên
//        List<SeatEntity> seats = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            SeatEntity s = new SeatEntity();
//            s.setSeatRow("R" + (i / 5 + 1));
//            s.setSeatNumber("N" + (i % 5 + 1));
//            s.setStatus("AVAILABLE");
//            s.setVersion(1);
//            s.setTicketType(ticketTypes.get(0));
//            seats.add(s);
//            seatRepository.save(s);
//        }
//
//        // 8. Tạo 10 Orders
//        List<OrderEntity> orders = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            OrderEntity o = new OrderEntity();
//            o.setUser(users.get(i));
//            o.setPromotion(promotions.get(i));
//            o.setTotalAmount(new BigDecimal("500000.00"));
//            o.setStatus("COMPLETED");
//            o.setExpiresAt(LocalDateTime.now().plusMinutes(30));
//            orders.add(o);
//            orderRepository.save(o);
//        }
//
//        // 9. Tạo 10 OrderItems (Mỗi order 1 item)
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            OrderItem item = new OrderItem();
//            item.setOrder(orders.get(i));
//            item.setSeatId(seats.get(i).getId());
//            orderItems.add(item);
//            orderItemRepository.save(item);
//        }
//
//        // 10. Tạo 10 Payments
//        List<PaymentEntity> payments = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            PaymentEntity pay = new PaymentEntity();
//            pay.setOrderId(orders.get(i).getId());
//            pay.setTransactionId("TXN-" + faker.idNumber().valid());
//            pay.setPaymentMethod("CREDIT_CARD");
//            pay.setAmount(orders.get(i).getTotalAmount());
//            pay.setPaymentStatus("PAID");
//            payments.add(pay);
//            paymentRepository.save(pay);
//        }
//	}
//}
