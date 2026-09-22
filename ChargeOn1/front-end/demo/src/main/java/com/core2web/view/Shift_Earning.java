// package com.core2web.view;

// import java.time.Instant;
// import java.time.LocalDate;
// import java.time.ZoneId;
// import java.time.format.TextStyle;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Locale;
// import java.util.Map;

// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.canvas.Canvas;
// import javafx.scene.canvas.GraphicsContext;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;

// import com.core2web.controller.BookingController;
// import com.core2web.controller.BusController;
// import com.core2web.controller.DriverEarningController;
// import com.core2web.controller.ShiftController;
// import com.core2web.controller.WalletController;
// import com.core2web.model.DriverEarning;
// import com.core2web.model.Pricing;
// import com.core2web.model.Shift;
// import com.core2web.model.WalletTransaction;
// import com.core2web.util.DateTimeUtil;

// public class Shift_Earning {

//     private static final BookingController bookingController = new BookingController();
//     private static final BusController busController = new BusController();
//     private static final DriverEarningController driverEarningController =
//             new DriverEarningController();
//     private static final ShiftController shiftController = new ShiftController();
//     private static final WalletController walletController = new WalletController();

//     Scene getShiftScene() {
//         DriverDashboard.goTo("Shift & Earnings");
//         return DriverDashboard.scene;
//     }

//     static ScrollPane buildMainContent() {
//         return new Shift_Earning().buildContent();
//     }

//     private ScrollPane buildContent() {
//         VBox content = new VBox(16);
//         content.setPadding(new Insets(16));

//         Shift activeShift = shiftController.getMyActiveShift();
//         List<Shift> history = shiftController.getMyShiftHistory();
//         List<com.core2web.model.Booking> bookings = bookingController.getBookingsForCurrentDriver();

//         List<com.core2web.model.Booking> todaysCompleted = new ArrayList<>();
//         double kwhToday = 0;
//         for (com.core2web.model.Booking b : bookings) {
//             if ("COMPLETED".equals(b.getStatus()) && isToday(b.getCreatedAt())) {
//                 todaysCompleted.add(b);
//                 kwhToday += b.getKwh();
//             }
//         }

//         double billedToday = 0;
//         for (com.core2web.model.Booking b : todaysCompleted) {
//             WalletTransaction txn = walletController.getTransactionForBooking(b.getOwnerId(), b.getId());
//             if (txn != null) {
//                 billedToday += txn.getAmount();
//             }
//         }

//         List<LocalDate> days = new ArrayList<>();
//         LocalDate today = LocalDate.now(DateTimeUtil.INDIA);
//         for (int i = 6; i >= 0; i--) {
//             days.add(today.minusDays(i));
//         }
//         double[] kwhByDay = new double[7];
//         for (com.core2web.model.Booking b : bookings) {
//             if (!"COMPLETED".equals(b.getStatus())) {
//                 continue;
//             }
//             try {
//                 Instant timestamp = DateTimeUtil.parse(b.getCreatedAt());
//                 if (timestamp == null) continue;
//                 LocalDate d = timestamp.atZone(DateTimeUtil.INDIA).toLocalDate();
//                 int idx = days.indexOf(d);
//                 if (idx >= 0) {
//                     kwhByDay[idx] += b.getKwh();
//                 }
//             } catch (Exception ignored) {
//             }
//         }

//         int sessionCount = todaysCompleted.size();

//         VBox clockCardHolder = new VBox();

//         VBox clockedInCard = statCard("Clocked In");
//         HBox.setHgrow(clockedInCard, Priority.ALWAYS);

//         VBox sessionsCard = statCard("Sessions today");
//         HBox.setHgrow(sessionsCard, Priority.ALWAYS);

//         VBox billedCard = statCard("Billed today");
//         HBox.setHgrow(billedCard, Priority.ALWAYS);

//         // Real payouts now, from the DriverEarning ledger written on completion.
//         VBox payoutCard = statCard("My earnings today");
//         double earnedToday = 0;
//         int paidSessions = 0;
//         for (DriverEarning earning : driverEarningController.getMyEarningsToday()) {
//             earnedToday += earning.getAmount();
//             paidSessions++;
//         }
//         Label payAmt = new Label("₹" + (int) earnedToday);
//         payAmt.setStyle("-fx-text-fill:#10b981;-fx-font-size:28px;-fx-font-weight:bold;");
//         Label paySub = label(paidSessions == 0
//                 ? "No completed sessions today"
//                 : paidSessions + (paidSessions == 1 ? " session" : " sessions")
//                         + " · " + (int) (Pricing.DRIVER_SHARE * 100) + "% of fare",
//                 "card-sub");
//         payoutCard.getChildren().addAll(payAmt, paySub);
//         HBox.setHgrow(payoutCard, Priority.ALWAYS);

