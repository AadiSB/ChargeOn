package com.core2web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.core2web.dao.BookingDao;
import com.core2web.dao.ChargingSessionDao;
import com.core2web.dao.OwnerDao;
import com.core2web.dao.WalletDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Booking;
import com.core2web.model.ChargingSession;
import com.core2web.model.Owner;
import com.core2web.model.WalletTransaction;
import com.core2web.util.DateTimeUtil;

public class OwnerDashboardController {


    public static class DashboardData {

        public final Booking upcomingBooking;
        /** Resolved busCode for {@link #upcomingBooking}, or "" — never a raw doc ID. */
        public final String  upcomingBusCode;
        public final int     bookingsThisMonth;
        public final int     completedCount;
        public final int     scheduledCount;
        public final int     activeCount;
        public final double  spentThisMonth;
        public final ChargingSession activeSession;
        public final List<ActivityItem> recentActivity;

        public DashboardData(
                Booking upcomingBooking,
                String upcomingBusCode,
                int bookingsThisMonth,
                int completedCount,
                int scheduledCount,
                int activeCount,
                double spentThisMonth,
                ChargingSession activeSession,
                List<ActivityItem> recentActivity) {
            this.upcomingBooking   = upcomingBooking;
            this.upcomingBusCode   = upcomingBusCode == null ? "" : upcomingBusCode;
            this.bookingsThisMonth = bookingsThisMonth;
            this.completedCount    = completedCount;
            this.scheduledCount    = scheduledCount;
            this.activeCount       = activeCount;
            this.spentThisMonth    = spentThisMonth;
            this.activeSession     = activeSession;
            this.recentActivity    = recentActivity;
        }

        public String spentThisMonthFormatted() {
            if (spentThisMonth <= 0) return "₹0";
            int rounded = (int) spentThisMonth;
            if (rounded >= 1000) {
                int thousands = rounded / 1000;
                int hundreds  = rounded % 1000;
                return String.format("₹%d,%03d", thousands, hundreds);
            }
            return "₹" + rounded;
        }

        public String bookingBreakdown() {
            List<String> parts = new ArrayList<>();
            if (completedCount > 0) parts.add(completedCount + " completed");
            if (scheduledCount > 0) parts.add(scheduledCount + " scheduled");
            if (activeCount    > 0) parts.add(activeCount    + " active");
            return parts.isEmpty() ? "No bookings this month" : String.join(" · ", parts);
        }
    }


    public static class ActivityItem {

        public final String dotColor;
        public final String text;
        public final String time;
        public final String createdAt;

        public ActivityItem(String dotColor, String text,
                            String time, String createdAt) {
            this.dotColor  = dotColor;
            this.text      = text;
            this.time      = time;
            this.createdAt = createdAt;
        }
    }


    private final BookingDao bookingDao  = new BookingDao();
    private final ChargingSessionDao sessionDao = new ChargingSessionDao();
    private final WalletDao walletDao = new WalletDao();
    private final OwnerDao ownerDao = new OwnerDao();
    private final BusController busController = new BusController();


