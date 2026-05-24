//package com.jonet.eventbooking;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import com.github.javafaker.Faker;
//import com.jonet.eventbooking.entity.EventEntity;
//import com.jonet.eventbooking.entity.OrderEntity;
//import com.jonet.eventbooking.entity.OrderItemEntity;
//import com.jonet.eventbooking.entity.PaymentEntity;
//import com.jonet.eventbooking.entity.PromotionEntity;
//import com.jonet.eventbooking.entity.RoleEntity;
//import com.jonet.eventbooking.entity.SeatEntity;
//import com.jonet.eventbooking.entity.TicketTypeEntity;
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
//	private final OrderRepository orderRepository;
//	private final OrderItemRepository orderItemRepository;
//	private final PaymentRepository paymentRepository;
//	private final SeatRepository seatRepository;
//	
//
//	@Override
//	public void run(String... args) throws Exception {
//	    Faker faker = new Faker();
//	    
//	    // 1. Tạo 10 Roles
//	    List<RoleEntity> roles = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        RoleEntity role = new RoleEntity();
//	        role.setName(faker.job().title());
//	        role.setCode("ROLE_" + i);
//	        roles.add(roleRepository.saveAndFlush(role)); // Dùng saveAndFlush
//	    }
//
//	    // 2. Tạo 10 Users
//	    List<UserEntity> users = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        UserEntity user = new UserEntity();
//	        user.setFullname(faker.name().fullName());
//	        user.setEmail(faker.internet().emailAddress());
//	        user.setPassword(faker.internet().password());
//	        user.setRoles(Collections.singletonList(roles.get(i))); 
//	        users.add(userRepository.saveAndFlush(user)); // Dùng saveAndFlush
//	    }
//
//	    // 3. Tạo 10 Venues
//	    List<VenueEntity> venues = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        VenueEntity v = new VenueEntity();
//	        v.setName(faker.company().name() + " Center");
//	        v.setAddress(faker.address().fullAddress());
//	        v.setCapacity(faker.number().numberBetween(500, 10000));
//	        venues.add(venueRepository.saveAndFlush(v)); // Dùng saveAndFlush
//	    }
//
//	    // 4. Tạo 10 Promotions
//	    List<PromotionEntity> promotions = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        PromotionEntity p = new PromotionEntity();
//	        p.setCode("GIFT" + faker.number().digits(10));
//	        p.setDiscountType("FIXED_AMOUNT");
//	        p.setDiscountValue(new BigDecimal("50000.00"));
//	        p.setMaxUsage(100);
//	        p.setUsed_count(faker.number().numberBetween(0, 50));
//	        promotions.add(promotionRepository.saveAndFlush(p)); // Dùng saveAndFlush
//	    }
//
//	    // 5. Tạo 10 Events
//	    List<EventEntity> events = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        EventEntity e = new EventEntity();
//	        e.setName("Sự kiện âm nhạc " + faker.artist().name());
//	        e.setDescription(faker.lorem().sentence(20));
//	        e.setStartTime(LocalDateTime.now().plusDays(i + 1));
//	        e.setEndTime(e.getStartTime().plusHours(4));
//	        e.setStatus("OPEN_FOR_SALE");
//	        e.setImageUrl(faker.internet().image());
//	        e.setVenue(venues.get(i));
//	        events.add(eventRepository.saveAndFlush(e)); // Dùng saveAndFlush
//	    }
//
//	    // 6. Tạo 10 TicketTypes cho Event đầu tiên
//	    List<TicketTypeEntity> ticketTypes = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        TicketTypeEntity t = new TicketTypeEntity();
//	        t.setName("Hạng vé " + (i + 1));
//	        t.setPrice(new BigDecimal(faker.commerce().price(100000, 2000000)));
//	        t.setTotalQuantity(100);
//	        t.setAvailableQuantity(100);
//	        t.setEvent(events.get(0)); 
//	        ticketTypes.add(t); // Dùng saveAndFlush
//	        ticketTypeRepository.saveAndFlush(t);
//	    }
//	    
//	    List<SeatEntity> seats = new ArrayList<SeatEntity>();
//	    for (int i = 0; i < 10; i++) {
//	        SeatEntity s = new SeatEntity();
//	        s.setSeatNumber("A" + i);
//	        s.setSeatRow("G" + i);
//	        s.setStatus("OK");
//	        s.setTicketType(ticketTypes.get(i));
//	        s.setVersion(1);
//	        seatRepository.save(s);
//	    }
//
//	    // 8. Tạo 10 Orders
//	    List<OrderEntity> orders = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        OrderEntity o = new OrderEntity();
//	        o.setUser(users.get(i));
//	        o.setPromotion(promotions.get(i));
//	        o.setTotalAmount(new BigDecimal("500000.00"));
//	        o.setStatus("COMPLETED");
//	        o.setExpiresAt(LocalDateTime.now().plusMinutes(30));
//	        orders.add(orderRepository.saveAndFlush(o)); // Bắt buộc lưu đứt điểm để lấy ID chuẩn xuống DB
//	    }
//
//	    // 9. Tạo 10 OrderItems
//	    List<OrderItemEntity> orderItems = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        OrderItemEntity item = new OrderItemEntity();
//	        item.setOrder(orders.get(i));
//	        item.setQuantity(3);
//	        item.setPrice(new BigDecimal("150000.00")); // FIXED: Sửa lại cách khởi tạo BigDecimal bằng String
//	        item.setTicketType(ticketTypes.get(2));
//	        orderItems.add(orderItemRepository.saveAndFlush(item));
//	    }
//
//	    // 10. Tạo 10 Payments
//	    List<PaymentEntity> payments = new ArrayList<>();
//	    for (int i = 0; i < 10; i++) {
//	        PaymentEntity pay = new PaymentEntity();
//	        pay.setOrderId(orders.get(i).getId()); // Khóa ngoại bây giờ đã chắc chắn có dữ liệu
//	        pay.setTransactionId("TXN-" + faker.idNumber().valid());
//	        pay.setPaymentMethod("CREDIT_CARD");
//	        pay.setAmount(orders.get(i).getTotalAmount());
//	        pay.setPaymentStatus("PAID");
//	        payments.add(paymentRepository.saveAndFlush(pay));
//	    }
//	}
//}