//         HBox statsRow = new HBox(12, clockedInCard, sessionsCard, billedCard, payoutCard);

//         VBox historyRows = new VBox(0);
//         VBox scheduleCard = buildShiftHistoryCard(historyRows);
//         HBox.setHgrow(scheduleCard, Priority.ALWAYS);

//         Canvas canvas = new Canvas(280, 120);
//         HBox dayLabels = new HBox();
//         VBox earningsCard = buildEarningsChartCard(canvas, dayLabels);

//         VBox rightCol = new VBox(16);
//         rightCol.setPrefWidth(320);
//         rightCol.getChildren().addAll(clockCardHolder, earningsCard);

//         HBox bottom = new HBox(16, scheduleCard, rightCol);

//         // Only added when a real pending emergency exists — no placeholder banner.
//         HBox emergencyBanner = buildEmergencyBanner();
//         if (emergencyBanner != null) {
//             content.getChildren().add(emergencyBanner);
//         }

//         content.getChildren().addAll(statsRow, bottom);

//         populateClockCard(clockCardHolder, clockedInCard, activeShift);
//         populateSessionsCard(sessionsCard, sessionCount, kwhToday);
//         populateBilledCard(billedCard, billedToday, sessionCount);
//         populateHistory(historyRows, history);
//         populateChart(canvas, dayLabels, days, kwhByDay);

//         ScrollPane sp = new ScrollPane(content);
//         sp.setFitToWidth(true);
//         sp.getStyleClass().add("scroll-pane");
//         sp.setStyle("-fx-background-color: transparent;");
//         return sp;
//     }

//     private boolean isToday(String createdAt) {
//         try {
//             Instant timestamp = DateTimeUtil.parse(createdAt);
//             return timestamp != null && timestamp.atZone(DateTimeUtil.INDIA).toLocalDate()
//                     .equals(LocalDate.now(DateTimeUtil.INDIA));
//         } catch (Exception e) {
//             return false;
//         }
//     }

//     private String formatTime(String createdAt) {
//         try {
//             return DateTimeUtil.time(createdAt);
//         } catch (Exception e) {
//             return "—";
//         }
//     }

//     private String dayOfWeek(String createdAt) {
//         try {
//             Instant timestamp = DateTimeUtil.parse(createdAt);
//             if (timestamp == null) return "N/A";
//             return timestamp.atZone(DateTimeUtil.INDIA).getDayOfWeek()
//                     .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
//         } catch (Exception e) {
//             return "—";
//         }
//     }

//     private String formatElapsed(java.time.Duration d) {
//         long totalMinutes = Math.max(0, d.toMinutes());
//         long hours = totalMinutes / 60;
//         long minutes = totalMinutes % 60;
//         return String.format("%02dh %02dm", hours, minutes);
//     }

//     private void populateClockCard(VBox clockCardHolder, VBox clockedInCard, Shift activeShift) {
//         clockCardHolder.getChildren().setAll(buildClockWidget(clockCardHolder, clockedInCard, activeShift));

//         clockedInCard.getChildren().clear();
//         if (activeShift != null) {
//             Label clockTime = new Label(formatTime(activeShift.getClockInAt()));
//             clockTime.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");
//             Label elapsed = label(formatElapsed(activeShift.elapsed()) + " elapsed", "card-sub");
//             clockedInCard.getChildren().addAll(clockTime, elapsed);
//         } else {
//             Label offLabel = new Label("Off duty");
//             offLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:20px;-fx-font-weight:bold;");
//             clockedInCard.getChildren().add(offLabel);
//         }
//     }

//     private void populateSessionsCard(VBox sessionsCard, int count, double kwh) {
//         sessionsCard.getChildren().clear();
//         Label sessNum = new Label(String.valueOf(count));
//         sessNum.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");
//         Label sessDetail = label(((int) kwh) + " kWh dispensed", "card-sub");
//         sessionsCard.getChildren().addAll(sessNum, sessDetail);
//     }

//     private void populateBilledCard(VBox billedCard, double billed, int count) {
//         billedCard.getChildren().clear();
//         Label billAmt = new Label("₹" + Math.round(billed));
//         billAmt.setStyle("-fx-text-fill:#10b981;-fx-font-size:28px;-fx-font-weight:bold;");
//         String avgLine = count > 0 ? "Avg ₹" + Math.round(billed / count) + " / session" : "No sessions today";
//         Label billSub = label(avgLine, "card-sub");
//         billedCard.getChildren().addAll(billAmt, billSub);
//     }

//     private void populateHistory(VBox historyRows, List<Shift> history) {
//         historyRows.getChildren().clear();
//         if (history.isEmpty()) {
//             historyRows.getChildren().add(loadingLabel("No shifts recorded yet."));
//             return;
//         }
//         // One fleet read for the whole list, so rows show "BUS05" not a doc ID.
//         Map<String, String> busCodes = busController.getBusCodeLookup();
//         int limit = Math.min(5, history.size());
//         for (int i = 0; i < limit; i++) {
//             historyRows.getChildren().add(buildShiftRow(history.get(i), busCodes));
//         }
//     }

