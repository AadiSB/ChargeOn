
package com.core2web.view; 
 
import java.util.List; 
 
import javafx.application.Platform; 
import javafx.geometry.Insets; 
import javafx.geometry.Pos; 
import javafx.scene.Scene; 
import javafx.scene.control.*; 
import javafx.scene.layout.*; 
import javafx.scene.paint.Color; 
 
import com.core2web.controller.AlertController; 
import com.core2web.controller.BookingController; 
import com.core2web.controller.NotificationController; 
import com.core2web.model.Alert; 
import com.core2web.model.Notification; 
 
public class Alerts_Issues { 
 
private static final AlertController alertController = new AlertController(); 
private static final NotificationController notificationController = new NotificationController(); 
private static final BookingController bookingController = new BookingController(); 
 
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
 
    return EmergencyBanner.build(pending, () -> DriverDashboard.goTo("Alerts & Issues")); 
} 
 
Scene getAlertsScene() { 
    DriverDashboard.goTo("Alerts & Issues"); 
    return DriverDashboard.scene; 
} 
 
static ScrollPane buildMainContent() { 
    return new Alerts_Issues().buildContent(); 
} 
 
private ScrollPane buildContent() { 
    VBox content = new VBox(16); 
    content.setPadding(new Insets(16)); 
 
    List<Alert> reports = alertController.getMyReports(); 
    List<Notification> notifications = notificationController.getMyNotifications(); 
 
    VBox reportsHolder = new VBox(); 
    populateReports(reportsHolder, reports); 
 
    VBox opsAlertsHolder = new VBox(); 
    populateOpsAlerts(opsAlertsHolder, notifications); 
 
    HBox columns = new HBox(16); 
 
    VBox leftCol = new VBox(16); 
    HBox.setHgrow(leftCol, Priority.ALWAYS); 
    leftCol.getChildren().add(buildReportForm(reportsHolder)); 
 
    VBox rightCol = new VBox(16); 
    rightCol.setPrefWidth(380); 
    rightCol.getChildren().addAll(buildRecentReports(reportsHolder), buildAlertsFromOps(opsAlertsHolder)); 
 
    columns.getChildren().addAll(leftCol, rightCol); 
 
    // Only added when a real pending emergency exists — no placeholder banner. 
    HBox emergencyBanner = buildEmergencyBanner(); 
    if (emergencyBanner != null) { 
        content.getChildren().add(emergencyBanner); 
    } 
 
    content.getChildren().add(columns); 
 
    ScrollPane sp = new ScrollPane(content); 
    sp.setFitToWidth(true); 
    sp.getStyleClass().add("scroll-pane"); 
    sp.setStyle("-fx-background-color: transparent;"); 
    return sp; 
} 
 
private static void populateReports(VBox reportsHolder, List<Alert> reports) { 
    reportsHolder.getChildren().clear(); 
 
    boolean hasActiveReports = false; 
 
    for (Alert a : reports) { 
 
        if ("CANCELLED".equalsIgnoreCase(a.getStatus())) { 
            continue; 
        } 
 
        hasActiveReports = true; 
 
        reportsHolder.getChildren().add( 
                reportItem( 
                        a, 
                        reportsHolder 
                ) 
        ); 
    } 
 
    if (!hasActiveReports) { 
        reportsHolder.getChildren().add(new Label("No reports submitted yet.") { 
            { 
                setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;"); 
            } 
        }); 
    } 
} 
 
private static void populateOpsAlerts(VBox opsAlertsHolder, List<Notification> notifications) { 
    opsAlertsHolder.getChildren().clear(); 
 
    if (notifications.isEmpty()) { 
        opsAlertsHolder.getChildren().add(new Label("No alerts from operations yet.") { 
            { 
                setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;"); 
            } 
        }); 
        return; 
    } 
 
    for (Notification n : notifications) { 
        opsAlertsHolder.getChildren().add( 
                opsAlertItem(n.displayTitle(), n.getMessage(), n.displayColor())); 
    } 
} 
 
