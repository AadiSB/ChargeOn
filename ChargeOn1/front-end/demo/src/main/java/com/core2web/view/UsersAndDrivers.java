
// package com.core2web.view;

// import java.time.Instant;
// import java.time.LocalTime;
// import java.time.ZoneId;
// import java.time.ZonedDateTime;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.ButtonType;
// import javafx.scene.control.ComboBox;
// import javafx.scene.control.Dialog;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.Tooltip;
// import javafx.util.StringConverter;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle;
// import javafx.stage.Stage;

// import com.core2web.controller.BusController;
// import com.core2web.controller.DriverController;
// import com.core2web.controller.OwnerController;
// import com.core2web.controller.TicketController;
// import com.core2web.controller.VehicleController;
// import com.core2web.model.Bus;
// import com.core2web.model.Driver;
// import com.core2web.model.Owner;
// import com.core2web.model.Ticket;

// import static com.core2web.view.AdminLayout.*;

// public class UsersAndDrivers {

//     private static final DriverController driverController =
//             new DriverController();

//     private static final OwnerController ownerController =
//             new OwnerController();

//     private static final VehicleController vehicleController =
//             new VehicleController();

//     private static final BusController busController =
//             new BusController();

//     private static final TicketController ticketController =
//             new TicketController();

//     /*
//      * India time zone.
//      */
//     private static final ZoneId INDIA_ZONE =
//             ZoneId.of("Asia/Kolkata");

//     public static void show(Stage stage) {
//         AdminDashboard.goTo("Users & Drivers");
//     }

//     static ScrollPane buildMainContent() {

//         VBox content = new VBox(16);

//         content.setPadding(
//                 new Insets(16)
//         );

//         /*
//          * Read current data from Firebase through the existing controllers.
//          */
//         List<Driver> drivers =
//                 driverController.getAllDrivers();

//         List<Owner> owners =
//                 ownerController.getAllOwners();

//         int[] vehicleCounts =
//                 new int[owners.size()];

//         for (int i = 0;
//              i < owners.size();
//              i++) {

//             vehicleCounts[i] =
//                     vehicleController.getVehicleCountForOwner(
//                             owners.get(i).getUid()
//                     );
//         }

//         /*
//          * KPI row.
//          *
//          * This now calculates:
//          *
//          * - Total drivers dynamically
//          * - On shift now dynamically
//          * - Total customers dynamically
//          * - Pending approvals dynamically from OPEN tickets
//          */
//         HBox kpiRow =
//                 buildKpiRow(
//                         drivers,
//                         owners
//                 );

//         /*
//          * One fleet read, shared by the roster column
//          * and the integrity panel.
//          */
//         FleetIndex index =
//                 new FleetIndex(
//                         busController.getAllBuses()
//                 );

//         VBox driverRows =
//                 new VBox(2);

//         populateDriverRows(
//                 driverRows,
//                 drivers,
//                 index
//         );

//         VBox customerRows =
//                 new VBox(2);

//         populateCustomerRows(
//                 customerRows,
//                 owners,
//                 vehicleCounts
//         );

//         content.getChildren().addAll(
//                 kpiRow,
//                 buildIntegrityPanel(
//                         drivers,
//                         index
//                 ),
//                 buildDriverRosterCard(
//                         driverRows
//                 ),
//                 buildCustomerRosterCard(
//                         customerRows
//                 )
//         );

//         ScrollPane sp =
//                 new ScrollPane(content);

//         sp.setFitToWidth(true);

//         sp.getStyleClass().add(
//                 "scroll-pane"
//         );

//         return sp;
//     }

//     private static Label loadingLabel(
//             String text) {

//         Label l =
//                 new Label(text);

//         l.setStyle(
//                 "-fx-text-fill:#64748b;" +
//                 "-fx-font-size:12px;"
//         );

//         return l;
//     }

//     private static void openReassignDialog() {

//         List<Driver> drivers =
//                 driverController.getAllDrivers();

//         List<Bus> buses =
//                 busController.getAllBuses();

//         if (drivers.isEmpty()
//                 || buses.isEmpty()) {

//             Alert none =
//                     new Alert(
//                             Alert.AlertType.INFORMATION
//                     );

//             none.setHeaderText(null);

//             none.setContentText(
//                     drivers.isEmpty()
//                             ? "There are no drivers to reassign yet."
//                             : "There are no buses in the fleet yet."
//             );

//             none.showAndWait();

//             return;
//         }

//         Map<String, String> holderByBusId =
//                 new HashMap<>();

//         for (Bus bus : buses) {

//             if (BusController.hasDriver(bus)) {

//                 Driver holder = null;

//                 for (Driver d : drivers) {

//                     if (d.getUid().equals(
//                             bus.getAssignedDriverId()
//                     )) {

//                         holder = d;
//                         break;
//                     }
//                 }

//                 holderByBusId.put(
//                         bus.getId(),
//                         holder == null
//                                 ? "unknown driver"
//                                 : nameOf(holder)
//                 );
//             }
//         }

//         ComboBox<Driver> driverCombo =
//                 new ComboBox<>();

//         driverCombo.setMaxWidth(
//                 Double.MAX_VALUE
//         );

//         driverCombo.setConverter(
//                 new StringConverter<Driver>() {

//                     @Override
//                     public String toString(
//                             Driver d) {

//                         return d == null
//                                 ? ""
//                                 : nameOf(d)
//                                         + " · "
//                                         + d.getDepot();
//                     }

//                     @Override
//                     public Driver fromString(
//                             String s) {

//                         return null;
//                     }
//                 }
//         );

//         driverCombo.getItems().setAll(
//                 drivers
//         );

//         driverCombo.setPromptText(
//                 "Select driver"
//         );

//         ComboBox<Bus> busCombo =
//                 new ComboBox<>();

//         busCombo.setMaxWidth(
//                 Double.MAX_VALUE
//         );

//         busCombo.setConverter(
//                 new StringConverter<Bus>() {

//                     @Override
//                     public String toString(
//                             Bus b) {

//                         if (b == null) {
//                             return "";
//                         }

//                         String holder =
//                                 holderByBusId.get(
//                                         b.getId()
//                                 );

//                         return b.getBusCode()
//                                 + " · "
//                                 + b.getDepot()
//                                 + (
//                                         holder == null
//                                                 ? ""
//                                                 : "  (currently "
//                                                         + holder
//                                                         + ")"
//                                 );
//                     }

//                     @Override
//                     public Bus fromString(
//                             String s) {

//                         return null;
//                     }
//                 }
//         );

//         busCombo.getItems().setAll(
//                 buses
//         );

//         busCombo.setPromptText(
//                 "Select bus"
//         );

//         Label warning =
//                 new Label();

//         warning.setStyle(
//                 "-fx-text-fill:#f59e0b;" +
//                 "-fx-font-size:11px;"
//         );

//         warning.setWrapText(true);
//         warning.setVisible(false);
//         warning.setManaged(false);

//         busCombo.valueProperty().addListener(
//                 (obs, old, bus) -> {

//                     String holder =
//                             bus == null
//                                     ? null
//                                     : holderByBusId.get(
//                                             bus.getId()
//                                     );

//                     boolean occupied =
//                             holder != null;

//                     if (occupied) {

//                         warning.setText(
//                                 bus.getBusCode()
//                                         + " is currently assigned to "
//                                         + holder
//                                         + ". Reassigning it will leave them with no bus."
//                         );
//                     }

//                     warning.setVisible(
//                             occupied
//                     );

//                     warning.setManaged(
//                             occupied
//                     );
//                 }
//         );

//         VBox form =
//                 new VBox(
//                         10,
//                         new Label("Driver"),
//                         driverCombo,
//                         new Label("Bus"),
//                         busCombo,
//                         warning
//                 );

//         form.setPadding(
//                 new Insets(12)
//         );

//         Dialog<ButtonType> dialog =
//                 new Dialog<>();

//         dialog.setTitle(
//                 "Reassign bus"
//         );

//         dialog.setHeaderText(
//                 "Move a driver to a different bus"
//         );

//         dialog.getDialogPane().setContent(
//                 form
//         );

//         dialog.getDialogPane()
//                 .getButtonTypes()
//                 .addAll(
//                         ButtonType.CANCEL,
//                         ButtonType.OK
//                 );

//         if (dialog.showAndWait()
//                 .filter(
//                         b -> b == ButtonType.OK
//                 )
//                 .isEmpty()) {

//             return;
//         }

//         Driver driver =
//                 driverCombo.getValue();

//         Bus bus =
//                 busCombo.getValue();

//         if (driver == null
//                 || bus == null) {

//             Alert incomplete =
//                     new Alert(
//                             Alert.AlertType.WARNING
//                     );

//             incomplete.setHeaderText(
//                     null
//             );

//             incomplete.setContentText(
//                     "Pick both a driver and a bus."
//             );

//             incomplete.showAndWait();

//             return;
//         }

//         Thread worker =
//                 new Thread(() -> {

//                     boolean ok =
//                             driverController.assignBusToDriver(
//                                     driver.getUid(),
//                                     bus.getId()
//                             );