//     private void populateChart(Canvas canvas, HBox dayLabels, List<LocalDate> days, double[] values) {
//         drawBarChart(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight(), values);
//         dayLabels.getChildren().clear();
//         for (LocalDate d : days) {
//             Label dl = new Label(d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
//             dl.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
//             dl.setPrefWidth(canvas.getWidth() / days.size());
//             dl.setAlignment(Pos.CENTER);
//             dayLabels.getChildren().add(dl);
//         }
//     }

//     /**
//      * The emergency diversion banner, or null when this driver has no pending
//      * emergency — callers must skip adding it entirely in that case.
//      */
//     private HBox buildEmergencyBanner() {
//         com.core2web.model.Booking pending =
//                 bookingController.getPendingEmergencyBookingForCurrentDriver();

//         if (pending == null) {
//             return null;
//         }

//         return EmergencyBanner.build(pending, () -> DriverDashboard.goTo("Shift & Earnings"));
//     }

//     private VBox buildShiftHistoryCard(VBox historyRows) {
//         VBox section = new VBox(0);
//         section.getStyleClass().add("schedule-card");
//         section.setPadding(new Insets(18));

//         HBox header = new HBox(10);
//         header.setAlignment(Pos.CENTER_LEFT);
//         header.setPadding(new Insets(0, 0, 14, 0));
//         Label schedTitle = new Label("Recent shifts");
//         schedTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
//         header.getChildren().add(schedTitle);

//         HBox tableHeader = buildTableHeaderRow();
//         tableHeader.setPadding(new Insets(8, 0, 8, 0));
//         tableHeader
//                 .setStyle("-fx-border-color: transparent transparent #1e293b transparent; -fx-border-width: 0 0 1 0;");

//         section.getChildren().addAll(header, tableHeader, historyRows);
//         return section;
//     }

//     private HBox buildTableHeaderRow() {
//         HBox row = new HBox();
//         row.setAlignment(Pos.CENTER_LEFT);

//         String headerStyle = "-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;";

//         Label dayLbl = new Label("DAY");
//         dayLbl.setPrefWidth(60);
//         dayLbl.setStyle(headerStyle);

//         Label inLbl = new Label("CLOCK IN");
//         inLbl.setPrefWidth(110);
//         inLbl.setStyle(headerStyle);

//         Label outLbl = new Label("CLOCK OUT");
//         outLbl.setPrefWidth(110);
//         outLbl.setStyle(headerStyle);

//         Label busLbl = new Label("BUS / DEPOT");
//         busLbl.setPrefWidth(160);
//         busLbl.setStyle(headerStyle);

//         Label statusLbl = new Label("STATUS");
//         statusLbl.setPrefWidth(90);
//         statusLbl.setStyle(headerStyle);

//         row.getChildren().addAll(dayLbl, inLbl, outLbl, busLbl, statusLbl);
//         return row;
//     }

//     private HBox buildShiftRow(Shift shift, Map<String, String> busCodes) {
//         HBox row = new HBox();
//         row.setAlignment(Pos.CENTER_LEFT);
//         row.setPadding(new Insets(12, 0, 12, 0));
//         row.setStyle("-fx-border-color: transparent transparent #1e293b transparent; -fx-border-width: 0 0 1 0;");

//         Label dayLbl = new Label(dayOfWeek(shift.getClockInAt()));
//         dayLbl.setPrefWidth(60);
//         dayLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");

//         Label inLbl = new Label(formatTime(shift.getClockInAt()));
//         inLbl.setPrefWidth(110);
//         inLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

//         Label outLbl = new Label(shift.isActive() ? "In progress" : formatTime(shift.getClockOutAt()));
//         outLbl.setPrefWidth(110);
//         outLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

//         String bus = BusController.busCodeLabel(shift.getBusId(), busCodes, "— unassigned");
//         Label busLbl = new Label(bus + " · " + shift.getDepot());
//         busLbl.setPrefWidth(160);
//         busLbl.setWrapText(true);
//         busLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

//         Label statusLbl = new Label(shift.isActive() ? "ACTIVE" : "DONE");
//         statusLbl.setPrefWidth(90);
//         statusLbl.setStyle(shift.isActive()
//                 ? "-fx-text-fill:#10b981;-fx-font-size:11px;-fx-font-weight:bold;"
//                 : "-fx-text-fill:#64748b;-fx-font-size:11px;");

//         row.getChildren().addAll(dayLbl, inLbl, outLbl, busLbl, statusLbl);
//         return row;
//     }

//     private VBox buildClockWidget(VBox clockCardHolder, VBox clockedInCard, Shift activeShift) {
//         VBox card = new VBox(10);
//         card.getStyleClass().add("clock-card");
//         card.setPadding(new Insets(18));

