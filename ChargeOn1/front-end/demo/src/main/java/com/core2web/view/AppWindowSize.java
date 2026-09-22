package com.core2web.view;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/** Keeps every main application scene at the same usable window size. */
public final class AppWindowSize {
    // Deliberately below common laptop resolutions: the application must not
    // open maximized, but every page should still use one consistent size.
    private static final double PREFERRED_WIDTH = 1500;
    private static final double PREFERRED_HEIGHT = 740;
    private static final double WINDOW_DECORATION_HEIGHT = 40;

    private AppWindowSize() {
    }

    public static double width() {
        return Math.min(PREFERRED_WIDTH, screenBounds().getWidth());
    }

    public static double height() {
        return Math.min(PREFERRED_HEIGHT,
                screenBounds().getHeight() - WINDOW_DECORATION_HEIGHT);
    }

    private static Rectangle2D screenBounds() {
        return Screen.getPrimary().getVisualBounds();
    }
}
