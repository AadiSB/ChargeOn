
package com.core2web.view;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.core2web.controller.BookingController;
import com.core2web.controller.WalletController;
import com.core2web.model.WalletTransaction;
import com.core2web.util.DateTimeUtil;

import static com.core2web.view.OwnerLayout.*;

public class HistoryImpact {

    private static final BookingController bookingController = new BookingController();
    private static final WalletController walletController = new WalletController();

    Scene getHistoryScene() {
        OwnerDashboard.goTo("History & Impact");
        return OwnerDashboard.scene;
    }

    static ScrollPane buildMainContent() {
        return new HistoryImpact().buildContent();
    }

    private ScrollPane buildContent() {

        VBox content = new VBox(18);
        content.setPadding(new Insets(20));

        List<com.core2web.model.Booking> bookings =
                bookingController.getMyBookings();

        List<WalletTransaction> transactions =
                walletController.getMyTransactions();

        HBox statsRow = new HBox(14);

        // Existing statistics logic kept unchanged
        populateStats(statsRow, bookings, transactions);

        HBox mainRow = new HBox(18);
        mainRow.setFillHeight(true);

        // =========================================================
        // MONTHLY USAGE - UI SIZE ONLY
        // =========================================================

        VBox chartCard = new VBox(12);
        chartCard.getStyleClass().add("card");
        chartCard.setPadding(new Insets(22));

        // Smaller chart section so Recent Sessions gets more space
        chartCard.setPrefWidth(500);
        chartCard.setMinWidth(450);
        chartCard.setMaxWidth(550);

        Label chartTitle = new Label("Monthly usage");
        chartTitle.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        Label chartSub = new Label("kWh consumed per month");
        chartSub.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        // Smaller graph
        Canvas canvas = new Canvas(300, 120);

        HBox monthLabels = new HBox();
        monthLabels.setAlignment(Pos.CENTER);

        chartCard.getChildren().addAll(
                chartTitle,
                chartSub,
                canvas,
                monthLabels
        );

        populateChart(canvas, monthLabels, bookings);

        // =========================================================
        // RECENT SESSIONS - MORE SPACE
        // =========================================================

        VBox historyCard = new VBox(10);
        historyCard.getStyleClass().add("card");
        historyCard.setPadding(new Insets(22));

        // Increased width for Recent Sessions
        historyCard.setPrefWidth(520);
        historyCard.setMinWidth(480);
        historyCard.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(historyCard, Priority.ALWAYS);

        Label histTitle = new Label("Recent sessions");
        histTitle.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        VBox sessionRows = new VBox(0);
        sessionRows.setFillWidth(true);

        populateSessions(
                sessionRows,
                bookings,
                transactions
        );

        historyCard.getChildren().addAll(
                histTitle,
                sessionRows
        );

        // =========================================================
        // MAIN ROW
        // =========================================================

        mainRow.getChildren().addAll(
                chartCard,
                historyCard
        );

        content.getChildren().addAll(
                statsRow,
                mainRow
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color:transparent;");

        return scrollPane;
    }

    private Label loadingLabel(String text) {

        Label l = new Label(text);

        l.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;"
        );

        return l;
    }

    // =============================================================
    // STATISTICS
    // =============================================================

