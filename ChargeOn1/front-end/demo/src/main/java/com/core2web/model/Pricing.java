package com.core2web.model;

public final class Pricing {

    public static final double RATE_PER_KWH = 14.0;

    public static final double SERVICE_FEE = 40;

    public static final double EMERGENCY_FEE = 150;

    public static final double DRIVER_SHARE = 0.70;

    private Pricing() {
    }

    public static double serviceFee(boolean isEmergency) {
        return isEmergency ? EMERGENCY_FEE : SERVICE_FEE;
    }

    public static double energyCost(double kwh) {
        return kwh * RATE_PER_KWH;
    }

    public static double fare(double kwh, boolean isEmergency) {
        return energyCost(kwh) + serviceFee(isEmergency);
    }

    public static double driverPayout(double fare) {
        return Math.round(fare * DRIVER_SHARE);
    }

    public static double platformShare(double fare) {
        return fare - driverPayout(fare);
    }
}