private VBox buildReportForm(VBox reportsHolder) { 
    VBox form = new VBox(14); 
    form.getStyleClass().add("report-card"); 
    form.setPadding(new Insets(22)); 
 
    HBox header = new HBox(10); 
    header.setAlignment(Pos.CENTER_LEFT); 
    Label formTitle = new Label("Report a bus issue"); 
    formTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;"); 
    header.getChildren().addAll(formTitle); 
 
    Label formSubtitle = new Label("Goes straight to operations with bus ID, location and telemetry attached."); 
    formSubtitle.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;"); 
    formSubtitle.setWrapText(true); 
 
    Label whatLabel = new Label("What is wrong?"); 
    whatLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;-fx-font-weight:bold;"); 
    VBox.setMargin(whatLabel, new Insets(6, 0, 0, 0)); 
 
    ComboBox<String> issueType = new ComboBox<>(); 
    issueType.getItems().addAll("Charging port", "Display issue", "Battery fault", "Coolant system", 
            "Tyre pressure", "Other"); 
    issueType.setValue("Charging port"); 
    issueType.setMaxWidth(Double.MAX_VALUE); 
    issueType.getStyleClass().add("issue-combo"); 
 
    Label severityLabel = new Label("Severity"); 
    severityLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;-fx-font-weight:bold;"); 
    VBox.setMargin(severityLabel, new Insets(4, 0, 0, 0)); 
 
    ComboBox<String> severity = new ComboBox<>(); 
    severity.getItems().addAll("low", "medium", "critical"); 
    severity.setValue("medium"); 
    severity.setMaxWidth(Double.MAX_VALUE); 
    severity.getStyleClass().add("issue-combo"); 
 
    Label descLabel = new Label("Description"); 
    descLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;-fx-font-weight:bold;"); 
    VBox.setMargin(descLabel, new Insets(4, 0, 0, 0)); 
 
    TextArea descArea = new TextArea(); 
    descArea.setPromptText( 
            "Port 2 drops the handshake about 30 seconds into a session; customer had to re-plug twice..."); 
    descArea.setPrefRowCount(6); 
    descArea.setWrapText(true); 
    descArea.getStyleClass().add("desc-textarea"); 
 
    Label errorLbl = new Label(); 
    errorLbl.setStyle("-fx-text-fill:#ef4444;-fx-font-size:12px;"); 
    errorLbl.setWrapText(true); 
    errorLbl.setVisible(false); 
    errorLbl.setManaged(false); 
 
    HBox buttons = new HBox(12); 
    buttons.setAlignment(Pos.CENTER_LEFT); 
    buttons.setPadding(new Insets(4, 0, 0, 0)); 
 
    Button submitReport = new Button("Submit report"); 
    submitReport.getStyleClass().add("primary-btn"); 
    submitReport.setPrefWidth(200); 
    buttons.getChildren().addAll(submitReport); 
 
    submitReport.setOnAction(e -> { 
        String description = descArea.getText() == null ? "" : descArea.getText().trim(); 
 
        if (description.isEmpty()) { 
            errorLbl.setText("Please describe the issue before submitting."); 
            errorLbl.setVisible(true); 
            errorLbl.setManaged(true); 
            return; 
        } 
 
        submitReport.setDisable(true); 
        errorLbl.setVisible(false); 
        errorLbl.setManaged(false); 
 
        Thread submitter = new Thread(() -> { 
            String newId = alertController.submitAlert( 
                    issueType.getValue(), 
                    severity.getValue(), 
                    description 
            ); 
 
            List<Alert> refreshed = newId != null 
                    ? alertController.getMyReports() 
                    : null; 
 
            Platform.runLater(() -> { 
                submitReport.setDisable(false); 
 
                if (newId != null) { 
                    descArea.clear(); 
                    populateReports(reportsHolder, refreshed); 
                } else { 
                    errorLbl.setText("Failed to submit report. Please try again."); 
                    errorLbl.setVisible(true); 
                    errorLbl.setManaged(true); 
                } 
            }); 
        }); 
 
        submitter.setDaemon(true); 
        submitter.start(); 
    }); 
 
    form.getChildren().addAll( 
            header, 
            formSubtitle, 
            whatLabel, 
            issueType, 
            severityLabel, 
            severity, 
            descLabel, 
            descArea, 
            errorLbl, 
            buttons 
    ); 
 
    return form; 
} 
 
