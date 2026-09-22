package com.core2web.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.effect.Glow;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapView;

final class GluonMapPane {

    static {
        System.setProperty(
                "javafx.platform",
                "desktop");

        System.setProperty(
                "http.agent",
                "Gluon Mobile/1.0.3");
    }

    private static final double MIN_ZOOM = 3.0;
    private static final double MAX_ZOOM = 19.0;

    static final class Marker {

        final double latitude;
        final double longitude;
        final String color;
        final boolean highlight;

        Marker(
                double latitude,
                double longitude,
                String color) {

            this(
                    latitude,
                    longitude,
                    color,
                    false);
        }

        Marker(
                double latitude,
                double longitude,
                String color,
                boolean highlight) {

            this.latitude = latitude;
            this.longitude = longitude;
            this.color = color;
            this.highlight = highlight;
        }
    }

    private final MapView mapView =
            new MapView();

    private final StackPane wrapper;

    private final List<Marker> markers =
            new ArrayList<>();

    private final double centerLat;
    private final double centerLng;
    private final double defaultZoom;

    private Marker pickupMarker;

    private javafx.animation.Timeline pulse;

    private final MapLayer markerLayer;

    private Marker routeStart;
    private Marker routeEnd;

    GluonMapPane(
            double centerLat,
            double centerLng,
            double zoom,
            List<Marker> markers) {

        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.defaultZoom = zoom;

        if (markers != null) {

            for (Marker m : markers) {

                if (m != null) {
                    this.markers.add(m);
                }
            }
        }

        mapView.setCenter(
                centerLat,
                centerLng);

        mapView.setZoom(zoom);

        mapView.setPrefSize(
                550,
                300);

        mapView.setMinHeight(
                280);

        mapView.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        markerLayer =
                createMarkerLayer();

        mapView.addLayer(
                markerLayer);

        mapView.addEventFilter(
                ScrollEvent.SCROLL,
                event -> {

                    if (event.getDeltaY() > 0) {

                        zoomIn();

                    } else if (event.getDeltaY() < 0) {

                        zoomOut();
                    }

                    event.consume();
                });

        wrapper =
                new StackPane(mapView);

        wrapper.getStyleClass()
                .add("map-container");

        wrapper.setMinHeight(
                280);
    }

    StackPane node() {
        return wrapper;
    }

    void zoomIn() {

        mapView.setZoom(
                Math.min(
                        mapView.getZoom() + 1.0,
                        MAX_ZOOM));
    }

    void zoomOut() {

        mapView.setZoom(
                Math.max(
                        mapView.getZoom() - 1.0,
                        MIN_ZOOM));
    }

    void recenter() {

        mapView.setCenter(
                centerLat,
                centerLng);

        mapView.setZoom(
                defaultZoom);
    }

    void setPickupLocation(
            double latitude,
            double longitude) {

        pickupMarker =
                new Marker(
                        latitude,
                        longitude,
                        "#EF4444",
                        true);

        mapView.setCenter(
                latitude,
                longitude);

        mapView.setZoom(
                15.0);

        markerLayer.requestLayout();
    }

    void setRoute(
            double startLatitude,
            double startLongitude,
            double endLatitude,
            double endLongitude) {

        routeStart =
                new Marker(
                        startLatitude,
                        startLongitude,
                        "#10B981");

        routeEnd =
                new Marker(
                        endLatitude,
                        endLongitude,
                        "#3B82F6");

        markerLayer.requestLayout();
    }

    void dispose() {

        if (pulse != null) {

            pulse.stop();

            pulse = null;
        }
    }

    private MapLayer createMarkerLayer() {

        return new MapLayer() {

            private final List<Circle> nodes =
                    new ArrayList<>();

            private Circle pickupNode;

            private Line routeLine;

            {
                for (Marker m : markers) {

                    Circle dot =
                            new Circle(
                                    8,
                                    Color.web(m.color));

                    if (m.highlight) {

                        dot.setEffect(
                                new Glow(0.6));

                        startPulse(dot);
                    }

                    nodes.add(dot);

                    getChildren().add(dot);
                }
            }

            @Override
            protected void layoutLayer() {

                super.layoutLayer();

                if (routeStart != null
                        && routeEnd != null) {

                    if (routeLine == null) {

                        routeLine =
                                new Line();

                        routeLine.setStroke(
                                Color.web("#3B82F6"));

                        routeLine.setStrokeWidth(3);

                        routeLine.getStrokeDashArray()
                                .addAll(
                                        8.0,
                                        6.0);

                        getChildren().add(
                                0,
                                routeLine);
                    }

                    Point2D startPoint =
                            getMapPoint(
                                    routeStart.latitude,
                                    routeStart.longitude);

                    Point2D endPoint =
                            getMapPoint(
                                    routeEnd.latitude,
                                    routeEnd.longitude);

                    routeLine.setStartX(
                            startPoint.getX());

                    routeLine.setStartY(
                            startPoint.getY());

                    routeLine.setEndX(
                            endPoint.getX());

                    routeLine.setEndY(
                            endPoint.getY());
                }

                for (int i = 0;
                        i < nodes.size();
                        i++) {

                    Marker m =
                            markers.get(i);

                    Point2D point =
                            getMapPoint(
                                    m.latitude,
                                    m.longitude);

                    nodes.get(i).setCenterX(
                            point.getX());

                    nodes.get(i).setCenterY(
                            point.getY());
                }

                if (pickupMarker != null) {

                    if (pickupNode == null) {

                        pickupNode =
                                new Circle(
                                        9,
                                        Color.web(
                                                pickupMarker.color));

                        pickupNode.setEffect(
                                new Glow(0.8));

                        getChildren().add(
                                pickupNode);

                        startPulse(
                                pickupNode);
                    }

                    Point2D pickupPoint =
                            getMapPoint(
                                    pickupMarker.latitude,
                                    pickupMarker.longitude);

                    pickupNode.setCenterX(
                            pickupPoint.getX());

                    pickupNode.setCenterY(
                            pickupPoint.getY());
                }
            }
        };
    }

    private void startPulse(
            Circle marker) {

        if (pulse != null) {

            pulse.stop();
        }

        pulse =
                new javafx.animation.Timeline(

                        new javafx.animation.KeyFrame(
                                javafx.util.Duration.ZERO,

                                new javafx.animation.KeyValue(
                                        marker.opacityProperty(),
                                        1.0)),

                        new javafx.animation.KeyFrame(
                                javafx.util.Duration.seconds(1.2),

                                new javafx.animation.KeyValue(
                                        marker.opacityProperty(),
                                        0.45)));

        pulse.setCycleCount(
                javafx.animation.Animation.INDEFINITE);

        pulse.setAutoReverse(
                true);

        pulse.play();
    }
}