//         Label clockTitle = new Label("Clock");
//         clockTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");

//         VBox clockDisplay = new VBox(6);
//         clockDisplay.setAlignment(Pos.CENTER);
//         clockDisplay.setPadding(new Insets(18));
//         clockDisplay.setStyle("-fx-background-color:#0f1720;-fx-background-radius:12;");

//         if (activeShift != null) {
//             Label onDutyLabel = new Label("ON DUTY");
//             onDutyLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");

//             Label clockTime = new Label(formatElapsed(activeShift.elapsed()));
//             clockTime.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:36px;-fx-font-weight:bold;");

//             Label sinceLabel = new Label(
//                     "since " + formatTime(activeShift.getClockInAt()) + " · depot " + activeShift.getDepot());
//             sinceLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");

//             Button clockOutBtn = new Button("Clock out");
//             clockOutBtn.getStyleClass().add("clock-out-btn");
//             clockOutBtn.setMaxWidth(Double.MAX_VALUE);
//             VBox.setMargin(clockOutBtn, new Insets(8, 0, 0, 0));
//             clockOutBtn.setOnAction(e -> {
//                 clockOutBtn.setDisable(true);
//                 Thread t = new Thread(() -> {
//                     shiftController.clockOut(activeShift.getId());
//                     Shift refreshed = shiftController.getMyActiveShift();
//                     Platform.runLater(() -> populateClockCard(clockCardHolder, clockedInCard, refreshed));
//                 });
//                 t.setDaemon(true);
//                 t.start();
//             });

//             clockDisplay.getChildren().addAll(onDutyLabel, clockTime, sinceLabel, clockOutBtn);
//         } else {
//             Label offDutyLabel = new Label("OFF DUTY");
//             offDutyLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");

//             Label promptLabel = new Label("Not clocked in");
//             promptLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;");

//             Button clockInBtn = new Button("Clock in");
//             clockInBtn.getStyleClass().add("clock-out-btn");
//             clockInBtn.setMaxWidth(Double.MAX_VALUE);
//             VBox.setMargin(clockInBtn, new Insets(8, 0, 0, 0));
//             clockInBtn.setOnAction(e -> {
//                 clockInBtn.setDisable(true);
//                 Thread t = new Thread(() -> {
//                     shiftController.clockIn();
//                     Shift refreshed = shiftController.getMyActiveShift();
//                     Platform.runLater(() -> populateClockCard(clockCardHolder, clockedInCard, refreshed));
//                 });
//                 t.setDaemon(true);
//                 t.start();
//             });

//             clockDisplay.getChildren().addAll(offDutyLabel, promptLabel, clockInBtn);
//         }

//         card.getChildren().addAll(clockTitle, clockDisplay);
//         return card;
//     }

//     private VBox buildEarningsChartCard(Canvas canvas, HBox dayLabels) {
//         VBox card = new VBox(10);
//         card.getStyleClass().add("earnings-card");
//         card.setPadding(new Insets(18));

//         Label chartTitle = new Label("kWh dispensed, last 7 days");
//         chartTitle.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

//         dayLabels.setAlignment(Pos.CENTER);

//         card.getChildren().addAll(chartTitle, canvas, dayLabels);
//         return card;
//     }

//     private void drawBarChart(GraphicsContext gc, double width, double height, double[] values) {
//         gc.clearRect(0, 0, width, height);
//         int bars = values.length;
//         double max = 0;
//         for (double v : values) {
//             max = Math.max(max, v);
//         }
//         double barWidth = (width / bars) * 0.5;
//         double gap = width / bars;
//         double maxBarHeight = height - 10;

//         for (int i = 0; i < bars; i++) {
//             double frac = max > 0 ? values[i] / max : 0;
//             double barHeight = frac * maxBarHeight;
//             double x = (i * gap) + (gap - barWidth) / 2;
//             double y = height - barHeight;

//             if (i == bars - 1) {
//                 gc.setFill(Color.web("#10b981"));
//             } else {
//                 gc.setFill(Color.web("#0350cc"));
//             }

//             gc.fillRoundRect(x, y, barWidth, Math.max(barHeight, 0), 4, 4);
//         }
//     }

//     private VBox statCard(String title) {
//         VBox c = new VBox(6);
//         c.getStyleClass().add("card");
//         c.setPadding(new Insets(14));
//         c.getChildren().add(label(title, "card-title"));
//         return c;
//     }

//     private Label loadingLabel(String text) {
//         Label l = new Label(text);
//         l.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
//         return l;
//     }

//     private Label label(String text, String styleClass) {
//         Label l = new Label(text);
//         l.getStyleClass().add(styleClass);
//         return l;
//     }