//                     Platform.runLater(
//                             () -> {

//                                 Alert result =
//                                         new Alert(
//                                                 ok
//                                                         ? Alert.AlertType.INFORMATION
//                                                         : Alert.AlertType.ERROR
//                                         );

//                                 result.setHeaderText(
//                                         null
//                                 );

//                                 result.setContentText(
//                                         ok
//                                                 ? nameOf(driver)
//                                                         + " is now assigned to "
//                                                         + bus.getBusCode()
//                                                         + "."
//                                                 : "Could not reassign the bus. Check the app output for the reason."
//                                 );

//                                 result.showAndWait();

//                                 if (ok) {

//                                     AdminDashboard.goTo(
//                                             "Users & Drivers"
//                                     );
//                                 }
//                             }
//                     );
//                 });

//         worker.setDaemon(true);

//         worker.start();
//     }

//     private static void confirmAndRepairLinks(
//             Button trigger) {

//         Alert confirm =
//                 new Alert(
//                         Alert.AlertType.CONFIRMATION
//                 );

//         confirm.setTitle(
//                 "Repair driver–bus links"
//         );

//         confirm.setHeaderText(
//                 "Rewrite mismatched driver assignments?"
//         );

//         confirm.setContentText(
//                 "For every bus that names a driver, that driver's assignedBusId will be set to "
//                         + "the bus's document ID. Buses are treated as correct.\n\n"
//                         + "Ambiguous cases (two buses claiming one driver, a driver claiming "
//                         + "someone else's bus) are reported, not changed."
//         );

//         if (confirm.showAndWait()
//                 .filter(
//                         b -> b == ButtonType.OK
//                 )
//                 .isEmpty()) {

//             return;
//         }

//         trigger.setDisable(
//                 true
//         );

//         trigger.setText(
//                 "Repairing…"
//         );

//         Thread worker =
//                 new Thread(() -> {

//                     DriverController.LinkRepairReport report =
//                             driverController.repairBusLinks();

//                     Platform.runLater(
//                             () -> {

//                                 trigger.setDisable(
//                                         false
//                                 );

//                                 trigger.setText(
//                                         "Repair links"
//                                 );

//                                 Alert result =
//                                         new Alert(
//                                                 report.changedAnything()
//                                                         ? Alert.AlertType.INFORMATION
//                                                         : Alert.AlertType.WARNING
//                                         );

//                                 result.setTitle(
//                                         "Repair driver–bus links"
//                                 );

//                                 result.setHeaderText(
//                                         report.changedAnything()
//                                                 ? "Repaired "
//                                                         + report.repaired.size()
//                                                         + " link(s)."
//                                                 : "No links were changed."
//                                 );

//                                 TextArea detail =
//                                         new TextArea(
//                                                 report.summary()
//                                         );

//                                 detail.setEditable(
//                                         false
//                                 );

//                                 detail.setWrapText(
//                                         true
//                                 );

//                                 detail.setPrefRowCount(
//                                         12
//                                 );

//                                 detail.setPrefColumnCount(
//                                         60
//                                 );

//                                 result.getDialogPane()
//                                         .setContent(
//                                                 detail
//                                         );

//                                 result.showAndWait();

//                                 if (report.changedAnything()) {

//                                     AdminDashboard.goTo(
//                                             "Users & Drivers"
//                                     );
//                                 }
//                             }
//                     );
//                 });

//         worker.setDaemon(
//                 true
//         );

//         worker.start();
//     }

//     private static VBox buildIntegrityPanel(
//             List<Driver> drivers,
//             FleetIndex index) {

//         Map<LinkState, List<String>> byState =
//                 new LinkedHashMap<>();

//         for (Driver d : drivers) {

//             LinkState state =
//                     linkStateOf(
//                             d,
//                             index
//                     );

//             if (state == LinkState.OK
//                     || state == LinkState.UNASSIGNED) {

//                 continue;
//             }

//             byState
//                     .computeIfAbsent(
//                             state,
//                             k -> new ArrayList<>()
//                     )
//                     .add(
//                             nameOf(d)
//                                     + " ("
//                                     + busCodeOf(
//                                             d,
//                                             index
//                                     )
//                                     + ")"
//                     );
//         }

//         Set<String> driverUids =
//                 new HashSet<>();

//         for (Driver d : drivers) {

//             driverUids.add(
//                     d.getUid()
//             );
//         }

//         List<String> orphanBuses =
//                 new ArrayList<>();

//         int linkedBuses = 0;

//         for (Bus bus : index.buses) {

//             if (!BusController.hasDriver(bus)) {
//                 continue;
//             }

//             linkedBuses++;

//             if (!driverUids.contains(
//                     bus.getAssignedDriverId()
//             )) {

//                 orphanBuses.add(
//                         bus.getBusCode()
//                                 + " -> driver "
//                                 + bus.getAssignedDriverId()
//                 );
//             }
//         }

//         int problems =
//                 orphanBuses.size();

//         for (List<String> names :
//                 byState.values()) {

//             problems += names.size();
//         }

//         VBox card =
//                 new VBox(8);

//         card.getStyleClass().add(
//                 "card"
//         );

//         card.setPadding(
//                 new Insets(16)
//         );

//         HBox header =
//                 new HBox(10);

//         header.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         Circle dot =
//                 new Circle(
//                         5,
//                         Color.web(
//                                 problems == 0
//                                         ? "#10b981"
//                                         : "#f59e0b"
//                         )
//                 );

//         Label title =
//                 new Label(
//                         "Link integrity"
//                 );

//         title.getStyleClass().add(
//                 "section-title"
//         );

//         Region sp =
//                 new Region();

//         HBox.setHgrow(
//                 sp,
//                 Priority.ALWAYS
//         );

//         Label count =
//                 new Label(
//                         drivers.size()
//                                 + " drivers · "
//                                 + index.buses.size()
//                                 + " buses · "
//                                 + linkedBuses
//                                 + " linked · "
//                                 + (
//                                         problems == 0
//                                                 ? "0 problems"
//                                                 : problems
//                                                         + " problem(s)"
//                                 )
//                 );

//         count.getStyleClass().add(
//                 "small-muted"
//         );

//         header.getChildren().addAll(
//                 dot,
//                 title,
//                 sp,
//                 count
//         );

//         card.getChildren().add(
//                 header
//         );

//         if (problems == 0) {

//             Label ok =
//                     new Label(
//                             "Every driver–bus link agrees in both directions."
//                     );

//             ok.setStyle(
//                     "-fx-text-fill:#10b981;" +
//                     "-fx-font-size:12px;"
//             );

//             card.getChildren().add(
//                     ok
//             );

//             return card;
//         }

//         for (Map.Entry<LinkState, List<String>> entry :
//                 byState.entrySet()) {

//             card.getChildren().add(
//                     problemLine(
//                             entry.getKey().note,
//                             String.join(
//                                     ", ",
//                                     entry.getValue()
//                             )
//                     )
//             );
//         }

//         if (!orphanBuses.isEmpty()) {

//             card.getChildren().add(
//                     problemLine(
//                             "bus assigned to a driver with no Driver document",
//                             String.join(
//                                     ", ",
//                                     orphanBuses
//                             )
//                     )
//             );
//         }

//         Label hint =
//                 new Label(
//                         "\"Repair links\" below fixes the one-sided and "
//                                 + "legacy-code cases. Mismatches and orphans need a decision on the bus side first."
//                 );

//         hint.setStyle(
//                 "-fx-text-fill:#64748b;" +
//                 "-fx-font-size:11px;"
//         );

//         hint.setWrapText(
//                 true
//         );

//         card.getChildren().add(
//                 hint
//         );

//         return card;
//     }

//     private static Label problemLine(
//             String note,
//             String who) {

//         Label l =
//                 new Label(
//                         "⚠  "
//                                 + note
//                                 + ": "
//                                 + who
//                 );

//         l.setStyle(
//                 "-fx-text-fill:#f59e0b;" +
//                 "-fx-font-size:12px;"
//         );

//         l.setWrapText(
//                 true
//         );

//         return l;
//     }

//     private static String nameOf(
//             Driver d) {

//         return d.getName() == null
//                 || d.getName().isEmpty()
//                         ? d.getUid()
//                         : d.getName();
//     }

//     /*
//      * ============================================================
//      * KPI SECTION
//      * ============================================================
//      */

//     private static HBox buildKpiRow(
//             List<Driver> drivers,
//             List<Owner> owners) {

//         /*
//          * Dynamic active driver count.
//          */
//         long activeDrivers =
//                 drivers.stream()
//                         .filter(
//                                 d -> "active".equalsIgnoreCase(
//                                         d.getStatus()
//                                 )
//                         )
//                         .count();

//         /*
//          * Dynamic current-shift driver count.
//          *
//          * This checks:
//          *
//          * 1. Driver status = active
//          * 2. Driver shift = current India shift
//          */
//         long onShiftNow =
//                 drivers.stream()
//                         .filter(
//                                 UsersAndDrivers::isCurrentlyOnShift
//                         )
//                         .count();

