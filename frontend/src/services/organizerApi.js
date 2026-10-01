const venues = [
  { id: 'venue-1', name: 'Nhà hát Hòa Bình', address: 'TP. Hồ Chí Minh', capacity: 2500 },
  { id: 'venue-2', name: 'Trung tâm Hội nghị Quốc gia', address: 'Hà Nội', capacity: 3800 },
  { id: 'venue-3', name: 'Công viên Biển Đông', address: 'Đà Nẵng', capacity: 5000 },
];

let events = [
  {
    id: 'event-1', name: 'Đêm nhạc Những miền ký ức', description: 'Một đêm nhạc live cùng những nghệ sĩ trẻ và dàn nhạc acoustic.',
    status: 'UPCOMING', startTime: '2026-10-18T19:30:00', endTime: '2026-10-18T22:00:00', imageUrl: '',
    venue: { id: 'venue-1', name: 'Nhà hát Hòa Bình', address: 'TP. Hồ Chí Minh' },
    ticketTypes: [
      { id: 'ticket-1', name: 'Vé tiêu chuẩn', price: 450000, totalQuantity: 1200, availableQuantity: 762 },
      { id: 'ticket-2', name: 'Vé VIP', price: 1200000, totalQuantity: 180, availableQuantity: 54 },
    ],
  },
  {
    id: 'event-2', name: 'Product Makers Summit 2026', description: 'Ngày hội dành cho cộng đồng sản phẩm và công nghệ.',
    status: 'UPCOMING', startTime: '2026-11-07T08:30:00', endTime: '2026-11-07T17:30:00', imageUrl: '',
    venue: { id: 'venue-2', name: 'Trung tâm Hội nghị Quốc gia', address: 'Hà Nội' },
    ticketTypes: [
      { id: 'ticket-3', name: 'Early access', price: 890000, totalQuantity: 800, availableQuantity: 271 },
      { id: 'ticket-4', name: 'Business', price: 1600000, totalQuantity: 250, availableQuantity: 181 },
    ],
  },
  {
    id: 'event-3', name: 'Sắc màu bên sông', description: 'Lễ hội nghệ thuật ngoài trời bên bờ sông Hàn.',
    status: 'FINISHED', startTime: '2026-08-22T16:00:00', endTime: '2026-08-22T22:00:00', imageUrl: '',
    venue: { id: 'venue-3', name: 'Công viên Biển Đông', address: 'Đà Nẵng' },
    ticketTypes: [
      { id: 'ticket-5', name: 'Vé vào cổng', price: 250000, totalQuantity: 1600, availableQuantity: 0 },
    ],
  },
  {
    id: 'event-4', name: 'Workshop: Kể chuyện bằng dữ liệu', description: 'Workshop thực hành dành cho người làm nội dung và phân tích.',
    status: 'DRAFT', startTime: '2026-12-12T09:00:00', endTime: '2026-12-12T12:00:00', imageUrl: '',
    venue: { id: 'venue-1', name: 'Nhà hát Hòa Bình', address: 'TP. Hồ Chí Minh' },
    ticketTypes: [],
  },
];

const orders = [
  { id: 'ORD-2609281', eventId: 'event-1', eventName: 'Đêm nhạc Những miền ký ức', customerName: 'Nguyễn Minh Anh', customerEmail: 'minhanh@example.com', totalAmount: 900000, status: 'COMPLETED', createdAt: '2026-09-28T10:14:00', ticketQuantity: 2 },
  { id: 'ORD-2609274', eventId: 'event-2', eventName: 'Product Makers Summit 2026', customerName: 'Trần Hoàng Nam', customerEmail: 'nam@example.com', totalAmount: 1600000, status: 'COMPLETED', createdAt: '2026-09-27T15:42:00', ticketQuantity: 1 },
  { id: 'ORD-2609260', eventId: 'event-1', eventName: 'Đêm nhạc Những miền ký ức', customerName: 'Lê Thu Hà', customerEmail: 'thuha@example.com', totalAmount: 1200000, status: 'PENDING', createdAt: '2026-09-26T19:05:00', ticketQuantity: 1 },
  { id: 'ORD-2609236', eventId: 'event-3', eventName: 'Sắc màu bên sông', customerName: 'Phạm Quốc Bảo', customerEmail: 'quocbao@example.com', totalAmount: 500000, status: 'COMPLETED', createdAt: '2026-09-23T08:21:00', ticketQuantity: 2 },
  { id: 'ORD-2609208', eventId: 'event-2', eventName: 'Product Makers Summit 2026', customerName: 'Vũ Khánh Linh', customerEmail: 'linh@example.com', totalAmount: 890000, status: 'CANCELLED', createdAt: '2026-09-20T13:33:00', ticketQuantity: 1 },
  { id: 'ORD-2609189', eventId: 'event-1', eventName: 'Đêm nhạc Những miền ký ức', customerName: 'Đỗ Hoàng Long', customerEmail: 'long@example.com', totalAmount: 450000, status: 'COMPLETED', createdAt: '2026-09-18T11:49:00', ticketQuantity: 1 },
];