// }
package com.core2web.view;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.core2web.controller.BookingController;
import com.core2web.controller.BusController;
import com.core2web.controller.DriverEarningController;
import com.core2web.controller.ShiftController;
import com.core2web.model.DriverEarning;
import com.core2web.model.Pricing;
import com.core2web.model.Shift;
import com.core2web.util.DateTimeUtil;

public class Shift_Earning {


private static final BookingController bookingController = new BookingController();
private static final BusController busController = new BusController();
private static final DriverEarningController driverEarningController =
        new DriverEarningController();
private static final ShiftController shiftController = new ShiftController();

Scene getShiftScene() {
    DriverDashboard.goTo("Shift & Earnings");
    return DriverDashboard.scene;
}

static ScrollPane buildMainContent() {
    return new Shift_Earning().buildContent();
}

private ScrollPane buildContent() {
    VBox content = new VBox(16);
    content.setPadding(new Insets(16));

    Shift activeShift = shiftController.getMyActiveShift();
    List<Shift> history = shiftController.getMyShiftHistory();
    List<com.core2web.model.Booking> bookings = bookingController.getBookingsForCurrentDriver();

    List<com.core2web.model.Booking> todaysCompleted = new ArrayList<>();
    double kwhToday = 0;
    for (com.core2web.model.Booking b : bookings) {
        if ("COMPLETED".equals(b.getStatus()) && isToday(b.getCreatedAt())) {
            todaysCompleted.add(b);
            kwhToday += b.getKwh();
        }
    }

    /*
     * Real driver earnings for today.
     * This is the same data source used by the "My earnings today" card.
     * We also use it for the average-per-session calculation so both
     * cards are based on the same actual DriverEarning ledger data.
     */
    double earnedToday = 0;
    int paidSessions = 0;

    for (DriverEarning earning : driverEarningController.getMyEarningsToday()) {
        earnedToday += earning.getAmount();
        paidSessions++;
    }

    List<LocalDate> days = new ArrayList<>();
    LocalDate today = LocalDate.now(DateTimeUtil.INDIA);
    for (int i = 6; i >= 0; i--) {
        days.add(today.minusDays(i));
    }

    double[] kwhByDay = new double[7];
    for (com.core2web.model.Booking b : bookings) {
        if (!"COMPLETED".equals(b.getStatus())) {
            continue;
        }
        try {
            Instant timestamp = DateTimeUtil.parse(b.getCreatedAt());
            if (timestamp == null) continue;

            LocalDate d = timestamp.atZone(DateTimeUtil.INDIA).toLocalDate();
            int idx = days.indexOf(d);

            if (idx >= 0) {
                kwhByDay[idx] += b.getKwh();
            }
        } catch (Exception ignored) {
        }
    }

    int sessionCount = todaysCompleted.size();

    VBox clockCardHolder = new VBox();

    VBox clockedInCard = statCard("Clocked In");
    HBox.setHgrow(clockedInCard, Priority.ALWAYS);

    VBox sessionsCard = statCard("Sessions today");
    HBox.setHgrow(sessionsCard, Priority.ALWAYS);

    VBox billedCard = statCard("Billed today");
    HBox.setHgrow(billedCard, Priority.ALWAYS);

    // Real payouts now, from the DriverEarning ledger written on completion.
    VBox payoutCard = statCard("My earnings today");

    Label payAmt = new Label("₹" + (int) earnedToday);
    payAmt.setStyle("-fx-text-fill:#10b981;-fx-font-size:28px;-fx-font-weight:bold;");

    Label paySub = label(paidSessions == 0
            ? "No completed sessions today"
            : paidSessions + (paidSessions == 1 ? " session" : " sessions")
                    + " · " + (int) (Pricing.DRIVER_SHARE * 100) + "% of fare",
            "card-sub");

    payoutCard.getChildren().addAll(payAmt, paySub);
    HBox.setHgrow(payoutCard, Priority.ALWAYS);

    HBox statsRow = new HBox(12, clockedInCard, sessionsCard, billedCard, payoutCard);

    VBox historyRows = new VBox(0);
    VBox scheduleCard = buildShiftHistoryCard(historyRows);
    HBox.setHgrow(scheduleCard, Priority.ALWAYS);

    Canvas canvas = new Canvas(280, 120);
    HBox dayLabels = new HBox();
    VBox earningsCard = buildEarningsChartCard(canvas, dayLabels);

    VBox rightCol = new VBox(16);
    rightCol.setPrefWidth(320);
    rightCol.getChildren().addAll(clockCardHolder, earningsCard);

    HBox bottom = new HBox(16, scheduleCard, rightCol);

    // Only added when a real pending emergency exists — no placeholder banner.
    HBox emergencyBanner = buildEmergencyBanner();
    if (emergencyBanner != null) {
        content.getChildren().add(emergencyBanner);
    }

    content.getChildren().addAll(statsRow, bottom);

    populateClockCard(clockCardHolder, clockedInCard, activeShift);
    populateSessionsCard(sessionsCard, sessionCount, kwhToday);

    /*
     * Use the same real DriverEarning data as "My earnings today"
     * for the billed amount and average-per-session display.
     */
    populateBilledCard(billedCard, earnedToday, paidSessions);

    populateHistory(historyRows, history);
    populateChart(canvas, dayLabels, days, kwhByDay);

    ScrollPane sp = new ScrollPane(content);
    sp.setFitToWidth(true);
    sp.getStyleClass().add("scroll-pane");
    sp.setStyle("-fx-background-color: transparent;");
    return sp;
}

private boolean isToday(String createdAt) {
    try {
        Instant timestamp = DateTimeUtil.parse(createdAt);
        return timestamp != null && timestamp.atZone(DateTimeUtil.INDIA).toLocalDate()
                .equals(LocalDate.now(DateTimeUtil.INDIA));
    } catch (Exception e) {
        return false;
    }
}

private String formatTime(String createdAt) {
    try {
        return DateTimeUtil.time(createdAt);
    } catch (Exception e) {
        return "—";
    }
}

private String dayOfWeek(String createdAt) {
    try {
        Instant timestamp = DateTimeUtil.parse(createdAt);
        if (timestamp == null) return "N/A";

        return timestamp.atZone(DateTimeUtil.INDIA).getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    } catch (Exception e) {
        return "—";
    }
}

private String formatElapsed(java.time.Duration d) {
    long totalMinutes = Math.max(0, d.toMinutes());
    long hours = totalMinutes / 60;
    long minutes = totalMinutes % 60;

    return String.format("%02dh %02dm", hours, minutes);
}

private void populateClockCard(
        VBox clockCardHolder,
        VBox clockedInCard,
        Shift activeShift) {

    clockCardHolder.getChildren().setAll(
            buildClockWidget(clockCardHolder, clockedInCard, activeShift)
    );

    clockedInCard.getChildren().clear();

    if (activeShift != null) {
        Label clockTime = new Label(formatTime(activeShift.getClockInAt()));
        clockTime.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");

        Label elapsed = label(
                formatElapsed(activeShift.elapsed()) + " elapsed",
                "card-sub"
        );

        clockedInCard.getChildren().addAll(clockTime, elapsed);

    } else {
        Label offLabel = new Label("Off duty");
        offLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:20px;-fx-font-weight:bold;");

        clockedInCard.getChildren().add(offLabel);
    }
}

private void populateSessionsCard(
        VBox sessionsCard,
        int count,
        double kwh) {

    sessionsCard.getChildren().clear();

    Label sessNum = new Label(String.valueOf(count));
    sessNum.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");

    Label sessDetail = label(
            ((int) kwh) + " kWh dispensed",
            "card-sub"
    );

    sessionsCard.getChildren().addAll(sessNum, sessDetail);
}

private void populateBilledCard(
        VBox billedCard,
        double billed,
        int count) {

    billedCard.getChildren().clear();

    Label billAmt = new Label("₹" + Math.round(billed));
    billAmt.setStyle("-fx-text-fill:#10b981;-fx-font-size:28px;-fx-font-weight:bold;");

    String avgLine = count > 0
            ? "Avg ₹" + Math.round(billed / count) + " / session"
            : "No sessions today";

    Label billSub = label(avgLine, "card-sub");

    billedCard.getChildren().addAll(billAmt, billSub);
}

private void populateHistory(
        VBox historyRows,
        List<Shift> history) {

    historyRows.getChildren().clear();

    if (history.isEmpty()) {
        historyRows.getChildren().add(
                loadingLabel("No shifts recorded yet.")
        );
        return;
    }

    // One fleet read for the whole list, so rows show "BUS05" not a doc ID.
    Map<String, String> busCodes = busController.getBusCodeLookup();

    int limit = Math.min(5, history.size());

    for (int i = 0; i < limit; i++) {
        historyRows.getChildren().add(
                buildShiftRow(history.get(i), busCodes)
        );
    }
}

private void populateChart(
        Canvas canvas,
        HBox dayLabels,
        List<LocalDate> days,
        double[] values) {

    drawBarChart(
            canvas.getGraphicsContext2D(),
            canvas.getWidth(),
            canvas.getHeight(),
            values
    );

    dayLabels.getChildren().clear();

    for (LocalDate d : days) {
        Label dl = new Label(
                d.getDayOfWeek().getDisplayName(
                        TextStyle.SHORT,
                        Locale.ENGLISH
                )
        );

        dl.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        dl.setPrefWidth(canvas.getWidth() / days.size());
        dl.setAlignment(Pos.CENTER);

        dayLabels.getChildren().add(dl);
    }
}

/**
 * The emergency diversion banner, or null when this driver has no pending
 * emergency — callers must skip adding it entirely in that case.
 */
private HBox buildEmergencyBanner() {
    com.core2web.model.Booking pending =
            bookingController.getPendingEmergencyBookingForCurrentDriver();

    if (pending == null) {
        return null;
    }

    return EmergencyBanner.build(
            pending,
            () -> DriverDashboard.goTo("Shift & Earnings")
    );
}

private VBox buildShiftHistoryCard(VBox historyRows) {
    VBox section = new VBox(0);
    section.getStyleClass().add("schedule-card");
    section.setPadding(new Insets(18));

    HBox header = new HBox(10);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(0, 0, 14, 0));