//         /*
//          * Get current shift name.
//          *
//          * Example:
//          *
//          * 10:30 -> Morning
//          * 18:00 -> Afternoon
//          * 23:30 -> Night
//          */
//         String currentShift =
//                 getCurrentShiftName();

//         /*
//          * Dynamic customer count.
//          */
//         long newThisMonth =
//                 owners.stream()
//                         .filter(
//                                 UsersAndDrivers::createdThisMonth
//                         )
//                         .count();

//         /*
//          * Dynamic pending request count.
//          *
//          * Existing Support uses:
//          *
//          * OPEN
//          * RESOLVED
//          *
//          * Therefore OPEN tickets are the pending requests.
//          */
//         int pendingCount =
//                 getPendingTicketCount();

//         /*
//          * Pending approvals card.
//          *
//          * We are using the existing Support queue's OPEN tickets.
//          * No fake number is used.
//          */
//         VBox pendingApprovals =
//                 statCard(
//                         "Pending approvals",
//                         String.valueOf(
//                                 pendingCount
//                         ),
//                         "#f59e0b",
//                         pendingCount == 1
//                                 ? "1 open support request"
//                                 : pendingCount
//                                         + " open support requests"
//                 );

//         pendingApprovals.setStyle(
//                 "-fx-cursor: hand;"
//         );

//         /*
//          * Existing KPI row layout is preserved.
//          */
//         HBox row =
//                 new HBox(16);

//         row.getChildren().setAll(

//                 statCard(
//                         "Total drivers",
//                         String.valueOf(
//                                 drivers.size()
//                         ),
//                         "#f8fafc",
//                         activeDrivers
//                                 + " active"
//                 ),

//                 statCard(
//                         "On shift now",
//                         String.valueOf(
//                                 onShiftNow
//                         ),
//                         "#10b981",
//                         currentShift
//                                 + " shift"
//                 ),

//                 statCard(
//                         "Total customers",
//                         String.valueOf(
//                                 owners.size()
//                         ),
//                         "#f8fafc",
//                         newThisMonth
//                                 + " new this month"
//                 ),

//                 pendingApprovals
//         );

//         return row;
//     }

//     /*
//      * ============================================================
//      * CURRENT SHIFT
//      * ============================================================
//      *
//      * Shift schedule:
//      *
//      * Morning   = 06:00 - 14:00
//      * Afternoon = 14:00 - 22:00
//      * Night     = 22:00 - 06:00
//      *
//      * Time zone:
//      * Asia/Kolkata
//      */

//     private static String getCurrentShiftName() {

//         LocalTime now =
//                 ZonedDateTime.now(
//                         INDIA_ZONE
//                 ).toLocalTime();

//         LocalTime morningStart =
//                 LocalTime.of(
//                         6,
//                         0
//                 );

//         LocalTime afternoonStart =
//                 LocalTime.of(
//                         14,
//                         0
//                 );

//         LocalTime nightStart =
//                 LocalTime.of(
//                         22,
//                         0
//                 );

//         if (!now.isBefore(
//                 morningStart
//         )
//                 && now.isBefore(
//                         afternoonStart
//                 )) {

//             return "Morning";
//         }

//         if (!now.isBefore(
//                 afternoonStart
//         )
//                 && now.isBefore(
//                         nightStart
//                 )) {

//             return "Afternoon";
//         }

//         /*
//          * 22:00 - 23:59
//          * OR
//          * 00:00 - 05:59
//          */
//         return "Night";
//     }

//     /*
//      * Checks whether this particular driver
//      * is currently on their Firebase-defined shift.
//      */
//     private static boolean isCurrentlyOnShift(
//             Driver driver) {

//         if (driver == null) {
//             return false;
//         }

//         /*
//          * Driver must be active.
//          */
//         if (!"active".equalsIgnoreCase(
//                 driver.getStatus()
//         )) {

//             return false;
//         }

//         String driverShift =
//                 driver.getShift();

//         if (driverShift == null
//                 || driverShift.trim().isEmpty()) {

//             return false;
//         }

//         String currentShift =
//                 getCurrentShiftName();

//         /*
//          * Compare Firebase shift with
//          * current India shift.
//          *
//          * Example:
//          *
//          * Firebase:
//          * shift = Morning
//          *
//          * Current:
//          * Morning
//          *
//          * -> true
//          */
//         return currentShift.equalsIgnoreCase(
//                 driverShift.trim()
//         );
//     }

//     /*
//      * ============================================================
//      * PENDING SUPPORT REQUESTS
//      * ============================================================
//      *
//      * Support.java shows:
//      *
//      * OPEN
//      * RESOLVED
//      *
//      * Therefore OPEN tickets are currently pending.
//      */

//     private static int getPendingTicketCount() {

//         try {

//             List<Ticket> tickets =
//                     ticketController.getAllTickets();

//             if (tickets == null
//                     || tickets.isEmpty()) {

//                 return 0;
//             }

//             int count = 0;

//             for (Ticket ticket : tickets) {

//                 if (ticket == null) {
//                     continue;
//                 }

//                 String status =
//                         ticket.getStatus();

//                 if (status != null
//                         && "OPEN".equalsIgnoreCase(
//                                 status.trim()
//                         )) {

//                     count++;
//                 }
//             }

//             return count;

//         } catch (Exception e) {

//             /*
//              * Do not let a Support/Firebase read failure
//              * crash the Users & Drivers dashboard.
//              */
//             e.printStackTrace();

//             return 0;
//         }
//     }

//     private static boolean createdThisMonth(
//             Owner owner) {

//         try {

//             ZonedDateTime createdAt =
//                     Instant.parse(
//                             owner.getCreatedAt()
//                     ).atZone(
//                             ZoneId.of("UTC")
//                     );

//             ZonedDateTime now =
//                     ZonedDateTime.now(
//                             ZoneId.of("UTC")
//                     );

//             return createdAt.getYear()
//                     == now.getYear()
//                     && createdAt.getMonth()
//                     == now.getMonth();

//         } catch (Exception e) {

//             return false;
//         }
//     }

//     /*
//      * ============================================================
//      * FLEET INDEX
//      * ============================================================
//      */

//     private static final class FleetIndex {

//         final List<Bus> buses;

//         final Map<String, String> busCodeByKey =
//                 new HashMap<>();

//         final Map<String, String> driverToBusCode =
//                 new HashMap<>();

//         FleetIndex(
//                 List<Bus> buses) {

//             this.buses =
//                     buses;

//             for (Bus bus :
//                     buses) {

//                 busCodeByKey.put(
//                         bus.getId(),
//                         bus.getBusCode()
//                 );

//                 if (bus.getBusCode() != null
//                         && !bus.getBusCode().isEmpty()) {

//                     busCodeByKey.put(
//                             bus.getBusCode(),
//                             bus.getBusCode()
//                     );
//                 }

//                 if (BusController.hasDriver(
//                         bus
//                 )) {

//                     driverToBusCode.put(
//                             bus.getAssignedDriverId(),
//                             bus.getBusCode()
//                     );
//                 }
//             }
//         }
//     }

//     private enum LinkState {

//         OK("linked"),

//         UNASSIGNED("no bus"),

//         LEGACY_CODE(
//                 "assignedBusId holds the busCode, not the document ID"
//         ),

//         NOT_LINKED_BACK(
//                 "only one side of the link is set"
//         ),

//         MISMATCH(
//                 "driver and bus name different buses"
//         ),

//         UNKNOWN_BUS(
//                 "assignedBusId names a bus that does not exist"
//         );

//         final String note;

//         LinkState(
//                 String note) {

//             this.note =
//                     note;
//         }
//     }

//     private static LinkState linkStateOf(
//             Driver d,
//             FleetIndex index) {

//         String declared =
//                 d.getAssignedBusId();

//         boolean declaredBlank =
//                 declared == null
//                         || declared.isEmpty()
//                         || declared.equals(
//                                 "null"
//                         );

//         String claimedCode =
//                 index.driverToBusCode.get(
//                         d.getUid()
//                 );

//         if (claimedCode != null) {

//             if (declaredBlank) {
//                 return LinkState.NOT_LINKED_BACK;
//             }

//             if (declared.equals(
//                     claimedCode
//             )) {

//                 return LinkState.LEGACY_CODE;
//             }

//             return claimedCode.equals(
//                     index.busCodeByKey.get(
//                             declared
//                     )
//             )
//                     ? LinkState.OK
//                     : LinkState.MISMATCH;
//         }

//         if (declaredBlank) {
//             return LinkState.UNASSIGNED;
//         }

//         return index.busCodeByKey.containsKey(
//                 declared
//         )
//                 ? LinkState.NOT_LINKED_BACK
//                 : LinkState.UNKNOWN_BUS;
//     }

//     private static String busCodeOf(
//             Driver d,
//             FleetIndex index) {

//         String claimed =
//                 index.driverToBusCode.get(
//                         d.getUid()
//                 );

//         if (claimed != null) {
//             return claimed;
//         }

//         return BusController.busCodeLabel(
//                 d.getAssignedBusId(),
//                 index.busCodeByKey,
//                 ""
//         );
//     }

