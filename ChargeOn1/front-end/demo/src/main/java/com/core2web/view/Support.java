package com.core2web.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import com.core2web.controller.BusController;
import com.core2web.controller.TicketController;
import com.core2web.model.Ticket;

import static com.core2web.view.AdminLayout.*;

public class Support {

    public static void show(Stage stage) {
        AdminDashboard.goTo("Support");
    }

    private static final TicketController ticketController = new TicketController();
    private static final BusController busController = new BusController();

    private static final class QueueState {
        List<Ticket> tickets = new ArrayList<>();
        String filter = "OPEN";
    }

    static HBox buildMainContent() {
        HBox main = new HBox(16);
        main.setPadding(new Insets(16));

        QueueState state = new QueueState();
        state.tickets = ticketController.getAllTickets();

        VBox rows = new VBox(2);
        VBox detailHolder = new VBox();
        detailHolder.getChildren().add(buildTicketDetail(null));

        VBox center = new VBox(14);
        HBox.setHgrow(center, Priority.ALWAYS);
        center.getChildren().add(buildTicketQueue(rows, state, detailHolder));

        VBox rightCol = new VBox(14);
        rightCol.setPrefWidth(320);
        rightCol.getChildren().add(detailHolder);

        main.getChildren().addAll(center, rightCol);

        populateTicketRows(rows, state, detailHolder);

        return main;
    }

    private static VBox buildTicketQueue(VBox rows, QueueState state, VBox detailHolder) {
        VBox card = new VBox(12);
        card.getStyleClass().add("bookings-section");
        card.setPadding(new Insets(18));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Ticket queue");
        title.getStyleClass().add("booking-stage");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label tabAll = filterTab("All", false);
        Label tabOpen = filterTab("Open", true);

        Label tabResolved = filterTab("Resolved", false);
        Label[] allTabs = { tabAll, tabOpen, tabResolved };

        tabAll.setOnMouseClicked(e -> selectFilter(allTabs, tabAll, rows, "ALL", state, detailHolder));
        tabOpen.setOnMouseClicked(e -> selectFilter(allTabs, tabOpen, rows, "OPEN", state, detailHolder));

        tabResolved.setOnMouseClicked(e -> selectFilter(allTabs, tabResolved, rows, "RESOLVED", state, detailHolder));

        HBox tabs = new HBox(2, tabAll, tabOpen, tabResolved);
        tabs.getStyleClass().add("filter-tabs-container");

        header.getChildren().addAll(title, sp, tabs);

        HBox colHeaders = new HBox();
        colHeaders.setPadding(new Insets(10, 12, 10, 12));
        colHeaders.getChildren().addAll(
                colLabel("TICKET / CUSTOMER", 170), colLabel("ISSUE", 190), colLabel("RAISED", 90),
                colLabel("PRIORITY", 90), colLabel("STATUS", 100));

        card.getChildren().addAll(header, colHeaders, rows);
        return card;
    }

    private static Label filterTab(String text, boolean active) {
        Label tab = new Label(text);
        tab.getStyleClass().add(active ? "filter-tab-active" : "filter-tab");
        tab.setCursor(javafx.scene.Cursor.HAND);
        return tab;
    }

    private static void selectFilter(Label[] allTabs, Label active, VBox rows, String statusKey,
            QueueState state, VBox detailHolder) {
        for (Label t : allTabs) {
            t.getStyleClass().setAll(t == active ? "filter-tab-active" : "filter-tab");
        }
        state.filter = statusKey;
        populateTicketRows(rows, state, detailHolder);
    }

    private static void populateTicketRows(VBox rows, QueueState state, VBox detailHolder) {
        rows.getChildren().clear();
        for (Ticket t : state.tickets) {
            if (state.filter.equals("ALL") || t.getStatus().equals(state.filter)) {
                rows.getChildren().add(ticketRow(t, detailHolder));
            }
        }
        if (rows.getChildren().isEmpty()) {
            Label empty = new Label("No tickets in this view.");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;-fx-padding:16 0 16 0;");
            rows.getChildren().add(empty);
        }
    }