    Label schedTitle = new Label("Recent shifts");
    schedTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");

    header.getChildren().add(schedTitle);

    HBox tableHeader = buildTableHeaderRow();
    tableHeader.setPadding(new Insets(8, 0, 8, 0));

    tableHeader.setStyle(
            "-fx-border-color: transparent transparent #1e293b transparent; "
                    + "-fx-border-width: 0 0 1 0;"
    );

    section.getChildren().addAll(
            header,
            tableHeader,
            historyRows
    );

    return section;
}

private HBox buildTableHeaderRow() {
    HBox row = new HBox();
    row.setAlignment(Pos.CENTER_LEFT);

    String headerStyle =
            "-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;";

    Label dayLbl = new Label("DAY");
    dayLbl.setPrefWidth(60);
    dayLbl.setStyle(headerStyle);

    Label inLbl = new Label("CLOCK IN");
    inLbl.setPrefWidth(110);
    inLbl.setStyle(headerStyle);

    Label outLbl = new Label("CLOCK OUT");
    outLbl.setPrefWidth(110);
    outLbl.setStyle(headerStyle);

    Label busLbl = new Label("BUS / DEPOT");
    busLbl.setPrefWidth(160);
    busLbl.setStyle(headerStyle);

    Label statusLbl = new Label("STATUS");
    statusLbl.setPrefWidth(90);
    statusLbl.setStyle(headerStyle);

    row.getChildren().addAll(
            dayLbl,
            inLbl,
            outLbl,
            busLbl,
            statusLbl
    );

    return row;
}

