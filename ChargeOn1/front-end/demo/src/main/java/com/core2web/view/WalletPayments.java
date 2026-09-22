package com.core2web.view;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import com.core2web.controller.WalletController;
import com.core2web.model.WalletTransaction;
import com.core2web.util.DateTimeUtil;

public class WalletPayments {

    private static final WalletController walletController = new WalletController();

    Scene getWalletScene() {
        OwnerDashboard.goTo("Wallet & Payments");
        return OwnerDashboard.scene;
    }

    static ScrollPane buildMainContent() {
        return new WalletPayments().buildContent();
    }

    private ScrollPane buildContent() {
        double balance = walletController.getMyBalance();
        List<WalletTransaction> transactions = walletController.getMyTransactionsThisMonth();

        HBox content = new HBox(18);
        content.setPadding(new Insets(20));

        VBox leftCol = new VBox(18);
        leftCol.setPrefWidth(420);
        leftCol.setMinWidth(380);

        VBox walletCard = new VBox(14);
        walletCard.getStyleClass().add("card");
        walletCard.setPadding(new Insets(22));

        HBox walletHeader = new HBox();
        walletHeader.setAlignment(Pos.CENTER_LEFT);
        Label wTitle = new Label("ChargeOn wallet");
        wTitle.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:16px;-fx-font-weight:bold;");
        Region whsp = new Region();
        HBox.setHgrow(whsp, Priority.ALWAYS);
        Label frTag = new Label("FR-EVP-08");
        frTag.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        walletHeader.getChildren().addAll(wTitle, whsp, frTag);

        HBox balanceRow = new HBox(2);
        balanceRow.setAlignment(Pos.BASELINE_LEFT);
        Label rupee = new Label("\u20B9" + Math.round(balance));
        rupee.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:36px;-fx-font-weight:bold;");
        Label paise = new Label(".00");
        paise.setStyle("-fx-text-fill:#64748b;-fx-font-size:20px;");
        balanceRow.getChildren().addAll(rupee, paise);

        Label autoTopup = new Label("Auto top-up \u20B91,000 when below \u20B9500");
        autoTopup.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

        double[] selectedAmount = { 1000 };
        HBox amountBtns = new HBox(10);
        Button a500 = new Button("\u20B9500");
        a500.getStyleClass().add("amount-btn");
        Button a1000 = new Button("\u20B91,000");
        a1000.getStyleClass().add("amount-btn");
        Button a2000 = new Button("\u20B92,000");
        a2000.getStyleClass().add("amount-btn");
        Button[] amountButtons = { a500, a1000, a2000 };
        double[] amountValues = { 500, 1000, 2000 };
        for (int i = 0; i < amountButtons.length; i++) {
            Button btn = amountButtons[i];
            double value = amountValues[i];
            btn.setStyle(value == selectedAmount[0] ? "-fx-border-color:#10b981;-fx-border-width:2;" : "");
            btn.setOnAction(e -> {
                selectedAmount[0] = value;
                for (Button b : amountButtons) {
                    b.setStyle("");
                }
                btn.setStyle("-fx-border-color:#10b981;-fx-border-width:2;");
            });
        }
        amountBtns.getChildren().addAll(a500, a1000, a2000);

        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill:#ef4444;-fx-font-size:11px;");
        errorLbl.setWrapText(true);
        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        Button addMoneyBtn = new Button("Add money");
        addMoneyBtn.getStyleClass().add("primary-btn");
        addMoneyBtn.setMaxWidth(Double.MAX_VALUE);
        addMoneyBtn.setPrefHeight(44);

        Label simNote = new Label("Payments are simulated in this release \u00B7 UPI / card / netbanking");
        simNote.setStyle("-fx-text-fill:#334155;-fx-font-size:10px;");
        simNote.setWrapText(true);

        walletCard.getChildren().addAll(walletHeader, balanceRow, autoTopup, amountBtns, errorLbl, addMoneyBtn,
                simNote);

        VBox methodsCard = new VBox(0);
        methodsCard.getStyleClass().add("card");
        methodsCard.setPadding(new Insets(22));

        Label mTitle = new Label("Payment methods");
        mTitle.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:16px;-fx-font-weight:bold;");
        VBox.setMargin(mTitle, new Insets(0, 0, 10, 0));

        methodsCard.getChildren().addAll(mTitle,
                paymentMethod("\u25C9", "UPI \u00B7 anuj@okhdfc", "Default for top-ups", "DEFAULT", "#10b981"),
                paymentMethod("\u25A0", "HDFC Credit \u2022\u2022\u2022\u2022 4821", "Expires 08/28", "SAVED",
                        "#64748b"),
                paymentMethod("\u25A0", "Netbanking \u00B7 ICICI", "Used once", "SAVED", "#64748b"));

        leftCol.getChildren().addAll(walletCard, methodsCard);

        VBox rightCol = new VBox(14);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        VBox txnCard = new VBox(0);
        txnCard.getStyleClass().add("card");
        txnCard.setPadding(new Insets(22));

        HBox txnHeader = new HBox();
        txnHeader.setAlignment(Pos.CENTER_LEFT);
        Label txnTitle = new Label("Transactions");
        txnTitle.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:18px;-fx-font-weight:bold;");
        Region thsp = new Region();
        HBox.setHgrow(thsp, Priority.ALWAYS);
        Label monthLbl = new Label(
                java.time.LocalDate.now(DateTimeUtil.INDIA).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
        monthLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
        txnHeader.getChildren().addAll(txnTitle, thsp, monthLbl);

        HBox colHeaders = new HBox();
        colHeaders.setPadding(new Insets(12, 0, 8, 0));
        colHeaders.getChildren().addAll(
                colLabel("DESCRIPTION", 200), colLabel("DATE", 100),
                colLabel("METHOD", 80), colLabel("AMOUNT", 80));

        VBox rows = new VBox(0);
        populateTransactions(rows, transactions);

        txnCard.getChildren().addAll(txnHeader, colHeaders, rows);
        rightCol.getChildren().add(txnCard);

        content.getChildren().addAll(leftCol, rightCol);

        addMoneyBtn.setOnAction(e -> {
            addMoneyBtn.setDisable(true);
            errorLbl.setVisible(false);
            errorLbl.setManaged(false);

            Thread adder = new Thread(() -> {
                boolean ok = walletController.addTopUp(selectedAmount[0], "UPI");
                Platform.runLater(() -> {
                    addMoneyBtn.setDisable(false);
                    if (ok) {
                        loadWallet(rupee, rows);
                    } else {
                        errorLbl.setText("Failed to add money. Please try again.");
                        errorLbl.setVisible(true);
                        errorLbl.setManaged(true);
                    }
                });
            });
            adder.setDaemon(true);
            adder.start();
        });

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color:transparent;");
        return scrollPane;
    }

    private void populateTransactions(VBox rows, List<WalletTransaction> transactions) {
        rows.getChildren().clear();
        if (transactions.isEmpty()) {
            rows.getChildren().add(new Label("No transactions this month.") {
                {
                    setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;-fx-padding:12 0 12 0;");
                }
            });
            return;
        }
        for (WalletTransaction t : transactions) {
            String desc = t.isTopUp() ? "Wallet top-up" : "Charging session";
            String id = t.isTopUp() || t.getBookingId().isEmpty() ? t.getId() : t.getBookingId();
            String date = formatDate(t.getCreatedAt());
            String amount = (t.isTopUp() ? "+ ₹" : "– ₹") + Math.round(t.getAmount());
            String color = t.isTopUp() ? "#10b981" : "#EF4444";
            rows.getChildren().add(txnRow(desc, id, date, t.getMethod(), amount, color));
        }
    }


    private void loadWallet(Label rupee, VBox rows) {
        Thread loader = new Thread(() -> {
            double balance = walletController.getMyBalance();
            List<WalletTransaction> transactions = walletController.getMyTransactionsThisMonth();
            Platform.runLater(() -> {
                rupee.setText("\u20B9" + Math.round(balance));
                rows.getChildren().clear();
                if (transactions.isEmpty()) {
                    rows.getChildren().add(new Label("No transactions this month.") {
                        {
                            setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;-fx-padding:12 0 12 0;");
                        }
                    });
                    return;
                }
                for (WalletTransaction t : transactions) {
                    String desc = t.isTopUp() ? "Wallet top-up" : "Charging session";
                    String id = t.isTopUp() || t.getBookingId().isEmpty() ? t.getId() : t.getBookingId();
                    String date = formatDate(t.getCreatedAt());
                    String amount = (t.isTopUp() ? "+ \u20B9" : "\u2013 \u20B9") + Math.round(t.getAmount());
                    String color = t.isTopUp() ? "#10b981" : "#EF4444";
                    rows.getChildren().add(txnRow(desc, id, date, t.getMethod(), amount, color));
                }
            });
        });
        loader.setDaemon(true);
        loader.start();
    }

    private static String formatDate(String iso) {
        return DateTimeUtil.date(iso);
    }

    private HBox paymentMethod(String icon, String name, String detail, String badge, String badgeColor) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(14, 0, 14, 0));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-border-color:transparent transparent #1e293b transparent;-fx-border-width:0 0 1 0;");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill:#10b981;-fx-font-size:16px;");

        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:13px;-fx-font-weight:bold;");
        Label detLbl = new Label(detail);
        detLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        textBox.getChildren().addAll(nameLbl, detLbl);