//     /*
//      * ============================================================
//      * DRIVER ROSTER
//      * ============================================================
//      */

//     private static void populateDriverRows(
//             VBox driverRows,
//             List<Driver> drivers,
//             FleetIndex index) {

//         driverRows.getChildren().clear();

//         if (drivers.isEmpty()) {

//             driverRows.getChildren().add(
//                     loadingLabel(
//                             "No drivers registered yet."
//                     )
//             );

//             return;
//         }

//         for (Driver d :
//                 drivers) {

//             boolean active =
//                     "active".equalsIgnoreCase(
//                             d.getStatus()
//                     );

//             driverRows.getChildren().add(
//                     driverRow(
//                             d.getName(),
//                             d.getUid(),
//                             d.getDepot(),
//                             busLabel(
//                                     d,
//                                     index
//                             ),
//                             d.getShift(),
//                             d.getStatus() == null
//                                     ? ""
//                                     : d.getStatus()
//                                             .toUpperCase(),
//                             active
//                                     ? "#10b981"
//                                     : "#ef4444"
//                     )
//             );
//         }
//     }

//     private static String busLabel(
//             Driver d,
//             FleetIndex index) {

//         LinkState state =
//                 linkStateOf(
//                         d,
//                         index
//                 );

//         String code =
//                 busCodeOf(
//                         d,
//                         index
//                 );

//         switch (state) {

//             case OK:
//                 return code;

//             case UNASSIGNED:
//                 return "— unassigned";

//             case LEGACY_CODE:
//                 return code
//                         + "  ⚠ legacy code";

//             case NOT_LINKED_BACK:
//                 return code
//                         + "  ⚠ not linked back";

//             case MISMATCH:
//                 return code
//                         + "  ⚠ link mismatch";

//             default:
//                 return "⚠ unknown bus";
//         }
//     }

//     /*
//      * ============================================================
//      * CUSTOMER ROSTER
//      * ============================================================
//      */

//     private static void populateCustomerRows(
//             VBox customerRows,
//             List<Owner> owners,
//             int[] vehicleCounts) {

//         customerRows.getChildren().clear();

//         if (owners.isEmpty()) {

//             customerRows.getChildren().add(
//                     loadingLabel(
//                             "No customers registered yet."
//                     )
//             );

//             return;
//         }

//         for (int i = 0;
//              i < owners.size();
//              i++) {

//             Owner o =
//                     owners.get(i);

//             boolean active =
//                     "active".equalsIgnoreCase(
//                             o.getStatus()
//                     );

//             customerRows.getChildren().add(
//                     customerRow(
//                             o.getName(),
//                             o.getUid(),
//                             vehicleCounts[i]
//                                     + (
//                                             vehicleCounts[i] == 1
//                                                     ? " EV"
//                                                     : " EVs"
//                                     ),
//                             o.getPhone(),
//                             memberSinceLabel(
//                                     o.getCreatedAt()
//                             ),
//                             o.getStatus() == null
//                                     ? ""
//                                     : o.getStatus()
//                                             .toUpperCase(),
//                             active
//                                     ? "#10b981"
//                                     : "#6b7280"
//                     )
//             );
//         }
//     }

//     private static String memberSinceLabel(
//             String createdAt) {

//         try {

//             Instant instant =
//                     Instant.parse(
//                             createdAt
//                     );

//             return instant.atZone(
//                     ZoneId.of("UTC")
//             ).toLocalDate().toString();

//         } catch (Exception e) {

//             return "—";
//         }
//     }

//     /*
//      * ============================================================
//      * DRIVER ROSTER CARD
//      * ============================================================
//      */

//     private static VBox buildDriverRosterCard(
//             VBox rows) {

//         VBox card =
//                 new VBox(12);

//         card.getStyleClass().add(
//                 "bookings-section"
//         );

//         card.setPadding(
//                 new Insets(18)
//         );

//         HBox header =
//                 new HBox(10);

//         header.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         Label title =
//                 new Label(
//                         "Driver roster"
//                 );

//         title.getStyleClass().add(
//                 "booking-stage"
//         );

//         Region sp =
//                 new Region();

//         HBox.setHgrow(
//                 sp,
//                 Priority.ALWAYS
//         );

//         Button reassign =
//                 new Button(
//                         "Reassign bus"
//                 );

//         reassign.getStyleClass().add(
//                 "secondary-btn"
//         );

//         reassign.setTooltip(
//                 new Tooltip(
//                         "Move an existing driver to a different bus, updating both sides of the link."
//                 )
//         );

//         reassign.setOnAction(
//                 e -> openReassignDialog()
//         );

//         Button repairLinks =
//                 new Button(
//                         "Repair links"
//                 );

//         repairLinks.getStyleClass().add(
//                 "secondary-btn"
//         );

//         repairLinks.setTooltip(
//                 new Tooltip(
//                         "Rewrites driver assignedBusId values that disagree with the bus they're assigned to. Bus.assignedDriverId is treated as correct."
//                 )
//         );

//         repairLinks.setOnAction(
//                 e -> confirmAndRepairLinks(
//                         repairLinks
//                 )
//         );

//         Button addDriver =
//                 new Button(
//                         "+ Add driver"
//                 );

//         addDriver.getStyleClass().add(
//                 "primary-btn"
//         );

//         addDriver.setOnAction(
//                 e -> {

//                     AddUserAccount addDriverPage =
//                             new AddUserAccount(
//                                     AdminDashboard.window,
//                                     true
//                             );

//                     AdminDashboard.heading.setText(
//                             "Add Driver"
//                     );

//                     AdminDashboard.subheading.setText(
//                             "Register a new driver account"
//                     );

//                     AdminDashboard.middleBox
//                             .getChildren()
//                             .setAll(
//                                     addDriverPage.getMainContent()
//                             );

//                     AdminDashboard.window.setTitle(
//                             "ChargeOn · Add Driver"
//                     );
//                 }
//         );

//         header.getChildren().addAll(
//                 title,
//                 sp,
//                 reassign,
//                 repairLinks,
//                 addDriver
//         );

//         HBox colHeaders =
//                 new HBox();

//         colHeaders.setPadding(
//                 new Insets(
//                         10,
//                         12,
//                         10,
//                         12
//                 )
//         );

//         colHeaders.getChildren().addAll(
//                 colLabel(
//                         "DRIVER",
//                         170
//                 ),
//                 colLabel(
//                         "DEPOT",
//                         130
//                 ),
//                 colLabel(
//                         "ASSIGNED BUS",
//                         120
//                 ),
//                 colLabel(
//                         "SHIFT",
//                         150
//                 ),
//                 colLabel(
//                         "STATUS",
//                         100
//                 )
//         );

//         card.getChildren().addAll(
//                 header,
//                 colHeaders,
//                 rows
//         );

//         return card;
//     }

//     private static HBox driverRow(
//             String name,
//             String id,
//             String depot,
//             String bus,
//             String shift,
//             String status,
//             String color) {

//         HBox row =
//                 new HBox(10);

//         row.setPadding(
//                 new Insets(
//                         11,
//                         12,
//                         11,
//                         12
//                 )
//         );

//         row.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         row.getStyleClass().add(
//                 "booking-row"
//         );

//         HBox nameBox =
//                 new HBox(8);

//         nameBox.setPrefWidth(
//                 170
//         );

//         nameBox.setMinWidth(
//                 170
//         );

//         nameBox.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         Circle avatarDot =
//                 new Circle(
//                         4,
//                         Color.web(color)
//                 );

//         VBox nameCol =
//                 new VBox(2);

//         Label nameLbl =
//                 new Label(name);

//         nameLbl.setStyle(
//                 "-fx-text-fill:#f8fafc;" +
//                 "-fx-font-size:12px;" +
//                 "-fx-font-weight:bold;"
//         );

//         nameCol.getChildren().addAll(
//                 nameLbl,
//                 label(
//                         id,
//                         "small-muted"
//                 )
//         );

//         nameBox.getChildren().addAll(
//                 avatarDot,
//                 nameCol
//         );

//         Label depotLbl =
//                 new Label(depot);

//         depotLbl.setStyle(
//                 "-fx-text-fill:#cbd5e1;" +
//                 "-fx-font-size:12px;"
//         );

//         depotLbl.setPrefWidth(
//                 130
//         );

//         depotLbl.setMinWidth(
//                 130
//         );

//         Label busLbl =
//                 new Label(bus);

//         busLbl.setStyle(
//                 "-fx-text-fill:#cbd5e1;" +
//                 "-fx-font-size:12px;"
//         );

//         busLbl.setPrefWidth(
//                 120
//         );

//         busLbl.setMinWidth(
//                 120
//         );

//         Label shiftLbl =
//                 new Label(shift);

//         shiftLbl.setStyle(
//                 "-fx-text-fill:#94a3b8;" +
//                 "-fx-font-size:12px;"
//         );

//         shiftLbl.setPrefWidth(
//                 150
//         );

//         shiftLbl.setMinWidth(
//                 150
//         );

//         row.getChildren().addAll(
//                 nameBox,
//                 depotLbl,
//                 busLbl,
//                 shiftLbl,
//                 statusBadge(
//                         status,
//                         color,
//                         100
//                 )
//         );