    public DashboardData load() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return emptyData();
        }

        String uid     = session.getUid();
        String idToken = session.getIdToken();

        Booking upcoming = bookingDao.getUpcomingBooking(uid, idToken);

        List<Booking> monthBookings = bookingDao.getBookingsThisMonth(uid, idToken);
        int completed = 0, scheduled = 0, active = 0;
        for (Booking b : monthBookings) {
            if (b.isCompleted()) {
                completed++;
            } else if ("QUEUED".equals(b.getStatus())) {
                scheduled++;
            } else if (b.isInProgress()) {
                active++;
            }
        }

        double spent = walletDao.getSpentThisMonth(uid, idToken);

        ChargingSession activeSession = sessionDao.getActiveSession(uid, idToken);

        // One fleet read; reused for the upcoming card and the activity feed so no
        // user-facing string ever contains a raw bus document ID.
        Map<String, String> busCodes = busController.getBusCodeLookup();

        List<ActivityItem> activity = buildRecentActivity(
                uid, idToken, monthBookings, activeSession, busCodes
        );

        return new DashboardData(
                upcoming,
                upcoming == null
                        ? ""
                        : BusController.busCodeLabel(upcoming.getBusId(), busCodes, ""),
                monthBookings.size(),
                completed, scheduled, active,
                spent,
                activeSession,
                activity
        );
    }

    public Owner getOwnerProfile() {
        if (AuthSession.getCurrent() == null) {
            return null;
        }
        return ownerDao.getCurrentOwner();
    }


    private List<ActivityItem> buildRecentActivity(
            String uid, String idToken,
            List<Booking> monthBookings,
            ChargingSession activeSession,
            Map<String, String> busCodes) {

        List<ActivityItem> items = new ArrayList<>();

        if (activeSession != null) {
            String bookingRef = activeSession.getBookingId().isEmpty()
                    ? "" : " · " + activeSession.getBookingId();
            items.add(new ActivityItem(
                    "#10b981",
                    "Session started" + bookingRef,
                    formatTimestamp(activeSession.getStartedAt()),
                    activeSession.getStartedAt()
            ));
        }

        for (Booking b : monthBookings) {
            if ("QUEUED".equals(b.getStatus())) {
                String busCode = BusController.busCodeLabel(b.getBusId(), busCodes, "");
                String busRef = busCode.isEmpty() ? "" : " · Bus " + busCode;
                String loc    = b.getLocation().isEmpty() ? "" : " · " + b.getLocation();
                items.add(new ActivityItem(
                        "#3B82F6",
                        "Booking confirmed for " + b.getScheduledTime() + loc + busRef,
                        formatTimestamp(b.getCreatedAt()),
                        b.getCreatedAt()
                ));
            }
        }

        List<WalletTransaction> txns = walletDao.getTransactionsThisMonth(uid, idToken);
        for (WalletTransaction t : txns) {
            items.add(new ActivityItem(
                    t.activityDotColor(),
                    t.activityLabel(),
                    formatTimestamp(t.getCreatedAt()),
                    t.getCreatedAt()
            ));
        }

        items.sort((a, b) -> {
            java.time.Instant right = DateTimeUtil.parse(b.createdAt);
            java.time.Instant left = DateTimeUtil.parse(a.createdAt);
            if (right == null) return left == null ? 0 : -1;
            return left == null ? 1 : right.compareTo(left);
        });

        return items.size() > 5 ? items.subList(0, 5) : items;
    }


    private String formatTimestamp(String iso) {
        if (iso == null || iso.isEmpty()) return "—";
        try {
            java.time.Instant instant = DateTimeUtil.parse(iso);
            if (instant == null) return "N/A";
            java.time.ZonedDateTime dt = instant.atZone(DateTimeUtil.INDIA);
            java.time.LocalDate today = java.time.LocalDate.now(DateTimeUtil.INDIA);
            java.time.LocalDate dateOnly = dt.toLocalDate();

            String timePart = String.format("%02d:%02d %s",
                    dt.getHour() % 12 == 0 ? 12 : dt.getHour() % 12,
                    dt.getMinute(),
                    dt.getHour() < 12 ? "AM" : "PM");

            if (dateOnly.equals(today)) {
                return "Today, " + timePart;
            } else if (dateOnly.equals(today.minusDays(1))) {
                return "Yesterday, " + timePart;
            } else {
                String month = dt.getMonth().getDisplayName(
                        java.time.format.TextStyle.SHORT,
                        java.util.Locale.ENGLISH);
                return dt.getDayOfMonth() + " " + month + ", " + timePart;
            }
        } catch (Exception e) {
            return iso;
        }
    }


    private DashboardData emptyData() {
        return new DashboardData(null, "", 0, 0, 0, 0, 0.0, null, new ArrayList<>());
    }
}
