import { useEffect, useMemo, useState } from 'react';
import { CalendarDays, ChevronRight, Clock3, MapPin, QrCode, Ticket, XCircle, AlertCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import Modal from '../../components/Modal';
import { MOCK_TICKETS, TICKET_TABS, getTicketStatusMeta } from '../../data/mockTickets';

function formatDateTime(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function formatCurrency(amount) {
  return Number(amount || 0).toLocaleString('vi-VN', { maximumFractionDigits: 0 }) + 'đ';
}

function formatTabTickets(tickets, tab) {
  if (tab === 'ALL') return tickets;
  if (tab === 'UPCOMING') {
    return tickets.filter((ticket) => ['PENDING_PAYMENT', 'PAID', 'PAYMENT_FAILED', 'EXPIRED'].includes(ticket.status) || new Date(ticket.startTime) > new Date());
  }
  if (tab === 'USED') return tickets.filter((ticket) => ticket.status === 'USED');
  if (tab === 'CANCELLED') return tickets.filter((ticket) => ['CANCELLED', 'EXPIRED'].includes(ticket.status));
  return tickets;
}

function TicketCardSkeleton() {
  return (
    <div className="animate-pulse rounded-2xl border border-slate-200 bg-white p-3 shadow-sm sm:p-4">
      <div className="flex flex-col gap-4 md:flex-row">
        <div className="h-28 w-full rounded-xl bg-slate-200 md:w-36" />
        <div className="flex-1 space-y-3">
          <div className="h-4 w-24 rounded bg-slate-200" />
          <div className="h-6 w-2/3 rounded bg-slate-200" />
          <div className="h-4 w-1/2 rounded bg-slate-200" />
          <div className="h-4 w-1/3 rounded bg-slate-200" />
        </div>
        <div className="flex gap-2 md:ml-auto md:flex-col md:items-end">
          <div className="h-7 w-24 rounded-full bg-slate-200" />
          <div className="h-9 w-24 rounded-xl bg-slate-200" />
        </div>
      </div>
    </div>
  );
}

export default function MyTicketsPage() {
  const [activeTab, setActiveTab] = useState('ALL');
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchTickets = () => {
    setLoading(true);
    setError('');

    try {
      const resolved = MOCK_TICKETS;
      const isError = false;

      if (isError) {
        throw new Error('failed');
      }

      window.setTimeout(() => {
        setLoading(false);
        if (resolved?.length === 0) {
          setError('');
        }
      }, 500);
    } catch {
      setLoading(false);
      setError('Không thể tải danh sách vé của bạn. Vui lòng thử lại sau.');
    }
  };

  useEffect(() => {
    fetchTickets();
  }, []);

  const filteredTickets = useMemo(() => formatTabTickets(MOCK_TICKETS, activeTab), [activeTab]);

  const activeTicket = selectedTicket ? filteredTickets.find((ticket) => ticket.id === selectedTicket.id) || selectedTicket : selectedTicket;

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
      <header className="mb-6">
        <p className="mb-2 text-xs font-bold uppercase tracking-[0.18em] text-indigo-600">Vé của tôi</p>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">Vé của tôi</h1>
        <p className="mt-2 text-sm text-slate-600 sm:text-[15px]">Quản lý các vé sự kiện bạn đã đặt</p>
      </header>

      <div className="mb-6 flex flex-wrap gap-2 rounded-2xl border border-slate-200 bg-white p-2 shadow-sm shadow-slate-200/50">
        {TICKET_TABS.map((tab) => (
          <button
            key={tab.key}
            type="button"
            onClick={() => setActiveTab(tab.key)}
            className={`rounded-xl px-4 py-2 text-sm font-medium transition ${
              activeTab === tab.key
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="space-y-4">
          {Array.from({ length: 3 }).map((_, idx) => (
            <TicketCardSkeleton key={idx} />
          ))}
        </div>
      ) : error ? (
        <div className="rounded-3xl border border-dashed border-rose-200 bg-rose-50 px-6 py-12 text-center">
          <AlertCircle className="mx-auto mb-4 h-10 w-10 text-rose-500" />
          <p className="text-lg font-semibold text-slate-800">Không thể tải vé của bạn</p>
          <p className="mt-2 text-sm text-slate-600">{error}</p>
          <button
            type="button"
            onClick={fetchTickets}
            className="mt-5 rounded-xl bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-700"
          >
            Thử lại
          </button>
        </div>
      ) : filteredTickets.length === 0 ? (
        <div className="rounded-3xl border border-dashed border-indigo-200 bg-indigo-50 px-8 py-16 text-center">
          <div className="mb-4 flex justify-center text-5xl">🎟️</div>
          <h2 className="text-2xl font-bold text-slate-900">Bạn chưa có vé nào</h2>
          <p className="mt-3 text-slate-600">Khám phá các sự kiện và đặt vé cho trải nghiệm tiếp theo.</p>
          <Link
            to="/events"
            className="mt-6 inline-flex items-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white hover:bg-indigo-700"
          >
            Khám phá sự kiện
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredTickets.map((ticket) => {
            const statusMeta = getTicketStatusMeta(ticket.status);
            const isUpcoming = ['PENDING_PAYMENT', 'PAID', 'PAYMENT_FAILED', 'EXPIRED'].includes(ticket.status) || new Date(ticket.startTime) > new Date();

            return (
              <article key={ticket.id} className="rounded-2xl border border-slate-200 bg-white p-3 shadow-sm shadow-slate-200/40 transition hover:shadow-md sm:p-4">
                <div className="flex flex-col gap-4 md:flex-row md:items-center">
                  <div className="h-28 w-full overflow-hidden rounded-xl bg-slate-200 md:w-36">
                    <img
                      src={ticket.imageUrl}
                      alt={ticket.eventName}
                      className="h-full w-full object-cover"
                      onError={(event) => {
                        event.target.style.display = 'none';
                        event.target.parentElement.classList.add('bg-gradient-to-br', 'from-indigo-100', 'to-violet-200');
                        event.target.parentElement.innerHTML = '<div class="flex h-full w-full items-center justify-center text-3xl">🎟️</div>';
                      }}
                    />
                  </div>

                  <div className="flex-1 min-w-0">
                    <div className="mb-2 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                      <h2 className="truncate text-lg font-bold text-slate-900 sm:text-xl">{ticket.eventName}</h2>
                      <span className={`inline-flex w-fit items-center rounded-full px-2.5 py-1 text-xs font-semibold ${statusMeta.className}`}>
                        {statusMeta.label}
                      </span>
                    </div>

                    <div className="space-y-1 text-sm text-slate-600">
                      <div className="flex items-center gap-2">
                        <CalendarDays className="h-4 w-4 text-indigo-500" />
                        <span>{formatDateTime(ticket.startTime)}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <MapPin className="h-4 w-4 text-indigo-500" />
                        <span>{ticket.location}</span>
                      </div>
                    </div>

                    <div className="mt-3 flex flex-wrap items-center gap-3 text-sm text-slate-600">
                      <span className="font-medium text-slate-700">{ticket.ticketType}</span>
                      <span>·</span>
                      <span>x{ticket.quantity} vé</span>
                      <span>·</span>
                      <span>Mã đơn: {ticket.orderCode}</span>
                    </div>
                  </div>

                  <div className="flex flex-col items-stretch gap-3 md:ml-auto md:min-w-[170px] md:items-end">
                    <div className="text-left md:text-right">
                      <div className="text-2xl font-bold text-indigo-600">{formatCurrency(ticket.totalAmount)}</div>
                    </div>
                    <button
                      type="button"
                      onClick={() => setSelectedTicket(ticket)}
                      className="inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-indigo-700"
                    >
                      Xem vé
                      <ChevronRight className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      )}

      {activeTicket && (
        <Modal isOpen={Boolean(activeTicket)} onClose={() => setSelectedTicket(null)} title="Chi tiết vé">
          <div className="space-y-5">
            <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white">
              <img src={activeTicket.imageUrl} alt={activeTicket.eventName} className="h-48 w-full object-cover" />
            </div>

            <div className="space-y-4">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-xs font-semibold uppercase tracking-[0.12em] text-indigo-600">Sự kiện</p>
                  <h3 className="mt-1 text-2xl font-bold text-slate-900">{activeTicket.eventName}</h3>
                </div>
                <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${getTicketStatusMeta(activeTicket.status).className}`}>
                  {getTicketStatusMeta(activeTicket.status).label}
                </span>
              </div>

              <div className="grid gap-4 rounded-xl bg-slate-50 p-4 text-sm text-slate-600 sm:grid-cols-2">
                <div className="flex items-start gap-2"><CalendarDays className="mt-0.5 h-4 w-4 text-indigo-500" /><span>{formatDateTime(activeTicket.startTime)} - {formatDateTime(activeTicket.endTime)}</span></div>
                <div className="flex items-start gap-2"><MapPin className="mt-0.5 h-4 w-4 text-indigo-500" /><span>{activeTicket.address || activeTicket.location}</span></div>
                <div className="flex items-start gap-2"><Ticket className="mt-0.5 h-4 w-4 text-indigo-500" /><span>Loại vé: {activeTicket.ticketType}</span></div>
                <div className="flex items-start gap-2"><Clock3 className="mt-0.5 h-4 w-4 text-indigo-500" /><span>Số lượng: {activeTicket.quantity}</span></div>
              </div>

              <div className="grid gap-3 rounded-xl border border-slate-200 p-4 sm:grid-cols-2">
                <div>
                  <p className="text-xs uppercase tracking-[0.12em] text-slate-400">Giá</p>
                  <p className="mt-1 text-base font-semibold text-slate-800">{formatCurrency(activeTicket.unitPrice)}</p>
                </div>
                <div>
                  <p className="text-xs uppercase tracking-[0.12em] text-slate-400">Tổng tiền</p>
                  <p className="mt-1 text-base font-semibold text-slate-800">{formatCurrency(activeTicket.totalAmount)}</p>
                </div>
                <div>
                  <p className="text-xs uppercase tracking-[0.12em] text-slate-400">Mã đơn hàng</p>
                  <p className="mt-1 text-base font-semibold text-slate-800">{activeTicket.orderCode}</p>
                </div>
                <div>
                  <p className="text-xs uppercase tracking-[0.12em] text-slate-400">Mã vé</p>
                  <p className="mt-1 text-base font-semibold text-slate-800">{activeTicket.ticketCode}</p>
                </div>
              </div>

              {activeTicket.status !== 'USED' && activeTicket.status !== 'CANCELLED' && activeTicket.status !== 'EXPIRED' && (
                <div className="rounded-2xl border border-dashed border-indigo-200 bg-indigo-50 p-4 text-center">
                  <div className="mb-4 flex justify-center text-indigo-600">
                    <QrCode className="h-14 w-14" />
                  </div>
                  <p className="text-center text-sm font-medium text-slate-700">QR code để check-in</p>
                  <div className="mt-3 flex items-center justify-center rounded-xl border border-dashed border-indigo-200 bg-white p-3">
                    <div className="grid grid-cols-6 gap-1">
                      {Array.from({ length: 36 }).map((_, i) => (
                        <span key={i} className={`h-2.5 w-2.5 rounded-sm ${i % 2 === 0 ? 'bg-slate-900' : 'bg-slate-200'}`} />
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {activeTicket.status === 'USED' && (
                <div className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
                  <span className="font-semibold text-slate-800">Đã sử dụng</span> - Vé này đã hoàn tất check-in và không cần thực hiện thêm thao tác nào.
                </div>
              )}

              {['CANCELLED', 'EXPIRED'].includes(activeTicket.status) && (
                <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
                  <div className="flex items-center gap-2 font-semibold">
                    <XCircle className="h-4 w-4" />
                    {activeTicket.status === 'CANCELLED' ? 'Vé đã bị hủy' : 'Vé đã hết hạn'}
                  </div>
                </div>
              )}
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