    private static HBox ticketRow(Ticket t, VBox detailHolder) {
        HBox row = new HBox();
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("booking-row");
        row.setCursor(javafx.scene.Cursor.HAND);

        VBox idBox = new VBox(2);
        idBox.setPrefWidth(170);
        idBox.setMinWidth(170);
        Label idLbl = new Label("#" + t.getId() + " · " + t.getCustomerName());
        idLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:12px;-fx-font-weight:bold;");
        idLbl.setWrapText(true);
        idBox.getChildren().add(idLbl);

        Label issueLbl = new Label(t.getSubject());
        issueLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        issueLbl.setWrapText(true);
        issueLbl.setPrefWidth(190);
        issueLbl.setMinWidth(190);

        Label raisedLbl = new Label(t.raisedLabel());
        raisedLbl.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:12px;");
        raisedLbl.setPrefWidth(90);
        raisedLbl.setMinWidth(90);

        row.getChildren().addAll(idBox, issueLbl, raisedLbl,
                statusBadge(t.getPriority(), t.priorityColor(), 90),
                statusBadge(t.statusDisplay(), t.statusColor(), 100));

        row.setOnMouseClicked(e ->
                detailHolder.getChildren().setAll(buildTicketDetail(t)));

        return row;
    }

    private static VBox buildTicketDetail(Ticket t) {
        VBox card = new VBox(12);
        card.getStyleClass().add("assignment-card");
        card.setPadding(new Insets(18));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label tag = new Label("SELECTED TICKET");
        tag.getStyleClass().add("new-assignment-tag");
        header.getChildren().add(tag);

        if (t == null) {
            Label placeholder = new Label("Select a ticket from the queue to see its details.");
            placeholder.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
            placeholder.setWrapText(true);
            card.getChildren().addAll(header, placeholder);
            return card;
        }

        Label title = new Label("#" + t.getId() + " · " + t.getCustomerName());
        title.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");

        String detailText = t.getSubject();
        if (t.getDescription() != null && !t.getDescription().isBlank()) {
            detailText += "\n" + t.getDescription();
        }
        Label details = new Label(detailText);
        details.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        details.setWrapText(true);

        VBox meta = new VBox(6);
        meta.getStyleClass().add("inner-card");
        meta.setPadding(new Insets(12));
        List<HBox> metaRows = new ArrayList<>();
        if (!t.getBookingId().isEmpty()) metaRows.add(metaRow("Booking", t.getBookingId()));
        if (!t.getBusId().isEmpty()) {
            metaRows.add(metaRow("Bus", busController.busCodeFor(t.getBusId(), "unknown bus")));
        }
        metaRows.add(metaRow("Priority", t.getPriority()));
        metaRows.add(metaRow("Status", t.statusDisplay()));
        meta.getChildren().addAll(metaRows);

        HBox buttons = new HBox(10);
        Button resolve = new Button("Mark resolved");
        resolve.getStyleClass().add("accept-btn");
        resolve.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(resolve, Priority.ALWAYS);
        resolve.setDisable("RESOLVED".equals(t.getStatus()));

        Button escalate = new Button("Escalate");
        escalate.getStyleClass().add("decline-btn");
        escalate.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(escalate, Priority.ALWAYS);
        escalate.setDisable(t.isHighPriority());

        resolve.setOnAction(e -> {
            resolve.setDisable(true);
            escalate.setDisable(true);
            Thread updater = new Thread(() -> {
                boolean ok = ticketController.updateStatus(t.getId(), "RESOLVED");
                Platform.runLater(() -> {
                    if (ok) {
                        t.setStatus("RESOLVED");
                    }
                    AdminDashboard.goTo("Support");
                });
            });
            updater.setDaemon(true);
            updater.start();
        });

        escalate.setOnAction(e -> {
            resolve.setDisable(true);
            escalate.setDisable(true);
            Thread updater = new Thread(() -> {
                boolean ok = ticketController.escalate(t.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        t.setPriority("HIGH");
                    }
                    AdminDashboard.goTo("Support");
                });
            });
            updater.setDaemon(true);
            updater.start();
        });

        buttons.getChildren().addAll(resolve, escalate);

        card.getChildren().addAll(header, title, details, meta, buttons);
        return card;
    }

    private static HBox metaRow(String key, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label k = new Label(key);
        k.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:11px;-fx-font-weight:bold;");
        row.getChildren().addAll(k, sp, v);
        return row;
    }
}
