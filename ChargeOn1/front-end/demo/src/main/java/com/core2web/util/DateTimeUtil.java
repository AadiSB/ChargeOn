package com.core2web.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class DateTimeUtil {
    public static final ZoneId INDIA = ZoneId.of("Asia/Kolkata");
    public static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    public static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter TIME_ONLY = DateTimeFormatter.ofPattern("hh:mm a");

    private static final List<DateTimeFormatter> LEGACY = List.of(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private DateTimeUtil() { }

    public static Instant parse(String value) {
        if (value == null || value.isBlank()) return null;
        String text = value.trim();
        try { return Instant.parse(text); } catch (DateTimeParseException ignored) { }
        for (DateTimeFormatter formatter : LEGACY) {
            try { return LocalDateTime.parse(text, formatter).atZone(INDIA).toInstant(); }
            catch (DateTimeParseException ignored) { }
        }
        for (DateTimeFormatter formatter : List.of(DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                                                     DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                                                     DateTimeFormatter.ISO_LOCAL_DATE)) {
            try { return LocalDate.parse(text, formatter).atStartOfDay(INDIA).toInstant(); }
            catch (DateTimeParseException ignored) { }
        }
        return null;
    }

    public static String canonical(Instant instant) { return instant == null ? "" : instant.toString(); }
    public static String display(String value) {
        Instant instant = parse(value);
        return instant == null ? "N/A" : DISPLAY.format(instant.atZone(INDIA));
    }
    public static String date(String value) {
        Instant instant = parse(value);
        return instant == null ? "N/A" : DATE_ONLY.format(instant.atZone(INDIA));
    }
    public static String time(String value) {
        Instant instant = parse(value);
        return instant == null ? "N/A" : TIME_ONLY.format(instant.atZone(INDIA));
    }
}