private VBox buildRecentReports(VBox reportsHolder) { 
    VBox section = new VBox(10); 
 
    Label sectionTitle = new Label("Recent reports"); 
    sectionTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;"); 
    sectionTitle.setPadding(new Insets(0, 0, 4, 0)); 
 
    section.getChildren().addAll(sectionTitle, reportsHolder); 
    return section; 
} 
 
private static HBox reportItem( 
        Alert alert, 
        VBox reportsHolder) { 
 
    HBox item = new HBox(10); 
    item.setPadding(new Insets(14, 16, 14, 16)); 
    item.getStyleClass().add("report-item"); 
    item.setAlignment(Pos.CENTER_LEFT); 
 
    VBox textBox = new VBox(4); 
    HBox.setHgrow(textBox, Priority.ALWAYS); 
 
    Label titleLbl = new Label(alert.getIssueType()); 
    System.out.println("Alert issue type: " + alert.getIssueType()); 
    titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;"); 
 
    Label detailLbl = new Label( 
            "#" + alert.getId() 
                    + " · " 
                    + alert.raisedLabel() 
                    + " · " 
                    + alert.getSeverity() 
    ); 
 
    detailLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;"); 
 
    textBox.getChildren().addAll( 
            titleLbl, 
            detailLbl 
    ); 
 
    // Status label removed. 
    // Cancel button and cancellation logic removed. 
 
    item.getChildren().add( 
            textBox 
    ); 
 
    return item; 
} 
 
private VBox buildAlertsFromOps(VBox opsAlertsHolder) { 
    VBox section = new VBox(10); 
 
    Label sectionTitle = new Label("Alerts from operations"); 
    sectionTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;"); 
    sectionTitle.setPadding(new Insets(4, 0, 4, 0)); 
 
    section.getChildren().addAll(sectionTitle, opsAlertsHolder); 
    return section; 
} 
 
private static VBox opsAlertItem( 
        String title, 
        String detail, 
        String borderColor) { 
 
    VBox item = new VBox(4); 
    item.setPadding(new Insets(14, 16, 14, 16)); 
    item.getStyleClass().add("ops-alert-item"); 
 
    item.setStyle( 
            "-fx-border-color:" 
                    + borderColor 
                    + " transparent transparent transparent;" 
                    + "-fx-border-width:0 0 0 3;" 
                    + "-fx-background-color:#111827;" 
                    + "-fx-background-radius:10;" 
                    + "-fx-border-radius:0 10 10 0;" 
    ); 
 
    Label titleLbl = new Label(title); 
    titleLbl.setStyle( 
            "-fx-text-fill:#f8fafc;" 
                    + "-fx-font-size:13px;" 
                    + "-fx-font-weight:bold;" 
    ); 
    titleLbl.setWrapText(true); 
 
    Label detailLbl = new Label(detail); 
    detailLbl.setStyle( 
            "-fx-text-fill:#64748b;" 
                    + "-fx-font-size:11px;" 
    ); 
    detailLbl.setWrapText(true); 
 
    item.getChildren().addAll( 
            titleLbl, 
            detailLbl 
    ); 
 
    return item; 
} 
 
private static String hexToRgba( 
        String hex, 
        double alpha) { 
 
    Color c = Color.web(hex); 
 
    int r = (int) (c.getRed() * 255); 
    int g = (int) (c.getGreen() * 255); 
    int b = (int) (c.getBlue() * 255); 
 
    return r 
            + "," 
            + g 
            + "," 
            + b 
            + "," 
            + alpha; 
} 
 
 
}