//         return row;
//     }

//     /*
//      * ============================================================
//      * CUSTOMER CARD
//      * ============================================================
//      */

//     private static VBox buildCustomerRosterCard(
//             VBox rows) {

//         VBox card =
//                 new VBox(12);

//         card.getStyleClass().add(
//                 "bookings-section"
//         );

//         card.setPadding(
//                 new Insets(18)
//         );

//         HBox header =
//                 new HBox(10);

//         header.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         Label title =
//                 new Label(
//                         "Customer accounts"
//                 );

//         title.getStyleClass().add(
//                 "booking-stage"
//         );

//         Region sp =
//                 new Region();

//         HBox.setHgrow(
//                 sp,
//                 Priority.ALWAYS
//         );

//         header.getChildren().addAll(
//                 title,
//                 sp
//         );

//         HBox colHeaders =
//                 new HBox();

//         colHeaders.setPadding(
//                 new Insets(
//                         10,
//                         12,
//                         10,
//                         12
//                 )
//         );

//         colHeaders.getChildren().addAll(
//                 colLabel(
//                         "CUSTOMER",
//                         170
//                 ),
//                 colLabel(
//                         "EVS REGISTERED",
//                         150
//                 ),
//                 colLabel(
//                         "PHONE",
//                         140
//                 ),
//                 colLabel(
//                         "MEMBER SINCE",
//                         120
//                 ),
//                 colLabel(
//                         "STATUS",
//                         100
//                 )
//         );

//         card.getChildren().addAll(
//                 header,
//                 colHeaders,
//                 rows
//         );

//         return card;
//     }

//     private static HBox customerRow(
//             String name,
//             String id,
//             String evs,
//             String phone,
//             String memberSince,
//             String status,
//             String color) {

//         HBox row =
//                 new HBox(10);

//         row.setPadding(
//                 new Insets(
//                         11,
//                         12,
//                         11,
//                         12
//                 )
//         );

//         row.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         row.getStyleClass().add(
//                 "booking-row"
//         );

//         HBox nameBox =
//                 new HBox(8);

//         nameBox.setPrefWidth(
//                 170
//         );

//         nameBox.setMinWidth(
//                 170
//         );

//         nameBox.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         Circle avatarDot =
//                 new Circle(
//                         4,
//                         Color.web(color)
//                 );

//         VBox nameCol =
//                 new VBox(2);

//         Label nameLbl =
//                 new Label(name);

//         nameLbl.setStyle(
//                 "-fx-text-fill:#f8fafc;" +
//                 "-fx-font-size:12px;" +
//                 "-fx-font-weight:bold;"
//         );

//         nameCol.getChildren().addAll(
//                 nameLbl,
//                 label(
//                         id,
//                         "small-muted"
//                 )
//         );

//         nameBox.getChildren().addAll(
//                 avatarDot,
//                 nameCol
//         );

//         Label evsLbl =
//                 new Label(evs);

//         evsLbl.setStyle(
//                 "-fx-text-fill:#cbd5e1;" +
//                 "-fx-font-size:12px;"
//         );

//         evsLbl.setPrefWidth(
//                 150
//         );

//         evsLbl.setMinWidth(
//                 150
//         );

//         Label phoneLbl =
//                 new Label(phone);

//         phoneLbl.setStyle(
//                 "-fx-text-fill:#cbd5e1;" +
//                 "-fx-font-size:12px;"
//         );

//         phoneLbl.setPrefWidth(
//                 140
//         );

//         phoneLbl.setMinWidth(
//                 140
//         );

//         Label memberSinceLbl =
//                 new Label(memberSince);

//         memberSinceLbl.setStyle(
//                 "-fx-text-fill:#94a3b8;" +
//                 "-fx-font-size:12px;"
//         );

//         memberSinceLbl.setPrefWidth(
//                 120
//         );

//         memberSinceLbl.setMinWidth(
//                 120
//         );

//         row.getChildren().addAll(
//                 nameBox,
//                 evsLbl,
//                 phoneLbl,
//                 memberSinceLbl,
//                 statusBadge(
//                         status,
//                         color,
//                         100
//                 )
//         );

//         return row;
//     }
// }
package com.core2web.view;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.core2web.controller.BusController;
import com.core2web.controller.DriverController;
import com.core2web.controller.OwnerController;
import com.core2web.controller.TicketController;
import com.core2web.controller.VehicleController;
import com.core2web.model.Bus;
import com.core2web.model.Driver;
import com.core2web.model.Owner;
import com.core2web.model.Ticket;

import static com.core2web.view.AdminLayout.*;

public class UsersAndDrivers {

    private static final DriverController driverController =
            new DriverController();

    private static final OwnerController ownerController =
            new OwnerController();

    private static final VehicleController vehicleController =
            new VehicleController();

    private static final BusController busController =
            new BusController();

    private static final TicketController ticketController =
            new TicketController();

    /*
     * India time zone.
     */
    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");

    public static void show(Stage stage) {
        AdminDashboard.goTo("Users & Drivers");
    }

    static ScrollPane buildMainContent() {

        VBox content = new VBox(16);

        content.setPadding(
                new Insets(16)
        );

        /*
         * Read current data from Firebase through the existing controllers.
         */
        List<Driver> drivers =
                driverController.getAllDrivers();

        List<Owner> owners =
                ownerController.getAllOwners();

        int[] vehicleCounts =
                new int[owners.size()];

        for (int i = 0;
             i < owners.size();
             i++) {

            vehicleCounts[i] =
                    vehicleController.getVehicleCountForOwner(
                            owners.get(i).getUid()
                    );
        }

        /*
         * KPI row.
         *
         * Values are calculated dynamically from Firebase.
         */
        HBox kpiRow =
                buildKpiRow(
                        drivers,
                        owners
                );

        /*
         * One fleet read, shared by the roster column
         * and the integrity panel.
         */
        FleetIndex index =
                new FleetIndex(
                        busController.getAllBuses()
                );

        VBox driverRows =
                new VBox(2);

        populateDriverRows(
                driverRows,
                drivers,
                index
        );

        VBox customerRows =
                new VBox(2);

        populateCustomerRows(
                customerRows,
                owners,
                vehicleCounts
        );

        content.getChildren().addAll(
                kpiRow,
                buildIntegrityPanel(
                        drivers,
                        index
                ),
                buildDriverRosterCard(
                        driverRows
                ),
                buildCustomerRosterCard(
                        customerRows
                )
        );

        ScrollPane sp =
                new ScrollPane(content);

        sp.setFitToWidth(true);

        sp.getStyleClass().add(
                "scroll-pane"
        );

        return sp;
    }

    private static Label loadingLabel(
            String text) {

        Label l =
                new Label(text);

        l.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;"
        );

