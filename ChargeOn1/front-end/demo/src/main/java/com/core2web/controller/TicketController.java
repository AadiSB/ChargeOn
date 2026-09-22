package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.BookingDao;
import com.core2web.dao.TicketDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Booking;
import com.core2web.model.Ticket;

public class TicketController {

    private final TicketDao ticketDao = new TicketDao();
    private final BookingDao bookingDao = new BookingDao();

    public List<Ticket> getAllTickets() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return ticketDao.getAllTickets(session.getIdToken());
    }

    public int getOpenTicketCount() {
        int count = 0;
        for (Ticket ticket : getAllTickets()) {
            if (ticket.isOpen()) {
                count++;
            }
        }
        return count;
    }

    public int getHighPriorityOpenCount() {
        int count = 0;
        for (Ticket ticket : getAllTickets()) {
            if (ticket.isOpen() && ticket.isHighPriority()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Raises an owner support ticket. If no booking is supplied, the owner's
     * current/most-recent booking is auto-linked (same lookup the dashboard
     * uses for "upcoming booking"); the ticket is still raised even if the
     * owner has none.
     */
    public String submitOwnerIssue(
            Booking booking,
            String subject,
            String priority,
            String description) {

        AuthSession session = AuthSession.getCurrent();
        if (session == null || subject == null || subject.isBlank()) {
            return null;
        }

        Booking linkedBooking = booking != null
                ? booking
                : bookingDao.getUpcomingBooking(session.getUid(), session.getIdToken());

        Ticket ticket = new Ticket();
        ticket.setOwnerId(session.getUid());
        ticket.setCustomerName(
                session.getEmail() == null || session.getEmail().isBlank()
                        ? "Owner"
                        : session.getEmail());
        ticket.setSubject(subject.trim());
        ticket.setDescription(description == null ? "" : description.trim());
        ticket.setPriority("HIGH".equalsIgnoreCase(priority) ? "HIGH" : "MEDIUM");
        ticket.setStatus("OPEN");
        ticket.setBookingId(linkedBooking == null || linkedBooking.getId() == null ? "" : linkedBooking.getId());
        ticket.setBusId(linkedBooking == null || linkedBooking.getBusId() == null ? "" : linkedBooking.getBusId());
        ticket.setCreatedAt(Instant.now().toString());

        String ticketId = ticketDao.createTicket(ticket, session.getIdToken());
        if (ticketId != null) {
            String message = "New owner issue"
                    + (linkedBooking != null ? " for " + linkedBooking.shortRef() : "")
                    + ": " + ticket.getSubject();
            new NotificationController().notifyAdmins(
                    "owner_support_ticket", message, ticketId);
        }
        return ticketId;
    }

    public boolean updateStatus(String ticketId, String newStatus) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return ticketDao.updateStatus(ticketId, newStatus, session.getIdToken());
    }

    public boolean escalate(String ticketId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return ticketDao.updatePriority(ticketId, "HIGH", session.getIdToken());
    }
}
