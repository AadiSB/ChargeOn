package com.core2web.model;

public class Booking {

    public static final String STATUS_QUEUED                = "QUEUED";
    public static final String STATUS_ASSIGNED              = "ASSIGNED";
    public static final String STATUS_PENDING_DRIVER_ACCEPT = "PENDING_DRIVER_ACCEPT";
    public static final String STATUS_EN_ROUTE              = "EN_ROUTE";
    public static final String STATUS_ARRIVED               = "ARRIVED";
    public static final String STATUS_CHARGING              = "CHARGING";
    public static final String STATUS_COMPLETED             = "COMPLETED";
    public static final String STATUS_CANCELLED             = "CANCELLED";

    /*
     * Booking dispatch priority.
     *
     * EMERGENCY -> highest priority
     * INSTANT   -> second priority
     * RESERVE   -> third priority
     */
    public static final String TYPE_INSTANT   = "instant";
    public static final String TYPE_RESERVE   = "reserve";
    public static final String TYPE_EMERGENCY = "emergency";

    private String id;
    private String ownerId;
    private String vehicleId;
    private String status;
    private String scheduledTime;
    private String location;
    private String busId;
    private double kwh;
    private String createdAt;
    private String driverId;
    private boolean isEmergency;

    /*
     * The type of booking selected by the owner.
     *
     * instant   = immediate booking
     * reserve   = reserved/priority slot
     * emergency = emergency booking
     */
    private String bookingType;

    /**
     * Machine-readable pickup point. The human-readable {@link #location} label is
     * kept alongside it, not replaced. Absent or 0/0 means "coordinates unknown".
     */
    private double pickupLatitude;
    private double pickupLongitude;

    /**
     * When the owner's OTP was accepted, or "" if not yet verified.
     */
    private String otpVerifiedAt;

    /**
     * The fare quoted to the customer, and its split, frozen at booking time.
     */
    private double amount;
    private double serviceFee;
    private double driverPayout;

    public Booking() {
    }

    public Booking(
            String id,
            String ownerId,
            String vehicleId,
            String status,
            String scheduledTime,
            String location,
            String busId,
            double kwh,
            String createdAt) {

        this(
                id,
                ownerId,
                vehicleId,
                status,
                scheduledTime,
                location,
                busId,
                kwh,
                createdAt,
                "",
                false
        );
    }

    public Booking(
            String id,
            String ownerId,
            String vehicleId,
            String status,
            String scheduledTime,
            String location,
            String busId,
            double kwh,
            String createdAt,
            String driverId,
            boolean isEmergency) {

        this.id = id;
        this.ownerId = ownerId;
        this.vehicleId = vehicleId;
        this.status = status;
        this.scheduledTime = scheduledTime;
        this.location = location;
        this.busId = busId;
        this.kwh = kwh;
        this.createdAt = createdAt;
        this.driverId = driverId;
        this.isEmergency = isEmergency;

        /*
         * Preserve backward compatibility.
         *
         * Existing code which creates a Booking without explicitly specifying
         * bookingType will still work.
         */
        this.bookingType = isEmergency
                ? TYPE_EMERGENCY
                : TYPE_INSTANT;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public String getLocation() {
        return location;
    }

    public String getBusId() {
        return busId;
    }

    public double getKwh() {
        return kwh;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDriverId() {
        return driverId;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public String getBookingType() {
        return bookingType;
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public String getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public double getAmount() {
        return amount;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public double getDriverPayout() {
        return driverPayout;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setEmergency(boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public void setPickupLatitude(double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public void setOtpVerifiedAt(String otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public void setDriverPayout(double driverPayout) {
        this.driverPayout = driverPayout;
    }

    /**
     * Fare to bill, falling back to a recomputation for bookings created before
     * the amount was stored.
     */
    public double fareToBill() {
        return amount > 0
                ? amount
                : Pricing.fare(kwh, isEmergency);
    }

    /**
     * Driver's cut, with the same legacy fallback as {@link #fareToBill()}.
     */
    public double payoutToPay() {
        return driverPayout > 0
                ? driverPayout
                : Pricing.driverPayout(fareToBill());
    }

    /**
     * True once the owner has verified the OTP.
     */
    public boolean isOtpVerified() {
        return otpVerifiedAt != null
                && !otpVerifiedAt.isEmpty();
    }

    /**
     * Whether the owner may still withdraw this booking themselves.
     */
    public boolean isCancellableByOwner() {
        return STATUS_QUEUED.equals(status)
                || STATUS_ASSIGNED.equals(status)
                || STATUS_PENDING_DRIVER_ACCEPT.equals(status);
    }

    /**
     * Admins may cancel anything that hasn't already finished or been cancelled.
     */
    public boolean isCancellableByAdmin() {
        return !isCompleted()
                && !STATUS_CANCELLED.equals(status);
    }

    /**
     * The driver may only ask for the code once they have arrived at the pickup.
     */
    public boolean canVerifyOtp() {
        return !isOtpVerified()
                && (STATUS_ARRIVED.equals(status)
                || STATUS_EN_ROUTE.equals(status));
    }

    /**
     * True only when this booking carries a real pickup point.
     */
    public boolean hasPickupCoordinates() {
        return pickupLatitude != 0
                || pickupLongitude != 0;
    }

    private static final int SHORT_REF_LENGTH = 6;

    /**
     * Short human-readable booking reference.
     */
    public String shortRef() {

        if (id == null || id.isEmpty()) {
            return "BKG-?";
        }

        String head = id.length() <= SHORT_REF_LENGTH
                ? id
                : id.substring(0, SHORT_REF_LENGTH);

        return "BKG-" + head.toUpperCase();
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    /**
     * An emergency dispatch that the driver has neither accepted nor rejected yet.
     */
    public boolean isPendingDriverAccept() {
        return STATUS_PENDING_DRIVER_ACCEPT.equals(status);
    }

    public boolean isInProgress() {
        return STATUS_ASSIGNED.equals(status)
                || STATUS_EN_ROUTE.equals(status)
                || STATUS_ARRIVED.equals(status)
                || STATUS_CHARGING.equals(status);
    }

    public String statusColor() {

        switch (status == null ? "" : status) {

            case STATUS_QUEUED:
                return "#f59e0b";

            case STATUS_ASSIGNED:
                return "#10b981";

            case STATUS_PENDING_DRIVER_ACCEPT:
                return "#f87171";

            case STATUS_EN_ROUTE:
                return "#3b82f6";

            case STATUS_ARRIVED:
                return "#8b5cf6";

            case STATUS_CHARGING:
                return "#06b6d4";

            case STATUS_COMPLETED:
                return "#6b7280";

            case STATUS_CANCELLED:
                return "#ef4444";

            default:
                return "#6b7280";
        }
    }

    /**
     * Location and energy only.
     */
    public String summaryLine() {
        return summaryLine(null);
    }

    /**
     * @param busCode resolved human-readable bus code
     */
    public String summaryLine(String busCode) {

        StringBuilder sb = new StringBuilder();

        if (location != null && !location.isEmpty()) {
            sb.append(location);
        }

        if (busCode != null && !busCode.isEmpty()) {

            if (sb.length() > 0) {
                sb.append(" · ");
            }

            sb.append("Bus ").append(busCode);
        }

        if (kwh > 0) {

            if (sb.length() > 0) {
                sb.append(" · ");
            }

            sb.append((int) kwh).append(" kWh");
        }

        return sb.length() > 0
                ? sb.toString()
                : "—";
    }
}