const clone = (value) => JSON.parse(JSON.stringify(value));

export async function getMyEvents() {
  return clone(events).sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
}

export async function getEvent(id) {
  const event = events.find((item) => item.id === id);
  return event ? clone(event) : null;
}

export async function getVenues() {
  return clone(venues);
}

export async function saveEvent(values) {
  const venue = venues.find((item) => item.id === values.venueId);
  const existingIndex = events.findIndex((item) => item.id === values.id);
  const event = {
    ...values,
    id: values.id || `event-${Date.now()}`,
    venue: venue ? { id: venue.id, name: venue.name, address: venue.address } : null,
    ticketTypes: existingIndex >= 0 ? events[existingIndex].ticketTypes : [],
  };
  if (existingIndex >= 0) events[existingIndex] = event;
  else events = [event, ...events];
  return clone(event);
}

export async function cancelEvent(id) {
  events = events.map((event) => event.id === id ? { ...event, status: 'CANCELLED' } : event);
}

export async function saveTicketType(eventId, values) {
  events = events.map((event) => {
    if (event.id !== eventId) return event;
    const ticket = { ...values, id: values.id || `ticket-${Date.now()}`, availableQuantity: values.availableQuantity ?? values.totalQuantity };
    const ticketTypes = values.id
      ? event.ticketTypes.map((item) => item.id === values.id ? ticket : item)
      : [...event.ticketTypes, ticket];
    return { ...event, ticketTypes };
  });
}

export async function deleteTicketType(eventId, ticketId) {
  events = events.map((event) => event.id === eventId
    ? { ...event, ticketTypes: event.ticketTypes.filter((ticket) => ticket.id !== ticketId) }
    : event);
}

export async function getOrders(filters = {}) {
  return clone(orders.filter((order) => (
    (!filters.eventId || order.eventId === filters.eventId)
    && (!filters.status || order.status === filters.status)
    && (!filters.from || order.createdAt >= filters.from)
    && (!filters.to || order.createdAt.slice(0, 10) <= filters.to)
  )));
}

export async function getSalesStatistics() {
  const completed = orders.filter((order) => order.status === 'COMPLETED');
  const topEvents = events.map((event) => ({
    eventId: event.id,
    eventName: event.name,
    soldQuantity: event.ticketTypes.reduce((sum, ticket) => sum + ticket.totalQuantity - ticket.availableQuantity, 0),
  })).sort((a, b) => b.soldQuantity - a.soldQuantity);

  return {
    revenue: completed.reduce((sum, order) => sum + order.totalAmount, 0),
    soldQuantity: completed.reduce((sum, order) => sum + order.ticketQuantity, 0),
    topEvents,
    timeline: [
      { label: 'T.04', revenue: 4200000 }, { label: 'T.05', revenue: 6100000 },
      { label: 'T.06', revenue: 5300000 }, { label: 'T.07', revenue: 8700000 },
      { label: 'T.08', revenue: 12400000 }, { label: 'T.09', revenue: 9800000 },
    ],
  };
}

export async function getDashboard() {
  const activeEvents = events.filter((event) => ['UPCOMING', 'ONGOING'].includes(event.status));
  const soldQuantity = events.reduce((sum, event) => sum + event.ticketTypes.reduce(
    (ticketSum, ticket) => ticketSum + ticket.totalQuantity - ticket.availableQuantity, 0
  ), 0);
  const stats = await getSalesStatistics();
  return {
    eventCount: activeEvents.length,
    soldQuantity,
    revenue: stats.revenue,
    upcomingCount: events.filter((event) => event.status === 'UPCOMING' && new Date(event.startTime) > new Date()).length,
    timeline: [
      { label: 'T.04', soldQuantity: 380 }, { label: 'T.05', soldQuantity: 540 },
      { label: 'T.06', soldQuantity: 470 }, { label: 'T.07', soldQuantity: 690 },
      { label: 'T.08', soldQuantity: 920 }, { label: 'T.09', soldQuantity: 810 },
    ],
    recentEvents: clone(events).sort((a, b) => new Date(a.startTime) - new Date(b.startTime)).slice(0, 4),
  };
}