        Label badgeLbl = new Label(badge);
        badgeLbl.setStyle("-fx-text-fill:" + badgeColor + ";-fx-font-size:10px;-fx-font-weight:bold;");

        item.getChildren().addAll(iconLbl, textBox, badgeLbl);
        return item;
    }

    private Label colLabel(String text, double width) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        l.setPrefWidth(width);
        l.setMinWidth(width);
        return l;
    }

    private HBox txnRow(String desc, String id, String date, String method, String amount, String amountColor) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color:transparent transparent #1e293b transparent;-fx-border-width:0 0 1 0;");

        VBox descBox = new VBox(2);
        descBox.setPrefWidth(200);
        descBox.setMinWidth(200);
        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:13px;-fx-font-weight:bold;");
        Label idLbl = new Label(id);
        idLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        descBox.getChildren().addAll(descLbl, idLbl);

        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        dateLbl.setPrefWidth(100);
        dateLbl.setMinWidth(100);

        Label methodLbl = new Label(method);
        methodLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        methodLbl.setPrefWidth(80);
        methodLbl.setMinWidth(80);

        Label amountLbl = new Label(amount);
        amountLbl.setStyle("-fx-text-fill:" + amountColor + ";-fx-font-size:13px;-fx-font-weight:bold;");
        amountLbl.setPrefWidth(80);
        amountLbl.setMinWidth(80);
        amountLbl.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(descBox, dateLbl, methodLbl, amountLbl);
        return row;
    }
}