private HBox buildShiftRow(
        Shift shift,
        Map<String, String> busCodes) {

    HBox row = new HBox();
    row.setAlignment(Pos.CENTER_LEFT);
    row.setPadding(new Insets(12, 0, 12, 0));

    row.setStyle(
            "-fx-border-color: transparent transparent #1e293b transparent; "
                    + "-fx-border-width: 0 0 1 0;"
    );

    Label dayLbl = new Label(
            dayOfWeek(shift.getClockInAt())
    );
    dayLbl.setPrefWidth(60);
    dayLbl.setStyle(
            "-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;"
    );

    Label inLbl = new Label(
            formatTime(shift.getClockInAt())
    );
    inLbl.setPrefWidth(110);
    inLbl.setStyle(
            "-fx-text-fill:#94a3b8;-fx-font-size:12px;"
    );

    Label outLbl = new Label(
            shift.isActive()
                    ? "In progress"
                    : formatTime(shift.getClockOutAt())
    );
    outLbl.setPrefWidth(110);
    outLbl.setStyle(
            "-fx-text-fill:#94a3b8;-fx-font-size:12px;"
    );

    String bus = BusController.busCodeLabel(
            shift.getBusId(),
            busCodes,
            "— unassigned"
    );

    Label busLbl = new Label(
            bus + " · " + shift.getDepot()
    );
    busLbl.setPrefWidth(160);
    busLbl.setWrapText(true);
    busLbl.setStyle(
            "-fx-text-fill:#94a3b8;-fx-font-size:12px;"
    );

    Label statusLbl = new Label(
            shift.isActive() ? "ACTIVE" : "DONE"
    );
    statusLbl.setPrefWidth(90);

    statusLbl.setStyle(
            shift.isActive()
                    ? "-fx-text-fill:#10b981;-fx-font-size:11px;-fx-font-weight:bold;"
                    : "-fx-text-fill:#64748b;-fx-font-size:11px;"
    );

    row.getChildren().addAll(
            dayLbl,
            inLbl,
            outLbl,
            busLbl,
            statusLbl
    );

    return row;
}

