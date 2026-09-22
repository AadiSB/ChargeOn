package com.core2web.model;

/**
 * Every rate and split in one place.
 *
 * <p>RATE_PER_KWH used to be duplicated in BookCharging and LiveTracking, and the
 * service fee was an inline literal in the booking form — so a rate change could
 * silently disagree between the quote and the receipt.
 *
 * <p>These are quote-time inputs only. The fare a customer was actually shown is
 * stored on the Booking ({@code amount}, {@code serviceFee}, {@code driverPayout})
 * and must never be recomputed from these constants afterwards: changing a rate
 * would otherwise retroactively rewrite the price of completed jobs.
 */
public final class Pricing {

    public static final double RATE_PER_KWH = 14.0;

    /** Service and travel fee on a normal booking. */
    public static final double SERVICE_FEE = 40;

    /** Priority dispatch fee, charged instead of SERVICE_FEE on emergencies. */
    public static final double EMERGENCY_FEE = 150;

    /**
     * Driver's share of the fare. The remainder is platform revenue.
     * Tune this one number to change every future payout.
     */
    public static final double DRIVER_SHARE = 0.70;

    private Pricing() {
    }

    public static double serviceFee(boolean isEmergency) {
        return isEmergency ? EMERGENCY_FEE : SERVICE_FEE;
    }

    public static double energyCost(double kwh) {
        return kwh * RATE_PER_KWH;
    }

    /** What the customer pays: energy plus the applicable fee. */
    public static double fare(double kwh, boolean isEmergency) {
        return energyCost(kwh) + serviceFee(isEmergency);
    }

    /** Rounded so the stored payout matches what is displayed to the driver. */
    public static double driverPayout(double fare) {
        return Math.round(fare * DRIVER_SHARE);
    }

    public static double platformShare(double fare) {
        return fare - driverPayout(fare);
    }
}
