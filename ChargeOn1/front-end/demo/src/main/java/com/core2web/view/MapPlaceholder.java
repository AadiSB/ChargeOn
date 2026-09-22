package com.core2web.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Stand-in panel for the screens that used to draw a map.
 *
 * <p>Says plainly that there is no map rather than drawing a decorative grid — the
 * position data behind it is still real and still shown as text, so a fake map
 * would be the only dishonest thing on the screen.
 */
final class MapPlaceholder {

    private MapPlaceholder() {
    }

    /** @param detail what the caller would have plotted, in words */
    static Region of(String detail) {

        Label title = new Label("Map view not available");
        title.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:14px;-fx-font-weight:bold;");

        Label body = new Label(detail == null ? "" : detail);
        body.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        body.setWrapText(true);
        body.setMaxWidth(380);
        body.setAlignment(Pos.CENTER);

        VBox box = new VBox(6, title, body);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        box.setMinHeight(230);
        box.setStyle("-fx-background-color:#0b1420;-fx-background-radius:8;"
                + "-fx-border-color:#1e293b;-fx-border-radius:8;");
        return box;
    }
}
