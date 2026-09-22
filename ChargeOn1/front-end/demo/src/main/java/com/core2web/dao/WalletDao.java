package com.core2web.dao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.WalletTransaction;
import com.core2web.util.DateTimeUtil;

public class WalletDao {

    private static final String COLLECTION = "Wallet";


    public List<WalletTransaction> getTransactionsForOwner(String uid, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("ownerId", "EQUAL", uid)),
                idToken
        );
        List<WalletTransaction> list = toDomainList(docs);
        list.sort((a, b) -> compareTimestamps(b.getCreatedAt(), a.getCreatedAt()));
        return list;
    }


    public WalletTransaction getTransactionForBooking(String ownerId, String bookingId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(
                        new FirestoreHelper.Filter("ownerId",   "EQUAL", ownerId),
                        new FirestoreHelper.Filter("bookingId", "EQUAL", bookingId)
                ),
                idToken
        );
        return docs.isEmpty() ? null : fromDocument(docs.get(0));
    }


    public double getSpentThisMonth(String uid, String idToken) {
        ZonedDateTime now = ZonedDateTime.now(DateTimeUtil.INDIA);
        ZonedDateTime startOfMonth = now.withDayOfMonth(1)
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                                        .withNano(0);
        Instant startInstant = startOfMonth.toInstant();

        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(
                        new FirestoreHelper.Filter("ownerId", "EQUAL", uid),
                        new FirestoreHelper.Filter("type",    "EQUAL", "charge_payment")
                ),
                idToken
        );

        double total = 0;
        for (JSONObject doc : docs) {
            if (isOnOrAfter(doc, startInstant)) {
                total += FirestoreHelper.getNumber(doc, "amount");
            }
        }
        return total;
    }


    public List<WalletTransaction> getTransactionsThisMonth(String uid, String idToken) {
        ZonedDateTime now = ZonedDateTime.now(DateTimeUtil.INDIA);
        ZonedDateTime startOfMonth = now.withDayOfMonth(1)
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                                        .withNano(0);
        Instant startInstant = startOfMonth.toInstant();

        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("ownerId", "EQUAL", uid)),
                idToken
        );
        docs.removeIf(doc -> !isOnOrAfter(doc, startInstant));
        List<WalletTransaction> list = toDomainList(docs);
        list.sort((a, b) -> compareTimestamps(b.getCreatedAt(), a.getCreatedAt()));
        return list;
    }


    public double getRevenueToday(String idToken) {
        ZonedDateTime now = ZonedDateTime.now(DateTimeUtil.INDIA);
        ZonedDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        Instant startInstant = startOfDay.toInstant();

        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("type", "EQUAL", "charge_payment")),
                idToken
        );

        double total = 0;
        for (JSONObject doc : docs) {
            if (isOnOrAfter(doc, startInstant)) {
                total += FirestoreHelper.getNumber(doc, "amount");
            }
        }
        return total;
    }


    public double getRevenueThisMonth(String idToken) {
        ZonedDateTime now = ZonedDateTime.now(DateTimeUtil.INDIA);
        ZonedDateTime startOfMonth = now.withDayOfMonth(1)
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                                        .withNano(0);
        Instant startInstant = startOfMonth.toInstant();

        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("type", "EQUAL", "charge_payment")),
                idToken
        );

        double total = 0;
        for (JSONObject doc : docs) {
            if (isOnOrAfter(doc, startInstant)) {
                total += FirestoreHelper.getNumber(doc, "amount");
            }
        }
        return total;
    }


    public Map<String, Double> getRevenueLast7Days(String idToken) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        LocalDate today = now.toLocalDate();
        LocalDate startDate = today.minusDays(6);

        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("type", "EQUAL", "charge_payment")),
                idToken
        );

        Map<String, Double> byDay = new LinkedHashMap<>();
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            byDay.put(d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), 0.0);
        }

        for (JSONObject doc : docs) {
            try {
                String createdAt = FirestoreHelper.getString(doc, "createdAt");
                Instant timestamp = DateTimeUtil.parse(createdAt);
                if (timestamp == null) continue;
                LocalDate day = timestamp.atZone(DateTimeUtil.INDIA).toLocalDate();
                if (day.isBefore(startDate) || day.isAfter(today)) {
                    continue;
                }
                String key = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                byDay.put(key, byDay.get(key) + FirestoreHelper.getNumber(doc, "amount"));
            } catch (Exception ignored) {
            }
        }
        return byDay;
    }
public double getAverageSessionValue(String idToken) {

    List<JSONObject> docs = FirestoreHelper.queryWithFilters(
            COLLECTION,
            Arrays.asList(
                    new FirestoreHelper.Filter(
                            "type",
                            "EQUAL",
                            "charge_payment"
                    )
            ),
            idToken
    );

    double totalRevenue = 0;
    java.util.Set<String> bookingIds =
            new java.util.HashSet<>();

    for (JSONObject doc : docs) {

        totalRevenue +=
                FirestoreHelper.getNumber(
                        doc,
                        "amount"
                );

        String bookingId =
                FirestoreHelper.getString(
                        doc,
                        "bookingId"
                );

        if (bookingId != null &&
                !bookingId.isBlank()) {

            bookingIds.add(bookingId);
        }
    }

    if (bookingIds.isEmpty()) {
        return 0;
    }

    return totalRevenue / bookingIds.size();
}

    public String addTransaction(WalletTransaction txn, String idToken) {
        Map<String, Object> fields = Map.of(
                "ownerId", txn.getOwnerId(),
                "type", txn.getType(),
                "amount", txn.getAmount(),
                "method", txn.getMethod(),
                "createdAt", txn.getCreatedAt(),
                "bookingId", txn.getBookingId()
        );
        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }


    private boolean isOnOrAfter(JSONObject doc, Instant threshold) {
        return FirestoreHelper.isFieldOnOrAfter(doc, "createdAt", threshold);
    }

    private static int compareTimestamps(String left, String right) {
        Instant a = DateTimeUtil.parse(left);
        Instant b = DateTimeUtil.parse(right);
        if (a == null) return b == null ? 0 : -1;
        return b == null ? 1 : a.compareTo(b);
    }

    private List<WalletTransaction> toDomainList(List<JSONObject> docs) {
        List<WalletTransaction> list = new ArrayList<>();
        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private WalletTransaction fromDocument(JSONObject doc) {
        WalletTransaction t = new WalletTransaction();
        t.setId(FirestoreHelper.getDocumentId(doc));
        t.setOwnerId(FirestoreHelper.getString(doc, "ownerId"));
        t.setType(FirestoreHelper.getString(doc, "type"));
        t.setAmount(FirestoreHelper.getNumber(doc, "amount"));
        t.setMethod(FirestoreHelper.getString(doc, "method"));
        t.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        t.setBookingId(FirestoreHelper.getString(doc, "bookingId"));
        return t;
    }
}
