


package com.core2web.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.core2web.controller.WalletController;
import com.core2web.controller.SubscriptionPlanController;
import com.core2web.model.SubscriptionPlan;

import static com.core2web.view.AdminLayout.*;

public class Revenue {

    private static final WalletController walletController =
            new WalletController();

    private static final SubscriptionPlanController planController =
            new SubscriptionPlanController();

    private static final int CHART_HEIGHT = 160;
    private static final int CHART_AXIS_MARKS = 4;


    public static void show(Stage stage) {
        AdminDashboard.goTo("Revenue");
    }


    static ScrollPane buildMainContent() {

        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        double revenueToday = walletController.getRevenueToday();
        double revenueThisMonth = walletController.getRevenueThisMonth();
        Map<String, Double> last7Days =
                walletController.getRevenueLast7Days();


        /*
         * Load subscription plans from Firestore once.
         *
         * The same list is used for:
         * 1. Active subscription plan count
         * 2. Subscription plans section below
         */
        List<SubscriptionPlan> plans =
                planController.getAllPlans();


        int activeSubscriptionPlans = 0;

        for (SubscriptionPlan plan : plans) {

            if (plan.isActive()) {
                activeSubscriptionPlans++;
            }
        }


        Label revenueTodayVal = kpiValueLabel(
                formatINR((int) Math.round(revenueToday)),
                "#10b981"
        );


        java.util.List<Double> dayValues =
                new java.util.ArrayList<>(last7Days.values());

        double yesterday =
                dayValues.size() >= 2
                        ? dayValues.get(dayValues.size() - 2)
                        : 0;

        String revenueTodaySubText;

        if (yesterday > 0) {

            double pctChange =
                    ((revenueToday - yesterday) / yesterday) * 100;

            revenueTodaySubText =
                    (pctChange >= 0 ? "+" : "")
                            + Math.round(pctChange)
                            + "% vs. yesterday";

        } else {

            revenueTodaySubText =
                    "vs. yesterday: no data";
        }


        Label revenueTodaySub =
                kpiSubLabel(revenueTodaySubText);


        Label revenueMonthVal =
                kpiValueLabel(
                        formatINRShort(
                                (int) Math.round(revenueThisMonth)
                        ),
                        "#f8fafc"
                );


        HBox kpiRow =
                buildKpiRow(
                        revenueTodayVal,
                        revenueTodaySub,
                        revenueMonthVal,
                        activeSubscriptionPlans
                );


        VBox chartHolder = new VBox();

        chartHolder.getChildren().add(
                buildChartCard(last7Days)
        );


        content.getChildren().addAll(
                kpiRow,
                chartHolder,
                buildSubscriptionsCard(plans)
        );


        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.getStyleClass().add(
                "scroll-pane"
        );

        return scrollPane;
    }


    private static Label kpiValueLabel(
            String initialText,
            String color
    ) {

        Label l =
                new Label(initialText);

        l.setStyle(
                "-fx-text-fill:" + color +
                ";-fx-font-size:26px;" +
                "-fx-font-weight:bold;"
        );

        return l;
    }


    private static Label kpiSubLabel(
            String initialText
    ) {

        Label l =
                new Label(initialText);

        l.getStyleClass().add(
                "card-sub"
        );

        return l;
    }


    private static HBox buildKpiRow(
            Label revenueTodayVal,
            Label revenueTodaySub,
            Label revenueMonthVal,
            int activeSubscriptionPlans
    ) {

        HBox row =
                new HBox(16);


        VBox revenueTodayCard =
                card("Revenue today");

        revenueTodayCard.getChildren().addAll(
                revenueTodayVal,
                revenueTodaySub
        );

        HBox.setHgrow(
                revenueTodayCard,
                Priority.ALWAYS
        );


        VBox revenueMonthCard =
                card("Revenue this month");

        revenueMonthCard.getChildren().addAll(
                revenueMonthVal,
                label(
                        "This calendar month",
                        "card-sub"
                )
        );

        HBox.setHgrow(
                revenueMonthCard,
                Priority.ALWAYS
        );


        row.getChildren().addAll(

                revenueTodayCard,

                revenueMonthCard,

                statCard(
                        "Active subscription plans",
                        String.valueOf(activeSubscriptionPlans),
                        "#f8fafc",
                        "Currently active plans"
                ),
                  statCard(
        "Avg. session value",
        "₹" + String.format(
                "%.0f",
                walletController.getAverageSessionValue()
        ),
        "#f8fafc",
        "Average value per charging session"
)
        );

        return row;
    }