private VBox buildClockWidget(
        VBox clockCardHolder,
        VBox clockedInCard,
        Shift activeShift) {

    VBox card = new VBox(10);
    card.getStyleClass().add("clock-card");
    card.setPadding(new Insets(18));

    Label clockTitle = new Label("Clock");
    clockTitle.setStyle(
            "-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;"
    );

    VBox clockDisplay = new VBox(6);
    clockDisplay.setAlignment(Pos.CENTER);
    clockDisplay.setPadding(new Insets(18));
    clockDisplay.setStyle(
            "-fx-background-color:#0f1720;-fx-background-radius:12;"
    );

    if (activeShift != null) {

        Label onDutyLabel = new Label("ON DUTY");
        onDutyLabel.setStyle(
                "-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;"
        );

        Label clockTime = new Label(
                formatElapsed(activeShift.elapsed())
        );
        clockTime.setStyle(
                "-fx-text-fill:#f8fafc;-fx-font-size:36px;-fx-font-weight:bold;"
        );

        Label sinceLabel = new Label(
                "since " + formatTime(activeShift.getClockInAt())
                        + " · depot " + activeShift.getDepot()
        );
        sinceLabel.setStyle(
                "-fx-text-fill:#64748b;-fx-font-size:11px;"
        );

        Button clockOutBtn = new Button("Clock out");
        clockOutBtn.getStyleClass().add("clock-out-btn");
        clockOutBtn.setMaxWidth(Double.MAX_VALUE);

        VBox.setMargin(
                clockOutBtn,
                new Insets(8, 0, 0, 0)
        );

        clockOutBtn.setOnAction(e -> {
            clockOutBtn.setDisable(true);

            Thread t = new Thread(() -> {
                shiftController.clockOut(activeShift.getId());

                Shift refreshed =
                        shiftController.getMyActiveShift();

                Platform.runLater(() ->
                        populateClockCard(
                                clockCardHolder,
                                clockedInCard,
                                refreshed
                        )
                );
            });

            t.setDaemon(true);
            t.start();
        });

        clockDisplay.getChildren().addAll(
                onDutyLabel,
                clockTime,
                sinceLabel,
                clockOutBtn
        );

    } else {

        Label offDutyLabel = new Label("OFF DUTY");
        offDutyLabel.setStyle(
                "-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;"
        );

        Label promptLabel = new Label("Not clocked in");
        promptLabel.setStyle(
                "-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;"
        );

        Button clockInBtn = new Button("Clock in");
        clockInBtn.getStyleClass().add("clock-out-btn");
        clockInBtn.setMaxWidth(Double.MAX_VALUE);

        VBox.setMargin(
                clockInBtn,
                new Insets(8, 0, 0, 0)
        );

        clockInBtn.setOnAction(e -> {
            clockInBtn.setDisable(true);

            Thread t = new Thread(() -> {
                shiftController.clockIn();

                Shift refreshed =
                        shiftController.getMyActiveShift();

                Platform.runLater(() ->
                        populateClockCard(
                                clockCardHolder,
                                clockedInCard,
                                refreshed
                        )
                );
            });

            t.setDaemon(true);
            t.start();
        });

        clockDisplay.getChildren().addAll(
                offDutyLabel,
                promptLabel,
                clockInBtn
        );
    }

    card.getChildren().addAll(
            clockTitle,
            clockDisplay
    );

    return card;
}

private VBox buildEarningsChartCard(
        Canvas canvas,
        HBox dayLabels) {

    VBox card = new VBox(10);
    card.getStyleClass().add("earnings-card");
    card.setPadding(new Insets(18));

    Label chartTitle =
            new Label("kWh dispensed, last 7 days");

    chartTitle.setStyle(
            "-fx-text-fill:#94a3b8;-fx-font-size:12px;"
    );

    dayLabels.setAlignment(Pos.CENTER);

    card.getChildren().addAll(
            chartTitle,
            canvas,
            dayLabels
    );

    return card;
}

private void drawBarChart(
        GraphicsContext gc,
        double width,
        double height,
        double[] values) {

    gc.clearRect(0, 0, width, height);

    int bars = values.length;

    double max = 0;

    for (double v : values) {
        max = Math.max(max, v);
    }

    double barWidth = (width / bars) * 0.5;
    double gap = width / bars;
    double maxBarHeight = height - 10;

    for (int i = 0; i < bars; i++) {

        double frac =
                max > 0
                        ? values[i] / max
                        : 0;

        double barHeight =
                frac * maxBarHeight;

        double x =
                (i * gap)
                        + (gap - barWidth) / 2;

        double y =
                height - barHeight;

        if (i == bars - 1) {
            gc.setFill(Color.web("#10b981"));
        } else {
            gc.setFill(Color.web("#0350cc"));
        }

        gc.fillRoundRect(
                x,
                y,
                barWidth,
                Math.max(barHeight, 0),
                4,
                4
        );
    }
}

private VBox statCard(String title) {
    VBox c = new VBox(6);
    c.getStyleClass().add("card");
    c.setPadding(new Insets(14));
    c.getChildren().add(
            label(title, "card-title")
    );
    return c;
}

private Label loadingLabel(String text) {
    Label l = new Label(text);
    l.setStyle(
            "-fx-text-fill:#64748b;-fx-font-size:12px;"
    );
    return l;
}

private Label label(
        String text,
        String styleClass) {

    Label l = new Label(text);
    l.getStyleClass().add(styleClass);

    return l;
}


}