        return l;
    }

    private static void openReassignDialog() {

        List<Driver> drivers =
                driverController.getAllDrivers();

        List<Bus> buses =
                busController.getAllBuses();

        if (drivers.isEmpty()
                || buses.isEmpty()) {

            Alert none =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            none.setHeaderText(null);

            none.setContentText(
                    drivers.isEmpty()
                            ? "There are no drivers to reassign yet."
                            : "There are no buses in the fleet yet."
            );

            none.showAndWait();

            return;
        }

        Map<String, String> holderByBusId =
                new HashMap<>();

        for (Bus bus : buses) {

            if (BusController.hasDriver(bus)) {

                Driver holder = null;

                for (Driver d : drivers) {

                    if (d.getUid().equals(
                            bus.getAssignedDriverId()
                    )) {

                        holder = d;
                        break;
                    }
                }

                holderByBusId.put(
                        bus.getId(),
                        holder == null
                                ? "unknown driver"
                                : nameOf(holder)
                );
            }
        }

        ComboBox<Driver> driverCombo =
                new ComboBox<>();

        driverCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        driverCombo.setConverter(
                new StringConverter<Driver>() {

                    @Override
                    public String toString(
                            Driver d) {

                        return d == null
                                ? ""
                                : nameOf(d)
                                        + " · "
                                        + d.getDepot();
                    }

                    @Override
                    public Driver fromString(
                            String s) {

                        return null;
                    }
                }
        );

        driverCombo.getItems().setAll(
                drivers
        );

        driverCombo.setPromptText(
                "Select driver"
        );

        ComboBox<Bus> busCombo =
                new ComboBox<>();

        busCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        busCombo.setConverter(
                new StringConverter<Bus>() {

                    @Override
                    public String toString(
                            Bus b) {

                        if (b == null) {
                            return "";
                        }

                        String holder =
                                holderByBusId.get(
                                        b.getId()
                                );

                        return b.getBusCode()
                                + " · "
                                + b.getDepot()
                                + (
                                        holder == null
                                                ? ""
                                                : "  (currently "
                                                        + holder
                                                        + ")"
                                );
                    }

                    @Override
                    public Bus fromString(
                            String s) {

                        return null;
                    }
                }
        );

        busCombo.getItems().setAll(
                buses
        );

        busCombo.setPromptText(
                "Select bus"
        );

        Label warning =
                new Label();

        warning.setStyle(
                "-fx-text-fill:#f59e0b;" +
                "-fx-font-size:11px;"
        );

        warning.setWrapText(true);
        warning.setVisible(false);
        warning.setManaged(false);

        busCombo.valueProperty().addListener(
                (obs, old, bus) -> {

                    String holder =
                            bus == null
                                    ? null
                                    : holderByBusId.get(
                                            bus.getId()
                                    );

                    boolean occupied =
                            holder != null;

                    if (occupied) {

                        warning.setText(
                                bus.getBusCode()
                                        + " is currently assigned to "
                                        + holder
                                        + ". Reassigning it will leave them with no bus."
                        );
                    }

                    warning.setVisible(
                            occupied
                    );

                    warning.setManaged(
                            occupied
                    );
                }
        );

        VBox form =
                new VBox(
                        10,
                        new Label("Driver"),
                        driverCombo,
                        new Label("Bus"),
                        busCombo,
                        warning
                );

        form.setPadding(
                new Insets(12)
        );

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Reassign bus"
        );

        dialog.setHeaderText(
                "Move a driver to a different bus"
        );

        dialog.getDialogPane().setContent(
                form
        );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.CANCEL,
                        ButtonType.OK
                );

        if (dialog.showAndWait()
                .filter(
                        b -> b == ButtonType.OK
                )
                .isEmpty()) {

            return;
        }

        Driver driver =
                driverCombo.getValue();

        Bus bus =
                busCombo.getValue();

        if (driver == null
                || bus == null) {

            Alert incomplete =
                    new Alert(
                            Alert.AlertType.WARNING
                    );

            incomplete.setHeaderText(
                    null
            );

            incomplete.setContentText(
                    "Pick both a driver and a bus."
            );

            incomplete.showAndWait();

            return;
        }

        Thread worker =
                new Thread(() -> {

                    boolean ok =
                            driverController.assignBusToDriver(
                                    driver.getUid(),
                                    bus.getId()
                            );

                    Platform.runLater(
                            () -> {

                                Alert result =
                                        new Alert(
                                                ok
                                                        ? Alert.AlertType.INFORMATION
                                                        : Alert.AlertType.ERROR
                                        );

                                result.setHeaderText(
                                        null
                                );

                                result.setContentText(
                                        ok
                                                ? nameOf(driver)
                                                        + " is now assigned to "
                                                        + bus.getBusCode()
                                                        + "."
                                                : "Could not reassign the bus. Check the app output for the reason."
                                );

                                result.showAndWait();

                                if (ok) {

                                    AdminDashboard.goTo(
                                            "Users & Drivers"
                                    );
                                }
                            }
                    );
                });

        worker.setDaemon(true);

        worker.start();
    }

    private static void confirmAndRepairLinks(
            Button trigger) {

        Alert confirm =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirm.setTitle(
                "Repair driver–bus links"
        );

        confirm.setHeaderText(
                "Rewrite mismatched driver assignments?"
        );

        confirm.setContentText(
                "For every bus that names a driver, that driver's assignedBusId will be set to "
                        + "the bus's document ID. Buses are treated as correct.\n\n"
                        + "Ambiguous cases (two buses claiming one driver, a driver claiming "
                        + "someone else's bus) are reported, not changed."
        );

        if (confirm.showAndWait()
                .filter(
                        b -> b == ButtonType.OK
                )
                .isEmpty()) {

            return;
        }

        trigger.setDisable(
                true
        );

        trigger.setText(
                "Repairing…"
        );

        Thread worker =
                new Thread(() -> {

                    DriverController.LinkRepairReport report =
                            driverController.repairBusLinks();

                    Platform.runLater(
                            () -> {

                                trigger.setDisable(
                                        false
                                );

                                trigger.setText(
                                        "Repair links"
                                );

                                Alert result =
                                        new Alert(
                                                report.changedAnything()
                                                        ? Alert.AlertType.INFORMATION
                                                        : Alert.AlertType.WARNING
                                        );

                                result.setTitle(
                                        "Repair driver–bus links"
                                );

                                result.setHeaderText(
                                        report.changedAnything()
                                                ? "Repaired "
                                                        + report.repaired.size()
                                                        + " link(s)."
                                                : "No links were changed."
                                );

                                TextArea detail =
                                        new TextArea(
                                                report.summary()
                                        );

                                detail.setEditable(
                                        false
                                );

                                detail.setWrapText(
                                        true
                                );

                                detail.setPrefRowCount(
                                        12
                                );

                                detail.setPrefColumnCount(
                                        60
                                );

                                result.getDialogPane()
                                        .setContent(
                                                detail
                                        );

                                result.showAndWait();

                                if (report.changedAnything()) {

                                    AdminDashboard.goTo(
                                            "Users & Drivers"
                                    );
                                }
                            }
                    );
                });

        worker.setDaemon(
                true
        );

        worker.start();
    }

    private static VBox buildIntegrityPanel(
            List<Driver> drivers,
            FleetIndex index) {

        Map<LinkState, List<String>> byState =
                new LinkedHashMap<>();

        for (Driver d : drivers) {

            LinkState state =
                    linkStateOf(
                            d,
                            index
                    );

            if (state == LinkState.OK
                    || state == LinkState.UNASSIGNED) {

                continue;
            }

            byState
                    .computeIfAbsent(
                            state,
                            k -> new ArrayList<>()
                    )
                    .add(
                            nameOf(d)
                                    + " ("
                                    + busCodeOf(
                                            d,
                                            index
                                    )
                                    + ")"
                    );
        }

        Set<String> driverUids =
                new HashSet<>();

        for (Driver d : drivers) {

            driverUids.add(
                    d.getUid()
            );
        }

        List<String> orphanBuses =
                new ArrayList<>();

        int linkedBuses = 0;

        for (Bus bus : index.buses) {

            if (!BusController.hasDriver(bus)) {
                continue;
            }

            linkedBuses++;

            if (!driverUids.contains(
                    bus.getAssignedDriverId()
            )) {

                orphanBuses.add(
                        bus.getBusCode()
                                + " -> driver "
                                + bus.getAssignedDriverId()
                );
            }
        }

        int problems =
                orphanBuses.size();

        for (List<String> names :
                byState.values()) {

            problems += names.size();
        }

        VBox card =
                new VBox(8);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(16)
        );

        HBox header =
                new HBox(10);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        5,
                        Color.web(
                                problems == 0
                                        ? "#10b981"
                                        : "#f59e0b"
                        )
                );

        Label title =
                new Label(
                        "Link integrity"
                );

        title.getStyleClass().add(
                "section-title"
        );

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );

        Label count =
                new Label(
                        drivers.size()
                                + " drivers · "
                                + index.buses.size()
                                + " buses · "
                                + linkedBuses
                                + " linked · "
                                + (
                                        problems == 0
                                                ? "0 problems"
                                                : problems
                                                        + " problem(s)"
                                )
                );

        count.getStyleClass().add(
                "small-muted"
        );

        header.getChildren().addAll(
                dot,
                title,
                sp,
                count
        );

        card.getChildren().add(
                header
        );

        if (problems == 0) {

            Label ok =
                    new Label(
                            "Every driver–bus link agrees in both directions."
                    );

            ok.setStyle(
                    "-fx-text-fill:#10b981;" +
                    "-fx-font-size:12px;"
            );

            card.getChildren().add(
                    ok
            );

            return card;
        }

        for (Map.Entry<LinkState, List<String>> entry :
                byState.entrySet()) {

            card.getChildren().add(
                    problemLine(
                            entry.getKey().note,
                            String.join(
                                    ", ",
                                    entry.getValue()
                            )
                    )
            );
        }

        if (!orphanBuses.isEmpty()) {

            card.getChildren().add(
                    problemLine(
                            "bus assigned to a driver with no Driver document",
                            String.join(
                                    ", ",
                                    orphanBuses
                            )
                    )
            );
        }

        Label hint =
                new Label(
                        "\"Repair links\" below fixes the one-sided and "
                                + "legacy-code cases. Mismatches and orphans need a decision on the bus side first."
                );

        hint.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        hint.setWrapText(
                true
        );

        card.getChildren().add(
                hint
        );

        return card;
    }

    private static Label problemLine(
            String note,
            String who) {

        Label l =
                new Label(
                        "⚠  "
                                + note
                                + ": "
                                + who
                );

        l.setStyle(
                "-fx-text-fill:#f59e0b;" +
                "-fx-font-size:12px;"
        );

        l.setWrapText(
                true
        );

        return l;
    }

    private static String nameOf(
            Driver d) {

        return d.getName() == null
                || d.getName().isEmpty()
                        ? d.getUid()
                        : d.getName();
    }

    /*
     * ============================================================
     * KPI SECTION
     * ============================================================
     */

    private static HBox buildKpiRow(
            List<Driver> drivers,
            List<Owner> owners) {

        /*
         * Total active drivers from Firebase.
         */
        long activeDrivers =
                drivers.stream()
                        .filter(
                                d -> d != null
                                        && "active".equalsIgnoreCase(
                                                d.getStatus()
                                        )
                        )
                        .count();

        /*
         * Count only drivers belonging to the
         * CURRENT shift.
         *
         * There are ONLY two shifts:
         *
         * Morning
         * Night
         */
        long onShiftNow =
                drivers.stream()
                        .filter(
                                UsersAndDrivers::isCurrentlyOnShift
                        )
                        .count();

        /*
         * Current shift is calculated from
         * the current Indian time.
         */
        String currentShift =
                getCurrentShiftName();

        /*
         * Dynamic customer count for this month.
         */
        long newThisMonth =
                owners.stream()
                        .filter(
                                UsersAndDrivers::createdThisMonth
                        )
                        .count();

        /*
         * Dynamic pending support request count.
         *
         * Support.java uses:
         *
         * OPEN
         * RESOLVED
         *
         * Therefore OPEN tickets are pending.
         */
        int pendingCount =
                getPendingTicketCount();

        VBox pendingApprovals =
                statCard(
                        "Pending approvals",
                        String.valueOf(
                                pendingCount
                        ),
                        "#f59e0b",
                        pendingCount == 1
                                ? "1 open support request"
                                : pendingCount
                                        + " open support requests"
                );

        pendingApprovals.setStyle(
                "-fx-cursor: hand;"
        );

        /*
         * Existing KPI layout is preserved.
         */
        HBox row =
                new HBox(16);

        row.getChildren().setAll(

                statCard(
                        "Total drivers",
                        String.valueOf(
                                drivers.size()
                        ),
                        "#f8fafc",
                        activeDrivers
                                + " active"
                ),

                statCard(
                        "On shift now",
                        String.valueOf(
                                onShiftNow
                        ),
                        "#10b981",
                        currentShift
                                + " shift"
                ),

                statCard(
                        "Total customers",
                        String.valueOf(
                                owners.size()
                        ),
                        "#f8fafc",
                        newThisMonth
                                + " new this month"
                ),

                pendingApprovals
        );

        return row;
    }

    /*
     * ============================================================
     * CURRENT SHIFT
     * ============================================================
     *
     * ChargeOn has ONLY TWO shifts:
     *
     * Morning = 06:00 - 17:59
     * Night   = 18:00 - 05:59
     *
     * There is NO afternoon shift.
     *
     * Time zone:
     * Asia/Kolkata
     */

    private static String getCurrentShiftName() {

        LocalTime now =
                ZonedDateTime.now(
                        INDIA_ZONE
                ).toLocalTime();

        /*
         * Night starts at 18:00.
         *
         * 06:00 - 17:59 -> Morning
         * 18:00 - 05:59 -> Night
         */
        LocalTime nightStart =
                LocalTime.of(
                        18,
                        0
                );

        if (now.isBefore(
                nightStart
        )) {

            /*
             * This covers:
             *
             * 00:00 - 05:59
             * 06:00 - 17:59
             *
             * But midnight to 05:59 belongs to Night.
             */
            LocalTime morningStart =
                    LocalTime.of(
                            6,
                            0
                    );

            if (!now.isBefore(
                    morningStart
            )) {

                return "Morning";
            }

            return "Night";
        }

        /*
         * 18:00 - 23:59
         */
        return "Night";
    }

    /*
     * ============================================================
     * CURRENT SHIFT DRIVER CHECK
     * ============================================================
     *
     * A driver is counted as "On shift now" ONLY when:
     *
     * 1. Driver exists
     * 2. Driver status is active
     * 3. Driver has a shift in Firebase
     * 4. Firebase shift matches the current Indian shift
     */

    private static boolean isCurrentlyOnShift(
            Driver driver) {

        if (driver == null) {
            return false;
        }

        /*
         * Only active drivers count.
         */
        if (!"active".equalsIgnoreCase(
                driver.getStatus()
        )) {

            return false;
        }

        String driverShift =
                driver.getShift();

        if (driverShift == null
                || driverShift.trim().isEmpty()) {

            return false;
        }

        String currentShift =
                getCurrentShiftName();

        /*
         * Firebase examples:
         *
         * shift = Morning
         * shift = Night
         *
         * No Afternoon value is expected.
         */
        return currentShift.equalsIgnoreCase(
                driverShift.trim()
        );
    }

    /*
     * ============================================================
     * PENDING SUPPORT REQUESTS
     * ============================================================
     *
     * Support.java already uses:
     *
     * OPEN
     * RESOLVED
     *
     * So OPEN tickets are counted as pending.
     */

    private static int getPendingTicketCount() {

        try {

            List<Ticket> tickets =
                    ticketController.getAllTickets();

            if (tickets == null
                    || tickets.isEmpty()) {

                return 0;
            }

            int count = 0;

            for (Ticket ticket :
                    tickets) {

                if (ticket == null) {
                    continue;
                }

                String status =
                        ticket.getStatus();

                if (status != null
                        && "OPEN".equalsIgnoreCase(
                                status.trim()
                        )) {

                    count++;
                }
            }

            return count;

        } catch (Exception e) {

            /*
             * Support/Firebase failure should not
             * crash the Users & Drivers page.
             */
            e.printStackTrace();

            return 0;
        }
    }

    private static boolean createdThisMonth(
            Owner owner) {

        try {

            ZonedDateTime createdAt =
                    Instant.parse(
                            owner.getCreatedAt()
                    ).atZone(
                            ZoneId.of("UTC")
                    );

            ZonedDateTime now =
                    ZonedDateTime.now(
                            ZoneId.of("UTC")
                    );

            return createdAt.getYear()
                    == now.getYear()
                    && createdAt.getMonth()
                    == now.getMonth();

        } catch (Exception e) {

            return false;
        }
    }

    /*
     * ============================================================
     * FLEET INDEX
     * ============================================================
     */

    private static final class FleetIndex {

        final List<Bus> buses;

        final Map<String, String> busCodeByKey =
                new HashMap<>();

        final Map<String, String> driverToBusCode =
                new HashMap<>();

        FleetIndex(
                List<Bus> buses) {

            this.buses =
                    buses;

            for (Bus bus :
                    buses) {

                busCodeByKey.put(
                        bus.getId(),
                        bus.getBusCode()
                );

                if (bus.getBusCode() != null
                        && !bus.getBusCode().isEmpty()) {

                    busCodeByKey.put(
                            bus.getBusCode(),
                            bus.getBusCode()
                    );
                }

                if (BusController.hasDriver(
                        bus
                )) {

                    driverToBusCode.put(
                            bus.getAssignedDriverId(),
                            bus.getBusCode()
                    );
                }
            }
        }
    }

    private enum LinkState {

        OK("linked"),

        UNASSIGNED("no bus"),

        LEGACY_CODE(
                "assignedBusId holds the busCode, not the document ID"
        ),

        NOT_LINKED_BACK(
                "only one side of the link is set"
        ),

        MISMATCH(
                "driver and bus name different buses"
        ),

        UNKNOWN_BUS(
                "assignedBusId names a bus that does not exist"
        );

        final String note;

        LinkState(
                String note) {

            this.note =
                    note;
        }
    }

    private static LinkState linkStateOf(
            Driver d,
            FleetIndex index) {

        String declared =
                d.getAssignedBusId();

        boolean declaredBlank =
                declared == null
                        || declared.isEmpty()
                        || declared.equals(
                                "null"
                        );

        String claimedCode =
                index.driverToBusCode.get(
                        d.getUid()
                );

        if (claimedCode != null) {

            if (declaredBlank) {
                return LinkState.NOT_LINKED_BACK;
            }

            if (declared.equals(
                    claimedCode
            )) {

                return LinkState.LEGACY_CODE;
            }

            return claimedCode.equals(
                    index.busCodeByKey.get(
                            declared
                    )
            )
                    ? LinkState.OK
                    : LinkState.MISMATCH;
        }

        if (declaredBlank) {
            return LinkState.UNASSIGNED;
        }

        return index.busCodeByKey.containsKey(
                declared
        )
                ? LinkState.NOT_LINKED_BACK
                : LinkState.UNKNOWN_BUS;
    }

    private static String busCodeOf(
            Driver d,
            FleetIndex index) {

        String claimed =
                index.driverToBusCode.get(
                        d.getUid()
                );

        if (claimed != null) {
            return claimed;
        }

        return BusController.busCodeLabel(
                d.getAssignedBusId(),
                index.busCodeByKey,
                ""
        );
    }

    /*
     * ============================================================
     * DRIVER ROSTER
     * ============================================================
     */

    private static void populateDriverRows(
            VBox driverRows,
            List<Driver> drivers,
            FleetIndex index) {

        driverRows.getChildren().clear();

        if (drivers.isEmpty()) {

            driverRows.getChildren().add(
                    loadingLabel(
                            "No drivers registered yet."
                    )
            );

            return;
        }

        for (Driver d :
                drivers) {

            boolean active =
                    "active".equalsIgnoreCase(
                            d.getStatus()
                    );

            driverRows.getChildren().add(
                    driverRow(
                            d.getName(),
                            d.getUid(),
                            d.getDepot(),
                            busLabel(
                                    d,
                                    index
                            ),
                            d.getShift(),
                            d.getStatus() == null
                                    ? ""
                                    : d.getStatus()
                                            .toUpperCase(),
                            active
                                    ? "#10b981"
                                    : "#ef4444"
                    )
            );
        }
    }

    private static String busLabel(
            Driver d,
            FleetIndex index) {

        LinkState state =
                linkStateOf(
                        d,
                        index
                );

        String code =
                busCodeOf(
                        d,
                        index
                );

        switch (state) {

            case OK:
                return code;

            case UNASSIGNED:
                return "— unassigned";

            case LEGACY_CODE:
                return code
                        + "  ⚠ legacy code";

            case NOT_LINKED_BACK:
                return code
                        + "  ⚠ not linked back";

            case MISMATCH:
                return code
                        + "  ⚠ link mismatch";

            default:
                return "⚠ unknown bus";
        }
    }

    /*
     * ============================================================
     * CUSTOMER ROSTER
     * ============================================================
     */

    private static void populateCustomerRows(
            VBox customerRows,
            List<Owner> owners,
            int[] vehicleCounts) {

        customerRows.getChildren().clear();

        if (owners.isEmpty()) {

            customerRows.getChildren().add(
                    loadingLabel(
                            "No customers registered yet."
                    )
            );

            return;
        }

        for (int i = 0;
             i < owners.size();
             i++) {

            Owner o =
                    owners.get(i);

            boolean active =
                    "active".equalsIgnoreCase(
                            o.getStatus()
                    );

            customerRows.getChildren().add(
                    customerRow(
                            o.getName(),
                            o.getUid(),
                            vehicleCounts[i]
                                    + (
                                            vehicleCounts[i] == 1
                                                    ? " EV"
                                                    : " EVs"
                                    ),
                            o.getPhone(),
                            memberSinceLabel(
                                    o.getCreatedAt()
                            ),
                            o.getStatus() == null
                                    ? ""
                                    : o.getStatus()
                                            .toUpperCase(),
                            active
                                    ? "#10b981"
                                    : "#6b7280"
                    )
            );
        }
    }

    private static String memberSinceLabel(
            String createdAt) {

        try {

            Instant instant =
                    Instant.parse(
                            createdAt
                    );

            return instant.atZone(
                    ZoneId.of("UTC")
            ).toLocalDate().toString();

        } catch (Exception e) {

            return "—";
        }
    }

    /*
     * ============================================================
     * DRIVER ROSTER CARD
     * ============================================================
     */

    private static VBox buildDriverRosterCard(
            VBox rows) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "bookings-section"
        );

        card.setPadding(
                new Insets(18)
        );

        HBox header =
                new HBox(10);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        "Driver roster"
                );

        title.getStyleClass().add(
                "booking-stage"
        );

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );

        Button reassign =
                new Button(
                        "Reassign bus"
                );

        reassign.getStyleClass().add(
                "secondary-btn"
        );

        reassign.setTooltip(
                new Tooltip(
                        "Move an existing driver to a different bus, updating both sides of the link."
                )
        );

        reassign.setOnAction(
                e -> openReassignDialog()
        );

        Button repairLinks =
                new Button(
                        "Repair links"
                );

        repairLinks.getStyleClass().add(
                "secondary-btn"
        );

        repairLinks.setTooltip(
                new Tooltip(
                        "Rewrites driver assignedBusId values that disagree with the bus they're assigned to. Bus.assignedDriverId is treated as correct."
                )
        );

        repairLinks.setOnAction(
                e -> confirmAndRepairLinks(
                        repairLinks
                )
        );

        Button addDriver =
                new Button(
                        "+ Add driver"
                );

        addDriver.getStyleClass().add(
                "primary-btn"
        );

        addDriver.setOnAction(
                e -> {

                    AddUserAccount addDriverPage =
                            new AddUserAccount(
                                    AdminDashboard.window,
                                    true
                            );

                    AdminDashboard.heading.setText(
                            "Add Driver"
                    );

                    AdminDashboard.subheading.setText(
                            "Register a new driver account"
                    );

                    AdminDashboard.middleBox
                            .getChildren()
                            .setAll(
                                    addDriverPage.getMainContent()
                            );

                    AdminDashboard.window.setTitle(
                            "ChargeOn · Add Driver"
                    );
                }
        );

        header.getChildren().addAll(
                title,
                sp,
                reassign,
                repairLinks,
                addDriver
        );

        HBox colHeaders =
                new HBox();

        colHeaders.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        colHeaders.getChildren().addAll(
                colLabel(
                        "DRIVER",
                        170
                ),
                colLabel(
                        "DEPOT",
                        130
                ),
                colLabel(
                        "ASSIGNED BUS",
                        120
                ),
                colLabel(
                        "SHIFT",
                        150
                ),
                colLabel(
                        "STATUS",
                        100
                )
        );

        card.getChildren().addAll(
                header,
                colHeaders,
                rows
        );

        return card;
    }

    private static HBox driverRow(
            String name,
            String id,
            String depot,
            String bus,
            String shift,
            String status,
            String color) {

        HBox row =
                new HBox(10);

        row.setPadding(
                new Insets(
                        11,
                        12,
                        11,
                        12
                )
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.getStyleClass().add(
                "booking-row"
        );

        HBox nameBox =
                new HBox(8);

        nameBox.setPrefWidth(
                170
        );

        nameBox.setMinWidth(
                170
        );

        nameBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle avatarDot =
                new Circle(
                        4,
                        Color.web(color)
                );

        VBox nameCol =
                new VBox(2);

        Label nameLbl =
                new Label(name);

        nameLbl.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        nameCol.getChildren().addAll(
                nameLbl,
                label(
                        id,
                        "small-muted"
                )
        );

        nameBox.getChildren().addAll(
                avatarDot,
                nameCol
        );

        Label depotLbl =
                new Label(depot);

        depotLbl.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        depotLbl.setPrefWidth(
                130
        );

        depotLbl.setMinWidth(
                130
        );

        Label busLbl =
                new Label(bus);

        busLbl.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        busLbl.setPrefWidth(
                120
        );

        busLbl.setMinWidth(
                120
        );

        Label shiftLbl =
                new Label(shift);

        shiftLbl.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;"
        );

        shiftLbl.setPrefWidth(
                150
        );

        shiftLbl.setMinWidth(
                150
        );

        row.getChildren().addAll(
                nameBox,
                depotLbl,
                busLbl,
                shiftLbl,
                statusBadge(
                        status,
                        color,
                        100
                )
        );

        return row;
    }

    /*
     * ============================================================
     * CUSTOMER CARD
     * ============================================================
     */

    private static VBox buildCustomerRosterCard(
            VBox rows) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "bookings-section"
        );

        card.setPadding(
                new Insets(18)
        );

        HBox header =
                new HBox(10);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        "Customer accounts"
                );

        title.getStyleClass().add(
                "booking-stage"
        );

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );

        header.getChildren().addAll(
                title,
                sp
        );

        HBox colHeaders =
                new HBox();

        colHeaders.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        colHeaders.getChildren().addAll(
                colLabel(
                        "CUSTOMER",
                        170
                ),
                colLabel(
                        "EVS REGISTERED",
                        150
                ),
                colLabel(
                        "PHONE",
                        140
                ),
                colLabel(
                        "MEMBER SINCE",
                        120
                ),
                colLabel(
                        "STATUS",
                        100
                )
        );

        card.getChildren().addAll(
                header,
                colHeaders,
                rows
        );

        return card;
    }

    private static HBox customerRow(
            String name,
            String id,
            String evs,
            String phone,
            String memberSince,
            String status,
            String color) {

        HBox row =
                new HBox(10);

        row.setPadding(
                new Insets(
                        11,
                        12,
                        11,
                        12
                )
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.getStyleClass().add(
                "booking-row"
        );

        HBox nameBox =
                new HBox(8);

        nameBox.setPrefWidth(
                170
        );

        nameBox.setMinWidth(
                170
        );

        nameBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle avatarDot =
                new Circle(
                        4,
                        Color.web(color)
                );

        VBox nameCol =
                new VBox(2);

        Label nameLbl =
                new Label(name);

        nameLbl.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        nameCol.getChildren().addAll(
                nameLbl,
                label(
                        id,
                        "small-muted"
                )
        );

        nameBox.getChildren().addAll(
                avatarDot,
                nameCol
        );

        Label evsLbl =
                new Label(evs);

        evsLbl.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        evsLbl.setPrefWidth(
                150
        );

        evsLbl.setMinWidth(
                150
        );

        Label phoneLbl =
                new Label(phone);

        phoneLbl.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        phoneLbl.setPrefWidth(
                140
        );

        phoneLbl.setMinWidth(
                140
        );

        Label memberSinceLbl =
                new Label(memberSince);

        memberSinceLbl.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;"
        );

        memberSinceLbl.setPrefWidth(
                120
        );

        memberSinceLbl.setMinWidth(
                120
        );

        row.getChildren().addAll(
                nameBox,
                evsLbl,
                phoneLbl,
                memberSinceLbl,
                statusBadge(
                        status,
                        color,
                        100
                )
        );

        return row;
    }
}