    private static VBox buildChartCard(
            Map<String, Double> revenueByDay
    ) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(18)
        );


        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        Label title =
                new Label(
                        "Revenue — last 7 days"
                );

        title.getStyleClass().add(
                "section-title"
        );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Button exportButton =
                new Button("Export report");

        exportButton.getStyleClass().add(
                "secondary-btn"
        );


        exportButton.setOnAction(
                e -> exportReport(revenueByDay)
        );


        header.getChildren().addAll(
                title,
                spacer,
                exportButton
        );


        String[] days =
                revenueByDay.keySet()
                        .toArray(new String[0]);


        int[] values =
                new int[days.length];

        int i = 0;

        for (double v :
                revenueByDay.values()) {

            values[i++] =
                    (int) Math.round(v);
        }


        int maxValue = 0;

        for (int v : values) {

            maxValue =
                    Math.max(
                            maxValue,
                            v
                    );
        }


        int scaleMax =
                niceScaleMax(maxValue);


        HBox chartRow =
                new HBox(0);

        chartRow.setAlignment(
                Pos.BOTTOM_LEFT
        );


        chartRow.getChildren().addAll(
                buildAxis(scaleMax),
                buildBars(
                        days,
                        values,
                        scaleMax
                )
        );


        card.getChildren().addAll(
                header,
                chartRow
        );

        return card;
    }


    /**
     * Rounds {@code maxValue} up to a "nice" axis ceiling (1/2/5 x a power of
     * ten) so the tallest bar always fills most of the chart height, whether
     * daily revenue is in the hundreds or in lakhs — instead of measuring
     * every day against a fixed ceiling that dwarfs small values.
     */
    private static int niceScaleMax(
            int maxValue
    ) {

        if (maxValue <= 0) {
            return 100;
        }

        double roughStep =
                maxValue / (double) CHART_AXIS_MARKS;

        double magnitude =
                Math.pow(
                        10,
                        Math.floor(Math.log10(roughStep))
                );

        double normalized =
                roughStep / magnitude;

        double niceNormalized =
                normalized <= 1 ? 1
                        : normalized <= 2 ? 2
                        : normalized <= 5 ? 5
                        : 10;

        return (int) Math.round(
                niceNormalized * magnitude * CHART_AXIS_MARKS
        );
    }


    private static VBox buildAxis(
            int scaleMax
    ) {

        VBox axis =
                new VBox();

        axis.setAlignment(
                Pos.TOP_RIGHT
        );

        axis.setPadding(
                new Insets(
                        0,
                        10,
                        20,
                        0
                )
        );


        int marks = CHART_AXIS_MARKS;


        for (int i = 0;
             i <= marks;
             i++) {

            int value =
                    scaleMax -
                    (scaleMax / marks) * i;


            Label label =
                    new Label(
                            formatINR(value)
                    );


            label.setStyle(
                    "-fx-text-fill:#64748b;" +
                    "-fx-font-size:10px;"
            );


            label.setPrefHeight(
                    (double) CHART_HEIGHT / marks
            );


            label.setAlignment(
                    Pos.CENTER_RIGHT
            );


            axis.getChildren().add(
                    label
            );
        }

        return axis;
    }


    private static HBox buildBars(
            String[] days,
            int[] values,
            int scaleMax
    ) {

        HBox chart =
                new HBox(14);

        chart.setAlignment(
                Pos.BOTTOM_LEFT
        );


        for (int i = 0;
             i < values.length;
             i++) {

            chart.getChildren().add(
                    barColumn(
                            days[i],
                            values[i],
                            scaleMax,
                            i == values.length - 1
                    )
            );
        }


        HBox.setHgrow(
                chart,
                Priority.ALWAYS
        );

        return chart;
    }


    private static VBox barColumn(
            String day,
            int value,
            int scaleMax,
            boolean highlight
    ) {

        VBox column =
                new VBox(6);

        column.setAlignment(
                Pos.BOTTOM_CENTER
        );


        Label valueLabel =
                new Label(
                        formatINRShort(value)
                );


        valueLabel.setStyle(
                "-fx-text-fill:" +
                (highlight
                        ? "#10b981"
                        : "#cbd5e1") +
                ";" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );


        Region bar =
                new Region();

        bar.setPrefWidth(40);

        bar.setPrefHeight(
                Math.max(
                        4,
                        CHART_HEIGHT *
                        ((double) value / scaleMax)
                )
        );


        bar.setStyle(
                "-fx-background-color:" +
                (highlight
                        ? "#10b981"
                        : "#334155") +
                ";" +
                "-fx-background-radius:6 6 0 0;"
        );


        column.getChildren().addAll(
                valueLabel,
                bar,
                label(
                        day,
                        "small-muted"
                )
        );


        HBox.setHgrow(
                column,
                Priority.ALWAYS
        );

        return column;
    }


    /*
     * Subscription plans are now loaded from Firestore.
     *
     * Existing Revenue UI is preserved.
     * Status is taken from SubscriptionPlan.active.
     */
    private static VBox buildSubscriptionsCard(
            List<SubscriptionPlan> plans
    ) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "bookings-section"
        );

        card.setPadding(
                new Insets(18)
        );


        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        Label title =
                new Label(
                        "Subscription plans"
                );

        title.getStyleClass().add(
                "booking-stage"
        );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Button manageButton =
                new Button("+ Manage plans");

        manageButton.getStyleClass().add(
                "primary-btn"
        );


        manageButton.setOnAction(
                e -> SubscriptionPlanManagement.show()
        );


        header.getChildren().addAll(
                title,
                spacer,
                manageButton
        );


        HBox columnHeaders =
                new HBox();

        columnHeaders.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );


        columnHeaders.getChildren().addAll(

                colLabel("PLAN", 160),

                colLabel("PRICE", 100),

                colLabel("SUBSCRIBERS", 120),

                colLabel("MRR", 110),

                colLabel("STATUS", 100)
        );


        VBox rows =
                new VBox(2);


        /*
         * Build Revenue rows from the actual Firestore
         * subscription plans.
         */
        for (SubscriptionPlan plan : plans) {

            String status =
                    plan.isActive()
                            ? "ACTIVE"
                            : "INACTIVE";

            String statusColor =
                    plan.isActive()
                            ? "#10b981"
                            : "#ef4444";


            String price;

            if (plan.getBillingCycle() == null ||
                    plan.getBillingCycle().isBlank()) {

                price =
                        "₹" +
                        String.format(
                                "%.0f",
                                plan.getPriceInr()
                        );

            } else {

                price =
                        "₹" +
                        String.format(
                                "%.0f",
                                plan.getPriceInr()
                        ) +
                        " / " +
                        plan.getBillingCycle().toLowerCase();
            }


            rows.getChildren().add(

                    planRow(
                            plan.getName(),
                            price,
                            "N/A",
                            "N/A",
                            status,
                            statusColor
                    )
            );
        }


        /*
         * If no plans exist, show a small message instead
         * of leaving the section looking broken.
         */
        if (plans.isEmpty()) {

            Label empty =
                    new Label(
                            "No subscription plans available."
                    );

            empty.setStyle(
                    "-fx-text-fill:#64748b;" +
                    "-fx-font-size:12px;"
            );

            rows.getChildren().add(
                    empty
            );
        }


        card.getChildren().addAll(
                header,
                columnHeaders,
                rows
        );

        return card;
    }


    private static HBox planRow(
            String plan,
            String price,
            String subscribers,
            String mrr,
            String status,
            String color
    ) {

        HBox row =
                new HBox();

        row.setPadding(
                new Insets(12)
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.getStyleClass().add(
                "booking-row"
        );


        row.setOnMouseClicked(
                e -> SubscriptionPlanManagement.show()
        );


        Label planLabel =
                new Label(plan);

        planLabel.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        planLabel.setPrefWidth(160);
        planLabel.setMinWidth(160);


        Label priceLabel =
                new Label(price);

        priceLabel.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        priceLabel.setPrefWidth(100);
        priceLabel.setMinWidth(100);


        Label subscriberLabel =
                new Label(subscribers);

        subscriberLabel.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        subscriberLabel.setPrefWidth(120);
        subscriberLabel.setMinWidth(120);


        Label mrrLabel =
                new Label(mrr);

        mrrLabel.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        mrrLabel.setPrefWidth(110);
        mrrLabel.setMinWidth(110);


        row.getChildren().addAll(
                planLabel,
                priceLabel,
                subscriberLabel,
                mrrLabel,
                statusBadge(
                        status,
                        color,
                        100
                )
        );

        return row;
    }


    private static void exportReport(
            Map<String, Double> revenueByDay
    ) {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Save Revenue Report"
        );

        fileChooser.setInitialFileName(
                "ChargeOn_Revenue_Report.csv"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV Files (*.csv)",
                        "*.csv"
                )
        );


        File file =
                fileChooser.showSaveDialog(null);


        if (file == null) {
            return;
        }


        try (PrintWriter writer =
                     new PrintWriter(
                             new FileWriter(file)
                     )) {

            writer.println(
                    "ChargeOn Revenue Report"
            );

            writer.println();

            writer.println(
                    "Day,Revenue"
            );

            double total = 0;


            for (Map.Entry<String, Double> entry :
                    revenueByDay.entrySet()) {

                double revenue =
                        entry.getValue();

                writer.println(
                        entry.getKey()
                                + ","
                                + String.format(
                                        "%.2f",
                                        revenue
                                )
                );

                total += revenue;
            }


            writer.println();

            writer.println(
                    "Total Revenue,"
                            + String.format(
                                    "%.2f",
                                    total
                            )
            );


            System.out.println(
                    "Revenue report saved to: "
                            + file.getAbsolutePath()
            );


        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }


    private static String formatINR(
            int amount
    ) {

        String s =
                String.valueOf(amount);

        if (s.length() <= 3) {
            return "₹" + s;
        }


        String last3 =
                s.substring(
                        s.length() - 3
                );


        String rest =
                s.substring(
                        0,
                        s.length() - 3
                );


        StringBuilder grouped =
                new StringBuilder();

        int count = 0;


        for (int i = rest.length() - 1;
             i >= 0;
             i--) {

            grouped.insert(
                    0,
                    rest.charAt(i)
            );

            count++;


            if (count % 2 == 0 &&
                    i != 0) {

                grouped.insert(
                        0,
                        ','
                );
            }
        }


        return "₹" +
                grouped +
                "," +
                last3;
    }


    private static String formatINRShort(
            int amount
    ) {

        double lakhs =
                amount / 100000.0;

        return "₹" +
                String.format(
                        "%.2fL",
                        lakhs
                );
    }
}
