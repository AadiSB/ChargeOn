package com.core2web.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Ticket;

public class TicketDao {

    private static final String COLLECTION = "tickets";

    public List<Ticket> getAllTickets(String idToken) {
        List<JSONObject> docs = FirestoreHelper.listCollection(COLLECTION, idToken);
        return toDomainList(docs);
    }

    public String createTicket(Ticket ticket, String idToken) {
        if (ticket == null) {
            return null;
        }

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("ownerId", ticket.getOwnerId());
        fields.put("customerName", ticket.getCustomerName());
        fields.put("subject", ticket.getSubject());
        fields.put("description", ticket.getDescription());
        fields.put("priority", ticket.getPriority());
        fields.put("status", ticket.getStatus());
        fields.put("bookingId", ticket.getBookingId());
        fields.put("busId", ticket.getBusId());
        fields.put("createdAt", ticket.getCreatedAt());

        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }

    public boolean updateStatus(String ticketId, String newStatus, String idToken) {
        return FirestoreHelper.updateFields(
                COLLECTION, ticketId,
                Map.of("status", newStatus),
                idToken
        );
    }

    public boolean updatePriority(String ticketId, String newPriority, String idToken) {
        return FirestoreHelper.updateFields(
                COLLECTION, ticketId,
                Map.of("priority", newPriority),
                idToken
        );
    }

    private List<Ticket> toDomainList(List<JSONObject> docs) {
        List<Ticket> list = new ArrayList<>();
        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Ticket fromDocument(JSONObject doc) {
        Ticket ticket = new Ticket();
        ticket.setId(FirestoreHelper.getDocumentId(doc));
        ticket.setOwnerId(FirestoreHelper.getString(doc, "ownerId"));
        ticket.setCustomerName(FirestoreHelper.getString(doc, "customerName"));
        ticket.setSubject(FirestoreHelper.getString(doc, "subject"));
        ticket.setDescription(FirestoreHelper.getString(doc, "description"));
        ticket.setPriority(FirestoreHelper.getString(doc, "priority"));
        ticket.setStatus(FirestoreHelper.getString(doc, "status"));
        ticket.setBookingId(FirestoreHelper.getString(doc, "bookingId"));
        ticket.setBusId(FirestoreHelper.getString(doc, "busId"));
        ticket.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        return ticket;
    }
}
