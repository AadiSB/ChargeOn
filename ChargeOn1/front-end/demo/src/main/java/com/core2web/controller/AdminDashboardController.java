package com.core2web.controller;

import java.util.List;

import com.core2web.dao.BookingDao;
import com.core2web.dao.WalletDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Booking;
import com.core2web.model.Bus;

public class AdminDashboardController {


    public static class DashboardData {

        public final int bookingsToday;
        public final int completedToday;
        public final int activeToday;
        public final double revenueToday;
        public final BusController.FleetStatusCounts fleetStatus;
        public final List<Bus> lowBatteryBuses;
        public final int openTicketCount;
        public final int highPriorityOpenCount;

        public DashboardData(
                int bookingsToday, int completedToday, int activeToday,
                double revenueToday,
                BusController.FleetStatusCounts fleetStatus,
                List<Bus> lowBatteryBuses,
                int openTicketCount, int highPriorityOpenCount) {
            this.bookingsToday = bookingsToday;
            this.completedToday = completedToday;
            this.activeToday = activeToday;
            this.revenueToday = revenueToday;
            this.fleetStatus = fleetStatus;
            this.lowBatteryBuses = lowBatteryBuses;
            this.openTicketCount = openTicketCount;
            this.highPriorityOpenCount = highPriorityOpenCount;
        }

        public String revenueTodayFormatted() {
            return formatRupees(revenueToday);
        }

        public String bookingsBreakdown() {
            StringBuilder sb = new StringBuilder();
            if (completedToday > 0) sb.append(completedToday).append(" completed");
            if (activeToday > 0) {
                if (sb.length() > 0) sb.append(" · ");
                sb.append(activeToday).append(" active");
            }
            return sb.length() > 0 ? sb.toString() : "No bookings yet today";
        }

        public String fleetSubLabel() {
            return fleetStatus.charging + " charging · " + fleetStatus.fault + " offline";
        }

        public String busesOnlineLabel() {
            return fleetStatus.online() + " / " + fleetStatus.total;
        }

        public String ticketsSubLabel() {
            return highPriorityOpenCount + " high priority";
        }
    }


    private final BookingDao bookingDao = new BookingDao();
    private final WalletDao walletDao = new WalletDao();
    private final BusController busController = new BusController();
    private final TicketController ticketController = new TicketController();


    public DashboardData load() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return emptyData();
        }

        String idToken = session.getIdToken();

        List<Booking> todayBookings = bookingDao.getBookingsToday(idToken);
        int completed = 0, active = 0;
        for (Booking b : todayBookings) {
            if (b.isCompleted()) {
                completed++;
            } else if (b.isInProgress()) {
                active++;
            }
        }

        double revenue = walletDao.getRevenueToday(idToken);

        BusController.FleetStatusCounts fleetStatus = busController.getFleetStatusCounts();
        List<Bus> lowBattery = busController.getLowBatteryBuses();

        int openTickets = ticketController.getOpenTicketCount();
        int highPriorityOpen = ticketController.getHighPriorityOpenCount();

        return new DashboardData(
                todayBookings.size(), completed, active,
                revenue,
                fleetStatus, lowBattery,
                openTickets, highPriorityOpen
        );
    }


    private static String formatRupees(double amount) {
        long rounded = Math.round(amount);
        if (rounded < 1000) {
            return "₹" + rounded;
        }

        String digits = String.valueOf(rounded);
        String lastThree = digits.substring(digits.length() - 3);
        String remaining = digits.substring(0, digits.length() - 3);

        StringBuilder sb = new StringBuilder();
        int i = remaining.length();
        while (i > 0) {
            int start = Math.max(0, i - 2);
            String group = remaining.substring(start, i);
            if (sb.length() > 0) {
                sb.insert(0, group + ",");
            } else {
                sb.insert(0, group);
            }
            i = start;
        }

        return "₹" + sb + "," + lastThree;
    }


    private DashboardData emptyData() {
        return new DashboardData(
                0, 0, 0, 0.0,
                new BusController.FleetStatusCounts(0, 0, 0, 0, 0),
                new java.util.ArrayList<>(),
                0, 0
        );
    }
}