    private void populateStats(
            HBox statsRow,
            List<com.core2web.model.Booking> bookings,
            List<WalletTransaction> transactions) {

        int totalSessions = 0;
        double totalEnergy = 0;

        for (com.core2web.model.Booking b : bookings) {

            if ("COMPLETED".equals(b.getStatus())) {

                totalSessions++;

                totalEnergy += b.getKwh();
            }
        }

        double totalSpent = 0;

        for (WalletTransaction t : transactions) {

            if (t.isChargePayment()) {

                totalSpent += t.getAmount();
            }
        }

        String avgLine = totalSessions > 0
                ? "Avg ₹" + Math.round(totalSpent / totalSessions)
                        + " per session"
                : "No completed sessions yet";

        // CO2 calculation kept exactly as requested
        double co2Saved = totalEnergy * 0.7;

        String co2Value =
                String.format(Locale.US, "%.1f kg", co2Saved);

        statsRow.getChildren().setAll(

                impactCard(
                        "🌿",
                        "CO₂ saved",
                        co2Value,
                        "Based on "
                                + String.format(
                                        Locale.US,
                                        "%.1f",
                                        totalEnergy
                                )
                                + " kWh consumed",
                        "#10b981"
                ),

                impactCard(
                        "⚡",
                        "Energy consumed",
                        ((int) totalEnergy) + " kWh",
                        "Across "
                                + totalSessions
                                + " sessions",
                        "#3B82F6"
                ),

                impactCard(
                        "🔌",
                        "Total sessions",
                        String.valueOf(totalSessions),
                        totalSessions > 0
                                ? "All completed sessions"
                                : "No sessions yet",
                        "#F59E0B"
                ),

                impactCard(
                        "₹",
                        "Total spent",
                        "₹" + Math.round(totalSpent),
                        avgLine,
                        "#64748b"
                )
        );
    }

    // =============================================================
    // MONTHLY CHART
    // =============================================================
private void populateChart(
        Canvas canvas,
        HBox monthLabels,
        List<com.core2web.model.Booking> bookings) {

    /*
     * Current month according to Indian time.
     */
    YearMonth current =
            YearMonth.now(DateTimeUtil.INDIA);

    int currentYear =
            current.getYear();

    int currentMonth =
            current.getMonthValue();

    /*
     * Create January -> December
     * of the current year.
     */
    List<YearMonth> months =
            new ArrayList<>();

    for (int i = 1; i <= 12; i++) {

        months.add(
                YearMonth.of(
                        currentYear,
                        i
                )
        );
    }

    double[] values =
            new double[12];

    /*
     * Read completed bookings from Firebase.
     */
    for (com.core2web.model.Booking b : bookings) {

        if (!"COMPLETED".equals(b.getStatus())) {
            continue;
        }

        try {

            Instant timestamp =
                    DateTimeUtil.parse(
                            b.getCreatedAt()
                    );

            if (timestamp == null) {
                continue;
            }

            /*
             * Convert booking time to India.
             */
            YearMonth bookingMonth =
                    YearMonth.from(
                            timestamp.atZone(
                                    DateTimeUtil.INDIA
                            )
                    );

            /*
             * Only current-year bookings.
             */
            if (bookingMonth.getYear()
                    != currentYear) {
                continue;
            }

            int index =
                    bookingMonth.getMonthValue() - 1;

            /*
             * Don't put data into future months.
             */
            if (index < currentMonth) {

                values[index] += b.getKwh();
            }

        } catch (Exception ignored) {
        }
    }

    /*
     * Use your EXISTING drawBarChart() method.
     */
    drawBarChart(
            canvas.getGraphicsContext2D(),
            canvas.getWidth(),
            canvas.getHeight(),
            values
    );

    /*
     * =====================================================
     * MONTH LABELS
     * =====================================================
     */

    monthLabels.getChildren().clear();

    /*
     * Exactly the same width as the canvas.
     */
    monthLabels.setPrefWidth(
            canvas.getWidth()
    );

    monthLabels.setMinWidth(
            canvas.getWidth()
    );

    monthLabels.setMaxWidth(
            canvas.getWidth()
    );

    monthLabels.setAlignment(
            Pos.CENTER
    );

    /*
     * 12 equal columns.
     */
    double labelWidth =
            canvas.getWidth() / 12.0;

    for (int i = 0; i < 12; i++) {

        YearMonth ym =
                months.get(i);

        Label ml =
                new Label(
                        ym.getMonth()
                                .getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.ENGLISH
                                )
                );

        /*
         * Future months are visible but dimmed.
         */
        if (i >= currentMonth) {

            ml.setStyle(
                    "-fx-text-fill:#475569;" +
                    "-fx-font-size:10px;"
            );

        } else {

            ml.setStyle(
                    "-fx-text-fill:white;" +
                    "-fx-font-size:10px;"
            );
        }

        /*
         * EXACT same column width.
         */
        ml.setPrefWidth(
                labelWidth
        );

        ml.setMinWidth(
                labelWidth
        );

        ml.setMaxWidth(
                labelWidth
        );

        ml.setAlignment(
                Pos.CENTER
        );

        monthLabels.getChildren().add(ml);
    }
}
    // =============================================================
    // RECENT SESSIONS
    // =============================================================

    private void populateSessions(
            VBox sessionRows,
            List<com.core2web.model.Booking> bookings,
            List<WalletTransaction> transactions) {

        Map<String, Double> costByBooking =
                new HashMap<>();

        for (WalletTransaction t : transactions) {

            if (t.isChargePayment()
                    && t.getBookingId() != null
                    && !t.getBookingId().isEmpty()) {

                costByBooking.put(
                        t.getBookingId(),
                        t.getAmount()
                );
            }
        }

        List<com.core2web.model.Booking> finished =
                new ArrayList<>();

        for (com.core2web.model.Booking b : bookings) {

            if ("COMPLETED".equals(b.getStatus())
                    || "CANCELLED".equals(b.getStatus())) {

                finished.add(b);
            }
        }

        finished.sort((a, b) -> {

            Instant right =
                    DateTimeUtil.parse(
                            b.getCreatedAt()
                    );

            Instant left =
                    DateTimeUtil.parse(
                            a.getCreatedAt()
                    );

            if (right == null) {
                return left == null ? 0 : -1;
            }

            return left == null
                    ? 1
                    : right.compareTo(left);
        });

        sessionRows.getChildren().clear();

        if (finished.isEmpty()) {

            sessionRows.getChildren().add(
                    loadingLabel(
                            "No sessions yet."
                    )
            );

            return;
        }

        int limit =
                Math.min(5, finished.size());

        for (int i = 0; i < limit; i++) {

            com.core2web.model.Booking b =
                    finished.get(i);

            /*
             * Existing price lookup is unchanged.
             * Only the UI layout has been adjusted.
             */
            Double cost =
                    costByBooking.get(b.getId());

            String costLabel =
                    cost != null
                            ? "₹" + Math.round(cost)
                            : "—";

            sessionRows.getChildren().add(
                    sessionItem(
                            "#" + b.getId(),
                            b.getLocation(),
                            formatSessionDate(
                                    b.getCreatedAt()
                            ),
                            ((int) b.getKwh()) + " kWh",
                            costLabel,
                            b.getStatus()
                    )
            );
        }
    }

    private String formatSessionDate(
            String createdAt) {

        try {

            Instant instant =
                    DateTimeUtil.parse(createdAt);

            return instant == null
                    ? "N/A"
                    : DateTimeUtil.DISPLAY.format(
                            instant.atZone(
                                    DateTimeUtil.INDIA
                            )
                    );

        } catch (Exception e) {

            return "—";
        }
    }

    // =============================================================
    // IMPACT CARD
    // =============================================================

    private VBox impactCard(
            String icon,
            String title,
            String value,
            String detail,
            String color) {

        VBox card = new VBox(6);

        card.getStyleClass().add("card");

        card.setPadding(
                new Insets(18)
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        Label iconLbl =
                new Label(icon);

        iconLbl.setStyle(
                "-fx-font-size:20px;"
        );

        Label titleLbl =
                new Label(title);

        titleLbl.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        Label valueLbl =
                new Label(value);

        valueLbl.setStyle(
                "-fx-text-fill:" + color + ";" +
                "-fx-font-size:24px;" +
                "-fx-font-weight:bold;"
        );

        Label detLbl =
                new Label(detail);

        detLbl.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:11px;"
        );

        detLbl.setWrapText(true);

        card.getChildren().addAll(
                iconLbl,
                titleLbl,
                valueLbl,
                detLbl
        );

        return card;
    }

    // =============================================================
    // BAR CHART DRAWING
    // =============================================================

    // private void drawBarChart(
    //         GraphicsContext gc,
    //         double width,
    //         double height,
    //         double[] values) {

    //     gc.clearRect(
    //             0,
    //             0,
    //             width,
    //             height
    //     );

    //     int bars =
    //             values.length;

    //     double max = 0;

    //     for (double v : values) {

    //         max =
    //                 Math.max(max, v);
    //     }

    //     double barWidth =
    //             (width / bars) * 0.5;

    //     double gap =
    //             width / bars;

    //     double maxBarHeight =
    //             height - 10;

    //     for (int i = 0; i < bars; i++) {

    //         double frac =
    //                 max > 0
    //                         ? values[i] / max
    //                         : 0;

    //         double barHeight =
    //                 frac * maxBarHeight;

    //         double x =
    //                 (i * gap)
    //                         + (gap - barWidth) / 2;

    //         double y =
    //                 height - barHeight;

    //         if (i == bars - 1) {

    //             gc.setFill(
    //                     Color.web("#10b981")
    //             );

    //         } else {

    //             gc.setFill(
    //                     Color.web("#0062ea")
    //             );
    //         }

    //         gc.fillRoundRect(
    //                 x,
    //                 y,
    //                 barWidth,
    //                 Math.max(
    //                         barHeight,
    //                         0
    //                 ),
    //                 4,
    //                 4
    //         );
    //     }
    // }
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

    // Each bar gets exactly the same column width
    // as each month label.
    double columnWidth = width / bars;

    // Smaller bar inside each month column
    double barWidth = columnWidth * 0.45;

    double maxBarHeight = height - 10;

    for (int i = 0; i < bars; i++) {

        double frac = max > 0
                ? values[i] / max
                : 0;

        double barHeight = frac * maxBarHeight;

        // Center the bar in the exact same column
        // used by the corresponding month label.
        double x =
                (i * columnWidth)
                + (columnWidth - barWidth) / 2;

        double y = height - barHeight;

        if (i == bars - 1) {
            gc.setFill(Color.web("#10b981"));
        } else {
            gc.setFill(Color.web("#0062ea"));
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

    // =============================================================
    // RECENT SESSION ITEM
    // =============================================================

    private HBox sessionItem(
            String id,
            String location,
            String date,
            String energy,
            String cost,
            String status) {

        HBox item =
                new HBox(14);

        item.setPadding(
                new Insets(
                        10,
                        0,
                        10,
                        0
                )
        );

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setStyle(
                "-fx-border-color:"
                        + "transparent transparent "
                        + "#1e293b transparent;"
                        + "-fx-border-width:"
                        + "0 0 1 0;"
        );

        // =========================================================
        // LEFT SIDE - LOCATION + ID + DATE
        // =========================================================

        VBox mainInfo =
                new VBox(2);

        HBox.setHgrow(
                mainInfo,
                Priority.ALWAYS
        );

        Label locLbl =
                new Label(location);

        locLbl.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        locLbl.setWrapText(true);

        Label idDateLbl =
                new Label(
                        id + " · " + date
                );

        idDateLbl.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;"
        );

        idDateLbl.setWrapText(true);

        mainInfo.getChildren().addAll(
                locLbl,
                idDateLbl
        );

        // =========================================================
        // RIGHT SIDE - PRICE + ENERGY
        // =========================================================

        VBox rightInfo =
                new VBox(2);

        rightInfo.setAlignment(
                Pos.CENTER_RIGHT
        );

        rightInfo.setMinWidth(80);

        Label costLbl =
                new Label(cost);

        costLbl.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        Label energyLbl =
                new Label(energy);

        energyLbl.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;"
        );

        rightInfo.getChildren().addAll(
                costLbl,
                energyLbl
        );

        // =========================================================
        // STATUS
        // =========================================================

        String statusColor =
                status.equals("CANCELLED")
                        ? "#EF4444"
                        : "#10b981";

        Label statusLbl =
                new Label(status);

        statusLbl.setStyle(
                "-fx-text-fill:"
                        + statusColor + ";"
                        + "-fx-font-size:10px;"
                        + "-fx-font-weight:bold;"
                        + "-fx-background-color:"
                        + statusColor + "22;"
                        + "-fx-padding:"
                        + "3 8 3 8;"
                        + "-fx-background-radius:4;"
        );

        // =========================================================
        // COMPLETE ROW
        // =========================================================

        item.getChildren().addAll(
                mainInfo,
                rightInfo,
                statusLbl
        );

        return item;
    }
}